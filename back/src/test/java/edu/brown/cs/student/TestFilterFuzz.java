package edu.brown.cs.student;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.handlers.FilterJsonHandler;
import edu.brown.cs.student.main.server.handlers.GetJsonHandler;
import edu.brown.cs.student.main.server.handlers.JSONParser;
import edu.brown.cs.student.main.server.handlers.SearchJsonHandler;
import edu.brown.cs.student.main.server.serializers.FeatureCollectionResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;
import spark.Spark;

public class TestFilterFuzz {

  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(3235);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }


  @BeforeEach
  public void setup() throws IOException {
    JSONParser jsonParser = new JSONParser();
    Spark.get("filterjson", new FilterJsonHandler(jsonParser.getFeatureCollection()));
    Spark.get("getjson", new GetJsonHandler(jsonParser.getFeatureCollection()));
    Spark.init();
    Spark.awaitInitialization();
  }

  @AfterEach
  public void teardown() {
    Spark.unmap("getjson");
    Spark.awaitStop();
  }

  public static Double[] getValidRandomInputs() {
    Double[] inputs = new Double[4];
    final ThreadLocalRandom r = ThreadLocalRandom.current();

    //Random minX and maxX
    double a = r.nextDouble(-125, -55);
    double b = r.nextDouble(-125, -55);

    if (a > b){
      inputs[0] = b;
      inputs[1] = a;
    } else {
      inputs[0] = a;
      inputs[1] = b;
    }
    //Random minY and maxY
    double c = r.nextDouble(20, 50);
    double d = r.nextDouble(20, 50);

    if (c > d){
      inputs[2] = d;
      inputs[3] = c;
    } else {
      inputs[2] = c;
      inputs[3] = d;
    }
    return inputs;
  }

  public static Double[] getRandomIdenticalInputs() {
    Double[] identicalInputArray = new Double[4];
    double identicalInput = ThreadLocalRandom.current().nextDouble(-1000,1000);
    identicalInputArray[0] = identicalInput;
    identicalInputArray[1] = identicalInput;
    identicalInputArray[2] = identicalInput;
    identicalInputArray[3] = identicalInput;
    return identicalInputArray;
  }

  public static String getRandomAPICall(Double[] inputs) {
    return "filterjson?minX="+ inputs[0] +"&maxX="+ inputs[1] +"&minY="+ inputs[2] +"&maxY="+ inputs[3];
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
      HttpURLConnection clientConnection = tryRequest(getRandomAPICall(getValidRandomInputs()));
      assertEquals(200, clientConnection.getResponseCode());
      clientConnection.disconnect();
    }
  }

  @Test
  public void testPBT(){
    for (int i = 0; i < 1000; i++) {
      Double[] inputs = getValidRandomInputs();
      Assert.assertTrue(checkValidity(inputs, getRandomAPICall(inputs)));
    }
  }

  @Test
  public void testValidityChecking(){
    for (int i = 0; i < 1; i++) {
      String apiCall = getRandomAPICall(getRandomIdenticalInputs());
      Assert.assertTrue(checkValidity(getRandomIdenticalInputs(),apiCall));
    }

    Double[] wholeInput = new Double[4];
    wholeInput[0] = (double) -125;
    wholeInput[1] = (double) 50;
    wholeInput[2] = (double) -125;
    wholeInput[3] = (double) 50;

    String filterCall = "filterjson?minX=-125&maxX=-55&minY=20&maxY=50";

    Assert.assertTrue(checkValidity(wholeInput,getRandomAPICall(wholeInput)));
    Assert.assertTrue(checkValidity(wholeInput,filterCall));
  }

  public Boolean checkValidity(Double[] inputs, String apiCall){
    FeatureCollectionResponse fcresponse;
    try{
      fcresponse = deserializeFCResponse(apiCall);
    } catch (IOException e){
      return false;
    }
    for (Double input:inputs) {
      if (input == null){
        System.out.println(Arrays.toString(inputs));
      }
    }
    return fcresponse.data().features().stream().allMatch(feature ->
            (feature.geometry() != null && feature.geometry().coordinates().get(0).get(0)
                .stream()
                .allMatch(
                    coordinate -> coordinate.get(0) >= inputs[0] && coordinate.get(0) <= inputs[1]
                        && coordinate.get(1) >= inputs[2] && coordinate.get(1) <= inputs[3])));
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
