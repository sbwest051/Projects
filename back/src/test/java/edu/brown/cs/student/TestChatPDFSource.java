package edu.brown.cs.student;

import static spark.Spark.after;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.CSV.CSVData;
import edu.brown.cs.student.main.CSV.Parser;
import edu.brown.cs.student.main.CSV.creators.ListCreator;
import edu.brown.cs.student.main.FactoryFailureException;
import edu.brown.cs.student.main.records.PLME.request.InputFile;
import edu.brown.cs.student.main.records.PLME.request.MDCInput;
import edu.brown.cs.student.main.records.PLME.request.PLMEInput;
import edu.brown.cs.student.main.server.handlers.LoadHandler;
import edu.brown.cs.student.main.server.handlers.ViewHandler;
import edu.brown.cs.student.main.server.serializers.ServerFailureResponse;
import edu.brown.cs.student.main.server.serializers.ServerSuccessResponse;
import java.io.FileReader;
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
    Spark.get("loadcsv", new LoadHandler(data));
    Spark.get("viewcsv", new ViewHandler(data));

    Spark.init();
    Spark.awaitInitialization();

    // Notice this link alone leads to a 404... Why is that?
    System.out.println("Server started at http://localhost:" + port);
  }


  @Test
  public void sampleInput() throws IOException, FactoryFailureException {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<PLMEInput> adapter = moshi.adapter(PLMEInput.class);
    InputFile file1 = new InputFile("sample pdf 1", "data/samplefilepath.pdf", null);
    InputFile file2 = new InputFile("sample pdf 2", null, "www.woohoopdf.com/getpdf");
    List<InputFile> list = new ArrayList<>();
    list.add(file1);
    list.add(file2);

    List<String> keywordList = new ArrayList<>();
    keywordList.add("keyword 1");
    keywordList.add("keyword 2");
    MDCInput listinput = new MDCInput("sample metadata column", "what is earth?", keywordList,
        null);

    Map<String, List<String>> map = new LinkedHashMap<>();
    map.put("keyword", keywordList);
    map.put("pretend this is the keyword for a different list", keywordList);

    MDCInput mapinput = new MDCInput("sample metadata column 2", "what is not earth?", null, map);

    List<MDCInput> inputs = new ArrayList<>();
    inputs.add(listinput);
    inputs.add(mapinput);
    System.out.println(adapter.toJson(new PLMEInput("csv filepath", list, inputs)));
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
