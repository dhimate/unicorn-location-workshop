package com.unicorn.location;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import lombok.extern.slf4j.Slf4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Ignore;
import org.junit.Test;


@Slf4j
public class UnicornPostLocationHandlerTest {
  @Test @Ignore
  public void successfulResponse() {
    UnicornGetLocationHandler app = new UnicornGetLocationHandler();
    APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent();
    APIGatewayProxyResponseEvent result = app.handleRequest(input, null);
    assertEquals(200, result.getStatusCode().intValue());
    assertEquals("application/json", result.getHeaders().get("Content-Type"));
    String content = result.getBody();
    assertNotNull(content);
    log.info(content);
  }
}
