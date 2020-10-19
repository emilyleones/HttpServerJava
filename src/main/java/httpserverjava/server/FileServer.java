package httpserverjava.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private final RequestHandler requestHandler;
    private final RequestParser requestParser;

    public FileServer(RequestParser requestParser, RequestHandler requestHandler) {
        this.requestParser = requestParser;
        this.requestHandler = requestHandler;
    }

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server has started.");

        while (true) {
            clientSocket = serverSocket.accept();
            SocketHandler socketHandler = new SocketHandler(clientSocket, requestParser, requestHandler);
            Thread thread = new Thread(socketHandler);
            thread.start();
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }
}
