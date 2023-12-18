package edu.brown.cs.student.main;

import edu.brown.cs.student.main.records.PLME.MDCInput;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class RelevanceCalculator {
  private int count;
  private final Map<MDCInput, Map<String, Double>> idfMap;
  public RelevanceCalculator(){
    this.count = 0;
    this.idfMap = new HashMap<>();
  }
  public Map<String, Map<String, Double>> calculateTFMap(MDCInput column, String content) throws DatasourceException {
    Map<String, List<String>> keywordMap = column.keywordMap();
    Map<String, Map<String, Double>> frequencyMap = new HashMap<>();
    for (String keyword : keywordMap.keySet()){
      frequencyMap.put(keyword, this.calculateTFList(column, content, keywordMap.get(keyword)));
    }
    return frequencyMap;
  }

  public Map<String, Double> calculateTFList(MDCInput column, String content,
      List<String> keywordList)
      throws DatasourceException {
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

  public Map<String, Double> getMapRelevanceScore(MDCInput column, Map<String,
      Map<String, Double>> termFrequencies)
      throws DatasourceException {
    Map<String, Double> relevanceScores = new HashMap<>();
    for (String keyword : termFrequencies.keySet()){
      double maxTfIdf =
          this.getRelevanceScore(column, termFrequencies.get(keyword)).values().stream()
              .mapToDouble(d -> d).max().orElse(0);
      relevanceScores.put(keyword, maxTfIdf);
    }
    System.out.println(relevanceScores);
    return relevanceScores;
  }

  public Map<String, Double> getRelevanceScore(MDCInput column, Map<String, Double> termFrequencies)
      throws DatasourceException {
    if (!this.idfMap.get(column).keySet().containsAll(termFrequencies.keySet())){
      throw new DatasourceException("Keyword lists don't match. Relevance scores could not be "
          + "calculated.");
    }
    termFrequencies.replaceAll((t, v) -> termFrequencies.get(t) * this.calculateIDF(column,t));
    return termFrequencies;
  }

  private Double calculateIDF(MDCInput column, String keyword){
    double docFrequency = this.idfMap.get(column).get(keyword);
    return Math.log((1 + this.count)/(1 + docFrequency)) + 1;
  }
}