public class RequestParser {
    public Request parse(String request) {
        String[] requestParts = request.split(" ");
        String method = requestParts[0];
        String uri = requestParts[1];
        return new Request(method, uri);
    }
}
