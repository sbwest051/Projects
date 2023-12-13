package edu.brown.cs.student.main.server.serializers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * A generic response sent by each handler to indicate that there was an issue when making a request.
 * @param response_type A string which indicates there was an error
 * @param error_type A string indicating what kind of error it was
 * @param details A string describing the error
 */
public record ServerFailureResponse(String response_type, String error_type, String details) {
  /**
   * Called by a handler to create a failure response by creating an instance of the record.
   * @param error_type The type of error the handler wants to provide.
   * @param details Describes the error from the handler.
   */
  public ServerFailureResponse(String error_type, String details) {
    this("error", error_type, details);
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
      responseMap.put("error_type", error_type);
      responseMap.put("details", details);
      return adapter.toJson(responseMap);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
}
