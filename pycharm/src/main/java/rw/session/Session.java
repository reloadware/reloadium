package rw.session;

import com.google.gson.Gson;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import rw.audit.RwSentry;
import rw.handler.runConf.BaseRunConfHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

import static java.util.Map.entry;

class RawEvent {
    public String ID;
    public String VERSION;
}

public class Session extends Thread {
    private static final Logger LOGGER = Logger.getInstance(Session.class);

    private final Project project;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Integer port = null;
    private final BaseRunConfHandler handler;
    private Map<String, Class<? extends Event>> events;
    private Map<String, String> eventVersions;

    public Session(Project project, BaseRunConfHandler handler) {
        this.project = project;
        this.handler = handler;

        this.events = Map.ofEntries(
                entry(Handshake.ID, Handshake.class),
                entry(ModuleUpdate.ID, ModuleUpdate.class),
                entry(FrameError.ID, FrameError.class),
                entry(LineProfile.ID, LineProfile.class),
                entry(StackUpdate.ID, StackUpdate.class),
                entry(UserError.ID, UserError.class),
                entry(ClearErrors.ID, ClearErrors.class),
                entry(LineProfileClear.ID, LineProfileClear.class)
        );

        this.eventVersions = Map.ofEntries(
                entry(Handshake.ID, Handshake.VERSION),
                entry(ModuleUpdate.ID, ModuleUpdate.VERSION),
                entry(FrameError.ID, FrameError.VERSION),
                entry(LineProfile.ID, LineProfile.VERSION),
                entry(UserError.ID, UserError.VERSION),
                entry(StackUpdate.ID, StackUpdate.VERSION),
                entry(ClearErrors.ID, ClearErrors.VERSION),
                entry(LineProfileClear.ID, LineProfileClear.VERSION)
        );

        try {
            this.serverSocket = new ServerSocket(0);
            this.port = this.serverSocket.getLocalPort();
        } catch (IOException ignored) {
        }
    }

    public void run() {
        if (this.serverSocket == null) {
            return;
        }

        try {
            this.clientSocket = this.serverSocket.accept();
            this.out = new PrintWriter(this.clientSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
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

    public int getPort() {
        return this.port;
    }

    private void ingestLine(String line) {
        try {
            Event event = this.eventFactory(line);
            if (event == null) {
                return;
            }
            event.handle();
        } catch (RuntimeException e) {
            RwSentry.get().captureException(e);
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
        String expectedVersion = this.eventVersions.get(event.ID);

        if (!expectedVersion.equals(event.VERSION)) {
            LOGGER.warn(String.format("Incompatible event versions for event type \"%s\" (expected=%s, got=%s)",
                    event.ID, expectedVersion, event.VERSION));
            return null;
        }
        ret = g.fromJson(payload, eventClass);
        ret.setHandler(this.handler);

        return ret;
    }

    public void close() {
        try {
            if (this.in != null) {
                this.in.close();
            }
            if (this.out != null) {
                this.out.close();
            }
            if (this.clientSocket != null) {
                this.clientSocket.close();
            }
            if (this.clientSocket != null) {
                this.serverSocket.close();
            }
        } catch (Exception e) {
            RwSentry.get().captureException(e);
        }
    }
}
