import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class FileServer {
    private static final int PORT = 8080;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private OutputStream out;
    private BufferedReader in;
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
            out = clientSocket.getOutputStream();
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            Request request = requestParser.parse(in.readLine());
            Response response = requestHandler.handle(request);
            response.sendToStream(out);
        }
    }

    public void stop() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }

    public static void main(String[] args) {
        RequestParser requestParser = new RequestParser();
        FileService fileService = new FileService(args[0]);
        RequestHandler requestHandler = new RequestHandler(fileService);
        FileServer server = new FileServer(requestParser, requestHandler);
        try {
            server.start(PORT);
            server.stop();
        } catch (Exception exception) {
            System.out.println("Encountered exception: " + exception);
            Arrays.stream(exception.getStackTrace()).forEach(line ->
                    System.out.println(line.toString()));
        }
    }
}
