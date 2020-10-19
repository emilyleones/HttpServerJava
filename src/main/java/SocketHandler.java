import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collections;

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
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            Request request = requestParser.parse(in.readLine());
            System.out.println(request.getMethod() + request.getUri());
            Response response;
            try {
                response = requestHandler.handle(request);
            } catch (Throwable e) {
                response = new Response(Collections.emptyMap(), "HTTP/1.1 500 Internal Server Error", "An error occurred.");
            }
            OutputStream out = clientSocket.getOutputStream();
            response.sendTo(out);
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
