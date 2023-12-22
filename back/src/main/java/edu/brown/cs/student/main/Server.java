package edu.brown.cs.student.main;

import static spark.Spark.after;

import edu.brown.cs.student.main.plme.sources.ChatPDFSource;
import edu.brown.cs.student.main.plme.MetadataHandler;
import spark.Spark;
import static spark.Spark.options;
/**
 * Top-level class for this demo. Contains the main() method which starts Spark and runs the various
 * handlers.
 *
 * <p>We have two endpoints in this demo. They need to share state (a menu). This is a great chance
 * to use dependency injection, as we do here with the menu set. If we needed more endpoints, more
 * functionality classes, etc. we could make sure they all had the same shared state.
 */
public class Server {
  public Server() {
    // Removes warning messages from pdfbox during pdf reading.
    System.setProperty("org.apache.commons.logging.Log",
        "org.apache.commons.logging.impl.NoOpLog");

    int port = 4000;
    Spark.port(port);

// Handle HTTP OPTIONS requests for any path
      options("/*", (request, response) -> {
          // Retrieve the Access-Control-Request-Headers header from the request
          String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
          // If it's not null, set the corresponding response header
          if (accessControlRequestHeaders != null) {
              response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
          }

          // Retrieve the Access-Control-Request-Method header from the request
          String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
          // If it's not null, set the corresponding response header
          if (accessControlRequestMethod != null) {
              response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
          }

          // Return "OK" as the response
          return "OK";
      });

// After processing each HTTP request, set CORS response headers
      after((request, response) -> {
          response.header("Access-Control-Allow-Origin", "*"); // Allow requests from any origin
          response.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS"); // Specify allowed HTTP methods
          response.header("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,"); // Specify allowed headers
      });
      Spark.post("plme", new MetadataHandler(new ChatPDFSource()));

    Spark.init();
    Spark.awaitInitialization();

    // Notice this link alone leads to a 404... Why is that?
    System.out.println("Server started at http://localhost:" + port);
  }
  public static void main(String[] args) {
    Server server = new Server();
  }
}
