package rw.session;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import rw.audit.RwSentry;
import rw.handler.runConf.BaseRunConfHandler;
import rw.session.cmds.Cmd;
import rw.session.events.Action;
import rw.session.events.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

class RawEvent {
    public String ID;
}

class Client extends Thread {
    private static final Logger LOGGER = Logger.getInstance(Client.class);

    private Socket socket;

    private PrintWriter out;
    private BufferedReader in;
    ServerSocket server;

    Session session;

    Client(ServerSocket server, Session session) {
        this.session = session;
        this.server = server;
    }

    public boolean connect() {
        try {
            this.socket = this.server.accept();
            LOGGER.info("Client connected to the Session");
        } catch (IOException ignored) {
            return false;
        }
        return true;
    }

    public void run() {
        try {
            this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),true);
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), StandardCharsets.UTF_8));

            String inputLine;
            try {
                while ((inputLine = this.in.readLine()) != null) {
                    this.ingestLine(inputLine);
                }
            } catch (SocketException e) {
                if (!e.getMessage().equals("Connection reset")) {
                    RwSentry.get().captureException(e);
                }
            }
        } catch (IOException e) {
            RwSentry.get().captureException(e);
        }
    }

    private void ingestLine(String line) {
        try {
            Event event = this.session.eventFactory(line);
            if (event == null) {
                return;
            }
            event.handle();
        } catch (RuntimeException e) {
            RwSentry.get().captureException(e);
        }
    }

    void send(Cmd cmd) {
        Gson gson = new Gson();
        String payload = gson.toJson(cmd);
        LOGGER.info(String.format("Sending cmd %s", cmd.getId()));
        this.out.println(payload);
    }

    public void close() {
        try {
            if (this.in != null) {
                this.in.close();
            }
            if (this.out != null) {
                this.out.close();
            }
            if (this.socket != null) {
                this.socket.close();
            }
        } catch (Exception e) {
            RwSentry.get().captureException(e);
        }
    }
}

public class Session extends Thread {
    private static final Logger LOGGER = Logger.getInstance(Session.class);

    private final Project project;

    private ServerSocket serverSocket;
    private Integer port = null;
    private final BaseRunConfHandler handler;
    private Map<String, Class<? extends Event>> events;
    private List<Client> clients;

    public Session(Project project, BaseRunConfHandler handler) {
        this.project = project;
        this.handler = handler;
        this.clients = new ArrayList<>();

        this.events = Map.ofEntries(
                entry(Handshake.ID, Handshake.class),
                entry(ModuleUpdate.ID, ModuleUpdate.class),
                entry(FrameError.ID, FrameError.class),
                entry(LineProfile.ID, LineProfile.class),
                entry(StackUpdate.ID, StackUpdate.class),
                entry(UserError.ID, UserError.class),
                entry(ClearErrors.ID, ClearErrors.class),
                entry(LineProfileClear.ID, LineProfileClear.class),
                entry(WatchingFiles.ID, WatchingFiles.class),
                entry(FrameDropped.ID, FrameDropped.class)
        );

        try {
            this.serverSocket = new ServerSocket(0);
            this.port = this.serverSocket.getLocalPort();
        } catch (IOException ignored) {
        }
    }

    @VisibleForTesting
    @Nullable
    public Event eventFactory(String payload) {
        Gson g = new Gson();

        RawEvent event = g.fromJson(payload, RawEvent.class);
        Event ret;

        Class<? extends Event> eventClass = this.events.get(event.ID);
        if (eventClass == null) {
            LOGGER.warn("Unknown event " + event.ID);
            return null;
        }

        ret = g.fromJson(payload, eventClass);
        ret.setHandler(this.handler);

        return ret;
    }

    public void run() {
        if (this.serverSocket == null) {
            return;
        }

        while (true) {
            Client client = new Client(this.serverSocket, this);
            if (!client.connect()) {
                return;
            }
            client.start();
            this.clients.add(client);
        }
    }

    public void send(Cmd cmd) {
        for (Client c : this.clients) {
            c.send(cmd);
        }
    }

    public BaseRunConfHandler getHandler() {
        return handler;
    }

    public int getPort() {
        return this.port;
    }

    public void close() {
        try {
            if (this.serverSocket != null) {
                this.serverSocket.close();
            }
        } catch (Exception e) {
            RwSentry.get().captureException(e);
        }

        for (Client c : this.clients) {
            c.close();
        }
    }
}
