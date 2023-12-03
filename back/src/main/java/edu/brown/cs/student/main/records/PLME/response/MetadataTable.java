package edu.brown.cs.student.main.records.PLME.response;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.records.PLME.MDCInput;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public record MetadataTable(List<MDCInput> headers, List<File> fileList) {

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
