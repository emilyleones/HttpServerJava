public class Request {
    private final String method;
    private final String uri;
    private boolean keepAlive;

    public Request(String method, String uri, boolean keepAlive) {
        this.method = method;
        this.uri = uri;
        this.keepAlive = keepAlive;
    }

    public String getMethod() {
        return this.method;
    }

    public String getUri() {
        return this.uri;
    }

    public boolean getKeepAlive() {
        return this.keepAlive;
    }
}
