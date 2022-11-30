package com.unicorn.location;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class UnicornPostLocationHandlerTest {
  private final Logger logger = LoggerFactory.getLogger(UnicornPostLocationHandlerTest.class);
  @Test  @Ignore
  public void successfulResponse() {
    UnicornPostLocationHandler app = new UnicornPostLocationHandler();
    APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent();
    input.setBody("{'unicornName' : 'hello'}");
    APIGatewayProxyResponseEvent result = app.handleRequest(input, null);
    assertEquals(200, result.getStatusCode().intValue());
    assertEquals("application/json", result.getHeaders().get("Content-Type"));
    String content = result.getBody();
    assertNotNull(content);
    logger.info(content);
  }
}
