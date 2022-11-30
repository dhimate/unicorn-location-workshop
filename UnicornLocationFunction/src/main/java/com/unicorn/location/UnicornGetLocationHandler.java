package com.unicorn.location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.unicorn.location.helper.UnicornDependencyFactory;
import com.unicorn.location.model.UnicornLocation;

import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.lang.String;

/**
 * Handler for requests to Lambda function.
 */
public class UnicornGetLocationHandler
        implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final Logger logger = LoggerFactory.getLogger(UnicornGetLocationHandler.class);
    // private final DynamoDbClient dynamoDbClient;
    private final DynamoDbAsyncClient dynamoDbClient;
    private final String tableName;

    // Removed the static code loading to accomodate SnapStart feature.
    /* static {

        UnicornGetLocationHandler unicornGetLocationHandler;
        try {
            unicornGetLocationHandler = new UnicornGetLocationHandler();
            System.out.println(new Gson().toJson(unicornGetLocationHandler.getAllLocations()));
        } catch (URISyntaxException e) {

            e.printStackTrace();
        }

    }  */

    public UnicornGetLocationHandler() {
        dynamoDbClient = UnicornDependencyFactory.DynamoDbAsyncClient();
        tableName = UnicornDependencyFactory.tableName();
    }

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        logger.info("Received a request here!");
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        try {

            String id = (input.getPathParameters() != null ? input.getPathParameters().get("id") : null);
            String output;

            if (id == null)
                output = new Gson().toJson(getAllLocations());
            else
                output = new Gson().toJson(getLocationById(id));

            logger.info(output);

            return response
                    .withStatusCode(200)
                    .withBody(output);
        } catch (Exception e) {
            logger.error("Error while processing request", e);
            return response
                    .withBody("{'message' :'" + e.getMessage() + "'}")
                    .withStatusCode(400);
        }
    }

    private ArrayList<UnicornLocation> getAllLocations() {
        var scanRequest = ScanRequest.builder().tableName(tableName).build();

        try {
            ArrayList<UnicornLocation> items = new ArrayList<>();
            ScanResponse response = dynamoDbClient.scan(scanRequest).get();

            response.items().forEach(item -> {
                UnicornLocation unicornLocation = new UnicornLocation();
                unicornLocation.setId(item.get("id").s());
                unicornLocation.setUnicornName(item.get("unicornName").s());
                unicornLocation.setLatitude(item.get("latitude").s());
                unicornLocation.setLongitude(item.get("longitude").s());
                items.add(unicornLocation);
            });
            return items;

        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error creating Scan Item request");

        }
    }

    private ArrayList<UnicornLocation> getLocationById(String id) {

        var queryRequest = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("#id = :id")
                .expressionAttributeNames(Map.of("#id", "id"))
                .expressionAttributeValues(Map.of(":id", AttributeValue.builder().s(id).build()))
                .build();

        try {
            ArrayList<UnicornLocation> items = new ArrayList<>();
            QueryResponse response = dynamoDbClient.query(queryRequest).get();

            response.items().forEach(item -> {
                UnicornLocation unicornLocation = new UnicornLocation();
                unicornLocation.setId(item.get("id").s());
                unicornLocation.setUnicornName(item.get("unicornName").s());
                unicornLocation.setLatitude(item.get("latitude").s());
                unicornLocation.setLongitude(item.get("longitude").s());
                items.add(unicornLocation);
            });
            return items;

        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error creating Query Item request");

        }
    }

}
