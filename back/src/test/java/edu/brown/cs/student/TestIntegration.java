package edu.brown.cs.student;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.CSV.CSVData;
import edu.brown.cs.student.main.CSV.Parser;
import edu.brown.cs.student.main.CSV.creators.ListCreator;
import edu.brown.cs.student.main.FactoryFailureException;
import edu.brown.cs.student.main.records.maps.Feature;
import edu.brown.cs.student.main.records.maps.FeatureCollection;
import edu.brown.cs.student.main.server.handlers.BroadbandHandler;
import edu.brown.cs.student.main.server.handlers.FilterJsonHandler;
import edu.brown.cs.student.main.server.handlers.GetJsonHandler;
import edu.brown.cs.student.main.server.handlers.JSONParser;
import edu.brown.cs.student.main.server.handlers.LoadHandler;
import edu.brown.cs.student.main.server.handlers.SearchHandler;
import edu.brown.cs.student.main.server.handlers.SearchJsonHandler;
import edu.brown.cs.student.main.server.handlers.ViewHandler;
import edu.brown.cs.student.main.server.serializers.FeatureCollectionResponse;
import edu.brown.cs.student.main.server.serializers.ServerSuccessResponse;
import edu.brown.cs.student.main.server.sources.ACSAPISource;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;
import spark.Spark;

