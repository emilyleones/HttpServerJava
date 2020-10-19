import java.util.Arrays;

public class Main {
    private static final int PORT = 8080;
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
