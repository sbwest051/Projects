package edu.brown.cs.student.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.CSV.*;
import edu.brown.cs.student.main.FactoryFailureException;
import edu.brown.cs.student.main.server.handlers.LoadHandler;
import edu.brown.cs.student.main.server.handlers.SearchHandler;
import edu.brown.cs.student.main.server.handlers.ViewHandler;
import edu.brown.cs.student.main.server.serializers.ServerFailureResponse;
import edu.brown.cs.student.main.server.serializers.ServerSuccessResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;
import spark.Spark;

public class TestHandlers {

  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  private CSVData data;

  @BeforeEach
  public void setup() throws IOException, FactoryFailureException {
    Parser<List<String>> parser = new Parser<>();
    this.data = new CSVData(parser);

    Spark.get("/loadcsv", new LoadHandler(this.data));
    Spark.get("/viewcsv", new ViewHandler(this.data));
    Spark.get("/searchcsv", new SearchHandler(this.data));
    Spark.init();
    Spark.awaitInitialization();
  }

  @AfterEach
  public void teardown() {
    Spark.unmap("/loadcsv");
    Spark.unmap("/viewcsv");
    Spark.unmap("/searchcsv");
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

  @Test
  public void testLoad() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=data/RIIncome.csv");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    ServerSuccessResponse response =
        moshi
            .adapter(ServerSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    Assert.assertEquals("data/RIIncome.csv", response.input());

    clientConnection.disconnect();
  }

  @Test
  public void testEmptyLoad() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    ServerFailureResponse response =
        moshi
            .adapter(ServerFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    Assert.assertEquals("No filepath found.", response.details());

    clientConnection.disconnect();
  }

  @Test
  public void testImproperDataLoad() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=RIIncome.csv");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    ServerFailureResponse response =
        moshi
            .adapter(ServerFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    Assert.assertEquals(
        "File must be in the data folder and filepath= RIIncome.csv "
            + "must start with /data (use content or repository root).",
        response.details());

    clientConnection.disconnect();
  }

  @Test
  public void testWrongFileLoad() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=data/Income.csv");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    ServerFailureResponse response =
        moshi
            .adapter(ServerFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    Assert.assertEquals("File data/Income.csv could not be found or read.", response.details());

    clientConnection.disconnect();
  }

  @Test
  public void testEmptySearch() throws IOException {
    HttpURLConnection clientConnection = tryRequest("searchcsv?keyword=Providence&hasHeaders=true");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    ServerFailureResponse response =
        moshi
            .adapter(ServerFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    Assert.assertEquals("Cannot retrieve CSV. CSV may not be loaded.", response.details());

    clientConnection.disconnect();
  }

  @Test
  public void testEmptyHeadersSearch() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=data/Income.csv");
    assertEquals(200, clientConnection.getResponseCode());
    clientConnection = tryRequest("searchcsv?keyword=Providence");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    ServerFailureResponse response =
        moshi
            .adapter(ServerFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    Assert.assertEquals("Whether the CSV file has headers must be specified.", response.details());

    clientConnection.disconnect();
  }

  @Test
  public void testEmptyKeywordSearch() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=data/RIIncome.csv");
    assertEquals(200, clientConnection.getResponseCode());
    clientConnection = tryRequest("searchcsv?hasHeaders=true");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    ServerFailureResponse response =
        moshi
            .adapter(ServerFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    Assert.assertEquals("No keyword to search was requested", response.details());

    clientConnection.disconnect();
  }

  @Test
  public void testWrongColumnIdSearch() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=data/RIIncome.csv");
    assertEquals(200, clientConnection.getResponseCode());
    clientConnection = tryRequest("searchcsv?keyword=Providence&hasHeaders=true&columnID=City");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    ServerFailureResponse response =
        moshi
            .adapter(ServerFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    Assert.assertEquals("columnID = City was not found or out of index.", response.details());

    clientConnection.disconnect();
  }

  @Test
  public void testSearchWithColumnID() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=data/RIIncome.csv");
    assertEquals(200, clientConnection.getResponseCode());
    clientConnection =
        tryRequest("searchcsv?keyword=Providence&hasHeaders=true&columnID=City/Town");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    ServerSuccessResponse response =
        moshi
            .adapter(ServerSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Searcher searcher = new Searcher(this.data.getCSV(), true);
    Assert.assertEquals(searcher.searchColumn("Providence", "City/Town"), response.data());

    clientConnection.disconnect();
  }

  @Test
  public void testSearchWithNoColumnID() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=data/RIIncome.csv");
    assertEquals(200, clientConnection.getResponseCode());
    clientConnection = tryRequest("searchcsv?keyword=Providence&hasHeaders=true");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    ServerSuccessResponse response =
        moshi
            .adapter(ServerSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Searcher searcher = new Searcher(this.data.getCSV(), true);
    Assert.assertEquals(searcher.searchAll("Providence"), response.data());

    clientConnection.disconnect();
  }
}
