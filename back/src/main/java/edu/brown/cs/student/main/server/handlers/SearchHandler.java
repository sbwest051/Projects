package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.CSV.CSVData;
import edu.brown.cs.student.main.CSV.Searcher;
import edu.brown.cs.student.main.server.serializers.ServerFailureResponse;
import edu.brown.cs.student.main.server.serializers.ServerSuccessResponse;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Responsible for handling the request for "searchcsv" and ensuring the correct parameters were
 * provided and sending back a response.
 */
public class SearchHandler implements Route {

  private final CSVData data;

  /**
   * Instantiates the data field of SearchHandler to reference the same data that is being used by
   * ViewHandler and LoadHandler
   *
   * @param data An object of CSVData which contains the parsed list of the CSV
   */
  public SearchHandler(CSVData data) {
    this.data = data;
  }

  /**
   * Handles the request for searchcsv and returns a failure response if the parameters are invalid
   * or if the column isn't in the csv and a success response if a result is found.
   *
   * @param request
   * @param response
   * @return Either a ServerFailureResponse or a ServerSuccessResponse if the CSV file is able to be
   *     searched. It is a success no matter if the keyword is found.
   * @throws Exception
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    String hasHeaders = request.queryParams("hasHeaders");
    String columnID = request.queryParams("columnID");
    String keyword = request.queryParams("keyword");

    if (hasHeaders == null) {
      return new ServerFailureResponse(
              "error_bad_request", "Whether the CSV file has headers must be specified.")
          .serialize();
    } else if (keyword == null) {
      return new ServerFailureResponse("error_bad_request", "No keyword to search was requested")
          .serialize();
    } else if (this.data.getCSV().isEmpty()) {
      return new ServerFailureResponse(
              "error_datasource", "Cannot retrieve CSV. CSV may not be loaded.")
          .serialize();
    }
    Boolean hasHeadersBoolean = Boolean.parseBoolean(hasHeaders);
    Searcher searcher = new Searcher(this.data.getCSV(), hasHeadersBoolean);

    Map<String, Object> inputMap = new HashMap<>();
    inputMap.put("hasHeaders", hasHeaders);
    inputMap.put("keyword", keyword);
    if (columnID == null) {
      return new ServerSuccessResponse(inputMap, searcher.searchAll(keyword)).serialize();
    } else {
      try {
        inputMap.put("columnID", columnID);
        return new ServerSuccessResponse(inputMap, searcher.searchColumn(keyword, columnID))
            .serialize();
      } catch (IllegalArgumentException e) {
        return new ServerFailureResponse(
                "error_bad_request", "columnID = " + columnID + " was not found or out of index.")
            .serialize();
      }
    }
  }
}
