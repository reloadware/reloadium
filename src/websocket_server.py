# Author: Johan Hanssen Seferidis
# License: MIT

import sys
import struct
import ssl
from base64 import b64encode
from hashlib import sha1
import logging
from socket import error as SocketError
import errno
import threading
from socketserver import ThreadingMixIn, TCPServer, StreamRequestHandler

import threading


class ThreadWithLoggedException(threading.Thread):
    """
    Similar to Thread but will log exceptions to passed logger.

    Args:
        logger: Logger instance used to log any exception in child thread

    Exception is also reachable via <thread>.exception from the main thread.
    """

    DIVIDER = "*"*80

    def __init__(self, *args, **kwargs):
        try:
            self.logger = kwargs.pop("logger")
        except KeyError:
            raise Exception("Missing 'logger' in kwargs")
        super().__init__(*args, **kwargs, name="reloadium-websocket-server")
        self.exception = None

    def run(self):
        try:
            if self._target is not None:
                self._target(*self._args, **self._kwargs)
        except Exception as exception:
            thread = threading.current_thread()
            self.exception = exception
            self.logger.exception(f"{self.DIVIDER}\nException in child thread {thread}: {exception}\n{self.DIVIDER}")
        finally:
            del self._target, self._args, self._kwargs


class WebsocketServerThread(ThreadWithLoggedException):
    """Dummy wrapper to make debug messages a bit more readable"""
    pass


logger = logging.getLogger(__name__)
logging.basicConfig()

'''
+-+-+-+-+-------+-+-------------+-------------------------------+
 0                   1                   2                   3
 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
+-+-+-+-+-------+-+-------------+-------------------------------+
|F|R|R|R| opcode|M| Payload len |    Extended payload length    |
|I|S|S|S|  (4)  |A|     (7)     |             (16/64)           |
|N|V|V|V|       |S|             |   (if payload len==126/127)   |
| |1|2|3|       |K|             |                               |
+-+-+-+-+-------+-+-------------+ - - - - - - - - - - - - - - - +
|     Extended payload length continued, if payload len == 127  |
+ - - - - - - - - - - - - - - - +-------------------------------+
|                     Payload Data continued ...                |
+---------------------------------------------------------------+
'''

FIN    = 0x80
OPCODE = 0x0f
MASKED = 0x80
PAYLOAD_LEN = 0x7f
PAYLOAD_LEN_EXT16 = 0x7e
PAYLOAD_LEN_EXT64 = 0x7f

OPCODE_CONTINUATION = 0x0
OPCODE_TEXT         = 0x1
OPCODE_BINARY       = 0x2
OPCODE_CLOSE_CONN   = 0x8
OPCODE_PING         = 0x9
OPCODE_PONG         = 0xA

CLOSE_STATUS_NORMAL = 1000
DEFAULT_CLOSE_REASON = bytes('', encoding='utf-8')


class API():

    def run_forever(self, threaded=False):
        return self._run_forever(threaded)

    def new_client(self, client, server):
        pass

    def client_left(self, client, server):
        pass

    def message_received(self, client, server, message):
        pass

    def set_fn_new_client(self, fn):
        self.new_client = fn

    def set_fn_client_left(self, fn):
        self.client_left = fn

    def set_fn_message_received(self, fn):
        self.message_received = fn

    def send_message(self, client, msg):
        self._unicast(client, msg)

    def send_message_to_all(self, msg):
        self._multicast(msg)

    def shutdown_gracefully(self, status=CLOSE_STATUS_NORMAL, reason=DEFAULT_CLOSE_REASON):
        self._shutdown_gracefully(status=CLOSE_STATUS_NORMAL, reason=DEFAULT_CLOSE_REASON)

    def shutdown_abruptly(self):
        self._shutdown_abruptly()


