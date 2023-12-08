package edu.brown.cs.student.server;

import static spark.Spark.after;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.serializers.ServerFailureResponse;
import edu.brown.cs.student.main.server.serializers.ServerSuccessResponse;
import edu.brown.cs.student.main.FactoryFailureException;
import edu.brown.cs.student.main.server.handlers.BroadbandHandler;
import edu.brown.cs.student.main.server.sources.MockACSAPISource;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;
import spark.Spark;

public class TestBroadbandHandler {

  public TestBroadbandHandler() {}

  @BeforeEach
  public void setup() {
    int port = 3235;
    Spark.port(port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });


    // Setting up the handler for the GET /order and /mock endpoints
    Spark.get("0", new BroadbandHandler(new MockACSAPISource(0)));
    Spark.get("1", new BroadbandHandler(new MockACSAPISource(1)));
    Spark.get("2", new BroadbandHandler(new MockACSAPISource(2)));
    Spark.get("3", new BroadbandHandler(new MockACSAPISource(3)));

    Spark.init();
    Spark.awaitInitialization();
    // Notice this link alone leads to a 404... Why is that?
    System.out.println("Server started at http://localhost:" + port);
  }

  @Test
  public void testMocks() throws IOException {
    String apiAppend = "?state=a&county=b";
    String apiCall = "0"+apiAppend;
    ServerFailureResponse failure =
        new ServerFailureResponse("error_datasource", "Test Case 0");
    Assert.assertEquals(this.deserialize(apiCall).get("result"), failure.response_type());
    Assert.assertEquals(this.deserialize(apiCall).get("error_type"), failure.error_type());
    Assert.assertEquals(this.deserialize(apiCall).get("details"), failure.details());

    apiCall = "1"+apiAppend;
    failure = new ServerFailureResponse("error_datasource", "Test Case 1");
    Assert.assertEquals(this.deserialize(apiCall).get("result"), failure.response_type());
    Assert.assertEquals(this.deserialize(apiCall).get("error_type"), failure.error_type());
    Assert.assertEquals(this.deserialize(apiCall).get("details"), failure.details());

    apiCall = "2"+apiAppend;
    failure = new ServerFailureResponse("error_bad_request", "Test Case 2");
    Assert.assertEquals(this.deserialize(apiCall).get("result"), failure.response_type());
    Assert.assertEquals(this.deserialize(apiCall).get("error_type"), failure.error_type());
    Assert.assertEquals(this.deserialize(apiCall).get("details"), failure.details());

    apiCall = "3"+apiAppend;
    Map<String, Object> inputMap = new HashMap<>();
    inputMap.put("Request date/time", new Date().toString());
    inputMap.put("State", "a");
    inputMap.put("County", "b");
    ServerSuccessResponse success = new ServerSuccessResponse(inputMap, "Pawnee, Indiana had 200% of houses with broadband.");
    Assert.assertEquals(this.deserialize(apiCall).get("result"), success.response_type());
    Assert.assertEquals(this.deserialize(apiCall).get("input"), success.input());
    Assert.assertEquals(this.deserialize(apiCall).get("data"), success.data());
  }

  @Test
  public void testQueryParams() throws IOException, FactoryFailureException {
    String apiAppend = "?state=a&county=b";
    String apiCall = "3"+apiAppend;

    Map<String, Object> inputMap = new HashMap<>();
    inputMap.put("Request date/time", new Date().toString());
    inputMap.put("State", "a");
    inputMap.put("County", "b");

    ServerSuccessResponse success = new ServerSuccessResponse(inputMap, "Pawnee, Indiana had 200% of houses with broadband.");
    Assert.assertEquals(this.deserialize(apiCall).get("result"), success.response_type());
    Assert.assertEquals(this.deserialize(apiCall).get("input"), success.input());
    Assert.assertEquals(this.deserialize(apiCall).get("data"), success.data());

    apiCall = "3";
    ServerFailureResponse failure = new ServerFailureResponse("error_bad_request",
        "No state to search was requested.");
    Assert.assertEquals(this.deserialize(apiCall).get("result"), failure.response_type());
    Assert.assertEquals(this.deserialize(apiCall).get("error_type"), failure.error_type());
    Assert.assertEquals(this.deserialize(apiCall).get("details"), failure.details());

    apiAppend = "?county=a";
    apiCall = "3"+apiAppend;
    Assert.assertEquals(this.deserialize(apiCall).get("result"), failure.response_type());
    Assert.assertEquals(this.deserialize(apiCall).get("error_type"), failure.error_type());
    Assert.assertEquals(this.deserialize(apiCall).get("details"), failure.details());

    apiAppend = "?state=a";
    apiCall = "3"+apiAppend;
    failure = new ServerFailureResponse("error_bad_request",
        "No county to search was requested.");
    Assert.assertEquals(this.deserialize(apiCall).get("result"), failure.response_type());
    Assert.assertEquals(this.deserialize(apiCall).get("error_type"), failure.error_type());
    Assert.assertEquals(this.deserialize(apiCall).get("details"), failure.details());

    apiAppend = "?state=";
    apiCall = "3"+apiAppend;
    Assert.assertEquals(this.deserialize(apiCall).get("result"), failure.response_type());
    Assert.assertEquals(this.deserialize(apiCall).get("error_type"), failure.error_type());
    Assert.assertEquals(this.deserialize(apiCall).get("details"), failure.details());

    apiAppend = "?state";
    apiCall = "3"+apiAppend;
    Assert.assertEquals(this.deserialize(apiCall).get("result"), failure.response_type());
    Assert.assertEquals(this.deserialize(apiCall).get("error_type"), failure.error_type());
    Assert.assertEquals(this.deserialize(apiCall).get("details"), failure.details());
  }

  @AfterEach
  public void tearDown() {
    Spark.unmap("/loadcsv");
    Spark.awaitStop();
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
}
