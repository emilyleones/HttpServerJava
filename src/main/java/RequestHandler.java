import java.io.IOException;
import java.io.Serializable;
import java.net.ResponseCache;
import java.util.Map;

public class RequestHandler {

    private FileService fileService;

    public RequestHandler(FileService fileService) {
        this.fileService = fileService;
    }

    public Response handle(Request request) throws IOException {
        String content = getResponseContent(request);
        Map<String, String> headers = Map.of("Connection", "keep-alive",
                "Content-Type", "text/html; charset=UTF-8",
                "Content-Length", String.valueOf(content.getBytes().length),
                "Keep-Alive", "timeout=5, max=1000");
        return new Response(headers, "HTTP/1.1 200 OK", content);
    }

    private String getResponseContent(Request request) throws IOException {
        if (fileService.getResourceType(request.getUri()).equals(ResourceTypeResult.DIRECTORY)) {
            StringBuilder directoryListing = new StringBuilder();
            fileService.getDirectoryListing(request.getUri())
                    .stream().sorted()
                    .forEach(element -> directoryListing.append(String.format("<li>%s</li>", element)));
            return String.format("<html><body><ul>%s</ul></body></html>", directoryListing);
        } else {
            return fileService.readFile(request.getUri());
        }
    }
}
