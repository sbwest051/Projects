package edu.brown.cs.student.main.server.serializers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.FeatureCollection;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the response containing a GeoJSON FeatureCollection as the data.
 * @param result will say "success"
 * @param input is an input map of string to object. May contain additional information.
 * @param data is the GeoJson Feature Collection object.
 */
public record FeatureCollectionResponse(String result, Map<String,Object> input,
                                        FeatureCollection data) {
  public FeatureCollectionResponse(FeatureCollection data) {
    this("success", new HashMap<String,Object>(), data);
  }
  public FeatureCollectionResponse(Map<String,Object> input, FeatureCollection data) {
    this("success", input, data);
  }
  /**
   * Creates a Json object out of this record.
   * @return json string.
   */
  public String serialize() {
    try {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<FeatureCollectionResponse> adapter = moshi.adapter(FeatureCollectionResponse.class);
      return adapter.toJson(this);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
}
