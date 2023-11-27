package edu.brown.cs.student;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.CSV.CSVData;
import edu.brown.cs.student.main.CSV.Parser;
import edu.brown.cs.student.main.CSV.Searcher;
import edu.brown.cs.student.main.FactoryFailureException;
import edu.brown.cs.student.main.server.handlers.BroadbandHandler;
import edu.brown.cs.student.main.server.handlers.LoadHandler;
import edu.brown.cs.student.main.server.handlers.SearchHandler;
import edu.brown.cs.student.main.server.handlers.ViewHandler;
import edu.brown.cs.student.main.server.serializers.ServerFailureResponse;
import edu.brown.cs.student.main.server.serializers.ServerSuccessResponse;
import edu.brown.cs.student.main.server.sources.MockACSAPISource;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;
import spark.Spark;

public class TestBroadbandFuzz {

  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(3235);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  private CSVData data;

  @BeforeEach
  public void setup() throws IOException, FactoryFailureException {
    Parser<List<String>> parser = new Parser<>();
    this.data = new CSVData(parser);

    Spark.get("/loadcsv", new LoadHandler(this.data));
    Spark.get("/viewcsv", new ViewHandler(this.data));
    Spark.get("/broadband", new BroadbandHandler(new MockACSAPISource(3)));
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

  public static String getRandomStringBounded(int length, int first, int last) {
    final ThreadLocalRandom r = ThreadLocalRandom.current();
    StringBuilder sb = new StringBuilder();
    for(int iCount=0;iCount<length;iCount++) {
      // upper-bound is exclusive
      int code = r.nextInt(first, last+1);
      sb.append((char) code);
    }
    return sb.toString();
  }

  public static String getRandomAPICall() {
    final ThreadLocalRandom r = ThreadLocalRandom.current();
    int nameLength = r.nextInt(10);
    // Beware: This excludes a lot of punctuation in the interest of not causing problems for CSV...
    String state = getRandomStringBounded(nameLength, 45, 126);
    String county = getRandomStringBounded(nameLength, 45, 126);

    return "broadband?state="+ state +"&county="+ county;
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
  public void testFuzz() throws IOException {
    for (int i = 0; i < 1000; i++) {
      HttpURLConnection clientConnection = tryRequest(getRandomAPICall());
      assertEquals(200, clientConnection.getResponseCode());
      clientConnection.disconnect();
    }
  }
}
