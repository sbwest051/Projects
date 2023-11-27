package edu.brown.cs.student.main.server.sources;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.CSV.Searcher;
import edu.brown.cs.student.main.server.exceptions.BadRequestException;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Connects to the American Community Survey API and pulls data from it. Will take in the name of a
 * state and county and retrieve the reference codes for them, to then search for and return the
 * percentage of homes in the county use broadband.
 */
public class ACSAPISource implements APISource {
  private final Map<String, String> stateCodes;

  public ACSAPISource() {
    this.stateCodes = new HashMap<>();
  }

  /**
   * Connects to a url address and returns the connection.
   *
   * @param requestURL The url to be connected to
   * @return The connection formed by the url.
   * @throws DatasourceException Thrown if there is an issue connecting to the site if the address
   *     is improper or it sends the wrong response code.
   * @throws IOException
   */
  private static HttpURLConnection connect(URL requestURL) throws DatasourceException, IOException {
    URLConnection urlConnection = requestURL.openConnection();
    if (!(urlConnection instanceof HttpURLConnection)) {
      throw new DatasourceException("unexpected: result of connection wasn't HTTP");
    }
    HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
    clientConnection.connect(); // GET
    if (clientConnection.getResponseCode() != 200 && clientConnection.getResponseCode() != 302) {
      throw new DatasourceException(
          "unexpected: API connection failure status: " + clientConnection.getResponseMessage());
    }
    return clientConnection;
  }

  /**
   * Initializes a list of all the state codes across the United States to be accessed later.
   *
   * @throws DatasourceException Thrown if there is an IOException whe reading the url
   */
  @Override
  public void setStateCodes() throws DatasourceException {
    try {
      List<List<String>> stateCodeList =
          this.deserialize(
              new URL("https", "api.census.gov", "/data/2010/dec/sf1?get=NAME&for=state:*"));
      for (int i = 1; i < stateCodeList.size(); i++) {
        this.stateCodes.put(stateCodeList.get(i).get(0), stateCodeList.get(i).get(1));
      }
    } catch (IOException e) {
      throw new DatasourceException("Error deserializing state codes.");
    }
  }

  /**
   * Searches the ACS api to return an array containing the percentage of homes in a particular
   * county that use broadband.
   *
   * @param state A string with the name of a particular state the county resides in
   * @param county A string with the name of the county to search for broadband access
   * @return An array of strings containing "county name, state name" and the percentage.
   * @throws DatasourceException Thrown when there is an issue deserializing the data from the API
   * @throws BadRequestException Thrown if the state and county fail to get a proper list of either
   *     the state data or county data
   */
  @Override
  public String[] getCountyBroadband(String state, String county)
      throws DatasourceException, BadRequestException {
    String[] result = new String[2];
    String stateCode;
    try {
      stateCode = this.stateCodes.get(state);
    } catch (NullPointerException e) {
      throw new BadRequestException(
          "State "
              + state
              + " not found. "
              + "Make sure state is capitalized and correctly spelled.");
    }
    if (stateCode == null) {
      throw new BadRequestException(
          "State "
              + state
              + " not found. "
              + "Make sure state is capitalized and correctly spelled.");
    }

    List<List<String>> countyCodes = this.getCountyCodes(stateCode); // throws DataSourceException

    Searcher countySearcher = new Searcher(countyCodes, true);
    List<List<String>> countyList = countySearcher.searchColumn(county, "NAME");
    if (countyList.isEmpty()) {
      throw new BadRequestException(
          "County "
              + county
              + " not found. "
              + "Make sure county is capitalized and correctly spelled.");
    } else if (countyList.get(0).isEmpty()) {
      throw new BadRequestException(
          "County "
              + county
              + " not found. "
              + "Make sure county is capitalized and correctly spelled.");
    } else if (countyList.size() != 1) {
      throw new BadRequestException(
          "County "
              + county
              + " query is too generic. "
              + "Make sure the county name is completely and correctly spelled.");
    }

    try {
      String countyCode = countyList.get(0).get(2); // 2 = "county" code
      result[0] = countyList.get(0).get(0); // comes with County, State name.
      result[1] =
          this.getCountyData(countyCode, stateCode)
              .get(1)
              .get(1); // broadband percentage (also throws DataSourceException)
    } catch (IndexOutOfBoundsException e) {
      throw new DatasourceException("Problem with obtaining needed county code or data.");
    }
    return result;
  }

  /**
   * Returns a list of lists of strings containing the county codes in a particular state.
   *
   * @param stateCode A string of the numerical code the ACS uses to identify a state.
   * @return A list of lists of strings containing each county in the state and its code
   * @throws DatasourceException Thrown if there is an issue deserializing the data in the API
   */
  private List<List<String>> getCountyCodes(String stateCode) throws DatasourceException {
    try {
      return this.deserialize(
          new URL(
              "https",
              "api.census.gov",
              "/data/2010/dec/sf1?get=NAME&for=county:*&in=state:" + stateCode));
    } catch (IOException e) {
      throw new DatasourceException(
          "Error deserializing county codes. " + "stateCode used: " + stateCode);
    }
  }

  /**
   * Returns a list of lists of strings containing data on a particular county containing its
   * broadband percentage.
   *
   * @param countyCode A string of the numerical code the ACS uses to identify a county.
   * @param stateCode A string of the numerical code the ACS uses to identify a state.
   * @return A list of lists of strings containing a county, its code, and its broadband user
   *     percentage.
   * @throws DatasourceException Thrown if there is an issue deserializing the data in the API
   */
  private List<List<String>> getCountyData(String countyCode, String stateCode)
      throws DatasourceException {
    try {
      return this.deserialize(
          new URL(
              "https",
              "api.census.gov",
              "/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
                  + countyCode
                  + "&in=state:"
                  + stateCode));
    } catch (IOException e) {
      throw new DatasourceException(
          "Error deserializing county data with broadband data."
              + "countyCode: "
              + countyCode
              + "; stateCode: "
              + stateCode);
    }
  }

  /**
   * Deserializes the data acquired from the API when given a URL
   *
   * @param requestURL A URL of the API which directs the server to data from the ACS
   * @return Returns the data from the API as a list of lists of strings.
   * @throws DatasourceException Thrown when there is an issue connecting to the URL
   * @throws IOException Thrown when there is an issue reading what was acquired by the URL
   */
  private List<List<String>> deserialize(URL requestURL) throws DatasourceException, IOException {
    HttpURLConnection clientConnection = connect(requestURL);
    Moshi moshi = new Moshi.Builder().build();
    Type stringListType = Types.newParameterizedType(List.class, String.class);
    Type listType = Types.newParameterizedType(List.class, stringListType);
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(listType);
    return adapter.fromJson(
        new Scanner(clientConnection.getInputStream()).useDelimiter("\\A").next());
  }
}
