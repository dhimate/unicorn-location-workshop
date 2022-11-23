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
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Handler for requests to Lambda function.
 */
public class UnicornPostLocationHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final Logger logger = LoggerFactory.getLogger(UnicornPostLocationHandler.class);
   //private final DynamoDbClient dynamoDbClient;
   private final DynamoDbAsyncClient dynamoDbClient;

    public UnicornPostLocationHandler() {
        // dynamoDbClient = DynamoDbClient
        //         .builder()
        //         .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
        //         .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
        //         .httpClientBuilder(UrlConnectionHttpClient.builder())
        //         .build();
        dynamoDbClient = DynamoDbAsyncClient
            .builder()
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
            .httpClientBuilder(AwsCrtAsyncHttpClient.builder())
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
            createLocationItem(unicornLocation);
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

    private String getPageContents(String address) throws IOException{
        URL url = new URL(address);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    private void createLocationItem(UnicornLocation unicornLocation) {
        var putItemRequest = PutItemRequest.builder().item(
         Map.of( "id", AttributeValue.fromS(UUID.randomUUID().toString()),
                 "unicornName", AttributeValue.fromS(unicornLocation.getUnicornName()),
                 "latitude", AttributeValue.fromS(unicornLocation.getLatitude()),
                 "longitude", AttributeValue.fromS(unicornLocation.getLongitude())
         ))
         .tableName("unicorn-locations")
         .build();
     
         try {
            dynamoDbClient.putItem(putItemRequest).get();
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException("Error creating Put Item request");
            //e.printStackTrace();
        }
     }
     
}
