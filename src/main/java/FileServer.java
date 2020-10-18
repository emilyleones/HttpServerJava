import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class FileServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private OutputStream out;
    private BufferedReader in;
    private FileService fileService;

    public FileServer(FileService fileService) {
        this.fileService = fileService;
    }

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server has started.");

        while (true) {
            clientSocket = serverSocket.accept();
            out = clientSocket.getOutputStream();
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String rawRequest = in.readLine();
            System.out.println(rawRequest);
            RequestParser requestParser = new RequestParser();
            Request request = requestParser.parse(rawRequest);
            String contentBytes;
            if (fileService.getResourceType(request.getUri()).equals(ResourceTypeResult.DIRECTORY)) {
                StringBuilder directoryListing = new StringBuilder();
                fileService.getDirectoryListing(request.getUri())
                        .stream().sorted()
                        .forEach(element -> directoryListing.append(String.format("<li>%s</li>", element)));
                contentBytes = String.format("<html><body><ul>%s</ul></body></html>", directoryListing);
            } else {
                contentBytes = new String(fileService.readFile(request.getUri()));
            }
            out.write("HTTP/1.1 200 OK\n".getBytes());
            out.write("Connection: keep-alive\n".getBytes());
            out.write("Content-Type: text/html; charset=UTF-8\n".getBytes());
            out.write(("Content-Length: " + contentBytes.getBytes().length + "\n").getBytes());
            out.write("Keep-Alive: timeout=5, max=1000\n".getBytes());
            out.write("\n".getBytes());
            out.write(contentBytes.getBytes());
            out.flush();
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
        FileServer server = new FileServer(fileService);
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
