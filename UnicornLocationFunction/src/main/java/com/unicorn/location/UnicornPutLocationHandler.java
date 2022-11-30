package com.unicorn.location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Handler for requests to Lambda function.
 */
public class UnicornPutLocationHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final Logger logger = LoggerFactory.getLogger(UnicornPostLocationHandler.class);
   //private final DynamoDbClient dynamoDbClient;
    private final DynamoDbAsyncClient dynamoDbClient;

    /* static {

        UnicornPostLocationHandler unicornPostLocationHandler;
        try {
            unicornPostLocationHandler = new UnicornPostLocationHandler();
            unicornPostLocationHandler.createLocationItem(null);
        } catch (Exception e) {
            
            e.printStackTrace();
        }

    }  */

    public UnicornPutLocationHandler() {
        // dynamoDbClient = DynamoDbClient
        //         .builder()
        //         .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
        //         .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
        //         .httpClientBuilder(UrlConnectionHttpClient.builder())
        //         .build();
        dynamoDbClient = DynamoDbAsyncClient
            .builder()
//            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
//            .httpClientBuilder(AwsCrtAsyncHttpClient.builder())
            .build();
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


            // final String pageContents = this.getPageContents("https://checkip.amazonaws.com");
            // String output = String.format("{ \"message\": \"hello world\", \"location\": \"%s\" }", pageContents);

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
        .tableName("unicorn-locations")
        .build();
     
         try {
            dynamoDbClient.updateItem(updateItemRequest).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error creating Update Item request");
        }
     }
     
}
