package edu.brown.cs.student.plme;

import edu.brown.cs.student.main.plme.RelevanceCalculator;
import edu.brown.cs.student.main.plme.ReliabilityCalculator;
import edu.brown.cs.student.main.records.PLME.MDCInput;
import edu.brown.cs.student.main.exceptions.DatasourceException;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

public class TestRelevanceCalculator {

  @Test
  public void testReadFile() throws DatasourceException {
    RelevanceCalculator rvCalc = new RelevanceCalculator();
    String content = rvCalc.readFile(new File("data/testPdfs/TestDoc1.pdf"));
    Assert.assertTrue(content.contains("Red"));
    Assert.assertTrue(content.contains("Orange"));
    Assert.assertTrue(content.contains("Yellow"));
    Assert.assertTrue(content.contains("Green"));
    Assert.assertTrue(content.contains("Blue"));
    Assert.assertTrue(content.contains("Indigo"));
    Assert.assertTrue(content.contains("Violet"));
    Assert.assertFalse(content.contains("Color"));

    String content2 = rvCalc.readFile(new File("data/testPdfs/TestDoc2.pdf"));
    Assert.assertTrue(content2.contains("Red"));
    Assert.assertTrue(content2.contains("White"));
    Assert.assertTrue(content2.contains("Blue"));
    Assert.assertTrue(content2.contains("America"));
    Assert.assertFalse(content2.contains("Orange"));

    String content3 = rvCalc.readFile(new File("data/testPdfs/TestDoc3.pdf"));
    Assert.assertEquals(ReliabilityCalculator.parseContent(content2),
        ReliabilityCalculator.parseContent(content3));

    Assert.assertThrows(DatasourceException.class,
        () -> rvCalc.readFile(new File("data")));
    Assert.assertThrows(DatasourceException.class,
        () -> rvCalc.readFile(new File("data/a")));
    Assert.assertThrows(DatasourceException.class,
        () -> rvCalc.readFile(new File("abwoefiaibewf")));
    Assert.assertThrows(DatasourceException.class,
        () -> rvCalc.readFile(new File("data/RIIncome.csv")));
  }

  @Test
  public void testTFList() throws DatasourceException {
    RelevanceCalculator rvCalc = new RelevanceCalculator();
    String content = rvCalc.readFile(new File("data/testPdfs/TestDoc1.pdf"));
    List<String> keywordList = ReliabilityCalculator.parseContent("red orange yellow green blue "
        + "indigo violet america white toyota");
    String content2 = rvCalc.readFile(new File("data/testPdfs/TestDoc2.pdf"));

    Map<String, Double> freqMap = rvCalc.calculateTFList(new MDCInput("1",null,null,null),
        content, keywordList);

    Assert.assertEquals(freqMap.get("red"), (double) 1 / 7);
    Assert.assertEquals(freqMap.get("white"), 0);
    Assert.assertEquals(freqMap.get("toyota"), 0);
    Assert.assertEquals(freqMap.get("orange"), (double) 1 / 7);

    Map<String, Double> freqMap2 = rvCalc.calculateTFList(new MDCInput("2",null,null,null),
        content2, keywordList);
    Assert.assertEquals(freqMap2.get("red"), (double) 1 / 5);
    Assert.assertEquals(freqMap2.get("white"), (double) 1 / 5);
    Assert.assertEquals(freqMap2.get("orange"), 0);
    Assert.assertEquals(freqMap2.get("toyota"), 0);
    Assert.assertEquals(freqMap2.get("america"), (double) 2 /5);

    String content3 = rvCalc.readFile(new File("data/testPdfs/TestDoc3.pdf"));
    Map<String, Double> freqMap3 = rvCalc.calculateTFList(new MDCInput("3",null,null,null),
        content3, keywordList);

    Assert.assertEquals(freqMap2, freqMap3);

    Assert.assertThrows(DatasourceException.class,() -> rvCalc.calculateTFList(null, null, null));
    Assert.assertThrows(DatasourceException.class,() -> rvCalc.calculateTFList(null, content,
        null));
    Assert.assertThrows(DatasourceException.class,() -> rvCalc.calculateTFList(null, content,
        keywordList));
    Assert.assertThrows(DatasourceException.class,() -> rvCalc.calculateTFList(new MDCInput(null,
            null, null, null),
        null, keywordList));
    Assert.assertThrows(DatasourceException.class,() -> rvCalc.calculateTFList(new MDCInput(null,
            null, null, null),
        content, null));
  }