public class TestIntegration {

  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(3235);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }
  @BeforeEach
  public void setup() throws IOException {
    Parser<List<String>> parser;
    CSVData data = null;
    try {
      parser = new Parser<>();
      data = new CSVData(parser);
    } catch (IOException e) {
      System.err.println("Encountered an error: file could not be found or read.");
      System.exit(0);
    } catch (FactoryFailureException e) {
      System.err.println(e.getMessage());
      System.exit(0);
    }

    Spark.get("loadcsv", new LoadHandler(data));
    Spark.get("viewcsv", new ViewHandler(data));
    Spark.get("searchcsv", new SearchHandler(data));
    Spark.get("broadband", new BroadbandHandler(new ACSAPISource()));

    JSONParser jsonParser = new JSONParser();
    Spark.get("getjson", new GetJsonHandler(jsonParser.getFeatureCollection()));
    Spark.get("searchjson", new SearchJsonHandler(jsonParser.getFeatureCollection()));
    Spark.get("filterjson", new FilterJsonHandler(jsonParser.getFeatureCollection()));
    Spark.init();
    Spark.awaitInitialization();
  }

  @AfterEach
  public void teardown() {
    Spark.unmap("loadcsv");
    Spark.unmap("viewcsv");
    Spark.unmap("searchcsv");
    Spark.unmap("getjson");
    Spark.unmap("filterjson");
    Spark.unmap("searchjson");
    Spark.unmap("broadband");
    Spark.awaitStop();
  }

  /**
   * Load (error), view, search, broadband (error), search, view, load (success),
   * search (error), search (success), view, broadband (error), broadband (success),
   * search w/ columns, search w/o columns, getjson, filterjson, searchjson, searchjson,
   * filterjson, getjson, view, search, load, view, search.
   * @throws IOException
   */
  @Test
  public void testIntegration() throws IOException, FactoryFailureException {
    Assert.assertEquals(deserialize("loadcsv").get("details"),"No filepath found.");
    Assert.assertEquals(deserialize("loadcsv?filepath=data/fabed").get("details"),"File "
        + "data/fabed could "
        + "not be found or read.");
    Assert.assertEquals(deserialize("viewcsv").get("details"),"Cannot retrieve CSV. CSV may not be loaded.");
    Assert.assertEquals(deserialize("searchcsv?keyword=abc").get("details"),
        "Whether the CSV file has headers must be specified.");
    Assert.assertEquals(deserialize("searchcsv?keyword=abc&hasHeaders=false").get("details"),
        "Cannot retrieve CSV. CSV may not be loaded.");
    Assert.assertEquals(deserialize("broadband").get("details"), "No state to search was requested.");
    Assert.assertEquals(deserialize("broadband?state=Illinois").get("details"), "No county to "
        + "search was requested.");
    Assert.assertEquals(deserialize("broadband?state=Illinois&county=Cook").get("data"),
        "Cook County, Illinois had 84.8% of houses with broadband.");
    Assert.assertEquals(deserialize("searchcsv?keyword=abc&hasHeaders=false").get("details"),
        "Cannot retrieve CSV. CSV may not be loaded.");
    Assert.assertEquals(deserialize("viewcsv").get("details"),"Cannot retrieve CSV. CSV may not be loaded.");

    String filePath = "data/RIIncome.csv";
    String apiCall = "loadcsv?filepath=" + filePath;
    tryRequest(apiCall);

    apiCall = "viewcsv";
    CSVData data = new CSVData(new Parser<>());
    data.setCSV(new FileReader(filePath), new ListCreator());
    ServerSuccessResponse response = new ServerSuccessResponse("none", data.getCSV());
    Assert.assertEquals(this.deserialize(apiCall).get("result"), response.response_type());
    Assert.assertEquals(this.deserialize(apiCall).get("input"), response.input());
    Assert.assertEquals(this.deserialize(apiCall).get("data"), response.data());

    Assert.assertEquals(deserialize("broadband?state=Illinois").get("details"), "No county to "
        + "search was requested.");
    Assert.assertEquals(deserialize("broadband?state=Illinois&county=Kane").get("data"),
        "Kane County, Illinois had 90.9% of houses with broadband.");

    Assert.assertEquals(deserialize("searchcsv?keyword=abc").get("details"),
        "Whether the CSV file has headers must be specified.");
    Assert.assertEquals(deserialize("searchcsv?keyword=abc&hasHeaders=true").get("data").toString(), "[]");
    Assert.assertEquals(deserialize("searchcsv?keyword=Woonsocket&hasHeaders=true&columnID=City"
            + "/Town").get("data").toString(), "[[Woonsocket, \"48,822.00\", \"58,896.00\", \"26,"
        + "561.00\"]]");

    Assert.assertEquals(this.deserialize(apiCall).get("result"), response.response_type());
    Assert.assertEquals(this.deserialize(apiCall).get("input"), response.input());
    Assert.assertEquals(this.deserialize(apiCall).get("data"), response.data());

    List<Feature> featureList = new ArrayList<>();
    FeatureCollection expectedFC = new FeatureCollection(featureList);
    Assert.assertEquals(deserializeFCResponse("filterjson?minX=-86.756777&maxX=-86.756777&minY=33.497543&maxY=33.497543")
        .data(), expectedFC);

    Assert.assertEquals(deserializeFCResponse("getjson").result(), "success");
    Assert.assertEquals(deserialize("searchjson").get("details"), "Keyword was not entered.");
    Assert.assertEquals(deserializeFCResponse("searchjson?keyword=Chicago%20Fair")
        .data().features().get(0).properties().city(), "Chicago");
    Assert.assertEquals(deserializeFCResponse("searchjson?keyword=Chicago")
        .input().get("avgX"), -87.67047363578276);
    Assert.assertEquals(deserializeFCResponse("searchjson?keyword=blahblahblah")
        .input().get("avgX"), 0.0);
    Assert.assertEquals(deserializeFCResponse("filterjson?minX=-200&maxX=-50&minY=0"
        + "&maxY=100")
        .data().features().size(), new JSONParser().getFeatureCollection().features().size() - 3);

    Assert.assertEquals(deserializeFCResponse("getjson").data(), new JSONParser().getFeatureCollection());

    Assert.assertEquals(this.deserialize(apiCall).get("result"), response.response_type());
    Assert.assertEquals(this.deserialize(apiCall).get("input"), response.input());
    Assert.assertEquals(this.deserialize(apiCall).get("data"), response.data());
    Assert.assertEquals(deserialize("searchcsv?keyword=Woonsocket&hasHeaders=true&columnID=City"
        + "/Town").get("data").toString(), "[[Woonsocket, \"48,822.00\", \"58,896.00\", \"26,"
        + "561.00\"]]");

    Assert.assertEquals(deserialize("loadcsv").get("details"),"No filepath found.");
    Assert.assertEquals(deserialize("loadcsv?filepath=data/fabed").get("details"),"File "
        + "data/fabed could "
        + "not be found or read.");
  }
  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    // The default method is "GET", which is what we're using here.
    // If we were using "POST", we'd need to say so.
    // clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }
  private Map<String, Object> deserialize(String apiCall) throws IOException {
    HttpURLConnection clientConnection = tryRequest(apiCall);
    Assert.assertEquals(clientConnection.getResponseCode(), 200);
    Moshi moshi = new Moshi.Builder().build();
    Type mapType = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapType);
    return adapter.fromJson(
        new Scanner(clientConnection.getInputStream()).useDelimiter("\\A").next());
  }
  private FeatureCollectionResponse deserializeFCResponse(String apiCall) throws IOException {
    HttpURLConnection clientConnection = tryRequest(apiCall);
    Assert.assertEquals(clientConnection.getResponseCode(), 200);
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<FeatureCollectionResponse> adapter =
        moshi.adapter(FeatureCollectionResponse.class);

    return adapter.fromJson(
        new Scanner(clientConnection.getInputStream()).useDelimiter("\\A").next());
  }
}
