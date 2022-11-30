package com.unicorn.location;

import java.util.HashMap;
import java.util.Map;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.unicorn.location.helper.UnicornDependencyFactory;

import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Handler for requests to Lambda function.
 */
public class UnicornDeleteLocationHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final Logger logger = LoggerFactory.getLogger(UnicornDeleteLocationHandler.class);
   //private final DynamoDbClient dynamoDbClient;
    private final DynamoDbAsyncClient dynamoDbClient;
    private final String tableName;

    public UnicornDeleteLocationHandler() {
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
            if (id != null)
                deleteLocationItem(id);
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
                        .tableName(tableName).
                        key(Map.of("id", AttributeValue.builder().s(id).build()))
                        .build();    
         try {
            dynamoDbClient.deleteItem(deleteItemRequest).get();
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error creating Delete Item request");
        }
     }
     
}
