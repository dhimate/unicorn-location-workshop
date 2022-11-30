package com.unicorn.location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.http.crt.AwsCrtAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Handler for requests to Lambda function.
 */
public class UnicornDeleteLocationHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final Logger logger = LoggerFactory.getLogger(UnicornDeleteLocationHandler.class);
   //private final DynamoDbClient dynamoDbClient;
    private final DynamoDbAsyncClient dynamoDbClient;

    /* static {

        UnicornPostLocationHandler unicornPostLocationHandler;
        try {
            unicornPostLocationHandler = new UnicornPostLocationHandler();
            unicornPostLocationHandler.createLocationItem(null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }  */

    public UnicornDeleteLocationHandler() {
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

            String id = (input.getPathParameters() != null ? input.getPathParameters().get("id") : null);
            if (id != null)
                deleteLocationItem(id);
            // final String pageContents = this.getPageContents("https://checkip.amazonaws.com");
            // String output = String.format("{ \"message\": \"hello world\", \"location\": \"%s\" }", pageContents);
            return response
                    .withStatusCode(200);
//                    .withBody(output);
        } catch (Exception e) {
            logger.error("Error while processing request",e);
            return response
                    .withBody("{'message' :'" + e.getMessage() + "'}")
                    .withStatusCode(400);
        }
    }

    private void deleteLocationItem(String id) {        
        DeleteItemRequest deleteItemRequest =  DeleteItemRequest.builder()
                        .tableName("unicorn-locations").
                        key(Map.of("id", AttributeValue.builder().s(id).build()))
                        //.expressionAttributeNames(Map.of("#id", "id"))
                        //.expressionAttributeValues(Map.of(":id", AttributeValue.builder().s(id).build()))
                        .build();    
         try {
            dynamoDbClient.deleteItem(deleteItemRequest).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error creating Delete Item request");
        }
     }
     
}
