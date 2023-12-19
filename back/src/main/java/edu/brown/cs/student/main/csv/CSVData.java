package edu.brown.cs.student.main.csv;

import edu.brown.cs.student.main.csv.creators.CreatorFromRow;
import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created to store the CSV so that it may be accessed by multiple handlers
 * @param parser Takes in a parser object parse the csv into a usable list
 */
public record CSVData<T>(Parser<T> parser) {
  static final List finalList = new ArrayList<>();

  /**
   * Sets the CSV data by calling the parse method of parser to populate finalList
   * @param reader Used to read through the CSV
   * @param creator Turns each line of the CSV into some type of object
   * @throws IOException thrown when there is an issue reading the csv
   * @throws FactoryFailureException thrown when there is an issue creating the objects.
   */
  public void setCSV(Reader reader, CreatorFromRow<T> creator)
      throws IOException, FactoryFailureException {
    this.parser.parse(reader, creator, finalList);
  }

  /**
   * Returns the finalList so that handlers can access the CSV data
   * @return the list created by the CSV
   */
  public List<T> getCSV() {
    return finalList;
  }
}
