package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.CSV.CSVData;
import edu.brown.cs.student.main.server.serializers.ServerFailureResponse;
import edu.brown.cs.student.main.server.serializers.ServerSuccessResponse;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Responsible for handling the request for "viewcsv" and providing the correct response of the CSV
 * data serialized as a JSON object.
 */
public class ViewHandler implements Route {
  private final CSVData data;

  /**
   * Instantiates the data field of ViewHandler to reference the same data that is being used by
   * LoadHandler and SearchHandler
   *
   * @param data An object of CSVData which contains the parsed list of the CSV
   */
  public ViewHandler(CSVData data) {
    this.data = data;
  }

  /**
   * Handles the request for viewcsv and returns a failure response if the data is empty or a
   * success response of the data serialized as a JSON otherwise.
   *
   * @param request
   * @param response
   * @return Either a ServerFailureResponse or a ServerSuccessResponse depending on if the data had
   *     been populated
   * @throws Exception
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    try {
      if (this.data.getCSV().isEmpty()) {
        return new ServerFailureResponse(
                "error_datasource", "Cannot retrieve CSV. CSV may not be loaded.")
            .serialize();
      } else {
        return new ServerSuccessResponse(this.data.getCSV()).serialize();
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
}
