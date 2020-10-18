import java.io.IOException;
import java.net.ResponseCache;

public class RequestHandler {

    private FileService fileService;

    public RequestHandler(FileService fileService) {
        this.fileService = fileService;
    }

    public Response handle(Request request) throws IOException {
        StringBuilder directoryListing = new StringBuilder();
        fileService.getDirectoryListing(request.getUri())
                .stream().sorted()
                .forEach(element -> directoryListing.append(String.format("<li>%s</li>", element)));
        String contentBytes = String.format("<html><body><ul>%s</ul></body></html>", directoryListing);
        return new Response(null, null, contentBytes);
    }
}
