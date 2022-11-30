package com.unicorn.location.helper;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.http.crt.AwsCrtAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

public class UnicornDependencyFactory {
    public static final String ENV_VARIABLE_TABLE = "unicorn-locations";

    public UnicornDependencyFactory() {
    }

    public static DynamoDbAsyncClient DynamoDbAsyncClient () {

/*         dynamoDbClient = DynamoDbClient
                .builder()
//                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                // .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
                .region(Region.US_EAST_1)
//              .endpointOverride(new URI("https://dynamodb.us-east-1.amazonaws.com"))
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .overrideConfiguration(ClientOverrideConfiguration.builder().build())
                .build();

            dynamoDbClient = DynamoDbAsyncClient
                .builder()
                // .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
                .httpClientBuilder(AwsCrtAsyncHttpClient.builder())
                .build();
    */    

        return DynamoDbAsyncClient
        .builder()
        .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
        .httpClientBuilder(AwsCrtAsyncHttpClient.builder())
        .build();
    }

    public static String tableName() {
        return ENV_VARIABLE_TABLE;
    }
    
}
