package edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.CSV.CSVData;
import edu.brown.cs.student.main.Feature;
import edu.brown.cs.student.main.FeatureCollection;
import edu.brown.cs.student.main.server.serializers.FeatureCollectionResponse;
import edu.brown.cs.student.main.server.serializers.ServerFailureResponse;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handler class that will be called when run as an api with "searchjson".
 */
public class SearchJsonHandler implements Route {

    private final FeatureCollection geojson;
    private final List<String> queryList;

    /**
     * Contains a queryList to keep track of the request history. Also takes in a FeatureCollection
     * which will contain the geojson data or mock data.
     * @param geojson is a FeatureCollection that will be searched.
     */
    public SearchJsonHandler(FeatureCollection geojson) {
        this.geojson = geojson;
        this.queryList = new ArrayList<>();
    }

    /**
     * Called in the API. Will look for a "keyword" and search through all of the area description
     * data in this.geojson and return a FeatureCollectionResponse containing the FeatureCollection
     * with all of the features that match the response. Will also call getCenter() to get the
     * average point and put it into the input map in the FeatureCollectionResponse.
     * @param request
     * @param response
     * @return FeatureCollectionResponse.
     */
    public Object handle(Request request, Response response) {
        String keyword = request.queryParams("keyword");
        if (keyword == null) {
            return new ServerFailureResponse("error_bad_request", "Keyword was not entered.").serialize();
        } else if (this.geojson == null){
            return new ServerFailureResponse("error_bad_json", "FeatureCollection is null.").serialize();
        }
        this.queryList.add(keyword);

        List<Feature> featureList = new ArrayList<>();
        try {
            featureList = this.geojson.features().stream().filter(feature ->
                (feature.properties().area_description_data().values().stream()
                    .anyMatch(value -> value.contains(keyword)))).toList();

        } catch (IndexOutOfBoundsException e) {
        return new ServerFailureResponse("error_bad_json",
            "GeoJSON contains malformed geometry coordinate data for multipolygon.").serialize();
        } catch (NullPointerException e){
            return new ServerFailureResponse("error_bad_json",
            "GeoJSON is null or contains null features.").serialize();
        }

        Double[] center = new Double[2];
        try {
            center = getCenter(featureList);
        } catch (NullPointerException | NoSuchElementException | IndexOutOfBoundsException e){
            center[0] = 0.0;
            center[1] = 0.0;
        }

        Map<String,Object> inputMap = new HashMap<>();
        inputMap.put("input", keyword);
        inputMap.put("input_history", this.queryList);
        inputMap.put("avgX", center[0]);
        inputMap.put("avgY", center[1]);

        return new FeatureCollectionResponse(inputMap,new FeatureCollection(featureList)).serialize();
    }

    /**
     * Will get the center point of the minimum spanning bounding box that contains the MultiPolygon
     * of the feature and average all of those points in the featureList to return the center point
     * of the results of the featureList.
     * @param featureList
     * @return Double[]
     */
    public Double[] getCenter(List<Feature> featureList){
        Double[] center = new Double[2];
        List<Double> avgXList = new ArrayList<>();
        List<Double> avgYList = new ArrayList<>();
        for (Feature feature : featureList) {
            if (feature.geometry() == null){
                break;
            }
            List<List<Double>> cList = feature.geometry().coordinates().get(0).get(0);
            double minX =
                cList.stream().map(coordinate -> (coordinate.get(0))).min(Double::compareTo).get();
            double maxX =
                cList.stream().map(coordinate -> (coordinate.get(0))).max(Double::compareTo).get();
            double minY =
                cList.stream().map(coordinate -> (coordinate.get(1))).min(Double::compareTo).get();
            double maxY =
                cList.stream().map(coordinate -> (coordinate.get(1))).max(Double::compareTo).get();

            double X = (minX + maxX)/2;
            double Y = (minY + maxY)/2;
            avgXList.add(X);
            avgYList.add(Y);
        }

        center[0] = avgXList.stream().mapToDouble(d -> d).average().orElse(0.0);
        center[1] = avgYList.stream().mapToDouble(d -> d).average().orElse(0.0);

        return center;
    }
}