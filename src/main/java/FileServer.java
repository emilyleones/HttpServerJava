import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;

public class FileServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private OutputStream out;
    private BufferedReader in;
    private String rootDirectory;

    public FileServer(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server has started.");

        while (true) {
            clientSocket = serverSocket.accept();
            out = clientSocket.getOutputStream();
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String request = in.readLine();
            System.out.println(request);
            String fileName = getFileName(request);
            File file = new File(rootDirectory + File.separator + fileName);
            byte[] contentBytes;
            if (file.isDirectory()) {
                StringBuilder directoryListing = new StringBuilder();
                String[] files = file.list();
                Arrays.sort(files);
                Arrays.stream(files).forEach(element -> directoryListing.append(String.format("<li>%s</li>", element)));
                String htmlString = String.format("<html><body><ul>%s</ul></body></html>", directoryListing);
                contentBytes = htmlString.getBytes();
            } else {
                FileInputStream inputStream = new FileInputStream(file);
                contentBytes = inputStream.readAllBytes();
            }
            out.write("HTTP/1.1 200 OK\n".getBytes());
            out.write("Connection: keep-alive\n".getBytes());
            out.write("Content-Type: text/html; charset=UTF-8\n".getBytes());
            out.write(("Content-Length: " + contentBytes.length + "\n").getBytes());
            out.write("Keep-Alive: timeout=5, max=1000\n".getBytes());
            out.write("\n".getBytes());
            out.write(contentBytes);
            out.flush();
        }
    }

    private String getFileName(String request) {
        String[] requestParts = request.split(" ");
        String filePath = requestParts[1];
        return filePath.split("/").length > 0 ? filePath.split("/")[1] : "";
    }

    public void stop() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }

    public static void main(String[] args) {
        FileServer server = new FileServer(args[0]);
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
