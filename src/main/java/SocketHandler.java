import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SocketHandler implements Runnable{
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
            Optional<Request> request = awaitRequest(socketReader);
            if (request.isPresent()) {
                Response response;
                try {
                    response = requestHandler.handle(request.get());
                } catch (Throwable e) {
                    response = new Response(Collections.emptyMap(), "HTTP/1.1 500 Internal Server Error", "An error occurred.");
                }
                OutputStream out = clientSocket.getOutputStream();
                response.sendTo(out);

                if (!clientSocket.getKeepAlive()) {
                    clientSocket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Optional<Request> awaitRequest(BufferedReader sockerReader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = sockerReader.readLine()) != null) {
            if (line.isBlank()) {
                return Optional.of(requestParser.parse(lines));
            }
            lines.add(line);
        }
        return Optional.empty();
    }
}
