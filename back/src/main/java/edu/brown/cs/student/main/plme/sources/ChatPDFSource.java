package edu.brown.cs.student.main.plme.sources;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.priv.key;
import edu.brown.cs.student.main.records.ChatPDF.ChatPDFRequest;
import edu.brown.cs.student.main.records.ChatPDF.ChatPDFResponse;
import edu.brown.cs.student.main.records.ChatPDF.Message;
import edu.brown.cs.student.main.exceptions.DatasourceException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * Class that communicates with the ChatPDF API. Involves loading in URLs, filepaths, and asking
 * questions.
 */
public class ChatPDFSource implements PDFSource {
  private final HttpClient httpClient;
  private final CloseableHttpClient closeableHttpClient;

  /**
   * Instantiates a new HttpClient for url adding, instantiates a CloseableHttpClient for file
   * adding (involves multipart entity).
   */
  public ChatPDFSource() {
    this.httpClient = HttpClient.newHttpClient();
    this.closeableHttpClient = HttpClients.createDefault();
  }

  /**
   * Will add a URL to the ChatPDF api via post request. Returns sourceID for messages.
   * @param url String url.
   * @return String SourceID for messages correlating to the url.
   * @throws DatasourceException if error from ChatPDF, serialization, or deserialization.
   */
  @Override
  public String addURL(String url) throws DatasourceException {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://api.chatpdf.com/v1/sources/add-url"))
        .timeout(Duration.ofMinutes(2))
        .header("x-api-key", key.API_KEY)
        .header("Content-Type", "application/json")
        .POST(BodyPublishers.ofString("{\"url\": \""+ url +"\"}"))
        .build();
    try {
      return deserializeResponse(this.httpClient.send(request, BodyHandlers.ofString()).body()).sourceId();
    } catch (IOException | InterruptedException | DatasourceException e) {
      throw new DatasourceException(e.getMessage());
    }
  }

  /**
   * Adds an existing file to the ChatPDF API via post request. Involves MultipartEntity post
   * request, so uses a CloseableHttpClient.
   * @param filepath must be in the data folder in the backend.
   * @return SourceID for messages correlating to the file.
   * @throws DatasourceException if error from ChatPDF, serialization, or deserialization.
   */
  @Override
  public String addFile(String filepath) throws DatasourceException {
    try {
      if (!filepath.startsWith("data/")){
        throw new DatasourceException("File must be in the data folder.");
      }
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
      CloseableHttpResponse response = this.closeableHttpClient.execute(request);
      HttpEntity responseEntity = response.getEntity();
      if (response.getStatusLine().getStatusCode() != 200){
        throw new DatasourceException("Status: "+ response.getStatusLine().getStatusCode() + "; "
            + "Message: "+ responseEntity);
      }
      return deserializeResponse(new BufferedReader(
          new InputStreamReader(responseEntity.getContent())).readLine()).sourceId();
    } catch (IOException | DatasourceException e) {
      throw new DatasourceException(e.getMessage());
    }
  }

  /**
   * Sends a post request to the ChatPDF API with a sourceID pertaining to the pdf and a
   * user-inputted question.
   * @param sourceId string that corresponds to the loaded pdf from the addFile or addUrl methods.
   * @param question query to ask pertaining to the pdf.
   * @return String content response from the GPT.
   * @throws DatasourceException if sourceID, question is null. Will also be thrown if there is a
   * connection issue, serialization, or deserialization issue.
   */
  @Override
  public String getContent(String sourceId, String question) throws DatasourceException {
    if (sourceId == null){
      throw new DatasourceException("SourceID was null.");
    }
    if (question == null || question.isEmpty()){
      throw new DatasourceException("Query was null or empty.");
    }

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://api.chatpdf.com/v1/chats/message"))
        .timeout(Duration.ofMinutes(2))
        .header("x-api-key", key.API_KEY)
        .header("Content-Type", "application/json")
        .POST(BodyPublishers.ofString(this.serializeRequest(sourceId, question)))
        .build();
    try {
      HttpResponse<String> response = this.httpClient.send(request, BodyHandlers.ofString());
      if (response.statusCode() != 200){
        throw new DatasourceException("Status: "+ response.statusCode() + "; "
            + "Message: "+ response.body());
      }
      return this.deserializeResponse(response.body()).content();
    } catch (IOException | InterruptedException | DatasourceException e) {
      throw new DatasourceException(e.getMessage());
    }
  }

  /**
   * Helper method that will serialize the ChatPDFRequest object into a readable json file for
   * the post request.
   * @param sourceId string that corresponds to the loaded pdf from the addFile or addUrl methods.
   * @param question query to ask pertaining to the pdf.
   * @return serialized request in the form of a json string.
   * @throws DatasourceException if sourceID is null.
   */
  private String serializeRequest(String sourceId, String question) throws DatasourceException {
    if (sourceId == null){
      throw new DatasourceException("SourceId of PDF was null. PDF may not have been loaded.");
    }
    Message message = new Message("user", question);
    List<Message> messages = new ArrayList<>();
    messages.add(message);
    ChatPDFRequest request = new ChatPDFRequest(true, sourceId, messages);

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<ChatPDFRequest> adapter = moshi.adapter(ChatPDFRequest.class);
    return adapter.toJson(request);
  }

  /**
   * Responsible for deserializing the json response from the ChatPDF API. Will transform the
   * json string into a ChatPDFResponse object.
   * @param responseJson json from the api.
   * @return converted ChatPDFResponse object.
   * @throws DatasourceException if there is a parsing error (IOException).
   */
  private ChatPDFResponse deserializeResponse(String responseJson) throws DatasourceException {
    try {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<ChatPDFResponse> adapter = moshi.adapter(ChatPDFResponse.class);
      return Objects.requireNonNull(adapter.fromJson(responseJson));
    } catch (IOException e) {
      throw new DatasourceException(e.getMessage());
    }
  }
}