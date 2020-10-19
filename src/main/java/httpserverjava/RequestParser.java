package httpserverjava;

import httpserverjava.http.Request;

import java.util.List;

public class RequestParser {
    public Request parse(List<String> request) {
        String[] requestParts = request.get(0).split(" ");
        String method = requestParts[0];
        String uri = requestParts[1];

        boolean keepAlive = request.stream()
                .filter(headerLine -> headerLine.startsWith("Connection:"))
                .findFirst()
                .map(connectionHeaderLine -> connectionHeaderLine.split(" ")[1])
                .map(String::toUpperCase)
                .filter(connectionHeaderValue -> connectionHeaderValue.equals("CLOSE")).isEmpty();

        return new Request(method, uri, keepAlive);
    }
}
