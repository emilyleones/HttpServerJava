package httpserverjava.server;

import httpserverjava.http.Request;
import httpserverjava.http.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SocketHandler implements Runnable {
    private final Socket clientSocket;
    private final RequestParser requestParser;
    private final RequestHandler requestHandler;

    public SocketHandler(Socket clientSocket, RequestParser requestParser, RequestHandler requestHandler) {
        this.clientSocket = clientSocket;
        this.requestParser = requestParser;
        this.requestHandler = requestHandler;
    }

    @Override
    public void run() {
        try {
            clientSocket.setKeepAlive(true);
            BufferedReader socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            Optional<Request> request;
            while ((request = awaitRequest(socketReader)).isPresent()) {
                clientSocket.setKeepAlive(request.get().getKeepAlive());
                Response response = requestHandler.handle(request.get());
                OutputStream out = clientSocket.getOutputStream();
                response.sendTo(out);

                if (!clientSocket.getKeepAlive()) {
                    break;
                }
            }
            // Received EOF from client or Connection: Closed
            System.out.println("Client disconnected.");
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Optional<Request> awaitRequest(BufferedReader socketReader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = socketReader.readLine()) != null) {
            if (line.isBlank()) {
                System.out.println("New request from client: " + lines.get(0));
                return Optional.of(requestParser.parse(lines));
            }
            lines.add(line);
        }
        System.out.println("No more requests from client.");
        return Optional.empty();
    }
}
