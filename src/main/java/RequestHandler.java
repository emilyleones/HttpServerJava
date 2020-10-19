import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class RequestHandler {
    private final FileService fileService;

    public RequestHandler(FileService fileService) {
        this.fileService = fileService;
    }

    public Response handle(Request request) {
        try {
            String statusLine = getStatusLine(request.getUri());
            String content = resolveResponseBody(request.getUri());

            Map<String, String> headers = getResponseHeaders(content, request.getKeepAlive());
            return request.getMethod().equals("GET") ?
                    new Response(headers, statusLine, content)
                    : new Response(headers, statusLine, "");
        } catch (Throwable e) {
            return new Response(Collections.emptyMap(), "HTTP/1.1 500 Internal Server Error", "An error occurred.");
        }
    }

    private Map<String, String> getResponseHeaders(String content, boolean keepAlive) {
        String connectionHeaderValue = keepAlive ? "Keep-Alive" : "Close";
        return Map.of("Connection", connectionHeaderValue,
                "Content-Type", "text/html; charset=UTF-8",
                "Content-Length", String.valueOf(content.getBytes().length));
    }

    private String getStatusLine(String resourcePath) {
        return (fileService.resolveResourceType(resourcePath).equals(ResourceTypeResult.NOT_FOUND)) ?
                "HTTP/1.1 404 Not Found" : "HTTP/1.1 200 OK";
    }

    private String resolveResponseBody(String resourcePath) throws IOException {
        switch (fileService.resolveResourceType(resourcePath)) {
            case DIRECTORY:
                StringBuilder directoryListing = new StringBuilder();
                fileService.readDirectoryListing(resourcePath)
                        .stream().sorted()
                        .forEach(element -> directoryListing.append(String.format("<li>%s</li>", element)));
                return String.format("<html><head><title>My Http File Server</title></head><body><ul>%s</ul></body></html>", directoryListing);
            case FILE:
                return fileService.readFile(resourcePath);
            case NOT_FOUND:
                return "Resource Not Found";
            default:
                throw new IllegalStateException("No implementation for this enum value.");
        }
    }
}
