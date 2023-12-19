package edu.brown.cs.student.main.csv;

import edu.brown.cs.student.main.csv.creators.CreatorFromRow;
import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

/**
 * Class is where a CSV in some form via a Reader object will be parsed and converted into a passed
 * in parameter via a CreatorFromRow<T></T>. Final product will be an List<T></T>.
 *
 * @param <T> is the desired object type for the creator to convert the CSV data into.
 */
public class Parser<T> {
  static final Pattern regexSplitCSVRow =
      Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");

  /**
   * Instantiates an instance of the parser object to be used to parse CSV data.
   * @throws IOException if error with the Reader class (will usually be passed if invalid filepath
   *     is inputted.)
   */
  public Parser() throws IOException, FactoryFailureException {}

  /**
   * Called in CSVData.
   * Will wrap the passed in reader into a BufferedReader which will systematically parse with
   * regexSplitCSVRow into an ArrayList of strings. Will then call the passed in creator to create
   * the desired objects with each List<String></String>. If there is an error with the conversion
   * in the creator, the method will catch a FactoryFailureException and print an error message,
   * specifying the row.
   *
   * @param reader will be wrapped by BufferedReader. Should contain CSV in some shape or form.
   * @param creator will be called to convert CSV into the desired Object.
   * @param finalList passed in and mutated.
   * @throws IOException if error with the Reader class.
   */
  public void parse(Reader reader, CreatorFromRow<T> creator, @NotNull List<T> finalList)
      throws IOException, FactoryFailureException {
    finalList.clear();
    BufferedReader buffReader = new BufferedReader(reader);
    String line = buffReader.readLine();
    while (line != null) {
      if (!line.isEmpty()) {
        List<String> strings = new ArrayList<>(Arrays.asList(regexSplitCSVRow.split(line)));
        finalList.add(creator.create(strings));
      }
      line = buffReader.readLine();
    }
    buffReader.close();
  }
}
