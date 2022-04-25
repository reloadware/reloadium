package rw.session;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import rw.audit.RwSentry;
import rw.handler.runConf.PythonRunConfHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;


public class Session extends Thread {
    private static final Logger LOGGER = Logger.getInstance(Session.class);

    private final Project project;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Integer port = null;
    private PythonRunConfHandler runConfHandler;

    public Session(Project project, PythonRunConfHandler runConfHandler) {
        this.project = project;
        this.runConfHandler = runConfHandler;

        try {
            this.serverSocket = new ServerSocket(0);
            this.port = this.serverSocket.getLocalPort();
        } catch (IOException e) {
            RwSentry.get().captureException(e);
        }
    }

    public void run() {
        try {
            this.clientSocket = this.serverSocket.accept();
            this.out = new PrintWriter(this.clientSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            String inputLine;
            try {
                while ((inputLine = this.in.readLine()) != null) {
                    this.ingestLine(inputLine);
                }
            } catch (IOException e) {
                RwSentry.get().captureException(e);
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
    public Event eventFactory(String line) {
        String[] parts = line.split("\t");
        String eventName = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        switch (eventName) {
            case Handshake.ID: {
                return new Handshake(this.project, this.runConfHandler, args);
            }

            case UpdateModule.ID: {
                return new UpdateModule(this.project, this.runConfHandler, args);
            }

            case FrameError.ID: {
                return new FrameError(this.project, this.runConfHandler, args);
            }

            case UserError.ID: {
                return new UserError(this.project, this.runConfHandler, args);
            }

            case UpdateFrame.ID: {
                return new UpdateFrame(this.project, this.runConfHandler, args);
            }

            default: {
                LOGGER.warn("Unknown event " + eventName);
            }
        }
        return null;
    }

    public void close() {
        try {
            if (this.in != null){
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
