import java.util.Map;

public class Response {
    private Map<String, String> headers;
    private String statusLine;
    private String content;

    public Response(Map<String, String> headers, String statusLine, String content) {
        this.headers = headers;
        this.statusLine = statusLine;
        this.content = content;
    }

    public String getStatusLine() {
        return this.statusLine;
    }

    public String getContent() {
        return content;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
