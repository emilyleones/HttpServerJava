import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class Response {
    private final Map<String, String> headers;
    private final String statusLine;
    private final String content;

    public Response(Map<String, String> headers, String statusLine, String content) {
        this.headers = headers;
        this.statusLine = statusLine;
        this.content = content;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public String getStatusLine() {
        return this.statusLine;
    }

    public String getContent() {
        return this.content;
    }

    public void sendToStream(OutputStream outputStream) throws IOException {
        outputStream.write(this.statusLine.getBytes());
        outputStream.write("\n".getBytes());
        for (Map.Entry header : this.headers.entrySet()) {
            outputStream.write((header.getKey() + ": " + header.getValue() + "\n").getBytes());
        }
        outputStream.write("\n".getBytes());
        outputStream.write(this.content.getBytes());
        outputStream.flush();
    }
}
