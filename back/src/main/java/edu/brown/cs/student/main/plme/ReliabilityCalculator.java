package edu.brown.cs.student.main.plme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * Class that contains the algorithm to get the similarity/reliability scores.
 */
public class ReliabilityCalculator {

  /**
   * Value that determines the weight of differences in phrasing. (a b) vs (b a) will have a 1%
   * difference.
   */
  static final Double wordDistanceFactor = 0.99;

  /**
   * Arbitrary low value that corresponds to minimum reliability. Can be used to see errors in
   * calculations.
   */
  static final Double minimumReliability = -100000.0;

  static final String regex = "\\s+|\\n";
  /**
   * Calculates reliability scores for every string in a keyword map from string to list of strings.
   * @param content String that houses that answer we are looking for (in our case PDFSource
   *                content).
   * @param keywordMap keyword map from string to list of strings.
   * @return Map from the keyword to the max reliability score from the list of strings it mapped
   * to.
   */
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

  /**
   * Calculates reliability scores for every string in a keyword list of strings.
   * @param content String that houses that answer we are looking for (in our case PDFSource
   *                content).
   * @param keywordList keyword list of strings.
   * @return Map from the keyword to its reliability score (double).
   */
  public Map<String, Double> getReliabilityScore(String content, List<String> keywordList) {
    List<String> contentList = parseContent(content);
    Map<String, Double> rScores = new HashMap<>();
    for (String phrase : keywordList) {
      List<String> phraseList = List.of(phrase.split(regex));
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

  /**
   * Parses a string into a list of string. Will split at a new line or space, remove any common
   * punctuation at the ends of a word, and make everything lower case.
   * @param content string to parse.
   * @return parsed list of strings.
   */
  @NotNull
  public static List<String> parseContent(@NotNull String content) {
    List<String> contentList = new ArrayList<>(List.of(content.split(regex)));
    return contentList.stream().map(word -> {
      word = checkPunctuation(word);
      return word.toLowerCase();}).toList();
  }

  /**
   * Removes common punctuation at the ends of words.
   * @param word string.
   * @return converted word.
   */
  @NotNull
  public static String checkPunctuation(String word) {
    if((word.endsWith(".") || word.endsWith("!") || word.endsWith("?") || word.endsWith(":") ||
        word.endsWith(",") || word.endsWith(";") || word.endsWith("\"") || word.endsWith(")") ||
        word.endsWith("*")) && word.length() > 1) {
      word = word.substring(0, word.length() - 1);
    }
    if((word.startsWith("(") || word.startsWith("\"")) && word.length() > 1){
      word = word.substring(1);
    }
    return word;
  }

  /**
   * Uses levenshtein edit distance algorithm to find the minimum edit distance between to words.
   * @param keyword word #1.
   * @param content word #2.
   * @return integer minimum number of edits.
   */
  public int getEditDistance(String keyword, String content) {
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

          d[i][j] = 1 + Math.min(insertion, Math.min(deletion, replacement));
        }
      }
    }
    return d[keyword.length()][content.length()];
  }

  /**
   * Uses the levenshtein edit distance algorithm to determine the minimum number of edits to
   * convert one list of strings to another.
   * @param keyPhrase list #1.
   * @param content list #2.
   * @return integer minimum edit distance.
   */
  public int getPhraseSimilarity(List<String> keyPhrase, List<String> content) {
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

          d[i][j] = 1 + Math.min(insertion, Math.min(deletion, replacement));
        }
      }
    }
    return d[keyPhrase.size()][content.size()];
  }
}