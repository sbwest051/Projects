package edu.brown.cs.student.main.records.PLME.response;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.records.PLME.MDCInput;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * The response object for MetadataHandler. Contains all relevant information with metadata for
 * each question and each file inputted.
 * @param result either "success" or "error" to denote whether there was a problem retrieving any
 *              of the data.
 * @param headers List of MDCInputs, functioning as the headers for the columns of the table.
 * @param fileList List of Files, containing all of its metadata values.
 * @param message Error message if there was a problem retrieving data.
 */
public record MetadataTable(String result, List<MDCInput> headers, List<File> fileList,
                            String message) {

  /**
   * Embedded method to serialize the object into a json.
   * @return String serialized json.
   */
  public String serialize(){
    try {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<MetadataTable> adapter = moshi.adapter(MetadataTable.class);
      return adapter.toJson(this);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
}
