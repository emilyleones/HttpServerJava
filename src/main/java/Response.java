import java.io.IOException;
import java.io.OutputStream;
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
