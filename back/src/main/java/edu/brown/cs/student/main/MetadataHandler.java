package edu.brown.cs.student.main;

import static spark.Spark.notFound;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.records.ChatPDF.ChatPDFResponse;
import edu.brown.cs.student.main.records.PLME.request.InputFile;
import edu.brown.cs.student.main.records.PLME.request.MDCInput;
import edu.brown.cs.student.main.records.PLME.request.PLMEInput;
import edu.brown.cs.student.main.records.PLME.response.MetadataTable;
import edu.brown.cs.student.main.server.exceptions.BadRequestException;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import edu.brown.cs.student.main.server.serializers.ServerFailureResponse;
import java.io.IOException;
import java.util.List;
import spark.Request;
import spark.Response;
import spark.Route;

public class MetadataHandler implements Route {
  public static final int CHAR_LIMIT = 2500;
  public MetadataHandler(){

  }

  @Override
  public String handle(Request request, Response response) throws Exception {
    if (!request.contentType().equals("application/json")){
      return new ServerFailureResponse("error_bad_request", "Body must be of content-type "
          + "application/json.").serialize();
    }
    PLMEInput input;
    try {
      input = deserialize(request.body());
    } catch (BadRequestException e) {
      return new ServerFailureResponse("error_bad_request", "Trouble deserializing json. Json "
          + "maybe ill-formatted. More details: " + e.getMessage()).serialize();
    }

    if (checkEmptyInput(input)){
      return new ServerFailureResponse("error_bad_request", "Input is missing one or more "
          + "parameters. Input must contain at least one pdf (filepath or url) and a full column "
          + "data with keywords.").serialize();
    }

    if (checkColumnValidity(input.columns())){
      return new ServerFailureResponse("error_bad_request", "Please limit your questions to a "
          + "maximum of 2500 characters.").serialize();
    }

    return null;
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

  private boolean checkEmptyInput(PLMEInput input){
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

  private boolean checkColumnValidity(List<MDCInput> columns){
    for (MDCInput column : columns){
      if (column.question().length() > CHAR_LIMIT){
        return false;
      }
    }
    return true;
  }

  private void delegate(){

  }
}
