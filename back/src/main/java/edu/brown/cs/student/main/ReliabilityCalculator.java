package edu.brown.cs.student.main;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ReliabilityCalculator {
  static final Pattern regexSplitContent =
      Pattern.compile("[^\\W_]+|\\d+|\"(?:[^\"]|(?<=\\\\)\")*\"");

  public ReliabilityCalculator(){}
  public Map<String, Double[]> getReliabilityScore(String content, List<String> keywordList){
    List<String> contentList = List.of(content.split(regexSplitContent.pattern()));
    for (String word : keywordList){

    }
    return null;
  }

}
