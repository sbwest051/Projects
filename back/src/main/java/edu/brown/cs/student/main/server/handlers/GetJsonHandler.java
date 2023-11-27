package edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.FeatureCollection;
import edu.brown.cs.student.main.server.serializers.FeatureCollectionResponse;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Class that contains the handle method to retrieve the geojson.
 */
public class GetJsonHandler implements Route {
    private final FeatureCollection geojson;

    /**
     * Takes in a FeatureCollection.
     * @param geojson FeatureCollection.
     */
    public GetJsonHandler(FeatureCollection geojson) {
        this.geojson = geojson;
    }

    /**
     * When called, will serialize the FeatureCollection object passed in.
     * @param request
     * @param response
     * @return FeatureCollectionResponse serialized string.
     */
    public Object handle(Request request, Response response) {
        return new FeatureCollectionResponse(this.geojson).serialize();
    }
}