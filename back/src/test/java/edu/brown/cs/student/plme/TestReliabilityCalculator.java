package edu.brown.cs.student.plme;

import edu.brown.cs.student.main.plme.ReliabilityCalculator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

public class TestReliabilityCalculator {
  private final ReliabilityCalculator rc;
  public TestReliabilityCalculator(){
    this.rc = new ReliabilityCalculator();
  }

  @Test
  public void testParse() {
    List<String> keywordList = new ArrayList<>();
    keywordList.add("everyone");
    keywordList.add("loves");
    keywordList.add("cake");
    keywordList.add("visit");
    keywordList.add("www.cake123.com");
    keywordList.add("and");
    keywordList.add("don't");
    keywordList.add("forget");
    keywordList.add("about");
    keywordList.add("winter");
    keywordList.add("wonderland");
    keywordList.add("along");
    keywordList.add("with");
    keywordList.add("you");
    keywordList.add("oops");
    keywordList.add("here");
    keywordList.add("is");
    keywordList.add("a");
    keywordList.add("random");
    keywordList.add("quote");
    keywordList.add("nevermind");


    String content = "Everyone loVes\tcake! Visit? www.cake123.com and; don't forget about "
        + "\"winter wonderland\" along with you. \n \n (oops) Here is a random quote: nevermind.";
    Assert.assertEquals(ReliabilityCalculator.parseContent(content), keywordList);
  }

  @Test
  public void testGetEditDistance() {
    // Identity
    Assert.assertEquals(this.rc.getEditDistance("qwertyuiopasdfghjklzxcvbnm",
        "qwertyuiopasdfghjklzxcvbnm"), 0);
    Assert.assertEquals(this.rc.getEditDistance("abice`1234567890-=[];',./   ",
        "abice`1234567890-=[];',./   "), 0);

    // Substitutions
    Assert.assertEquals(this.rc.getEditDistance("a","b"), 1);
    Assert.assertEquals(this.rc.getEditDistance("aa","bb"), 2);
    Assert.assertEquals(this.rc.getEditDistance("a ","b!"), 2);
    Assert.assertEquals(this.rc.getEditDistance("abcdefghijk","lmnopqrstuv"), 11);

    // Deletions
    Assert.assertEquals(this.rc.getEditDistance("a",""), 1);
    Assert.assertEquals(this.rc.getEditDistance("aa",""), 2);
    Assert.assertEquals(this.rc.getEditDistance("ab","b"), 1);
    Assert.assertEquals(this.rc.getEditDistance("     ",""), 5);

    // Insertions
    Assert.assertEquals(this.rc.getEditDistance("","1234567890"), 10);
    Assert.assertEquals(this.rc.getEditDistance("a","abcde"), 4);
    Assert.assertEquals(this.rc.getEditDistance("blake","blakefield"), 5);

    // Mixes
    Assert.assertEquals(this.rc.getEditDistance("boat","tab"), 3);
    Assert.assertEquals(this.rc.getEditDistance("eproproped","rope"), 6);
    Assert.assertEquals(this.rc.getEditDistance("orange","angular"), 6);
    Assert.assertEquals(this.rc.getEditDistance("abc1!@#","1!@#abc"), 6);
  }

