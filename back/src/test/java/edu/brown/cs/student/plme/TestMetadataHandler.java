package edu.brown.cs.student.plme;

import static spark.Spark.after;
import static spark.Spark.options;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.CSV.CSVData;
import edu.brown.cs.student.main.CSV.Parser;
import edu.brown.cs.student.main.ChatPDFSource;
import edu.brown.cs.student.main.FactoryFailureException;
import edu.brown.cs.student.main.MetadataHandler;
import edu.brown.cs.student.main.records.PLME.MDCInput;
import edu.brown.cs.student.main.records.PLME.request.InputFile;
import edu.brown.cs.student.main.records.PLME.request.PLMEInput;
import edu.brown.cs.student.main.records.PLME.response.MetadataTable;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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

public class TestMetadataHandler {
  public TestMetadataHandler() {}
  @BeforeEach
  public void setup() {
    System.setProperty("org.apache.commons.logging.Log",
        "org.apache.commons.logging.impl.NoOpLog");
    int port = 3234;
    Spark.port(port);

    options("/*", (request, response) -> {
      String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
      if (accessControlRequestHeaders != null) {
        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
      }

      String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
      if (accessControlRequestMethod != null) {
        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
      }

      return "OK";
    });

    after((request, response) -> {
      response.header("Access-Control-Allow-Origin", "*");
      response.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
      response.header("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,");
    });
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
    Spark.post("plme", new MetadataHandler(new ChatPDFSource()));

    Spark.init();
    Spark.awaitInitialization();

    System.out.println("Server started at http://localhost:" + port);
  }

  @Test
  public void largerInputTest() throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<PLMEInput> adapter = moshi.adapter(PLMEInput.class);
    List<String> keywordList = new ArrayList<>();
    keywordList.add("Bronchoalveolar Lavage Fluid");
    keywordList.add("Sputum");
    keywordList.add("Saliva");
    keywordList.add("oropharyngial");
    keywordList.add("not sampled");
    MDCInput sampleInput = new MDCInput("Sample Type", "How was the respiratory tract sampled?",
        keywordList, null);

    Map<String, List<String>> map = new LinkedHashMap<>();
    List<String> kl = new ArrayList<>();
    kl.add("longitudinal");
    kl.add("over time");
    map.put("Longitudinal", kl);
    List<String> kl1 = new ArrayList<>();
    kl1.add("cross-sectional");
    kl1.add("at one time");
    kl1.add("cohort");
    map.put("Cross-sectional", kl1);
    List<String> kl2 = new ArrayList<>();
    kl2.add("scoping");
    kl2.add("review");
    map.put("review", kl2);
    List<String> kl3 = new ArrayList<>();
    kl3.add("it is not clear");
    kl3.add("unknown");
    map.put("N/A", kl3);
    MDCInput studyInput = new MDCInput("Type of Study", "Was this paper a review paper, "
        + "longitudinal study, or cross-sectional/cohort study?", null, map);

    List<String> subjectList = new ArrayList<>();
    subjectList.add("sheep");
    subjectList.add("pigs");
    subjectList.add("cows");
    subjectList.add("children");
    subjectList.add("human");
    subjectList.add("mice");
    MDCInput subjectInput = new MDCInput("Subjects", "Who were the subjects of the study?",
        subjectList, null);

    List<MDCInput> inputs = new ArrayList<>();
    inputs.add(sampleInput);
    inputs.add(studyInput);
    inputs.add(subjectInput);
    System.out.println(adapter.toJson(new PLMEInput("data/LargerTestFiles.csv", null, inputs)));
    System.out.println(this.deserialize(adapter.toJson(new PLMEInput("data/LargerTestFiles.csv",
        null, inputs))).serialize());
  }

  @AfterEach
  public void tearDown() {
    Spark.unmap("/plme");
    Spark.awaitStop();
  }

  private static HttpURLConnection tryRequest(String body) throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + "plme");
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    // The default method is "GET", which is what we're using here.
    // If we were using "POST", we'd need to say so.
    clientConnection.setRequestMethod("POST");
    clientConnection.setRequestProperty("Content-Type", "application/json");
    clientConnection.setRequestProperty("Accept", "application/json");
    clientConnection.setDoOutput(true);

    try(OutputStream os = clientConnection.getOutputStream()) {
      byte[] input = body.getBytes(StandardCharsets.UTF_8);
      os.write(input, 0, input.length);
    }

    clientConnection.connect();
    return clientConnection;
  }

  private MetadataTable deserialize(String body) throws IOException {
    HttpURLConnection clientConnection = tryRequest(body);
    Assert.assertEquals(clientConnection.getResponseCode(), 200);
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<MetadataTable> adapter = moshi.adapter(MetadataTable.class);
    return adapter.fromJson(
        new Scanner(clientConnection.getInputStream()).useDelimiter("\\A").next());
  }
}