  @Test
  public void testTFMap() throws DatasourceException {
    RelevanceCalculator rvCalc = new RelevanceCalculator();
    String content = rvCalc.readFile(new File("data/testPdfs/TestDoc1.pdf"));
    Map<String, List<String>> keywordMap = new HashMap<>();
    List<String> keywordList = ReliabilityCalculator.parseContent("red orange yellow green blue "
        + "indigo violet white");
    List<String> keywordList2 = ReliabilityCalculator.parseContent("america toyota");
    keywordMap.put("color", keywordList);
    keywordMap.put("non-color", keywordList2);

    String content2 = rvCalc.readFile(new File("data/testPdfs/TestDoc2.pdf"));

    Map<String, Map<String, Double>> freqMap = rvCalc.calculateTFMap(new MDCInput("1",null,null,
        keywordMap), content);

    Assert.assertEquals(freqMap.get("color").get("red"), (double) 1 / 7);
    Assert.assertEquals(freqMap.get("color").get("white"), 0);
    Assert.assertEquals(freqMap.get("non-color").get("toyota"), 0);
    Assert.assertEquals(freqMap.get("color").get("red"), (double) 1 / 7);

    Map<String, Map<String, Double>> freqMap2 = rvCalc.calculateTFMap(new MDCInput("2",null,null,
            keywordMap),
        content2);
    Assert.assertEquals(freqMap2.get("color").get("red"), (double) 1 / 5);
    Assert.assertEquals(freqMap2.get("color").get("white"), (double) 1 / 5);
    Assert.assertEquals(freqMap2.get("color").get("orange"), 0);
    Assert.assertEquals(freqMap2.get("non-color").get("toyota"), 0);
    Assert.assertEquals(freqMap2.get("non-color").get("america"), (double) 2 /5);

    String content3 = rvCalc.readFile(new File("data/testPdfs/TestDoc3.pdf"));
    Map<String, Map<String, Double>> freqMap3 = rvCalc.calculateTFMap(new MDCInput("3",null,null,
            keywordMap), content3);

    Assert.assertEquals(freqMap2, freqMap3);

    Assert.assertThrows(DatasourceException.class,() -> rvCalc.calculateTFMap(null, null));
    Assert.assertThrows(DatasourceException.class,() -> rvCalc.calculateTFMap(new MDCInput(null,
            null, null, null),null));
    Assert.assertThrows(DatasourceException.class,() -> rvCalc.calculateTFMap(new MDCInput(null,
        null, null, null),content));
    Assert.assertThrows(DatasourceException.class,() -> rvCalc.calculateTFMap(null,content));
  }

