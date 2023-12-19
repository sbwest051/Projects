package edu.brown.cs.student.main.plme;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.csv.CSVData;
import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.csv.Parser;
import edu.brown.cs.student.main.csv.creators.InputFileCreator;
import edu.brown.cs.student.main.plme.sources.PDFSource;
import edu.brown.cs.student.main.records.PLME.MDCInput;
import edu.brown.cs.student.main.records.PLME.RScores;
import edu.brown.cs.student.main.records.PLME.request.InputFile;
import edu.brown.cs.student.main.records.PLME.request.PLMEInput;
import edu.brown.cs.student.main.records.PLME.response.File;
import edu.brown.cs.student.main.records.PLME.response.Metadata;
import edu.brown.cs.student.main.records.PLME.response.MetadataTable;
import edu.brown.cs.student.main.exceptions.BadRequestException;
import edu.brown.cs.student.main.exceptions.DatasourceException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import spark.Request;
import spark.Response;
import spark.Route;

public class MetadataHandler implements Route {
  public static final int CHAR_LIMIT = 2500;
  private final PDFSource source;
  public MetadataHandler(PDFSource source){
    this.source = source;
  }

  @Override
  public String handle(Request request, Response response) throws Exception {
    if (!request.contentType().equals("application/json")){
      return new MetadataTable("error", null, null,
          "Body must be of content-type application/json.").serialize();
    }
    PLMEInput input;
    try {
      input = deserialize(request.body());
    } catch (BadRequestException e) {
      return new MetadataTable("error", null, null, "Trouble deserializing json. Json "
          + "maybe ill-formatted. More details: " + e.getMessage()).serialize();
    }

    if (input == null) {
      return new MetadataTable("error", null, null, "Empty json.").serialize();
    }

    if (checkEmptyInput(input)){
      return new MetadataTable("error", input.columns(), null, "Input is missing one or more "
          + "parameters. Input must contain at least one pdf (filepath or url) and a full column"
          + "data with keywords.").serialize();
    }

    if (!checkColumnValidity(input.columns())){
      return new MetadataTable("error", input.columns(), null, "Please limit your questions to a "
          + "maximum of 2500 characters.").serialize();
    }

    if (input.filepath() == null || input.filepath().isEmpty()) {
      return this.compile(input.files(), input.columns()).serialize();
    } else {
      try {
        CSVData<InputFile> fileCSV = new CSVData<>(new Parser<InputFile>());
        fileCSV.setCSV(new FileReader(input.filepath()), new InputFileCreator());
        return this.compile(fileCSV.getCSV(), input.columns()).serialize();
      } catch (IOException | FactoryFailureException e) {
        return new MetadataTable("error", input.columns(), null, "csv not formatted "
            + "correctly." + e.getMessage()).serialize();
      }
    }
  }

