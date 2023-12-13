package edu.brown.cs.student.main.server.sources;

import edu.brown.cs.student.main.server.exceptions.BadRequestException;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that represents a Mock ACS API Source.
 */
public class MockACSAPISource implements APISource{
  private Map<String, String> stateCodes;
  private final int testCase;

  /**
   * Test case 0: setStateCodes() throws DatasourceException
   *  * Test case 1: getCountyBroadband() throws DatasourceException
   *  * Test case 2: getCountyBroadband() throws BadRequestException
   *  * Test case 3: both work swimmingly
   * @param testCase
   */
  public MockACSAPISource(int testCase){
    this.stateCodes = null;
    this.testCase = testCase;
  }

  /**
   * Sets the arbitrary statecode value.
   * @throws DatasourceException thrown for broadband handler testing.
   */
  @Override
  public void setStateCodes() throws DatasourceException {
    switch (this.testCase) {
      case 0 -> throw new DatasourceException("Test Case 0");
      case 1, 2, 3 -> {
        this.stateCodes = new HashMap<>();
        this.stateCodes.put("Alabama", "0");
        this.stateCodes.put("Wyoming", "1");
      }
    }
  }

  /**
   * Used for broadband handler testing.
   * @param state A string with the name of a particular state the county resides in
   * @param county A string with the name of the county to search for broadband access
   * @return
   * @throws DatasourceException in case 1
   * @throws BadRequestException in case 2
   */
  @Override
  public String[] getCountyBroadband(String state, String county)
      throws DatasourceException, BadRequestException {
    switch (this.testCase) {
      case 0 -> {
        return new String[2];
      }
      case 1 -> throw new DatasourceException("Test Case 1");
      case 2 -> throw new BadRequestException("Test Case 2");
      case 3 -> {
        String[] result = new String[2];
        result[0] = "Pawnee, Indiana";
        result[1] = "200";
        return result;
      }
    }
    return new String[2];
  }

}
