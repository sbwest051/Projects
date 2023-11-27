package edu.brown.cs.student;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Feature;
import edu.brown.cs.student.main.FeatureCollection;
import edu.brown.cs.student.main.geometry;
import edu.brown.cs.student.main.properties;
import edu.brown.cs.student.main.server.handlers.GetJsonHandler;
import edu.brown.cs.student.main.server.handlers.JSONParser;
import edu.brown.cs.student.main.server.handlers.SearchJsonHandler;
import edu.brown.cs.student.main.server.serializers.FeatureCollectionResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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

public class TestSearchJson {

  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(3235);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }


  @BeforeEach
  public void setup() throws IOException {
    JSONParser jsonParser = new JSONParser();
    jsonParser.fromJSON("data/test/TestFeatureCollection.json");
    Spark.get("searchjson", new SearchJsonHandler(jsonParser.getFeatureCollection()));
    Spark.init();
    Spark.awaitInitialization();
  }
  @AfterEach
  public void teardown() {
    Spark.unmap("getjson");
    Spark.awaitStop();
  }
  @Test
  public void testSuccess() throws IOException {
    //Using mocked json
    List<List<List<List<Double>>>> coordinates = new ArrayList<>();
    List<Double> c1 = new ArrayList<>();
    c1.add(-86.756777);
    c1.add(33.497543);
    List<Double> c2 = new ArrayList<>();
    c2.add(-86.756777);
    c2.add(33.497543);
    List<List<Double>> cl = new ArrayList<>();
    cl.add(c1);
    cl.add(c2);
    List<List<List<Double>>> cll = new ArrayList<>();
    cll.add(cl);
    coordinates.add(cll);
    geometry geometry = new geometry(coordinates);

    Map<String,String> area_description_data = new HashMap<>();
    area_description_data.put("5", "Both sales and rental prices in 1929 were off about 20% from "
        + "1925-28 peak. Location of property within this area will justify policy of holding for its value.");
    area_description_data.put("6", "Mountain Brook Estates and County Club Gardens (outside city "
        + "limits) Ample 1");
    properties properties = new properties("AL", "Birmingham", "Mountain Brook Estates and "
        + "Country Club Gardens (outside city limits)","A1","A",244,area_description_data);
    Feature feature = new Feature(geometry,properties);
    List<Feature> featureList = new ArrayList<>();
    featureList.add(feature);
    FeatureCollection expectedFC = new FeatureCollection(featureList);
    Assert.assertEquals(deserializeFCResponse("searchjson?keyword=outside").data(), expectedFC);
    Assert.assertEquals(deserializeFCResponse("searchjson?keyword=outside").input().get("avgX"),
        -86.756777);
    Assert.assertEquals(deserializeFCResponse("searchjson?keyword=outside").input().get("avgY"),
        33.497543);
    List<String> queryList = new ArrayList<>();
    queryList.add("outside");
    queryList.add("outside");
    queryList.add("outside");
    queryList.add("outside");
    Assert.assertEquals(deserializeFCResponse("searchjson?keyword=outside").input().get(
        "input_history"), queryList);
    Assert.assertTrue(deserializeFCResponse("searchjson?keyword=abcdefgh").data().features().isEmpty());
    Assert.assertEquals(deserializeFCResponse("searchjson?keyword=abcdefgh").input().get("avgX"),
        0.0);
    Assert.assertEquals(deserializeFCResponse("searchjson?keyword=abcdefgh").input().get("avgY"),
        0.0);
    queryList.add("abcdefgh");
    queryList.add("abcdefgh");
    queryList.add("abcdefgh");
    queryList.add("abcdefgh");
    Assert.assertEquals(deserializeFCResponse("searchjson?keyword=abcdefgh").input().get(
        "input_history"),
        queryList);
  }

  @Test
  public void testErrorResponses() throws IOException {
    Assert.assertEquals(deserialize("searchjson").get("error_type"),"error_bad_request");
    Assert.assertEquals(deserialize("searchjson").get("details"),"Keyword was not entered.");
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
