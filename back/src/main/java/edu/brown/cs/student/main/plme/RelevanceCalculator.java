package edu.brown.cs.student.main.plme;

import edu.brown.cs.student.main.records.PLME.MDCInput;
import edu.brown.cs.student.main.exceptions.DatasourceException;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * Class responsible for all the tf-idf calculations for the corpus of files.
 */
public class RelevanceCalculator {
  private int count;
  private final Map<MDCInput, Map<String, Double>> idfMap;

  /**
   * Instantiates the count and idfMap. Called in the this.compile method of MetadataHandler.
   */
  public RelevanceCalculator(){
    this.count = 0;
    this.idfMap = new HashMap<>();
  }

  /**
   * Reads a pdf document and returns its text. Will add to the count to get a total pdf count.
   * @param file pdf.
   * @return pdfContent string.
   * @throws DatasourceException if error parsing the pdf for text. (All EOFExceptions and
   * warnings from the pdfbox are silenced (see Server).
   */
  public String readFile(File file) throws DatasourceException {
    try {
      PDDocument pdDocument = Loader.loadPDF(file);
      PDFTextStripper textStripper = new PDFTextStripper();
      String text = textStripper.getText(pdDocument);
      pdDocument.close();
      this.count++;
      return text;
    } catch (IOException e) {
      throw new DatasourceException("Could not read document to obtain relevance score.");
    }
  }

  /**
   * See calculateTFList. Does the same thing but for a Map from String to List of Strings.
   * @param column MDCInput.
   * @param content string to contain keywords (in this case text from pdf).
   * @return frequency map that maps from a key word to a map of term frequencies from the
   * content in the list of strings it mapped to.
   * @throws DatasourceException if any of the parameters are null.
   */
  public Map<String, Map<String, Double>> calculateTFMap(MDCInput column, String content) throws DatasourceException {
    if (column == null || column.keywordMap() == null){
      throw new DatasourceException("Could not get relevance scores because column or keyword "
          + "Map was null.");
    }
    Map<String, List<String>> keywordMap = column.keywordMap();
    Map<String, Map<String, Double>> frequencyMap = new HashMap<>();
    for (String keyword : keywordMap.keySet()){
      frequencyMap.put(keyword, this.calculateTFList(column, content, keywordMap.get(keyword)));
    }
    return frequencyMap;
  }

  /**
   * Creates a term frequency map for each keyword in a keywordList from the text from a string
   * (content, in this case the text from a pdf file). Also loads the idfMap that maps from the
   * column to the term frequency map to count frequency of documents in the corpus containing
   * the term. (Not case-sensitive and ignores punctuation at the ends of words.)
   * @param column MDCInput.
   * @param content string to contain keywords (in this case text from pdf).
   * @param keywordList list of keywords to search through.
   * @return frequency map that maps from a key word to the frequency it showed up in the content.
   * @throws DatasourceException if any of the parameters are null.
   */
  public Map<String, Double> calculateTFList(MDCInput column, String content,
      List<String> keywordList)
      throws DatasourceException {
    if (column == null){
      throw new DatasourceException("Could not get relevance scores because column was null.");
    } else if (keywordList == null){
      throw new DatasourceException("Could not get relevance scores because keywordList was null.");
    } else if (content == null){
      throw new DatasourceException("Could not get relevance scores because content was null.");
    }
    Map<String, Double> frequencyMap = new HashMap<>();
    List<String> contentList = ReliabilityCalculator.parseContent(content);
    for (String keyword : keywordList) {
      List<String> subwordList = ReliabilityCalculator.parseContent(keyword);
      double frequency = subwordList.stream().mapToDouble(
          subword -> Collections.frequency(contentList, subword)).min().orElse(0);
      this.idfMap.putIfAbsent(column, new HashMap<>());
      this.idfMap.get(column).putIfAbsent(keyword, 0.0);
      if (frequency != 0){
        this.idfMap.get(column).put(keyword, this.idfMap.get(column).get(keyword) + 1);
      }
      frequencyMap.put(keyword, frequency / contentList.size());
    }
    return frequencyMap;
  }

  /**
   * Called to retrieve the final tf-idf values for a map. see this.getRelevanceScore. The
   * keyword corresponds to the MAX tf-idf value in the list of strings pertaining to that key.
   * @param column MDCInput.
   * @param termFrequencies frequency map that maps from a key word to the frequency it showed up in the content.
   * @return map from keyword to tf-idf relevance score.
   * @throws DatasourceException if any of the parameters are null.
   */
  public Map<String, Double> getMapRelevanceScore(MDCInput column, Map<String,
      Map<String, Double>> termFrequencies)
      throws DatasourceException {
    if (column == null || column.keywordMap() == null){
      throw new DatasourceException("Could not get relevance scores because column was null.");
    } else if (termFrequencies == null){
      throw new DatasourceException("Could not get relevance scores because TFMap was null.");
    }

    Map<String, Double> relevanceScores = new HashMap<>();
    for (String keyword : termFrequencies.keySet()){
      double maxTfIdf =
          this.getRelevanceScore(column, termFrequencies.get(keyword)).values().stream()
              .mapToDouble(d -> d).max().orElse(0);
      relevanceScores.put(keyword, maxTfIdf);
    }
    return relevanceScores;
  }

  /**
   * Remaps the given termFrequency map to its tf-idf score.
   * @param column MDCInput.
   * @param termFrequencies
   * @return frequency map that maps from a key word to the frequency it showed up in the content.
   * @throws DatasourceException if any of the parameters are null.
   */
  public Map<String, Double> getRelevanceScore(MDCInput column, Map<String, Double> termFrequencies)
      throws DatasourceException {
    if (column == null){
      throw new DatasourceException("Could not get relevance scores because column was null.");
    } else if (termFrequencies == null){
      throw new DatasourceException("Could not get relevance scores because TFMap was null.");
    } else if (!this.idfMap.containsKey(column)){
      throw new DatasourceException("Column not recognized by IDF map. Could not get RScore.");
    } else if (!this.idfMap.get(column).keySet().containsAll(termFrequencies.keySet())){
      throw new DatasourceException("Keyword lists don't match. Relevance scores could not be "
          + "calculated.");
    }
    Map<String, Double> relevanceScores = new HashMap<>(termFrequencies);
    relevanceScores.replaceAll((t, v) -> termFrequencies.get(t) * this.calculateIDF(column,t));
    return relevanceScores;
  }

  /**
   * Calculates tf-idf score.
   * @param column MDCInput to retrieve the docFrequency from this.idfMap.
   * @param keyword to get score from.
   * @return tf-idf score.
   */
  private Double calculateIDF(MDCInput column, String keyword){
    double docFrequency = this.idfMap.get(column).get(keyword);
    return Math.log((1 + this.count)/(1 + docFrequency)) + 1;
  }
}