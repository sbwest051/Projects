package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.server.serializers.ServerFailureResponse;
import edu.brown.cs.student.main.server.serializers.ServerSuccessResponse;
import edu.brown.cs.student.main.server.exceptions.BadRequestException;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import edu.brown.cs.student.main.server.sources.APISource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Responsible for handling the request for "broadband" and ensuring the correct parameters were
 * provided and sending back a response.
 */
public class BroadbandHandler implements Route {
  private final APISource state;
  private boolean stateSet;

  /**
   * Instantiates the state field of BroadbandHandler with an object of ACSAPISource which is
   * responsible for connecting to the API and retrieving data from it. Also instantiates stateSet
   * which indicates if the state codes have been stored already and if it needs to happen again.
   *
   * @param state an object of ACSAPISource responsible for dealing with the API
   */
  public BroadbandHandler(APISource state) {
    this.state = state;
    this.stateSet = false;
  }

  /**
   * Handles the request for broadband and returns a failure response if the parameters are invalid
   * or if state throws a Datasource Exception due to issues with dealing with the API.
   *
   * @param request
   * @param response
   * @return Either a ServerFailureResponse or a ServerSuccessResponse if the percentage is found
   * @throws Exception
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    String state = request.queryParams("state");
    String county = request.queryParams("county");
    if (!this.stateSet) {
      try {
        this.state.setStateCodes();
        this.stateSet = true;
      } catch (DatasourceException e) {
        return new ServerFailureResponse("error_datasource", e.getMessage()).serialize();
      }
    }
    if (state == null) {
      return new ServerFailureResponse("error_bad_request", "No state to search was requested.")
          .serialize();
    } else if (county == null) {
      return new ServerFailureResponse("error_bad_request", "No county to search was requested.")
          .serialize();
    } else {
      try {
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("Request date/time", new Date().toString());
        inputMap.put("State", state);
        inputMap.put("County", county);

        String[] broadbandResult = this.state.getCountyBroadband(state, county);
        return new ServerSuccessResponse(
                inputMap,
                broadbandResult[0] + " had " + broadbandResult[1] + "% of houses with broadband.")
            .serialize();

      } catch (DatasourceException e) {
        return new ServerFailureResponse("error_datasource", e.getMessage()).serialize();
      } catch (BadRequestException e) {
        return new ServerFailureResponse("error_bad_request", e.getMessage()).serialize();
      }
    }
  }
}
