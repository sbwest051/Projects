package edu.brown.cs.student.server;

import static spark.Spark.after;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.CSV.CSVData;
import edu.brown.cs.student.main.CSV.Parser;
import edu.brown.cs.student.main.server.serializers.ServerFailureResponse;
import edu.brown.cs.student.main.server.serializers.ServerSuccessResponse;
import edu.brown.cs.student.main.FactoryFailureException;
import edu.brown.cs.student.main.server.handlers.LoadHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;
import spark.Spark;

public class TestLoadHandler {

  public TestLoadHandler() {}

  @BeforeEach
  public void setup() {
    int port = 3233;
    Spark.port(port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    // Setting up the handler for the GET /order and /mock endpoints
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

    Spark.init();
    Spark.awaitInitialization();

    // Notice this link alone leads to a 404... Why is that?
    System.out.println("Server started at http://localhost:" + port);
  }

  @Test
  public void testFilePaths() throws IOException {
    // no filepath
    ServerFailureResponse badRequest =
        new ServerFailureResponse("error_bad_request", "No filepath found.");
    String filePath = "";
    String apiCall = "loadcsv";
    Assert.assertEquals(this.deserialize(apiCall).get("result"), badRequest.response_type());
    Assert.assertEquals(this.deserialize(apiCall).get("error_type"), badRequest.error_type());
    Assert.assertEquals(this.deserialize(apiCall).get("details"), badRequest.details());

    // valid filepath within /data
    filePath = "data/RIIncome.csv";
    apiCall = "loadcsv?filepath=" + filePath;
    ServerSuccessResponse success = new ServerSuccessResponse(filePath, "none");
    Assert.assertEquals(this.deserialize(apiCall).get("result"), success.response_type());
    Assert.assertEquals(this.deserialize(apiCall).get("input"), success.input());
    Assert.assertEquals(this.deserialize(apiCall).get("data"), success.data());

    // valid filepath within a folder in adata
    filePath = "data/stars/ten-star.csv";
    apiCall = "loadcsv?filepath=" + filePath;
    success = new ServerSuccessResponse(filePath, "none");
    Assert.assertEquals(this.deserialize(apiCall).get("result"), success.response_type());
    Assert.assertEquals(this.deserialize(apiCall).get("input"), success.input());
    Assert.assertEquals(this.deserialize(apiCall).get("data"), success.data());

    // valid filepath within a different folder in data
    filePath = "data/census/income_by_race_edited.csv";
    apiCall = "loadcsv?filepath=" + filePath;
    success = new ServerSuccessResponse(filePath, "none");
    Assert.assertEquals(this.deserialize(apiCall).get("result"), success.response_type());
    Assert.assertEquals(this.deserialize(apiCall).get("input"), success.input());
    Assert.assertEquals(this.deserialize(apiCall).get("data"), success.data());

    // filepaths in data that doesn't exist
    filePath = "data";
    apiCall = "loadcsv?filepath=" + filePath;
    ServerFailureResponse notData =
        new ServerFailureResponse(
            "error_bad_request",
            "File must be in the data folder and filepath= "
                + filePath
                + " "
                + "must start with /data (use content or repository root).");
    Assert.assertEquals(this.deserialize(apiCall).get("result"), notData.response_type());
    Assert.assertEquals(this.deserialize(apiCall).get("error_type"), notData.error_type());
    Assert.assertEquals(this.deserialize(apiCall).get("details"), notData.details());

    filePath = "data/";
    apiCall = "loadcsv?filepath=" + filePath;
    badRequest =
        new ServerFailureResponse(
            "error_bad_request", "File " + filePath + " could not be found or read.");
    Assert.assertEquals(this.deserialize(apiCall).get("result"), badRequest.response_type());
    Assert.assertEquals(this.deserialize(apiCall).get("error_type"), badRequest.error_type());
    Assert.assertEquals(this.deserialize(apiCall).get("details"), badRequest.details());

    filePath = "data/abc123.csv";
    apiCall = "loadcsv?filepath=" + filePath;
    badRequest =
        new ServerFailureResponse(
            "error_bad_request", "File " + filePath + " could not be found or read.");
    Assert.assertEquals(this.deserialize(apiCall).get("result"), badRequest.response_type());
    Assert.assertEquals(this.deserialize(apiCall).get("error_type"), badRequest.error_type());
    Assert.assertEquals(this.deserialize(apiCall).get("details"), badRequest.details());

    // filepaths not in data that exist
    filePath = "src/main/java/edu/brown/cs/student/main/CSV/CSVData.java";
    apiCall = "loadcsv?filepath=" + filePath;
    notData =
        new ServerFailureResponse(
            "error_bad_request",
            "File must be in the data folder and filepath= "
                + filePath
                + " "
                + "must start with /data (use content or repository root).");
    Assert.assertEquals(this.deserialize(apiCall).get("result"), notData.response_type());
    Assert.assertEquals(this.deserialize(apiCall).get("error_type"), notData.error_type());
    Assert.assertEquals(this.deserialize(apiCall).get("details"), notData.details());

    filePath = "config/intellij-java-google-style.xml";
    apiCall = "loadcsv?filepath=" + filePath;
    notData =
        new ServerFailureResponse(
            "error_bad_request",
            "File must be in the data folder and filepath= "
                + filePath
                + " "
                + "must start with /data (use content or repository root).");
    Assert.assertEquals(this.deserialize(apiCall).get("result"), notData.response_type());
    Assert.assertEquals(this.deserialize(apiCall).get("error_type"), notData.error_type());
    Assert.assertEquals(this.deserialize(apiCall).get("details"), notData.details());

    // filepaths that are obviously not in data because there are not enough letters
    filePath = "abc";
    apiCall = "loadcsv?filepath=" + filePath;
    notData =
        new ServerFailureResponse(
            "error_bad_request",
            "File must be in the data folder and filepath= "
                + filePath
                + " "
                + "must start with /data (use content or repository root).");
    Assert.assertEquals(this.deserialize(apiCall).get("result"), notData.response_type());
    Assert.assertEquals(this.deserialize(apiCall).get("error_type"), notData.error_type());
    Assert.assertEquals(this.deserialize(apiCall).get("details"), notData.details());
  }

  @AfterEach
  public void tearDown() {
    Spark.unmap("loadcsv");
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
