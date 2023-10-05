package rw.util;

public class ServerErrorException extends RuntimeException {
    private final int statusCode;
    private final String serverMessage;

    public ServerErrorException(int statusCode, String serverMessage) {
        super("Server Error: " + statusCode + " " + serverMessage);
        this.statusCode = statusCode;
        this.serverMessage = serverMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getServerMessage() {
        return serverMessage;
    }
}