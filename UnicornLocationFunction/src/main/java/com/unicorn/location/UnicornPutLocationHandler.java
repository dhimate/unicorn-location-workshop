package com.unicorn.location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.unicorn.location.helper.UnicornDependencyFactory;
import com.unicorn.location.model.UnicornLocation;

import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Handler for requests to Lambda function.
 */
public class UnicornPutLocationHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final Logger logger = LoggerFactory.getLogger(UnicornPutLocationHandler.class);
   //private final DynamoDbClient dynamoDbClient;
    private final DynamoDbAsyncClient dynamoDbClient;
    private final String tableName;

    public UnicornPutLocationHandler() {
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

            UnicornLocation unicornLocation = new Gson().fromJson(input.getBody(), UnicornLocation.class);
            unicornLocation.setId(input.getPathParameters() != null? input.getPathParameters().get("id"): UUID.randomUUID().toString());
            updateLocationItem(unicornLocation);
            String output = "Received unicorn " + unicornLocation.getUnicornName();

            return response
                    .withStatusCode(200)
                    .withBody(output);
        } catch (Exception e) {
            logger.error("Error while processing request",e);
            return response
                    .withBody("{'message' :'" + e.getMessage() + "'}")
                    .withStatusCode(400);
        }
    }


    private void updateLocationItem(UnicornLocation unicornLocation) {
        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
        .key(Map.of("id", AttributeValue.builder().s(unicornLocation.getId()).build()))
        .updateExpression("SET unicornName = :unicornName, longitude = :longitude, latitude = :latitude")
        .expressionAttributeValues(Map.of(
            ":unicornName", AttributeValue.builder().s(unicornLocation.getUnicornName()).build(),
            ":longitude", AttributeValue.builder().s(unicornLocation.getLongitude()).build(),
            ":latitude", AttributeValue.builder().s(unicornLocation.getLatitude()).build()
        ))
        .tableName(tableName)
        .build();
     
         try {
            dynamoDbClient.updateItem(updateItemRequest).get();
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error creating Update Item request");
        }
     }
     
}
