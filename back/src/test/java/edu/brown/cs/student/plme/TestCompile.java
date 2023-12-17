package edu.brown.cs.student.plme;

import static spark.Spark.after;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.CSV.CSVData;
import edu.brown.cs.student.main.CSV.Parser;
import edu.brown.cs.student.main.ChatPDFSource;
import edu.brown.cs.student.main.FactoryFailureException;
import edu.brown.cs.student.main.MetadataHandler;
import edu.brown.cs.student.main.records.PLME.MDCInput;
import edu.brown.cs.student.main.records.PLME.request.InputFile;
import edu.brown.cs.student.main.records.PLME.request.PLMEInput;
import edu.brown.cs.student.main.records.PLME.response.File;
import edu.brown.cs.student.main.records.PLME.response.Metadata;
import edu.brown.cs.student.main.records.PLME.response.MetadataTable;
import edu.brown.cs.student.main.server.exceptions.DatasourceException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;
import spark.Spark;

public class TestCompile {

  public TestCompile() {}

  @BeforeEach
  public void setup() {
    int port = 3234;
    Spark.port(port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    // Setting up the handler for the GET /order and /mock endpoints
    Parser<List<String>> parser;
    CSVData data = null;
    try {
      parser = new Parser<>();
      data = new CSVData(parser);
    } catch (IOException e) {
      System.err.println("Encountered an error: file could not be found or read.");
      System.exit(0);
    } catch (FactoryFailureException e) {
      System.err.println(e.getMessage());
      System.exit(0);
    }
    Spark.post("plme", new MetadataHandler(new ChatPDFSource()));

    Spark.init();
    Spark.awaitInitialization();

    System.out.println("Server started at http://localhost:" + port);
  }

  @AfterEach
  public void tearDown() {
    Spark.unmap("/plme");
    Spark.awaitStop();
  }

  @Test
  public void mapAndlistInput() throws IOException, FactoryFailureException {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<PLMEInput> adapter = moshi.adapter(PLMEInput.class);
    InputFile file1 = new InputFile("Allergy Paper", "data/allergy.pdf", null);
    InputFile file2 = new InputFile("PubMed Allergy Paper", null, "https://www.ncbi.nlm.nih"
       + ".gov/pmc/articles/PMC3539924/pdf/2045-7022-2-21.pdf");
    List<InputFile> list = new ArrayList<>();
    list.add(file1);
    list.add(file2);

    List<String> keywordList = new ArrayList<>();
    keywordList.add("allergies");
    keywordList.add("kittens");
    MDCInput listinput = new MDCInput("Basic Question", "What is this paper about?",
        keywordList, null);

    Map<String, List<String>> map = new LinkedHashMap<>();
    List<String> kl = new ArrayList<>();
    kl.add("Yes");
    map.put("Y", kl);
    List<String> kl1 = new ArrayList<>();
    kl1.add("No");
    kl1.add("not");
    map.put("N", kl1);

    MDCInput mapinput = new MDCInput("Review Paper?", "Was this paper a review paper?", null,
        map);

    List<MDCInput> inputs = new ArrayList<>();
    inputs.add(listinput);
    inputs.add(mapinput);
    System.out.println(adapter.toJson(new PLMEInput(null, list, inputs)));
    System.out.println(this.deserialize(adapter.toJson(new PLMEInput(null, list, inputs))).serialize());
  }

  @Test
  public void invalidFilePath() throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<PLMEInput> adapter = moshi.adapter(PLMEInput.class);
    InputFile file1 = new InputFile("Allergy Paper", "data/invalid.pdf", null);
    List<InputFile> list = new ArrayList<>();
    list.add(file1);

    List<String> keywordList = new ArrayList<>();
    keywordList.add("allergies");
    keywordList.add("kittens");
    MDCInput listinput = new MDCInput("Basic Question", "Q?",
        keywordList, null);

    List<MDCInput> inputs = new ArrayList<>();
    inputs.add(listinput);
    System.out.println(adapter.toJson(new PLMEInput(null, list, inputs)));
    System.out.println(this.deserialize(adapter.toJson(new PLMEInput(null, list, inputs))).serialize());

    String jsonData = (this.deserialize(adapter.toJson(new PLMEInput(null, list, inputs))).serialize());
    Assert.assertTrue(jsonData.contains("No such file or directory"));
  }