  @Test
  public void testListRelevance() throws DatasourceException {
    RelevanceCalculator rvCalc = new RelevanceCalculator();
    String content = rvCalc.readFile(new File("data/testPdfs/TestDoc1.pdf"));
    String content2 = rvCalc.readFile(new File("data/testPdfs/TestDoc2.pdf"));
    String content3 = rvCalc.readFile(new File("data/testPdfs/TestDoc3.pdf"));
    List<String> keywordList = ReliabilityCalculator.parseContent("red orange yellow green blue "
        + "indigo violet america white toyota");

    MDCInput input1 = new MDCInput("1",null,keywordList,null);
    Map<String, Double> freqMap = rvCalc.calculateTFList(input1,
        content, keywordList);
    Map<String, Double> freqMap2 = rvCalc.calculateTFList(input1, content2, keywordList);
    Map<String, Double> freqMap3 = rvCalc.calculateTFList(input1, content3, keywordList);

    Assert.assertEquals(rvCalc.getRelevanceScore(input1,freqMap).get("orange"),
        rvCalc.getRelevanceScore(input1,freqMap).get("yellow"));
    Assert.assertEquals(rvCalc.getRelevanceScore(input1,freqMap).get("green"),
        rvCalc.getRelevanceScore(input1,freqMap).get("yellow"));
    Assert.assertEquals(rvCalc.getRelevanceScore(input1,freqMap).get("green"),
        rvCalc.getRelevanceScore(input1,freqMap).get("indigo"));
    Assert.assertEquals(rvCalc.getRelevanceScore(input1,freqMap).get("green"),
        rvCalc.getRelevanceScore(input1,freqMap).get("violet"));
    Assert.assertEquals(rvCalc.getRelevanceScore(input1,freqMap).get("america"),
        rvCalc.getRelevanceScore(input1,freqMap).get("toyota"));
    Assert.assertEquals(rvCalc.getRelevanceScore(input1,freqMap).get("blue"),
        rvCalc.getRelevanceScore(input1,freqMap).get("red"));
    Assert.assertTrue(rvCalc.getRelevanceScore(input1,freqMap).get("orange") >
        rvCalc.getRelevanceScore(input1,freqMap).get("red"));

    Assert.assertEquals(rvCalc.getRelevanceScore(input1,freqMap2).get("red"),
        rvCalc.getRelevanceScore(input1,freqMap2).get("blue"));
    Assert.assertTrue(rvCalc.getRelevanceScore(input1,freqMap2).get("america") >
        rvCalc.getRelevanceScore(input1,freqMap2).get("white"));
    Assert.assertTrue(rvCalc.getRelevanceScore(input1,freqMap2).get("america") >
        rvCalc.getRelevanceScore(input1,freqMap2).get("red"));
    Assert.assertTrue(rvCalc.getRelevanceScore(input1,freqMap2).get("white") >
        rvCalc.getRelevanceScore(input1,freqMap2).get("red"));

    Assert.assertEquals(rvCalc.getRelevanceScore(input1,freqMap2),
        rvCalc.getRelevanceScore(input1,freqMap3));

    Assert.assertThrows(DatasourceException.class,() -> rvCalc.getRelevanceScore(new MDCInput(null,
        null, null, null),null));
    Assert.assertThrows(DatasourceException.class,() -> rvCalc.getRelevanceScore(null,null));
  }

  @Test
  public void testMapRelevance() throws DatasourceException {
    RelevanceCalculator rvCalc = new RelevanceCalculator();
    String content = rvCalc.readFile(new File("data/testPdfs/TestDoc1.pdf"));
    String content2 = rvCalc.readFile(new File("data/testPdfs/TestDoc2.pdf"));
    String content3 = rvCalc.readFile(new File("data/testPdfs/TestDoc3.pdf"));
    Map<String, List<String>> keywordMap = new HashMap<>();
    List<String> keywordList = ReliabilityCalculator.parseContent("red orange yellow green blue "
        + "indigo violet white");
    List<String> keywordList2 = ReliabilityCalculator.parseContent("america toyota");
    keywordMap.put("color", keywordList);
    keywordMap.put("non-color", keywordList2);

    MDCInput input1 = new MDCInput("1",null,null,keywordMap);
    Map<String, Map<String, Double>> freqMap = rvCalc.calculateTFMap(input1, content);
    Map<String, Map<String, Double>> freqMap2 = rvCalc.calculateTFMap(input1, content2);
    Map<String, Map<String, Double>> freqMap3 = rvCalc.calculateTFMap(input1, content3);

    Assert.assertTrue(rvCalc.getMapRelevanceScore(input1,freqMap).get("color") >
        rvCalc.getMapRelevanceScore(input1,freqMap).get("non-color"));

    Assert.assertTrue(rvCalc.getMapRelevanceScore(input1,freqMap2).get("non-color") >
        rvCalc.getMapRelevanceScore(input1,freqMap2).get("color"));

    Assert.assertEquals(rvCalc.getMapRelevanceScore(input1,freqMap2),
        rvCalc.getMapRelevanceScore(input1,freqMap3));

    Assert.assertThrows(DatasourceException.class,
        () -> rvCalc.getMapRelevanceScore(new MDCInput(null,
        null, null, null),null));
    Assert.assertThrows(DatasourceException.class,
        () -> rvCalc.getMapRelevanceScore(new MDCInput(null,
            null, null, null),freqMap));
    Assert.assertThrows(DatasourceException.class,
        () -> rvCalc.getMapRelevanceScore(new MDCInput(null,
            null, null, new HashMap<>()),null));
    Assert.assertThrows(DatasourceException.class,
        () -> rvCalc.getMapRelevanceScore(new MDCInput(null,
            null, null, new HashMap<>()),freqMap));
  }
}
