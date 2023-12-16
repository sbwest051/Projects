package edu.brown.cs.student.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

public class ReliabilityCalculator {
  static final Double wordDistanceFactor = 0.99;
  static final Double minimumReliability = -100000.0;

  public Map<String, Double> getReliabilityScore(String content,
      Map<String, List<String>> keywordMap){
    Map<String, Double> rScores = new HashMap<>();
    for (String item : keywordMap.keySet()){
      rScores.put(item,
          this.getReliabilityScore(content, keywordMap.get(item)).values()
              .stream().mapToDouble(Double::doubleValue).max().orElse(minimumReliability));
    }
    return rScores;
  }

  public Map<String, Double> getReliabilityScore(String content, List<String> keywordList) {
    List<String> contentList = parseContent(content);
    System.out.println(contentList);
    Map<String, Double> rScores = new HashMap<>();
    for (String phrase : keywordList) {
      List<String> phraseList = List.of(phrase.split(" "));
      List<Double> phraseScoreList = new ArrayList<>();
      List<String> indexList = new ArrayList<>();

      for (String word : phraseList.stream().distinct().toList()) {
        double rScore = minimumReliability;
        String index = null;

        for (String contentWord : contentList.stream().distinct().toList()) {
          double tempRScore =
              1 - ((double) this.getEditDistance(word, contentWord) / ((double) (word.length() +
                  contentWord.length()) / 2));
          if (tempRScore > rScore) {
            rScore = tempRScore;
            index = contentWord;
          }
        }
        phraseScoreList.add(rScore);
        indexList.add(index);
      }
      double avgScore =
          phraseScoreList.stream().mapToDouble(d -> d).average().orElse(minimumReliability);
      avgScore = avgScore * Math.pow(wordDistanceFactor, this.getPhraseSimilarity(indexList,
          contentList));
      rScores.put(phrase, avgScore);
    }
    return rScores;
  }

  @NotNull
  private static List<String> parseContent(String content) {
    List<String> contentList = new ArrayList<>(List.of(content.split(" ")));
    for (int i = 0; i < contentList.size(); i++) {
      String word = contentList.get(i);
      if((word.endsWith(".") || word.endsWith("!") || word.endsWith("?") || word.endsWith(":") ||
          word.endsWith(",") || word.endsWith(";") || word.endsWith("\"") || word.endsWith(")") ||
          word.endsWith("*")) && word.length() > 1) {
        word = word.substring(0, word.length() - 1);
      }
      if((word.startsWith("(") || word.startsWith("\"")) && word.length() > 1){
        word = word.substring(1);
      }
      contentList.set(i, word.toLowerCase());
    }
    return contentList;
  }

  private int getEditDistance(String keyword, String content) {
    int[][] d = new int[keyword.length() + 1][content.length() + 1];

    for (int i = 0; i <= keyword.length(); i++) {
      d[i][0] = i;
    }

    for (int j = 0; j <= content.length(); j++) {
      d[0][j] = j;
    }

    int insertion, deletion, replacement;
    for (int i = 1; i <= keyword.length(); i++) {
      for (int j = 1; j <= content.length(); j++) {
        if (keyword.toLowerCase().charAt(i - 1) == (content.charAt(j - 1)))
          d[i][j] = d[i - 1][j - 1];
        else {
          insertion = d[i][j - 1];
          deletion = d[i - 1][j];
          replacement = d[i - 1][j - 1];

          d[i][j] = 1 + findMin(insertion, deletion, replacement);
        }
      }
    }
    return d[keyword.length()][content.length()];
  }

  private int findMin(int x, int y, int z) {
    if (x <= y && x <= z)
      return x;
    if (y <= x && y <= z)
      return y;
    else
      return z;
  }

  private int getPhraseSimilarity(List<String> keyPhrase, List<String> content) {
    int[][] d = new int[keyPhrase.size() + 1][content.size() + 1];

    for (int i = 0; i <= keyPhrase.size(); i++) {
      d[i][0] = i;
    }

    for (int j = 0; j <= content.size(); j++) {
      d[0][j] = j;
    }

    int insertion, deletion, replacement;
    for (int i = 1; i <= keyPhrase.size(); i++) {
      for (int j = 1; j <= content.size(); j++) {
        if (keyPhrase.get(i-1).equals(content.get(j-1)))
          d[i][j] = d[i - 1][j - 1];
        else {
          insertion = d[i][j - 1];
          deletion = d[i - 1][j];
          replacement = d[i - 1][j - 1];

          d[i][j] = 1 + findMin(insertion, deletion, replacement);
        }
      }
    }
    return d[keyPhrase.size()][content.size()];
  }
}