  @Test
  public void invalidURLPath() throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<PLMEInput> adapter = moshi.adapter(PLMEInput.class);
    InputFile file2 = new InputFile("PubMed Allergy Paper", null, "https://www.ncbi.nlm.nih"
        + ".gov/pmc/articles/PMC3539924/pdf/2045-7022-2-213.pdf");
    List<InputFile> list = new ArrayList<>();
    list.add(file2);

    List<String> keywordList = new ArrayList<>();
    keywordList.add("allergies");
    keywordList.add("kittens");
    MDCInput listinput = new MDCInput("Basic Question", "Q?",
        keywordList, null);

    List<MDCInput> inputs = new ArrayList<>();
    inputs.add(listinput);
    System.out.println(adapter.toJson(new PLMEInput(null, list, inputs)));
    System.out.println(this.deserialize(adapter.toJson(new PLMEInput(null, list, inputs))).serialize());

    String jsonData = (this.deserialize(adapter.toJson(new PLMEInput(null, list, inputs))).serialize());
    Assert.assertTrue(jsonData.contains("No such file or directory"));
  }

  @Test
  public void validFilePathInput() throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<PLMEInput> adapter = moshi.adapter(PLMEInput.class);
    InputFile file1 = new InputFile("Allergy Paper", "data/allergy.pdf", null);
    List<InputFile> list = new ArrayList<>();
    list.add(file1);

    List<String> keywordList = new ArrayList<>();
    keywordList.add("allergies");
    MDCInput listinput = new MDCInput("Basic Question", "Q?",
        keywordList, null);

    List<MDCInput> inputs = new ArrayList<>();
    inputs.add(listinput);
    System.out.println(adapter.toJson(new PLMEInput(null, list, inputs)));
    System.out.println(this.deserialize(adapter.toJson(new PLMEInput(null, list, inputs))).serialize());

    String jsonData = (this.deserialize(adapter.toJson(new PLMEInput(null, list, inputs))).serialize());
    Assert.assertTrue(jsonData.contains("success"));
  }

  @Test
  public void noKeywords() throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<PLMEInput> adapter = moshi.adapter(PLMEInput.class);
    InputFile file1 = new InputFile("Allergy Paper", "data/allergy.pdf", null);
    List<InputFile> list = new ArrayList<>();
    list.add(file1);

    List<String> keywordList = new ArrayList<>();
    MDCInput listinput = new MDCInput("Basic Question", "Q?",
        keywordList, null);

    List<MDCInput> inputs = new ArrayList<>();
    inputs.add(listinput);
    System.out.println(adapter.toJson(new PLMEInput(null, list, inputs)));
    System.out.println(this.deserialize(adapter.toJson(new PLMEInput(null, list, inputs))).serialize());

    String jsonData = (this.deserialize(adapter.toJson(new PLMEInput(null, list, inputs))).serialize());
    Assert.assertTrue(jsonData.contains("Input is missing one or more parameters"));
  }

  @Test
  public void emptyInputs() throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<PLMEInput> adapter = moshi.adapter(PLMEInput.class);
    List<InputFile> list = new ArrayList<>();
    List<MDCInput> inputs = new ArrayList<>();
    System.out.println(adapter.toJson(new PLMEInput(null, list, inputs)));
    System.out.println(this.deserialize(adapter.toJson(new PLMEInput(null, list, inputs))).serialize());

    String jsonData = (this.deserialize(adapter.toJson(new PLMEInput(null, list, inputs))).serialize());
    Assert.assertTrue(jsonData.contains("Input is missing one or more parameters."));
  }

  @Test
  public void missingInputs() throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<PLMEInput> adapter = moshi.adapter(PLMEInput.class);
    InputFile file1 = new InputFile("Allergy Paper", "data/allergy.pdf", null);
    List<InputFile> list = new ArrayList<>();
    list.add(file1);

    List<MDCInput> inputs = new ArrayList<>();
    System.out.println(adapter.toJson(new PLMEInput(null, list, inputs)));
    System.out.println(this.deserialize(adapter.toJson(new PLMEInput(null, list, inputs))).serialize());

    String jsonData = (this.deserialize(adapter.toJson(new PLMEInput(null, list, inputs))).serialize());
    Assert.assertTrue(jsonData.contains("Input is missing one or more parameters"));
  }

  @Test
  public void DataFolderFilepath() throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<PLMEInput> adapter = moshi.adapter(PLMEInput.class);
    InputFile file1 = new InputFile("Allergy Paper", "lol.pdf", null);
    List<InputFile> list = new ArrayList<>();
    list.add(file1);

    List<String> keywordList = new ArrayList<>();
    keywordList.add("allergies");

    MDCInput listinput = new MDCInput("Basic Question", "Q?", keywordList, null);

    List<MDCInput> inputs = new ArrayList<>();
    inputs.add(listinput);

    System.out.println(adapter.toJson(new PLMEInput(null, list, inputs)));
    System.out.println(this.deserialize(adapter.toJson(new PLMEInput(null, list, inputs))).serialize());

    String jsonData = (this.deserialize(adapter.toJson(new PLMEInput(null, list, inputs))).serialize());
    Assert.assertTrue(jsonData.contains("File must be in the data folder"));
  }

  @Test
  public void NoPDFAttached() throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<PLMEInput> adapter = moshi.adapter(PLMEInput.class);
    InputFile file1 = new InputFile("Allergy Paper", null, null);
    List<InputFile> list = new ArrayList<>();
    list.add(file1);

    List<String> keywordList = new ArrayList<>();
    keywordList.add("allergies");

    MDCInput listinput = new MDCInput("Basic Question", "Q?", keywordList, null);

    List<MDCInput> inputs = new ArrayList<>();
    inputs.add(listinput);

    System.out.println(adapter.toJson(new PLMEInput(null, list, inputs)));
    System.out.println(this.deserialize(adapter.toJson(new PLMEInput(null, list, inputs))).serialize());

    String jsonData = (this.deserialize(adapter.toJson(new PLMEInput(null, list, inputs))).serialize());
    Assert.assertTrue(jsonData.contains("Input is missing one or more parameters"));
  }

  @Test
  public void tooLongRequest() throws IOException {
    String reallyLongQ = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam ullamcorper"
        + " euismod malesuada. Fusce non mauris vel nunc interdum fringilla ut nec nulla. In hac "
        + "habitasse platea dictumst. Etiam efficitur ex at lacus consequat tincidunt. Quisque "
        + "rhoncus nulla in tellus aliquet, in tincidunt tellus condimentum. Sed in velit nec "
        + "turpis dictum commodo. Vestibulum ante ipsum primis in faucibus orci luctus et "
        + "ultrices posuere cubilia Curae; Ut venenatis venenatis felis, vitae luctus dui lacinia "
        + "non. Nunc hendrerit hendrerit elit, eu cursus sem dapibus in. Nam vitae velit nec elit "
        + "euismod pellentesque id et ex. Phasellus dictum, metus ut tristique venenatis, odio nunc "
        + "pharetra diam, eu aliquam justo nisl sit amet libero. Integer nec justo eu risus fermentum"
        + "Pellentesque tincidunt justo ut fringilla ultricies. Sed semper vel ex a auctor. "
        + "Sed bibendum, ipsum id sollicitudin tincidunt, urna sapien gravida tellus, eu lacinia"
        + " nulla libero non risus. Morbi nec semper lectus. Vivamus in libero a justo aliquet "
        + "hendrerit eu et mi. Aliquam id lectus id turpis feugiat tempor. Integer ullamcorper "
        + "lectus nec lacus tincidunt, vel aliquet augue accumsan. Proin at justo ac nunc feugiat "
        + "vestibulum. Sed vitae nulla vel urna hendrerit imperdiet. Duis sed orci quis turpis "
        + "ultricies bibendum ut sit amet sem. Fusce vel dolor at elit fringilla commodo nec sit "
        + "amet velit. Donec sollicitudin tincidunt malesuada. Curabitur vel odio ut tellus "
        + "fringilla tristique. Vivamus vel scelerisque urna. Quisque non odio vitae neque "
        + "vestibulum interdum eu non libero. Aliquam in tristique mi. In hac habitasse platea "
        + "dictumst. Integer sit amet metus a arcu auctor tincidunt. Fusce nec tortor nec tortor "
        + "bibendum fringilla. Nulla facilisi. Fusce sit amet justo vel tortor volutpat cursus ac "
        + "et enim. Sed eu massa nec turpis commodo ultricies. Ut vel tristique sem. Suspendisse "
        + "vel justo eu orci scelerisque scelerisque. Vivamus vel bibendum arcu. Maecenas aliquet "
        + "libero vel tortor vehicula, ut posuere quam fermentum. Aenean eget mauris vitae mi "
        + "facilisis cursus. Integer lacinia justo eu ligula fermentum bibendum. Vestibulum auctor "
        + "velit vitae quam volutpat, id ultricies urna sodales. Sed in vestibulum velit. Sed quis "
        + "elit non lectus venenatis semper. Aenean vulputate, ex non aliquet ultricies, arcu sapien"
        + " dapibus lacus, a finibus enim quam nec orci. Suspendisse aliquet metus at sem suscipit, "
        + "ac vulputate urna eleifend. Aliquam ac massa eu nisi egestas suscipit in id elit. Proin "
        + "fermentum dui ac ligula efficitur hendrerit. Sed fringilla, libero et hendrerit interdum,"
        + " orci dui volutpat lacus, eu eleifend odio lectus nec nisi. Vivamus blandit ligula et "
        + "erat sollicitudin, vel bibendum dui scelerisque. Sed ac ante sit amet libero iaculis "
        + "lacinia. In eu eros sit amet urna ultrices elementum non eget mauris. Mauris vel justo "
        + "id felis iaculis feugiat. Curabitur pharetra scelerisque velit, id convallis ligula "
        + "aliquet a. Duis aliquet, mauris sit amet fringilla tincidunt, massa quam commodo lectus, "
        + "ut auctor nisl purus vitae velit. Nulla facilisi. Integer vestibulum, justo non fringilla"
        + " ultricies, eros tellus luctus dui, id imperdiet nunc felis id mi. Fusce vel nisi non "
        + "ante malesuada bibendum. Ut dapibus, enim ut luctus sodales, purus elit laoreet sem, non"
        + " tincidunt orci metus vel felis. Sed bibendum vestibulum dolor eu fermentum. Morbi ac mi"
        + " in odio consequat vestibulum in eget turpis. Maecenas consectetur fermentum ligula, ac "
        + "venenatis justo imperdiet et. Nulla facilisi. Nulla non nulla eu ligula efficitur interdum."
        + " Suspendisse eget tincidunt justo. Duis quis sapien et mi sodales accumsan a nec urna."
        + "Proin auctor commodo urna, a venenatis orci. Etiam varius feugiat elit, vitae feugiat nunc"
        + " efficitur non. Aenean ut nunc tincidunt, sollicitudin orci id, imperdiet urna. Ut vitae"
        + " augue eu arcu tristique tempus. Fus.";

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<PLMEInput> adapter = moshi.adapter(PLMEInput.class);
    InputFile file1 = new InputFile("Allergy Paper", "data/allergy.pdf", null);
    List<InputFile> list = new ArrayList<>();
    list.add(file1);

    List<String> keywordList = new ArrayList<>();
    keywordList.add("allergies");
    MDCInput listinput = new MDCInput("Basic Question", reallyLongQ, keywordList, null);

    List<MDCInput> inputs = new ArrayList<>();
    inputs.add(listinput);
    System.out.println(adapter.toJson(new PLMEInput(null, list, inputs)));
    System.out.println(this.deserialize(adapter.toJson(new PLMEInput(null, list, inputs))).serialize());

    String jsonData = (this.deserialize(adapter.toJson(new PLMEInput(null, list, inputs))).serialize());
    Assert.assertTrue(jsonData.contains("Please limit your questions to a maximum of 2500 characters."));
  }

  @Test
  public void validURLInput() throws IOException {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<PLMEInput> adapter = moshi.adapter(PLMEInput.class);
      InputFile file2 = new InputFile("PubMed Allergy Paper", null, "https://www.ncbi.nlm.nih"
          + ".gov/pmc/articles/PMC3539924/pdf/2045-7022-2-21.pdf");
      List<InputFile> list = new ArrayList<>();
      list.add(file2);

      List<String> keywordList = new ArrayList<>();
      keywordList.add("allergies");
      keywordList.add("kittens");
      MDCInput listinput = new MDCInput("Basic Question", "Q?",
          keywordList, null);

      List<MDCInput> inputs = new ArrayList<>();
      inputs.add(listinput);
      System.out.println(adapter.toJson(new PLMEInput(null, list, inputs)));
      System.out.println(this.deserialize(adapter.toJson(new PLMEInput(null, list, inputs))).serialize());

      String jsonData = (this.deserialize(adapter.toJson(new PLMEInput(null, list, inputs))).serialize());
      Assert.assertTrue(jsonData.contains("success"));
  }

  @Test
  public void multiplePDFs() throws IOException {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<PLMEInput> adapter = moshi.adapter(PLMEInput.class);
    InputFile file1 = new InputFile("Allergy Paper", "data/allergy.pdf", null);
    InputFile file2 = new InputFile("Immunotherapy Paper", "data/immunotherapy.pdf", null);
      List<InputFile> list = new ArrayList<>();
    list.add(file1);
      list.add(file2);

      List<String> keywordList = new ArrayList<>();
      keywordList.add("allergies");
      keywordList.add("kittens");
      MDCInput listinput = new MDCInput("Basic Question", "What allergens are examined?",
          keywordList, null);

      List<MDCInput> inputs = new ArrayList<>();
      inputs.add(listinput);
      System.out.println(adapter.toJson(new PLMEInput(null, list, inputs)));
      System.out.println(this.deserialize(adapter.toJson(new PLMEInput(null, list, inputs))).serialize());

      String jsonData = (this.deserialize(adapter.toJson(new PLMEInput(null, list, inputs))).serialize());
      Assert.assertTrue(jsonData.contains("success"));
    }

  @Test
  public void moreKeywords() throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<PLMEInput> adapter = moshi.adapter(PLMEInput.class);
    InputFile file1 = new InputFile("Allergy Paper", "data/allergy.pdf", null);
    List<InputFile> list = new ArrayList<>();
    list.add(file1);

    List<String> keywordList = new ArrayList<>();
    keywordList.add("allergies");
    keywordList.add("Bet v 1");
    keywordList.add("homologous food");
    MDCInput listinput = new MDCInput("Allergies tested", "What allergies were tested for",
        keywordList, null);

    List<MDCInput> inputs = new ArrayList<>();
    inputs.add(listinput);
    System.out.println(adapter.toJson(new PLMEInput(null, list, inputs)));
    System.out.println(this.deserialize(adapter.toJson(new PLMEInput(null, list, inputs))).serialize());

    String jsonData = (this.deserialize(adapter.toJson(new PLMEInput(null, list, inputs))).serialize());
    Assert.assertTrue(jsonData.contains("success"));
  }

  @Test
  public void multipleQuestions() throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<PLMEInput> adapter = moshi.adapter(PLMEInput.class);
    InputFile file1 = new InputFile("Allergy Paper", "data/allergy.pdf", null);
    List<InputFile> list = new ArrayList<>();
    list.add(file1);

    List<String> keywordList = new ArrayList<>();
    keywordList.add("allergies");
    MDCInput listinput = new MDCInput("Basic Question", "Q?",
        keywordList, null);
    MDCInput listinput2 = new MDCInput("Basic Question 2", "Hello?",
        keywordList, null);

    List<MDCInput> inputs = new ArrayList<>();
    inputs.add(listinput);
    inputs.add(listinput2);
    System.out.println(adapter.toJson(new PLMEInput(null, list, inputs)));
    System.out.println(this.deserialize(adapter.toJson(new PLMEInput(null, list, inputs))).serialize());

    String jsonData = (this.deserialize(adapter.toJson(new PLMEInput(null, list, inputs))).serialize());
    Assert.assertTrue(jsonData.contains("success"));
  }

  @Test
  public void getSampleOutput(){

    List<MDCInput> mdcl = new ArrayList<>();
    List<String> kl1 = new ArrayList<>();
    kl1.add("good");
    kl1.add("bad");
    kl1.add("very bad");
    MDCInput input1 = new MDCInput("Column 1", "What is life?", kl1, null);
    Map<String, List<String>> km1 = new HashMap<>();
    List<String> l1 = new ArrayList<>();
    l1.add("China");
    l1.add("Korea");
    List<String> l2 = new ArrayList<>();
    l2.add("Italy");
    l2.add("Spain");
    km1.put("Asia", l1);
    km1.put("Europe", l2);
    MDCInput input2 = new MDCInput("Column 2", "Where is life?", null, km1);

    List<String> kl3 = new ArrayList<>();
    kl3.add("Yes");
    kl3.add("No");
    MDCInput input3 = new MDCInput("Column 3", "Is life?", kl3, null);

    mdcl.add(input1);
    mdcl.add(input2);
    mdcl.add(input3);

    List<File> fl = new ArrayList<>();
    Map<String, Double[]> rscore1 = new HashMap<>();
    Double[] rsl1 = new Double[2];
    rsl1[0] = 0.43;
    rsl1[1] = 1.00;

    Double[] rsl2 = new Double[2];
    rsl2[0] = 0.00;
    rsl2[1] = 0.00;

    Double[] rsl3 = new Double[2];
    rsl3[0] = 0.00;
    rsl3[1] = 0.00;

    rscore1.put("good", rsl1);
    rscore1.put("bad", rsl2);
    rscore1.put("very bad", rsl3);

    Metadata m1 = new Metadata("success", "Life is full of good.", rscore1, null);

    Map<String, Double[]> rscore2 = new HashMap<>();
    Double[] rsl4 = new Double[2];
    rsl4[0] = 1.00;
    rsl4[1] = 0.00;

    Double[] rsl5 = new Double[2];
    rsl5[0] = 0.00;
    rsl5[1] = 0.00;

    rscore2.put("good", rsl4);
    rscore2.put("bad", rsl5);

    Metadata m2 = new Metadata("success", "Life is in Korea.", rscore2, null);
    Metadata m3 = new Metadata("error", null, null, "Random arbitrary error.");

    List<Metadata> ml1 = new ArrayList<>();
    ml1.add(m1);
    ml1.add(m2);
    ml1.add(m3);

    File f1 = new File("success", null, "www.life.com", "Life", ml1, null);

    File f2 = new File("error", "data/lol/life.pdf", null,
        "Life Almighty", null, "File not found.");

    fl.add(f1);
    fl.add(f2);
    MetadataTable mt = new MetadataTable("success", mdcl, fl,null);
    System.out.println(mt.serialize());
  }


  private static HttpURLConnection tryRequest(String body) throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + "plme");
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    // The default method is "GET", which is what we're using here.
    // If we were using "POST", we'd need to say so.
    clientConnection.setRequestMethod("POST");
    clientConnection.setRequestProperty("Content-Type", "application/json");
    clientConnection.setRequestProperty("Accept", "application/json");
    clientConnection.setDoOutput(true);

    try(OutputStream os = clientConnection.getOutputStream()) {
      byte[] input = body.getBytes(StandardCharsets.UTF_8);
      os.write(input, 0, input.length);
    }

    clientConnection.connect();
    return clientConnection;
  }

  private MetadataTable deserialize(String body) throws IOException {
    HttpURLConnection clientConnection = tryRequest(body);
    Assert.assertEquals(clientConnection.getResponseCode(), 200);
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<MetadataTable> adapter = moshi.adapter(MetadataTable.class);
    return adapter.fromJson(
        new Scanner(clientConnection.getInputStream()).useDelimiter("\\A").next());
  }
}
