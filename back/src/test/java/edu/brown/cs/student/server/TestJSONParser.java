package edu.brown.cs.student.server;

import edu.brown.cs.student.main.records.maps.Feature;
import edu.brown.cs.student.main.records.maps.FeatureCollection;
import edu.brown.cs.student.main.records.maps.geometry;
import edu.brown.cs.student.main.records.maps.properties;
import edu.brown.cs.student.main.server.handlers.JSONParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

public class TestJSONParser {

  @Test
  public void testBadJSON() throws IOException {
    JSONParser jsonParser = new JSONParser();
    String badFile = "WEBIOWBEOFIBWEOBFOWEBOFB";
    Assert.assertThrows(IOException.class, () -> {
      jsonParser.fromJSON(badFile);});
    String worseFile = "{}";
    Assert.assertThrows(IOException.class, () -> {
      jsonParser.fromJSON(worseFile);});
    String badJson = "data/test/file.csv";
    Assert.assertThrows(IOException.class, () -> {
      jsonParser.fromJSON(badJson);});
    String worseJson = "data/test/bad.json";
    Assert.assertThrows(IOException.class, () -> {
      jsonParser.fromJSON(worseJson);});
  }

  @Test
  public void TestReal() throws IOException {
    JSONParser handler = new JSONParser();
    handler.fromJSON("data/test/TestFeatureCollection.json");

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

    Assert.assertEquals(expectedFC, handler.getFeatureCollection());
  }
}
