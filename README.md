# switch-over-proxy

## Description
Proxy HTTP and websocket traffic in Java

Proxy service for proxying HTTP and websocket traffic. The proxy routes can be
added and removed on the fly through the Java API. When an existing proxy route is changed, the remaining
connections stay connected to the previous target, but all new connections will be 
routed to the new target.

Implemented on top of Jetty's ProxyServlet and Jetty's WebSocket API. The switchover
functionality is based on servlet mappings. A new servlet is created for each route and 
requests to the route's path are mapped to that servlet. On switchover, a new servlet is
created, and the mapping is updated to relay new connections to the new servlet. The old 
servlet continues to relay existing connections and is removed when all of them are closed.

## Limitations
- HTTP errors on websocket upgrade request won't cause the original upgrade request to 
fail with a HTTP error, but the error is converted to a websocket error 
- the same proxy path can't relay both HTTP and websocket traffic
- HTTPS hasn't been tested

## Status
This code was written for bioinformatics software Chipster but isn't used in its current
architecture and is not actively developed or maintained. However, it did work fine in our 
development setups and was written to be used in production eventually. If you 
need an pure Java reverse proxy for HTTP and WebSocket traffic, this project should give you a good head start.
