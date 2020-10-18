import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Map;

public class FileServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private OutputStream out;
    private BufferedReader in;
    private RequestHandler requestHandler;

    public FileServer(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server has started.");
        RequestParser requestParser = new RequestParser();

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
        FileService fileService = new FileService(args[0]);
        RequestHandler requestHandler = new RequestHandler(fileService);
        FileServer server = new FileServer(requestHandler);
        try {
            server.start(8080);
            server.stop();
        } catch (Exception exception) {
            System.out.println("Encountered exception: " + exception);
            Arrays.stream(exception.getStackTrace()).forEach(line ->
                    System.out.println(line.toString()));
        }
    }
}
