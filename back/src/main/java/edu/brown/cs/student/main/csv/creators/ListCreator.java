package edu.brown.cs.student.main.csv.creators;

import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import java.util.List;

/**
 * This class implements the CreatorFromRow interface for the purpose of converting the
 * List<String></String> object into a List<String></String> object for the searcher class.
 */
public class ListCreator implements CreatorFromRow<List<String>> {

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
  public List<String> create(List<String> row) throws FactoryFailureException {
    return row;
  }
}
