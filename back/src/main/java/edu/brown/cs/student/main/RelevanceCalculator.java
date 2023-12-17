package edu.brown.cs.student.main;

import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jetbrains.annotations.NotNull;

public class RelevanceCalculator {
  private int count;
  private final Map<String, Double> idfMap;
  public RelevanceCalculator(){
    this.count = 0;
    this.idfMap = new HashMap<>();
  }
  public Map<String, Map<String, Double>> calculateTF(File file,
      Map<String, List<String>> keywordMap) throws DatasourceException {
    Map<String, Map<String, Double>> frequencyMap = new HashMap<>();
    for (String keyword : keywordMap.keySet()){
      frequencyMap.put(keyword, this.calculateTF(file, keywordMap.get(keyword)));
    }
    return frequencyMap;
  }
  public Map<String, Double> calculateTF(File file, List<String> keywordList)
      throws DatasourceException {
    String content = this.readFile(file);
    Map<String, Double> frequencyMap = new HashMap<>();
    List<String> contentList = ReliabilityCalculator.parseContent(content);
    for (String keyword : keywordList) {
      List<String> subwordList = ReliabilityCalculator.parseContent(keyword);
      double frequency = subwordList.stream().mapToDouble(
          subword -> Collections.frequency(contentList, subword)).min().orElse(0);
      this.idfMap.putIfAbsent(keyword, 0.0);
      if (frequency != 0){
        this.idfMap.put(keyword, this.idfMap.get(keyword) + 1);
      }
      frequencyMap.put(keyword, frequency / contentList.size());
    }
    return frequencyMap;
  }

  private String readFile(File file) throws DatasourceException {
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

  public Map<String, Double> getMapRelevanceScore(Map<String, Map<String, Double>> termFrequencies)
      throws DatasourceException {
    if (!this.idfMap.keySet().containsAll(termFrequencies.keySet())){
      throw new DatasourceException("Keyword lists don't match. Relevance scores could not be "
          + "calculated.");
    }
    Map<String, Double> relevanceScores = new HashMap<>();
    for (String keyword : termFrequencies.keySet()){
      double maxTfIdf = this.getRelevanceScore(termFrequencies.get(keyword)).values().stream()
          .mapToDouble(d -> d).max().orElse(0);
      relevanceScores.put(keyword, maxTfIdf);
    }
    return relevanceScores;
  }

  public Map<String, Double> getRelevanceScore(Map<String, Double> termFrequencies)
      throws DatasourceException {
    if (!this.idfMap.keySet().containsAll(termFrequencies.keySet())){
      throw new DatasourceException("Keyword lists don't match. Relevance scores could not be "
          + "calculated.");
    }
    termFrequencies.replaceAll((t, v) -> termFrequencies.get(t) * this.calculateIDF(t));
    return termFrequencies;
  }

  private Double calculateIDF(String keyword){
    double docFrequency = this.idfMap.get(keyword);
    return Math.log((1 + this.count)/(1 + docFrequency)) + 1;
  }
}
