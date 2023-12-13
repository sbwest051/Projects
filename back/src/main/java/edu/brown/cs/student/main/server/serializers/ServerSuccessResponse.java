package edu.brown.cs.student.main.server.serializers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * A generic response sent by each handler to indicate that there was a successful request and return the data.
 * @param response_type A string which indicates the request was a success
 * @param input An object depicting what was input to the handler
 * @param data The data that is retrieved by the request.
 */
public record ServerSuccessResponse(String response_type, Object input, Object data) {

  /**
   * Called only by ViewHandler to portray the CSV as a string.
   * @param data The CSV data to be printed as a response.
   */
  public ServerSuccessResponse(Object data) {
    this("success", "none", data);
  }

  /**
   * Called by a handler to create a Success response by creating an instance of the record.
   * @param input The input which directs to the data output
   * @param data The data requested by the user
   */
  public ServerSuccessResponse(Object input, Object data) {
    this("success", input, data);
  }

  /**
   * Creates a Json object that maps Strings to Objects to provide the response to the user.
   * @return a Json object of the created responseMap.
   */
  public String serialize() {
    try {
      Moshi moshi = new Moshi.Builder().build();
      Type mapCSVObject = Types.newParameterizedType(Map.class, String.class, Object.class);
      JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapCSVObject);

      Map<String, Object> responseMap = new HashMap<>();
      responseMap.put("result", response_type);
      responseMap.put("input", input);
      responseMap.put("data", data);
      return adapter.indent("\n").toJson(responseMap);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
}