class WebsocketServer(ThreadingMixIn, TCPServer, API):
    """
	A websocket server waiting for clients to connect.

    Args:
        port(int): Port to bind to
        host(str): Hostname or IP to listen for connections. By default 127.0.0.1
            is being used. To accept connections from any client, you should use
            0.0.0.0.
        loglevel: Logging level from logging module to use for logging. By default
            warnings and errors are being logged.

    Properties:
        clients(list): A list of connected clients. A client is a dictionary
            like below.
                {
                 'id'      : id,
                 'handler' : handler,
                 'address' : (addr, port)
                }
    """

    allow_reuse_address = True
    daemon_threads = True  # comment to keep threads alive until finished

    def __init__(self, host='127.0.0.1', port=0, loglevel=logging.WARNING, key=None, cert=None):
        logger.setLevel(loglevel)
        TCPServer.__init__(self, (host, port), WebSocketHandler)
        self.host = host
        self.port = self.socket.getsockname()[1]

        self.key = key
        self.cert = cert

        self.clients = []
        self.id_counter = 0
        self.thread = None

    def _run_forever(self, threaded):
        cls_name = self.__class__.__name__
        try:
            logger.info("Listening on port %d for clients.." % self.port)
            if threaded:
                self.daemon = True
                self.thread = WebsocketServerThread(target=super().serve_forever, daemon=True, logger=logger)
                logger.info(f"Starting {cls_name} on thread {self.thread.getName()}.")
                self.thread.start()
            else:
                self.thread = threading.current_thread()
                logger.info(f"Starting {cls_name} on main thread.")
                super().serve_forever()
        except KeyboardInterrupt:
            self.server_close()
            logger.info("Server terminated.")
        except Exception as e:
            logger.error(str(e), exc_info=True)
            sys.exit(1)

    def _message_received_(self, handler, msg):
        self.message_received(self.handler_to_client(handler), self, msg)

    def _ping_received_(self, handler, msg):
        handler.send_pong(msg)

    def _pong_received_(self, handler, msg):
        pass

    def _new_client_(self, handler):
        self.id_counter += 1
        client = {
            'id': self.id_counter,
            'handler': handler,
            'address': handler.client_address
        }
        self.clients.append(client)
        self.new_client(client, self)

    def _client_left_(self, handler):
        client = self.handler_to_client(handler)
        self.client_left(client, self)
        if client in self.clients:
            self.clients.remove(client)

    def _unicast(self, receiver_client, msg):
        receiver_client['handler'].send_message(msg)

    def _multicast(self, msg):
        for client in self.clients:
            self._unicast(client, msg)

    def handler_to_client(self, handler):
        for client in self.clients:
            if client['handler'] == handler:
                return client

    def _terminate_client_handlers(self):
        """
        Ensures request handler for each client is terminated correctly
        """
        for client in self.clients:
            client["handler"].keep_alive = False
            client["handler"].finish()
            client["handler"].connection.close()

    def _shutdown_gracefully(self, status=CLOSE_STATUS_NORMAL, reason=DEFAULT_CLOSE_REASON):
        """
        Send a CLOSE handshake to all connected clients before terminating server
        """
        self.keep_alive = False

        # Send CLOSE to clients
        for client in self.clients:
            client["handler"].send_close(CLOSE_STATUS_NORMAL, reason)

        self._terminate_client_handlers()
        self.server_close()
        self.shutdown()

    def _shutdown_abruptly(self):
        """
        Terminate server without sending a CLOSE handshake
        """
        self.keep_alive = False
        self._terminate_client_handlers()
        self.server_close()
        self.shutdown()


