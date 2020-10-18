import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class GreetServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private OutputStream out;
    private BufferedReader in;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);

        while(true) {
            clientSocket = serverSocket.accept();
            out = clientSocket.getOutputStream();
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String request = in.readLine();
            String[] requestParts = request.split(" ");
            String filePath = requestParts[1];
            String fileName = filePath.split("/")[1];
            InputStream inputStream = GreetServer.class.getClassLoader().getResourceAsStream(fileName);
            byte[] contentBytes =  inputStream.readAllBytes();
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

    public void stop() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }

    public static void main(String[] args) {
        GreetServer server = new GreetServer();
        try {
            server.start(8080);
            server.stop();
        } catch (Exception exception) {
            System.out.println("Some exception: " + exception);
        }
    }
}
