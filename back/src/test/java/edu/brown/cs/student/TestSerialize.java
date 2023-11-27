package edu.brown.cs.student;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TestSerialize {
  public TestSerialize() {}

  @Test
  public void testSerialize() {
    System.out.println("serializeString(): " + serializeString("\"a\""));
    System.out.println("string: " + "\"a\"");

    List<String> stringList = new ArrayList<>();
    stringList.add("a");
    stringList.add("b");
    stringList.add("c");
    stringList.add("\"a\"");
    stringList.add("\"b\"");
    stringList.add("\"c\"");

    System.out.println("serialize(): " + serialize(stringList));
    System.out.println("stringList: " + stringList);
  }

  @Test
  public void testDeserialize() {
    String thing =
        "[[\"NAME\",\"state\"],\n"
            + "[\"Alabama\",\"01\"],\n"
            + "[\"Alaska\",\"02\"],\n"
            + "[\"Arizona\",\"04\"],\n"
            + "[\"Arkansas\",\"05\"],\n"
            + "[\"California\",\"06\"],\n"
            + "[\"Louisiana\",\"22\"],\n"
            + "[\"Kentucky\",\"21\"],\n"
            + "[\"Colorado\",\"08\"],\n"
            + "[\"Connecticut\",\"09\"],\n"
            + "[\"Delaware\",\"10\"],\n"
            + "[\"District of Columbia\",\"11\"],\n"
            + "[\"Florida\",\"12\"],\n"
            + "[\"Georgia\",\"13\"],\n"
            + "[\"Hawaii\",\"15\"],\n"
            + "[\"Idaho\",\"16\"],\n"
            + "[\"Illinois\",\"17\"],\n"
            + "[\"Indiana\",\"18\"],\n"
            + "[\"Iowa\",\"19\"],\n"
            + "[\"Kansas\",\"20\"],\n"
            + "[\"Maine\",\"23\"],\n"
            + "[\"Maryland\",\"24\"],\n"
            + "[\"Massachusetts\",\"25\"],\n"
            + "[\"Michigan\",\"26\"],\n"
            + "[\"Minnesota\",\"27\"],\n"
            + "[\"Mississippi\",\"28\"],\n"
            + "[\"Missouri\",\"29\"],\n"
            + "[\"Montana\",\"30\"],\n"
            + "[\"Nebraska\",\"31\"],\n"
            + "[\"Nevada\",\"32\"],\n"
            + "[\"New Hampshire\",\"33\"],\n"
            + "[\"New Jersey\",\"34\"],\n"
            + "[\"New Mexico\",\"35\"],\n"
            + "[\"New York\",\"36\"],\n"
            + "[\"North Carolina\",\"37\"],\n"
            + "[\"North Dakota\",\"38\"],\n"
            + "[\"Ohio\",\"39\"],\n"
            + "[\"Oklahoma\",\"40\"],\n"
            + "[\"Oregon\",\"41\"],\n"
            + "[\"Pennsylvania\",\"42\"],\n"
            + "[\"Rhode Island\",\"44\"],\n"
            + "[\"South Carolina\",\"45\"],\n"
            + "[\"South Dakota\",\"46\"],\n"
            + "[\"Tennessee\",\"47\"],\n"
            + "[\"Texas\",\"48\"],\n"
            + "[\"Utah\",\"49\"],\n"
            + "[\"Vermont\",\"50\"],\n"
            + "[\"Virginia\",\"51\"],\n"
            + "[\"Washington\",\"53\"],\n"
            + "[\"West Virginia\",\"54\"],\n"
            + "[\"Wisconsin\",\"55\"],\n"
            + "[\"Wyoming\",\"56\"],\n"
            + "[\"Puerto Rico\",\"72\"]]";

    try {
      System.out.println(deserialize(thing));
    } catch (IOException e) {
      System.out.println("bah");
    }
  }

  String serialize(List<String> stringList) {
    Moshi moshi = new Moshi.Builder().build();
    Type stringListType = Types.newParameterizedType(List.class, String.class);
    JsonAdapter<List<String>> stringListAdapter = moshi.adapter(stringListType);
    return stringListAdapter.toJson(stringList);
  }

  String serializeString(String string) {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<String> stringListAdapter = moshi.adapter(String.class);
    return stringListAdapter.toJson(string);
  }

  List<List<String>> deserialize(String string) throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    Type stringListType = Types.newParameterizedType(List.class, String.class);
    Type listListType = Types.newParameterizedType(List.class, stringListType);
    JsonAdapter<List<List<String>>> stringListAdapter = moshi.adapter(listListType);
    return stringListAdapter.fromJson(string);
  }
}