class WebSocketHandler(StreamRequestHandler):

    def __init__(self, socket, addr, server):
        self.server = server
        if server.key and server.cert:
            try:
                socket = ssl.wrap_socket(socket, server_side=True, certfile=server.cert, keyfile=server.key)
            except: # Not sure which exception it throws if the key/cert isn't found
                logger.warn("SSL not available (are the paths {} and {} correct for the key and cert?)".format(server.key, server.cert))
        StreamRequestHandler.__init__(self, socket, addr, server)

    def setup(self):
        StreamRequestHandler.setup(self)
        self.keep_alive = True
        self.handshake_done = False
        self.valid_client = False

    def handle(self):
        while self.keep_alive:
            if not self.handshake_done:
                self.handshake()
            elif self.valid_client:
                self.read_next_message()

    def read_bytes(self, num):
        return self.rfile.read(num)

    def read_next_message(self):
        try:
            b1, b2 = self.read_bytes(2)
        except SocketError as e:  # to be replaced with ConnectionResetError for py3
            if e.errno == errno.ECONNRESET:
                logger.info("Client closed connection.")
                self.keep_alive = 0
                return
            b1, b2 = 0, 0
        except ValueError as e:
            b1, b2 = 0, 0

        fin    = b1 & FIN
        opcode = b1 & OPCODE
        masked = b2 & MASKED
        payload_length = b2 & PAYLOAD_LEN

        if opcode == OPCODE_CLOSE_CONN:
            logger.info("Client asked to close connection.")
            self.keep_alive = 0
            return
        if not masked:
            logger.warn("Client must always be masked.")
            self.keep_alive = 0
            return
        if opcode == OPCODE_CONTINUATION:
            logger.warn("Continuation frames are not supported.")
            return
        elif opcode == OPCODE_BINARY:
            logger.warn("Binary frames are not supported.")
            return
        elif opcode == OPCODE_TEXT:
            opcode_handler = self.server._message_received_
        elif opcode == OPCODE_PING:
            opcode_handler = self.server._ping_received_
        elif opcode == OPCODE_PONG:
            opcode_handler = self.server._pong_received_
        else:
            logger.warn("Unknown opcode %#x." % opcode)
            self.keep_alive = 0
            return

        if payload_length == 126:
            payload_length = struct.unpack(">H", self.rfile.read(2))[0]
        elif payload_length == 127:
            payload_length = struct.unpack(">Q", self.rfile.read(8))[0]

        masks = self.read_bytes(4)
        message_bytes = bytearray()
        for message_byte in self.read_bytes(payload_length):
            message_byte ^= masks[len(message_bytes) % 4]
            message_bytes.append(message_byte)
        opcode_handler(self, message_bytes.decode('utf8'))

    def send_message(self, message):
        self.send_text(message)

    def send_pong(self, message):
        self.send_text(message, OPCODE_PONG)

    def send_close(self, status=CLOSE_STATUS_NORMAL, reason=DEFAULT_CLOSE_REASON):
        """
        Send CLOSE to client

        Args:
            status: Status as defined in https://datatracker.ietf.org/doc/html/rfc6455#section-7.4.1
            reason: Text with reason of closing the connection
        """
        if status < CLOSE_STATUS_NORMAL or status > 1015:
            raise Exception(f"CLOSE status must be between 1000 and 1015, got {status}")

        header = bytearray()
        payload = struct.pack('!H', status) + reason
        payload_length = len(payload)
        assert payload_length <= 125, "We only support short closing reasons at the moment"

        # Send CLOSE with status & reason
        header.append(FIN | OPCODE_CLOSE_CONN)
        header.append(payload_length)
        self.request.send(header + payload)

    def send_text(self, message, opcode=OPCODE_TEXT):
        """
        Important: Fragmented(=continuation) messages are not supported since
        their usage cases are limited - when we don't know the payload length.
        """

        # Validate message
        if isinstance(message, bytes):
            message = try_decode_UTF8(message)  # this is slower but ensures we have UTF-8
            if not message:
                logger.warning("Can\'t send message, message is not valid UTF-8")
                return False
        elif not isinstance(message, str):
            logger.warning('Can\'t send message, message has to be a string or bytes. Got %s' % type(message))
            return False

        header  = bytearray()
        payload = encode_to_UTF8(message)
        payload_length = len(payload)

        # Normal payload
        if payload_length <= 125:
            header.append(FIN | opcode)
            header.append(payload_length)

        # Extended payload
        elif payload_length >= 126 and payload_length <= 65535:
            header.append(FIN | opcode)
            header.append(PAYLOAD_LEN_EXT16)
            header.extend(struct.pack(">H", payload_length))

        # Huge extended payload
        elif payload_length < 18446744073709551616:
            header.append(FIN | opcode)
            header.append(PAYLOAD_LEN_EXT64)
            header.extend(struct.pack(">Q", payload_length))

        else:
            raise Exception("Message is too big. Consider breaking it into chunks.")
            return

        self.request.send(header + payload)

    def read_http_headers(self):
        headers = {}
        # first line should be HTTP GET
        http_get = self.rfile.readline().decode().strip()
        assert http_get.upper().startswith('GET')
        # remaining should be headers
        while True:
            header = self.rfile.readline().decode().strip()
            if not header:
                break
            head, value = header.split(':', 1)
            headers[head.lower().strip()] = value.strip()
        return headers

    def handshake(self):
        headers = self.read_http_headers()

        try:
            assert headers['upgrade'].lower() == 'websocket'
        except AssertionError:
            self.keep_alive = False
            return

        try:
            key = headers['sec-websocket-key']
        except KeyError:
            logger.warning("Client tried to connect but was missing a key")
            self.keep_alive = False
            return

        response = self.make_handshake_response(key)
        self.handshake_done = self.request.send(response.encode())
        self.valid_client = True
        self.server._new_client_(self)

    @classmethod
    def make_handshake_response(cls, key):
        return \
          'HTTP/1.1 101 Switching Protocols\r\n'\
          'Upgrade: websocket\r\n'              \
          'Connection: Upgrade\r\n'             \
          'Sec-WebSocket-Accept: %s\r\n'        \
          '\r\n' % cls.calculate_response_key(key)

    @classmethod
    def calculate_response_key(cls, key):
        GUID = '258EAFA5-E914-47DA-95CA-C5AB0DC85B11'
        hash = sha1(key.encode() + GUID.encode())
        response_key = b64encode(hash.digest()).strip()
        return response_key.decode('ASCII')

    def finish(self):
        self.server._client_left_(self)


def encode_to_UTF8(data):
    try:
        return data.encode('UTF-8')
    except UnicodeEncodeError as e:
        logger.error("Could not encode data to UTF-8 -- %s" % e)
        return False
    except Exception as e:
        raise(e)
        return False


def try_decode_UTF8(data):
    try:
        return data.decode('utf-8')
    except UnicodeDecodeError:
        return False
    except Exception as e:
        raise(e)
