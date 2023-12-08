package edu.brown.cs.student.plme;

import static spark.Spark.after;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.CSV.CSVData;
import edu.brown.cs.student.main.CSV.Parser;
import edu.brown.cs.student.main.ChatPDFSource;
import edu.brown.cs.student.main.FactoryFailureException;
import edu.brown.cs.student.main.MetadataHandler;
import edu.brown.cs.student.main.records.PLME.request.InputFile;
import edu.brown.cs.student.main.records.PLME.MDCInput;
import edu.brown.cs.student.main.records.PLME.request.PLMEInput;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;
import spark.Spark;

public class TestChatPDFSource {

  public TestChatPDFSource() {}

  @BeforeEach
  public void setup() {
    int port = 3234;
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
    Spark.get("plme", new MetadataHandler(new ChatPDFSource()));

    Spark.init();
    Spark.awaitInitialization();

    System.out.println("Server started at http://localhost:" + port);
  }

  @Test
  public void testAddURL() {
    ChatPDFSource pdfSource = new ChatPDFSource();
    String url = "https://example.com";
    try {
      String sourceId = pdfSource.addURL(url);
      Assert.assertNotNull(sourceId);
      Assert.assertFalse(sourceId.isEmpty());
    } catch (DatasourceException e) {
      Assert.fail("Exception thrown: " + e.getMessage());
    }
  }

  @Test
  public void testAddFile() {
    ChatPDFSource pdfSource = new ChatPDFSource();
    String filePath = "data/allergy.pdf";
    try {
      String sourceId = pdfSource.addFile(filePath);
      Assert.assertNotNull(sourceId);
      Assert.assertFalse(sourceId.isEmpty());
    } catch (DatasourceException e) {
      Assert.fail("Exception thrown: " + e.getMessage());
    }
  }

  @Test
  public void testGetContent() {
    ChatPDFSource pdfSource = new ChatPDFSource();
    String filePath = "data/allergy.pdf";
    String question = "Who is the author of this paper?";
    String result = "Claudia Kitzmueller";
    try {
      String sourceId = pdfSource.addFile(filePath);
      String content = pdfSource.getContent(sourceId, question);
      Assert.assertNotNull(content);
      Assert.assertFalse(content.isEmpty());
      Assert.assertTrue(content.contains(result));
    } catch (DatasourceException | NullPointerException e) {
      Assert.fail("Exception thrown: " + e.getMessage());
    }
  }

  @Test
  public void testGetMoreContent() {
    ChatPDFSource pdfSource = new ChatPDFSource();
    String filePath = "data/allergy.pdf";
    String question = "What allergen did they test";
    String result = "Bet v 1";
    try {
      String sourceId = pdfSource.addFile(filePath);
      String content = pdfSource.getContent(sourceId, question);
      Assert.assertNotNull(content);
      Assert.assertFalse(content.isEmpty());
      Assert.assertTrue(content.contains(result));
    } catch (DatasourceException | NullPointerException e) {
      Assert.fail("Exception thrown: " + e.getMessage());
    }
  }

  @Test
  public void addFileException() {
    boolean exceptionThrown = false;
    ChatPDFSource pdfSource = new ChatPDFSource();
    String filePath = "fake-path.pdf";
      try {
        String sourceId = pdfSource.addFile(filePath);
      } catch (DatasourceException e) {
        exceptionThrown = true;
      }
      Assert.assertTrue(exceptionThrown);
    }

    @Test
    public void sourceIDNull() {
      boolean exceptionThrown = false;
      ChatPDFSource pdfSource = new ChatPDFSource();
      String question = "Who is the author of this paper?";
      String result = "Claudia Kitzmueller";
      try {
        String sourceId = null;
        String content = pdfSource.getContent(sourceId, question);
        Assert.assertNotNull(content);
        Assert.assertFalse(content.isEmpty());
        Assert.assertTrue(content.contains(result));
      } catch (NullPointerException | DatasourceException e) {
        exceptionThrown = true;
      }
      Assert.assertTrue(exceptionThrown);
    }

  @Test
  public void getContentException() {
    boolean exceptionThrown = false;

    ChatPDFSource pdfSource = new ChatPDFSource();
    String filePath = "data/lol/allergy.pdf";
    String question = "What allergen did they test";
    String result = "Bet v 1";
    try {
      String sourceId = pdfSource.addFile(filePath);
      String content = pdfSource.getContent(sourceId, question);
      Assert.assertNotNull(content);
      Assert.assertFalse(content.isEmpty());
      Assert.assertTrue(content.contains(result));
    } catch (DatasourceException e) {
      exceptionThrown = true;
    }
    Assert.assertTrue(exceptionThrown);
  }

  @AfterEach
  public void tearDown() {
    Spark.unmap("/plme");
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
