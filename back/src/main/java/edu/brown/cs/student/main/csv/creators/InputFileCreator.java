package edu.brown.cs.student.main.csv.creators;

import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.records.PLME.request.InputFile;
import java.util.List;

/**
 * This class implements the CreatorFromRow interface for the purpose of converting the
 * List<String></String> object into a List<String></String> object for the searcher class.
 */
public class InputFileCreator implements CreatorFromRow<InputFile> {

  /**
   * Contractual method with the CreatorFromRow interface. Will take a List<String></String> and
   * return that exact same List<String></String>.
   *
   * @param row from Parser.
   * @return List<String></String>
   * @throws FactoryFailureException contractually, but has no use in this particular creator due to
   *     the simplicity of the class and method.
   */
  @Override
  public InputFile create(List<String> row) throws FactoryFailureException {
    if (row.size() < 2) {
      throw new FactoryFailureException("Row is of invalid number of objects. "+ row,
          row);
    }
    if (row.size() == 2){
      return new InputFile(helper(row.get(0)),helper(row.get(1)), null);
    }
    return new InputFile(helper(row.get(0)),helper(row.get(1)), helper(row.get(2)));
  }

  private String helper(String cell) {
    if (cell == null || cell.isEmpty()){
      return null;
    } else {
      return cell;
    }
  }
}
