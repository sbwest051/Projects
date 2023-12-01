package edu.brown.cs.student.main.server.sources;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.priv.key;
import edu.brown.cs.student.main.records.ChatPDFResponse;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * Connects to the American Community Survey API and pulls data from it. Will take in the name of a
 * state and county and retrieve the reference codes for them, to then search for and return the
 * percentage of homes in the county use broadband.
 */
public class ChatPDFSource {
  private String sourceId;
  public ChatPDFSource() {
    this.sourceId = "";
  }
  public void addURL(String url) throws DatasourceException {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://api.chatpdf.com/v1/sources/add-url"))
        .timeout(Duration.ofMinutes(2))
        .header("x-api-key", key.API_KEY)
        .header("Content-Type", "application/json")
        .POST(BodyPublishers.ofString("{\"url\": \""+ url +"\"}"))
        .build();
    try {
      this.setSourceId(client.send(request, BodyHandlers.ofString()).body());
    } catch (IOException | InterruptedException | DatasourceException e) {
      throw new DatasourceException(e.getMessage());
    }
  }

  public void addFile(String filepath) throws DatasourceException {
    try {
      CloseableHttpClient client = HttpClients.createDefault();
      HttpPost request = new HttpPost("https://api.chatpdf.com/v1/sources/add-file");
      request.addHeader("x-api-key", key.API_KEY);

      MultipartEntityBuilder builder = MultipartEntityBuilder.create();

      File file = new File(filepath);
      builder.addBinaryBody(
          "file",
          new FileInputStream(file),
          ContentType.APPLICATION_OCTET_STREAM,
          file.getName()
      );

      HttpEntity multipart = builder.build();
      request.setEntity(multipart);
      CloseableHttpResponse response = client.execute(request);
      HttpEntity responseEntity = response.getEntity();
      if (response.getStatusLine().getStatusCode() != 200){
        throw new DatasourceException("Status: "+ response.getStatusLine().getStatusCode() + "; "
            + "Message: "+ responseEntity);
      }
      this.setSourceId(new BufferedReader(new InputStreamReader(responseEntity.getContent())).readLine());
    } catch (IOException | DatasourceException e) {
      throw new DatasourceException(e.getMessage());
    }
  }

  private void setSourceId(String json) throws DatasourceException {
    try {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<ChatPDFResponse> adapter = moshi.adapter(ChatPDFResponse.class);
      this.sourceId = Objects.requireNonNull(adapter.fromJson(json)).sourceId();
    } catch (IOException | NullPointerException e) {
      throw new DatasourceException(e.getMessage());
    }
  }

  public String getSourceId() {
    return this.sourceId;
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
    clientConnection.setRequestMethod("GET");
    clientConnection.connect(); // GET
    if (clientConnection.getResponseCode() != 200 && clientConnection.getResponseCode() != 302) {
      throw new DatasourceException(
          "unexpected: API connection failure status: " + clientConnection.getResponseMessage());
    }
    return clientConnection;
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
