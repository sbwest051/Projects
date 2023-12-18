package edu.brown.cs.student.plme;

import edu.brown.cs.student.main.ReliabilityCalculator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class TestReliabilityCalculator {

  private final ReliabilityCalculator rc;

  public TestReliabilityCalculator(){
    this.rc = new ReliabilityCalculator();
  }

  @Test
  public void testParse() {
    List<String> keywordList = new ArrayList<>();
    keywordList.add("cake123.co");
    keywordList.add("Everyone cake");
    keywordList.add("loves loves");
    keywordList.add("winter wonderland");
    keywordList.add("wonderland winter");

    String content = "Everyone loves cake! Visit www.cake123.com and don't forget about \"winter wonderland\" along with you! (oops) Here is a random quote: nevermind.";
    System.out.println(this.rc.getReliabilityScore(content, keywordList));
  }

  @Test
  public void testBasic() {
    List<String> keywordList = new ArrayList<>();
    keywordList.add("dogs roar");
    keywordList.add("dogs dogs");
    keywordList.add("roar roar");
    keywordList.add("roar dogs");
    keywordList.add("roar that dogs");
    keywordList.add("dogs that roar");
    keywordList.add("ethan");

    String content = "dogs roar";
    System.out.println(this.rc.getReliabilityScore(content, keywordList));
  }

  @Test
  public void testMapBasic() {
    Map<String, List<String>> keywordMap = new HashMap<>();
    List<String> list1 = new ArrayList<>();
    list1.add("ethan");

    List<String> list2 = new ArrayList<>();
    list2.add("dogs roar");
    list2.add("roar dogs");

    List<String> list3 = new ArrayList<>();
    list3.add("dogs that roar");
    list3.add("roar that dog");

    keywordMap.put("one word", list1);
    keywordMap.put("two words", list2);
    keywordMap.put("three words", list3);
    String content = "dogs roar";
    System.out.println(this.rc.getReliabilityScore(content, keywordMap));
  }
}
