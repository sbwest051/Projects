package edu.brown.cs.student.main;

import static spark.Spark.after;

import spark.Spark;

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
    int port = 4002;

    Spark.port(port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    Spark.post("plme", new MetadataHandler(new ChatPDFSource()));

    // Setting up the handler for the GET /order and /mock endpoints

    /*Parser<List<String>> parser;
    CSVData data = null;
    try {
      parser = new Parser<>();
      data = new CSVData(parser);
    } catch (IOException e) {
      System.err.println("Encountered an error: file could not be found or read.");
      System.exit(0);
    } catch (FactoryFailureException e) {
      System.err.println("Error converting " + e.row + " to object.");
      System.err.println(e.getMessage());
      System.exit(0);
    }

    Spark.get("loadcsv", new LoadHandler(data));
    Spark.get("viewcsv", new ViewHandler(data));
    Spark.get("searchcsv", new SearchHandler(data));
    Spark.get("broadband", new BroadbandHandler(state));

    try {
      JSONParser jsonParser = new JSONParser();
      Spark.get("getjson", new GetJsonHandler(jsonParser.getFeatureCollection()));
      Spark.get("searchjson", new SearchJsonHandler(jsonParser.getFeatureCollection()));
      Spark.get("filterjson", new FilterJsonHandler(jsonParser.getFeatureCollection()));
    } catch (IOException e){
      System.err.println(e.getMessage());
      System.exit(0);
    }*/

    Spark.init();
    Spark.awaitInitialization();

    // Notice this link alone leads to a 404... Why is that?
    System.out.println("Server started at http://localhost:" + port);
  }

  public static void main(String[] args) {
    Server server = new Server();
  }
}
