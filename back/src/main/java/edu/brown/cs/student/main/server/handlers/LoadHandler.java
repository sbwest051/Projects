package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.CSV.CSVData;
import edu.brown.cs.student.main.server.serializers.ServerFailureResponse;
import edu.brown.cs.student.main.server.serializers.ServerSuccessResponse;
import edu.brown.cs.student.main.FactoryFailureException;
import edu.brown.cs.student.main.CSV.creators.ListCreator;
import java.io.FileReader;
import java.io.IOException;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Responsible for handling the request for "loadcsv" and ensuring the provided file reference is
 * valid and sending a response if it is invalid or if the file is loaded
 */
public class LoadHandler implements Route {

  private final CSVData data;

  /**
   * Instantiates the data field of LoadHandler to reference the same data that is being used by
   * ViewHandler and SearchHandler
   *
   * @param data An object of CSVData which contains the parsed list of the CSV
   */
  public LoadHandler(CSVData data) {
    this.data = data;
  }

  /**
   * Handles the request for loadcsv and returns a failure response if the filepath is invalid or
   * cant be read and a success response if the file is correctly parsed.
   *
   * @param request
   * @param response
   * @return Either a ServerFailureResponse or a ServerSuccessResponse depending on if the file is
   *     parsed or not.
   * @throws Exception
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    String filePath = request.queryParams("filepath");
    if (filePath == null) {
      return new ServerFailureResponse("error_bad_request", "No filepath found.").serialize();
    }
    char[] dataPathArray = "data/".toCharArray();
    if (filePath.toCharArray().length < 5) {
      return new ServerFailureResponse(
              "error_bad_request",
              "File must be in the data folder and filepath= "
                  + filePath
                  + " "
                  + "must start with /data (use content or repository root).")
          .serialize();
    } else {
      char[] filePathArray = filePath.toCharArray();
      for (int i = 0; i < 5; i++) {
        if (filePathArray[i] != dataPathArray[i]) {
          return new ServerFailureResponse(
                  "error_bad_request",
                  "File must be in the data folder and filepath= "
                      + filePath
                      + " "
                      + "must start with /data (use content or repository root).")
              .serialize();
        }
      }
    }
    try {
      this.data.setCSV(new FileReader(filePath), new ListCreator());
    } catch (IOException e) {
      return new ServerFailureResponse(
              "error_bad_request", "File " + filePath + " could not be found or read.")
          .serialize();
    } catch (FactoryFailureException e) {
      return new ServerFailureResponse("error_bad_request", e.getMessage()).serialize();
    }
    return new ServerSuccessResponse(filePath, "none").serialize();
  }
}