  @Test
  public void testGetPhraseSimilarity(){
    List<String> a = new ArrayList<>();
    List<String> b = new ArrayList<>();

    //Substitution
    a.add("Word 1");
    b.add("Word 1 different");
    Assert.assertEquals(this.rc.getPhraseSimilarity(a, b), 1);

    a.add("Word 2");
    b.add("Word 2");
    Assert.assertEquals(this.rc.getPhraseSimilarity(a, b), 1);

    b.replaceAll(String::toLowerCase);
    Assert.assertEquals(this.rc.getPhraseSimilarity(a, b), 2);

    a.clear();
    b.clear();
    a.add("everyone");
    a.add("loves");
    a.add("cake");
    a.add("visit");
    a.add("www.cake123.com");
    a.add("and");
    a.add("don't");
    a.add("forget");
    b.add("about");
    b.add("winter");
    b.add("wonderland");
    b.add("along");
    b.add("with");
    b.add("you");
    b.add("oops");
    b.add("here");
    Assert.assertEquals(this.rc.getPhraseSimilarity(a, b), 8);

    // Insertion and deletion
    a.clear();
    b.clear();

    a = ReliabilityCalculator.parseContent("I have to pee really bad.");
    b = ReliabilityCalculator.parseContent("");
    Assert.assertEquals(this.rc.getPhraseSimilarity(a,b), 6);
    Assert.assertEquals(this.rc.getPhraseSimilarity(b,a), 6);

    // Mix
    a = ReliabilityCalculator.parseContent("Laugh out loud");
    b = ReliabilityCalculator.parseContent("Loud out laugh");
    Assert.assertEquals(this.rc.getPhraseSimilarity(a,b), 2);
    Assert.assertEquals(this.rc.getPhraseSimilarity(b,a), 2);

    a = ReliabilityCalculator.parseContent("there is no way you get this right");
    b = ReliabilityCalculator.parseContent("right away there was no way");
    Assert.assertEquals(this.rc.getPhraseSimilarity(a,b), 7);
    Assert.assertEquals(this.rc.getPhraseSimilarity(b,a), 7);

    a = ReliabilityCalculator.parseContent("I have 4 reasons to quit.");
    b = ReliabilityCalculator.parseContent("Peppa pig likes to have boba!");
    Assert.assertEquals(this.rc.getPhraseSimilarity(a,b), 6);
    Assert.assertEquals(this.rc.getPhraseSimilarity(b,a), 6);
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
    Assert.assertEquals(this.rc.getReliabilityScore(content, keywordMap).get("two words"), 1.0);
    Assert.assertTrue(this.rc.getReliabilityScore(content, keywordMap).get("one word")
      < this.rc.getReliabilityScore(content, keywordMap).get("two words"));
    Assert.assertTrue(this.rc.getReliabilityScore(content, keywordMap).get("three words")
        < this.rc.getReliabilityScore(content, keywordMap).get("two words"));
    Assert.assertTrue(this.rc.getReliabilityScore(content, keywordMap).get("one word")
        < this.rc.getReliabilityScore(content, keywordMap).get("three words"));
  }

  @Test
  public void testListBasic() {
    String content = "iris";
    List<String> list1 = new ArrayList<>();
    list1.add("etha");
    list1.add("iris");
    list1.add("irishm");

    Assert.assertEquals(this.rc.getReliabilityScore(content, list1).get("etha"), 0.0);
    Assert.assertEquals(this.rc.getReliabilityScore(content, list1).get("iris"), 1.0);
    Assert.assertEquals(this.rc.getReliabilityScore(content, list1).get("irishm"), 0.6);

    content = "a b c d e";
    list1.clear();
    list1.add("");
    list1.add("a");
    list1.add("a b");
    list1.add("b a");
    list1.add("a b c");
    list1.add("a b c d");
    list1.add("a b c d e");

    Assert.assertTrue(this.rc.getReliabilityScore(content, list1).get("") < 0);
    Assert.assertTrue(this.rc.getReliabilityScore(content, list1).get("a")
        < this.rc.getReliabilityScore(content, list1).get("a b"));
    Assert.assertTrue(this.rc.getReliabilityScore(content, list1).get("a b")
        < this.rc.getReliabilityScore(content, list1).get("a b c"));
    Assert.assertTrue(this.rc.getReliabilityScore(content, list1).get("a b c")
        < this.rc.getReliabilityScore(content, list1).get("a b c d"));
    Assert.assertTrue(this.rc.getReliabilityScore(content, list1).get("b a")
        < this.rc.getReliabilityScore(content, list1).get("a b"));
    Assert.assertEquals(this.rc.getReliabilityScore(content, list1).get("a b c d e"), 1.0);
  }
}
