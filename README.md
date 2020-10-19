# My Http File Server

Basic HTTP/1.1 File Server which handles HEAD and GET requests and basic Keep-Alive behavior based on client Connection header.
## How to Run

1. Run `make build-image` to build Docker image.

2. Run `make run` to serve current working directory OR
 run `make run ROOT_DIR=<User specified root directory>`.

3. Make HEAD or GET requests to `localhost:8080`.

## Architecture and Design

![alt text](HttpFileServer.png "Title")

## Assumptions
* Clients make only valid HTTP/1.1 requests
* Although the server is multi-threaded, assumption was that it won't be overloaded with excessive requests
* The only relevant request header is `Connection: (Keep-Alive/Close)`

## Simplifications
These are the short-cuts/simplifications in order to meet time-constraints:
* Hard-coded Response Headers (For example, Content-Type is always `text/html; charset=UTF-8`)
* The only relevant request header is `Connection: (Keep-Alive/Close)`; the rest are ignored
* No proper logging
* Minimal exception, error, and edge-case handling (Assumption that clients make valid requests)
* Minimal thread management (e.g. no thread pool management or maximum thread count configuration)
* Basic Keep-Alive Behavior based on Connection header