  private PLMEInput deserialize(String body) throws BadRequestException {
    try {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<PLMEInput> adapter = moshi.adapter(PLMEInput.class);
      return adapter.fromJson(body);
    } catch (IOException e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  private boolean checkEmptyInput(@NotNull PLMEInput input){
    if (
        (input.filepath() == null || input.filepath().isEmpty())
        && (!this.checkEmptyFiles(input.files()))
            || (input.columns() == null || input.columns().isEmpty())
    ){
      return true;
    }
      for (MDCInput column : input.columns()) {
        if ((column.title() == null || column.title().isEmpty())
            || (column.question() == null || column.question().isEmpty())
            || ((column.keywordMap() == null || column.keywordMap().isEmpty())
            && (column.keywordList() == null || column.keywordList().isEmpty()))) {
          return true;
        }
    }
    return false;

  }

  private boolean checkEmptyFiles(List<InputFile> files){
    if (files == null || files.isEmpty()) {
      return false;
    }
    for (InputFile file : files) {
      boolean filepath = (file.filepath() == null || file.filepath().isEmpty());
      boolean url = (file.url() == null || file.url().isEmpty());
      if (filepath && url) {
        return false;
      }
    }
    return true;
  }

  private boolean checkColumnValidity(@NotNull List<MDCInput> columns){
    for (MDCInput column : columns){
      if (column.question().length() > CHAR_LIMIT){
        return false;
      }
    }
    return true;
  }

  @NotNull
  private MetadataTable compile(List<InputFile> files, List<MDCInput> columns){
    List<File> fileList = new ArrayList<>();
    ReliabilityCalculator raCalc = new ReliabilityCalculator();
    RelevanceCalculator rvCalc = new RelevanceCalculator();
    Map<String, Map<MDCInput, RScores>> rScoreMap = new HashMap<>();

    for (InputFile file : files){
      String fileResult = "success";
      File outputFile = null;

      // Attempts to get sourceID for file based on the path to pdf.
      String sourceId = null;
      try {
        sourceId = this.getSourceID(file);
      } catch (DatasourceException e) {
          fileResult = "error";
          outputFile = new File(fileResult, file.filepath(), file.url(), file.title(), null,
              e.getMessage());
      }

      String pdfContent = null;
      String pdfResult;
      // Attempts to read the file for relevance score
      try {
        if (file.url() == null || file.url().isEmpty()) {
          pdfContent = rvCalc.readFile(new java.io.File(file.filepath()));
          pdfResult = "success";
        } else {
          try {
            pdfContent = rvCalc.readFile(new java.io.File(new URI(file.url())));
            pdfResult = "success";
          } catch (URISyntaxException e) {
            throw new DatasourceException("Could not read document to obtain relevance score.");
          }
        }
      } catch (DatasourceException e) {
        // Error will be reflected in the metadata message.
        pdfResult = "error";
      }

      // If SourceID was successfully made, attempts to obtain the metadata.
      List<Metadata> metadataList = new ArrayList<>();
      if (fileResult.equals("success")){
        String mdResult = "success";
        String rawResponse = null;

        for (MDCInput column : columns){
          Metadata metadata = null;

          // Queries ChatPDF
          try {
            rawResponse = this.source.getContent(sourceId, column.question());
          } catch (DatasourceException e) {
            mdResult = "error";
            metadata = new Metadata(mdResult, null, null, e.getMessage());
          }
          // If ChatPDF successfully responds, gets the reliability score and tf scores and stores
          // it in a temporary data structure. idf scores are computed after all documents are run.
          if (mdResult.equals("success")){
            Map<MDCInput, RScores> ratfMap = new HashMap<>();
            rScoreMap.putIfAbsent(file.title(), ratfMap);
            rScoreMap.get(file.title()).put(column, this.getRaTfScores(column, rawResponse, raCalc,
                rvCalc, pdfContent, pdfResult));

            metadata = new Metadata(mdResult, rawResponse, null, null);
          }
          metadataList.add(metadata);
        }
        outputFile = new File(fileResult, file.filepath(), file.url(), file.title(), metadataList,
            null);
      }
      fileList.add(outputFile);
    }
    return new MetadataTable("success", columns,
        this.calculateRScores(columns, fileList,rScoreMap, rvCalc), null);
  }

  private String getSourceID(InputFile file) throws DatasourceException {
    String sourceId;
    if (file.url() == null || file.url().isEmpty()) {
      sourceId = this.source.addFile(file.filepath());
    } else {
      sourceId = this.source.addURL(file.url());
    }
    if (sourceId == null){
      throw new DatasourceException("SourceId could not be made for the file.");
    }
    return sourceId;
  }

  public RScores getRaTfScores(MDCInput column, String rawResponse, ReliabilityCalculator raCalc,
      RelevanceCalculator rvCalc, String pdfContent, String pdfResult){
    String errText = null;
    Map<String, Double> raMap = null;
    Map<String, Map<String, Double>> tfMap = null;
    Map<String, Double> tfList = null;
    try {
      if (column.keywordList() == null || column.keywordList().isEmpty()){
        raMap = raCalc.getReliabilityScore(rawResponse,column.keywordMap());
        if (pdfResult.equals("success")){
            tfMap = rvCalc.calculateTFMap(column,pdfContent);
        }
      } else {
        raMap = raCalc.getReliabilityScore(rawResponse,column.keywordList());
        if (pdfResult.equals("success")){
          tfList = rvCalc.calculateTFList(column, pdfContent, column.keywordList());
        }
      }
    } catch (DatasourceException e) {
      pdfResult = "error";
      errText = e.getMessage();
    }
    return new RScores(pdfResult, raMap, tfList, tfMap, errText);
  }

  /**
   * Re-loops into the FileList to add all the rScores into the metadata, having the idf values
   * completely calculated.
   * @param columnList
   * @param fileList
   * @param rScoreMap
   * @param rvCalc
   * @return
   */
  public List<File> calculateRScores(List<MDCInput> columnList, List<File> fileList,
      Map<String, Map<MDCInput, RScores>> rScoreMap, RelevanceCalculator rvCalc){
    for (File file: fileList) {
      if (file.result().equals("success")){
        for (int i=0; i < columnList.size(); i++){
          MDCInput column = columnList.get(i);
          if(rScoreMap.containsKey(file.title()) && rScoreMap.get(file.title()).containsKey(column)){
            RScores rScore = rScoreMap.get(file.title()).get(column);
            Map<String, Double> rvMap = null;
            try {
              if(rScore.tfList() == null){
                rvMap = rvCalc.getMapRelevanceScore(column, rScore.tfMap());
              } else {
                rvMap = rvCalc.getRelevanceScore(column, rScore.tfList());
              }
            } catch (DatasourceException e){
              rScoreMap.get(file.title()).replaceAll((col, score) -> new RScores("error",
                  score.reliability(), null, null, e.getMessage()));
            }
            Map<String, Double[]> data = new HashMap<>();
            for (String keyword : rScore.reliability().keySet()) {
              Double[] scores = new Double[2];
              scores[0] = rScore.reliability().get(keyword);
              if(rScore.rvResult().equals("success")){
                scores[1] = rvMap.get(keyword);
              } else {
                scores[1] = ReliabilityCalculator.minimumReliability;
              }
              data.put(keyword, scores);
            }
            Metadata md = file.metadata().get(i);
            file.metadata().set(i, new Metadata(md.result(), md.rawResponse(), data,
                rScore.message()));
          }
        }
      }
    }
    return fileList;
  }
}