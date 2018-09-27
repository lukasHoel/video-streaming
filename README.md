# video-streaming
Example of Video Streaming via HTTP 206 Partial Content to a Video Player

- Server: 
  1. Spring Boot
  2. HTTP 206 implementation from: https://github.com/davinkevin/Podcast-Server/blob/d927d9b8cb9ea1268af74316cd20b7192ca92da7/src/main/java/lan/dk/podcastserver/utils/multipart/MultipartFileSender.java

- Client: 
  1. VLCJ (https://github.com/caprica/vlcj)
  2. Every other video-client (e.g. Google Chrome, ...) is possible.

- Usage: 
  1. Make sure to edit the Video Path in `de.hoel.video.server.VideoController#getTestVideo` to find an actual example video source.
  2. Use or do not use the `RateLimiter` feature from `Guava` to simulate a slower network connection. Do that by (un)commenting the lines in `de.hoel.video.server.MultipartFileSender` that use the `RateLimiter`.
  3. Run `de.hoel.video.server.VideoServerApplication` as Java Program
  4. Open `http://localhost:8080/videos/test` in a Browser or by starting `de.hoel.video.client.VideoClient`
