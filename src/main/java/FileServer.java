import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class FileServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private OutputStream out;
    private BufferedReader in;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server has started.");

        while (true) {
            clientSocket = serverSocket.accept();
            out = clientSocket.getOutputStream();
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String request = in.readLine();
            String fileName = getFileName(request);
            String baseDirectory = System.getProperty("java.io.tmpdir") + File.separator + "TestWebRoot";
            File file = new File(baseDirectory + File.separator + fileName);
            FileInputStream inputStream = new FileInputStream(file);
            byte[] contentBytes = inputStream.readAllBytes();
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
        return filePath.split("/")[1];
    }

    public void stop() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }

    public static void main(String[] args) {
        FileServer server = new FileServer();
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
