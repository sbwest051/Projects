package edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.FeatureCollection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Class that deserializes a FeatureCollection JSON into an FeatureCollection object.
 */
public class JSONParser {
  private FeatureCollection featureCollection;

  /**
   * Will automatically parse the fullDownload.json to prevent errors.
   * @throws IOException
   */
  public JSONParser() throws IOException {
    this.fromJSON("data/fullDownload.json");
  }

  /**
   * Deserializes a FeatureCollection json file.
   * @param filePath filepath containing FeatureCollection json.
   * @throws IOException
   */
  public void fromJSON(String filePath) throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    try {
      String jsonString = (new BufferedReader(new FileReader(filePath))).readLine();
      this.featureCollection = moshi.adapter(FeatureCollection.class).fromJson(jsonString);
    } catch (IOException e) {
      throw e;
    }
  }

  /**
   * Getter method for the featureCollection
   * @return FeatureCollection
   */
  public FeatureCollection getFeatureCollection() {
    return this.featureCollection;
  }
}
