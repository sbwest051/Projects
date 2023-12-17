package edu.brown.cs.student.main;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.CSV.CSVData;
import edu.brown.cs.student.main.CSV.Parser;
import edu.brown.cs.student.main.CSV.creators.InputFileCreator;
import edu.brown.cs.student.main.records.PLME.MDCInput;
import edu.brown.cs.student.main.records.PLME.request.InputFile;
import edu.brown.cs.student.main.records.PLME.request.PLMEInput;
import edu.brown.cs.student.main.records.PLME.response.File;
import edu.brown.cs.student.main.records.PLME.response.Metadata;
import edu.brown.cs.student.main.records.PLME.response.MetadataTable;
import edu.brown.cs.student.main.server.exceptions.BadRequestException;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Contract;
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
  @Contract("_, _ -> new")
  private MetadataTable compile(List<InputFile> files, List<MDCInput> columns){
    List<File> fileList = new ArrayList<>();
    for (InputFile file : files){
      String result = "success";
      List<Metadata> metadataList = new ArrayList<>();

      String sourceId = null;
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
      if (result.equals("success")){
        String subresult = "success";
        String content = null;
        Map<String, Double[]> data = null;
        for (MDCInput column : columns){
          Metadata metadata = null;
          try {
            content = this.source.getContent(sourceId, column.question());
          } catch (DatasourceException e) {
            subresult = "error";
            metadata = new Metadata(subresult, null, null, e.getMessage());
          }
          if (subresult.equals("success")){
            if (column.keywordList() == null || column.keywordList().isEmpty()){
              data = this.calculateRScores(content, column.keywordMap());
            } else {
              data = this.calculateRScores(content, column.keywordList());
            }
            metadata = new Metadata(subresult, content, data, null);
          }
          metadataList.add(metadata);
        }
      }
      File outputFile = new File(result, file.filepath(), file.url(), file.title(), metadataList,
          null);
      fileList.add(outputFile);
    }
    return new MetadataTable("success", columns, fileList, null);
  }

  public Map<String, Double[]> calculateRScores(String content, List<String> keywordList){
    return null;
  }

  public Map<String, Double[]> calculateRScores(String content,
      Map<String, List<String>> keywordMap){
    return null;
  }
}
