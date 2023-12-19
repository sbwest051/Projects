package edu.brown.cs.student.main.csv;

import java.util.ArrayList;
import java.util.List;

/**
 * Performs a search for an inputted character sequence in a list of list of strings. Has two
 * options: search a specified column, or search the entire data structure. Search is case-sensitive
 * but will look for any character sequence in a particular list.
 */
public class Searcher {
  private final boolean hasHeaders;
  private final List<List<String>> finalList;

  /**
   * Constructor takes in a list (perhaps from a passed in CSV file via ListCreator) and a boolean
   * parameter.
   *
   * @param finalList is a List of list of strings that has parsed data from the CSV file.
   * @param hasHeaders is a boolean parameter that is true iff the CSV has a header for the search
   *     to use/avoid.
   */
  public Searcher(List<List<String>> finalList, boolean hasHeaders) {
    this.finalList = finalList;
    this.hasHeaders = hasHeaders;
  }

  /**
   * Searches the entire this.finalList for a character sequence match to the inputted keyword. If
   * this.hasHeaders is true, then search will start with the first data column (i = 1), otherwise i
   * = 0. Results are printed via system print line. Called in Main class if user doesn't specify a
   * column.
   *
   * @param keyword is a String that is used for search. If it is an empty string, all of
   *     this.finalList will be printed.
   */
  public List<List<String>> searchAll(String keyword) {
    int firstRow = 0;
    List<List<String>> finalList = new ArrayList<>();
    if (this.hasHeaders) {
      firstRow = 1;
    }
    for (int i = firstRow; i < this.finalList.size(); i++) {
      for (String listItem : this.finalList.get(i)) {
        if (listItem.contains(keyword)) {
          finalList.add(this.finalList.get(i));
          break;
        }
      }
    }
    return finalList;
  }

  /**
   * Searches a specific column of this.finalList (so a specific index of each list in
   * this.finalList) for a matching character sequence to the inputted keyword. If this.hasHeaders
   * is true, then user must enter the exact column name (include spaces and cases). If false, then
   * user must enter the column index, which would start from left to right at 0. Results are
   * printed via system print line. Called in main class if user specifies a column.
   *
   * @param keyword is a String that is used for search. If it is an empty string, all of
   *     this.finalList will be printed.
   * @param columnId is a String that will be the identifier for the specified column the user would
   *     like to search this.finalList under.
   * @return list of list of strings containing all matches.
   * @throws IndexOutOfBoundsException if this.hasHeaders is false and an invalid columnId is
   *     inputted.
   * @throws NumberFormatException if this.hasHeaders is false and a nonnumerical value is inputted.
   */
  public List<List<String>> searchColumn(String keyword, String columnId)
      throws IllegalArgumentException {
    int colId;
    int firstRow = 0;
    List<List<String>> finalList = new ArrayList<>();
    if (this.hasHeaders) {
      colId = this.finalList.get(0).indexOf(columnId);
      if (colId < 0) {
        throw new IllegalArgumentException("ColumnID is not a header of the CSV.");
      }
      firstRow = 1;
    } else {
      try {
        colId = Integer.parseInt(columnId);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Expected columnID in the form of an integer.");
      }
      if (colId < 0 || colId >= this.finalList.get(0).size()) {
        throw new IllegalArgumentException("ColumnID is out of the bounds of the CSV.");
      }
    }
    for (int i = firstRow; i < this.finalList.size(); i++) {
      if (this.finalList.get(i).get(colId).contains(keyword)) {
        finalList.add(this.finalList.get(i));
      }
    }
    return finalList;
  }
}
