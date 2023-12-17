package edu.brown.cs.student.main;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.CSV.CSVData;
import edu.brown.cs.student.main.CSV.Parser;
import edu.brown.cs.student.main.CSV.creators.InputFileCreator;
import edu.brown.cs.student.main.records.PLME.MDCInput;
import edu.brown.cs.student.main.records.PLME.RScores;
import edu.brown.cs.student.main.records.PLME.request.InputFile;
import edu.brown.cs.student.main.records.PLME.request.PLMEInput;
import edu.brown.cs.student.main.records.PLME.response.File;
import edu.brown.cs.student.main.records.PLME.response.Metadata;
import edu.brown.cs.student.main.records.PLME.response.MetadataTable;
import edu.brown.cs.student.main.server.exceptions.BadRequestException;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
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
          "Body must be of content-type" + "application/json.").serialize();
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
    if ((input.filepath() == null || input.filepath().isEmpty())
            && (this.checkEmptyFiles(input.files()))
            && (input.columns() == null || input.columns().isEmpty())){
      return true;
    }
    for (MDCInput column : input.columns()) {
      if ((column.title() == null || column.title().isEmpty())
      || (column.question() == null || column.question().isEmpty())
      || ((column.keywordMap() == null || column.keywordMap().isEmpty())
          && (column.keywordList() == null || column.keywordList().isEmpty()))){
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
      boolean filepath = file.filepath() == null || file.filepath().isEmpty();
      boolean url = file.url() == null || file.url().isEmpty();
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
    RelevanceCalculator rvCalc = new RelevanceCalculator();
    Map<MDCInput, RScores> rScoreMap = new HashMap<>();

    for (InputFile file : files){
      String result = "success";
      List<Metadata> metadataList = new ArrayList<>();

      // Attempts to get sourceID for file based on the path to pdf.
      // Will also attempt to read PDF now to get relevance score later.
      String sourceId = null;
      String pdfContent = null;
      String pdfResult = "error";
      try {
        if (file.url() == null || file.url().isEmpty()) {
          sourceId = this.source.addFile(file.filepath());
        } else {
          sourceId = this.source.addURL(file.url());
        }
        if (sourceId == null){
          throw new DatasourceException("SourceId could not be made for the file.");
        }
      } catch (DatasourceException e) {
          result = "error";
          File outputFile = new File(result, file.filepath(), file.url(), file.title(), null,
              e.getMessage());
          fileList.add(outputFile);
      }

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
        pdfResult = "error";
      }

      if (result.equals("success")){
        String subresult = "success";
        String content = null;

        // If SourceID was successfully made, attempts to obtain the metadata.
        for (MDCInput column : columns){
          Metadata metadata = null;

          // Queries ChatPDF
          try {
            content = this.source.getContent(sourceId, column.question());
          } catch (DatasourceException e) {
            subresult = "error";
            metadata = new Metadata(subresult, null, null, e.getMessage());
          }

          // If ChatPDF successfully responds, gets the reliability score and tf scores and stores
          // it in a temporary data structure. idf scores are computed after all documents are run.
          String message = null;
          Map<String, Double> reliability = null;
          ReliabilityCalculator reliabilityCalc = new ReliabilityCalculator();
          if (subresult.equals("success")){
            if (column.keywordList() == null || column.keywordList().isEmpty()){
              reliability = reliabilityCalc.getReliabilityScore(content,column.keywordMap());
              Map<String, Map<String, Double>> tfMap = null;
              if (pdfResult.equals("success")){
                try {
                  tfMap = rvCalc.calculateTFMap(column,pdfContent);
                } catch (DatasourceException e) {
                  pdfResult = "error";
                  message = e.getMessage();
                }
              }
              rScoreMap.put(column, new RScores(pdfResult,reliability,null, tfMap, message));
            } else {
              reliability = reliabilityCalc.getReliabilityScore(content,column.keywordList());
              Map<String, Double> tfList = null;
              if (pdfResult.equals("success")){
                try {
                  tfList = rvCalc.calculateTFList(column, pdfContent, column.keywordList());
                } catch (DatasourceException e) {
                  pdfResult = "error";
                  message = e.getMessage();
                }
              }
              rScoreMap.put(column, new RScores(pdfResult,reliability,tfList, null, message));
            }
            metadata = new Metadata(subresult, content, null, null);
          }
          metadataList.add(metadata);
        }
      }
      File outputFile = new File(result, file.filepath(), file.url(), file.title(), metadataList,
          null);
      fileList.add(outputFile);
    }
    List<File> finalList = this.calculateRScores(columns, fileList,rScoreMap, rvCalc);
    return new MetadataTable("success", columns, finalList, null);
  }

  public List<File> calculateRScores(List<MDCInput> columnList, List<File> fileList,
      Map<MDCInput, RScores> rScoreMap, RelevanceCalculator rvCalc){
    for (File file: fileList) {

      if (file.result().equals("success")){
        for (int i=0; i < columnList.size(); i++){

          MDCInput column = columnList.get(i);
          if(rScoreMap.containsKey(column)){
            RScores rScore = rScoreMap.get(column);
            Map<String, Double> rvMap = null;
            try {
              if(rScore.tfList() == null){
                rvMap = rvCalc.getMapRelevanceScore(column, rScore.tfMap());
              } else {
                rvMap = rvCalc.getRelevanceScore(column, rScore.tfList());
              }
            } catch (DatasourceException ignored){
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

