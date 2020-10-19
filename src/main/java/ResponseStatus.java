public enum ResponseStatus {
    OK("HTTP/1.1 200 OK"),
    NOT_FOUND("HTTP/1.1 404 Not Found"),
    INTERNAL_SERVER_ERROR("HTTP/1.1 500 Internal Server Error");

    private final String statusLine;

    ResponseStatus(String statusLine) {
        this.statusLine = statusLine;
    }

    public String getStatusLine() {
        return this.statusLine;
    }
}
