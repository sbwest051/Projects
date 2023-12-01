package edu.brown.cs.student.main.server.handlers;

import edu.brown.cs.student.main.records.maps.Feature;
import edu.brown.cs.student.main.records.maps.FeatureCollection;
import edu.brown.cs.student.main.server.serializers.FeatureCollectionResponse;
import edu.brown.cs.student.main.server.serializers.ServerFailureResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Class containing the methods for FeatureCollection filtering by bounding box.
 */
public class FilterJsonHandler implements Route {
    private final FeatureCollection geojson;

    /**
     * Takes in FeatureCollection to be filtered.
     * @param geojson FeatureCollection.
     */
    public FilterJsonHandler(FeatureCollection geojson) {
        this.geojson = geojson;
    }

    /**
     * Takes in four parameters: minX, maxX, minY, maxY (bounding box points). Will return
     * a FeatureCollectionResponse containing all features in the passed-in geojson that have
     * geometries that fall within the passed in constraints.
     * @param request
     * @param response
     * @return FeatureCollectionResponse or ServerFailureResponse.
     */
    public Object handle(Request request, Response response) {
        String paramA = request.queryParams("minX");
        String paramB = request.queryParams("maxX");
        String paramC = request.queryParams("minY");
        String paramD = request.queryParams("maxY");
        if (paramA == null || paramC == null || paramB == null || paramD == null) {
            return new ServerFailureResponse("error_bad_request",
                "At least one of the parameters (minX, minY, maxX, maxY)"
                    + " are missing.").serialize();
        }
        Double minX; Double minY; Double maxX; Double maxY;
        try {
            minX = Double.valueOf(paramA);
            maxX = Double.valueOf(paramB);
            minY = Double.valueOf(paramC);
            maxY = Double.valueOf(paramD);
        } catch (NumberFormatException e) {
            return new ServerFailureResponse("error_bad_request",
                "At least one of the parameters was not in the form of a double.").serialize();
        }

        List<Feature> featureList;
        try {
            featureList = this.geojson.features().stream().filter(feature ->
                    (feature.geometry() != null && feature.geometry().coordinates().get(0).get(0)
                        .stream()
                        .allMatch(
                            coordinate -> coordinate.get(0) >= minX && coordinate.get(0) <= maxX
                                && coordinate.get(1) >= minY && coordinate.get(1) <= maxY)))
                .toList();
        } catch (IndexOutOfBoundsException e) {
            return new ServerFailureResponse("error_bad_json",
                "GeoJSON contains malformed geometry coordinate data for multipolygon.").serialize();
        } catch (NullPointerException e){
            return new ServerFailureResponse("error_bad_json",
                "GeoJSON is null or contains null features.").serialize();
        }

        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("minX", paramA);
        inputMap.put("maxX", paramB);
        inputMap.put("minY", paramC);
        inputMap.put("maxY", paramD);
        return new FeatureCollectionResponse(inputMap,new FeatureCollection(featureList)).serialize();
    }
}