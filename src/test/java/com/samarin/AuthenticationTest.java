package com.samarin;


import static com.samarin.utils.AuthRestConstants.ACCEPT_JSON;
import static com.samarin.utils.AuthRestConstants.EMAIL_KEY;
import static com.samarin.utils.AuthRestConstants.INVALID_CREDENTIALS_STATUS;
import static com.samarin.utils.AuthRestConstants.PASSWORD_KEY;
import static com.samarin.utils.AuthRestConstants.SUCCESS_STATUS;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import com.google.gson.Gson;
import com.samarin.rest.authentication.model.success.SuccessAuthResponse;
import com.samarin.websocket.IQOptionWebSocketClient;
import com.samarin.websocket.exception.IQOptionWebSocketClientException;
import com.samarin.websocket.model.Message;
import io.qameta.allure.Description;
import io.qameta.allure.Flaky;
import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;


@Slf4j
public class AuthenticationTest {

  private IQOptionWebSocketClient webSocketClient;
  private RequestSpecification requestSpecification;
  private Gson gson;
  private int messageAccumulationTimeout;
  private int deltaForTimeSyncResponses;
  private String restUrl;
  private String websocketUrl;
  private final String BAD_SSID = "bad_ssid";


  /**
   * Initializing websocket client and set test parameters
   *
   * @param restAuthUrl - authentication url
   * @param websocketUrl - websocket connection url
   * @param messageAccumulationTimeout - time to accumulate responses from websocket server
   * @param deltaForTimeSyncResponses - delta to measure server responses
   */
  @Description(useJavaDoc = true)
  @BeforeClass
  @Parameters({"restAuthUrl", "websocketUrl", "messageAccumulationTimeout",
      "deltaForTimeSyncResponses"})
  public void init(String restAuthUrl, String websocketUrl, int messageAccumulationTimeout,
      int deltaForTimeSyncResponses) {
    this.restUrl = restAuthUrl;
    this.websocketUrl = websocketUrl;
    this.messageAccumulationTimeout = messageAccumulationTimeout;
    this.deltaForTimeSyncResponses = deltaForTimeSyncResponses;
    webSocketClient = new IQOptionWebSocketClient();
    RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
    requestSpecBuilder.setAccept(ACCEPT_JSON);
    requestSpecification = requestSpecBuilder.build();
    gson = new Gson();
  }

  /**
   * Clear responses that accumulated in websocket client after test methods
   */
  @Description(useJavaDoc = true)
  @AfterMethod
  public void clearResponses() throws IQOptionWebSocketClientException {
    webSocketClient.clearResponses();
    if (webSocketClient.isConnectionOpen()) {
      webSocketClient.close();
    }
  }

  @Step("Authenticate with email = {email}, password = {password}, and assert response status code = {statusCode} schema by path = {pathToSchema}")
  public Response authentificateAndAssertResponseSchema(String email, String password,
      String pathToSchema, int statusCode) {
    HashMap<String, String> params = new HashMap<>();
    params.put(EMAIL_KEY, email);
    params.put(PASSWORD_KEY, password);
    Response response = given().pathParams(params).spec(requestSpecification).when().post(restUrl);
    response.then().assertThat().statusCode(statusCode);
    response.then().assertThat().body(matchesJsonSchemaInClasspath(pathToSchema));
    return response;
  }

  @Step("Send auth message with ssid = {ssid}")
  public void sendAuthSsidToWebsocketServer(String ssid) {
    Message message = Message.builder().name("ssid").msg(ssid).build();
    webSocketClient.sendMessage(gson.toJson(message));
  }

  @Step("Assert that timeSync messages frequency about 1 second")
  private void assertTimeSyncFrequency(List<Message> responses) {
    for (int i = 1; i < responses.size(); i++) {
      Long first = Long.valueOf(responses.get(i - 1).getMsg());
      Long second = Long.valueOf(responses.get(i).getMsg());
      Long diff = second - first;
      assertTrue(diff >= 1000);
      assertTrue(diff <= 1000 + deltaForTimeSyncResponses);
    }
  }

  /**
   * Positive authentication case 1) Get ssid by post; 2) Send ssid to websocket server; 3) Assert
   * that we get profile 4) Assert timeSync messages frequency
   *
   * @param email - user email
   * @param password - user password
   * @param pathToSchema - path to success schema
   */
  @Test
  @Description(useJavaDoc = true)
  @Parameters({"email", "password", "pathToSchema"})
  public void positiveAuth(String email, String password, String pathToSchema)
      throws InterruptedException, IQOptionWebSocketClientException {
    webSocketClient.connectAndWait(websocketUrl);
    Response response = authentificateAndAssertResponseSchema(email, password, pathToSchema,
        SUCCESS_STATUS);
    SuccessAuthResponse authResponse = response.getBody().as(SuccessAuthResponse.class);
    String ssid = authResponse.getData().getSsid();
    assertNotEquals(ssid, null, "ssid is null");
    sendAuthSsidToWebsocketServer(ssid);
    TimeUnit.SECONDS.sleep(messageAccumulationTimeout);
    Map responses = webSocketClient.getResponses();
    assertTrue(responses.keySet().stream().anyMatch(o -> o.equals("profile")),
        "Didn't get a profile");
    assertTimeSyncFrequency((List<Message>) responses.get("timeSync"));
    webSocketClient.close();
  }

  /**
   * Negative authenticate case, bad credentials
   *
   * @param pathToSchema - path to error schema response
   */
  @Test
  @Description(useJavaDoc = true)
  @Parameters({"pathToSchema"})
  public void authenticateWithBadCredentials(String pathToSchema) {
    authentificateAndAssertResponseSchema("bad_email", "bad_password",
        pathToSchema, INVALID_CREDENTIALS_STATUS);
  }

  /**
   * Negative authentication case 1) Get ssid by post; 2) Send good ssid to websocket server; 3)
   * Send bad ssid to websocket server;
   *
   * @param email - user email
   * @param password - user password
   * @param pathToSchema - path to success schema
   */
  @Test
  @Description(useJavaDoc = true)
  @Parameters({"email", "password", "pathToSchema"})
  public void negativeSendGoodAndBadSsid(String email, String password, String pathToSchema)
      throws InterruptedException, IQOptionWebSocketClientException {
    webSocketClient.connectAndWait(websocketUrl);
    Response response = authentificateAndAssertResponseSchema(email, password, pathToSchema,
        SUCCESS_STATUS);
    SuccessAuthResponse authResponse = response.getBody().as(SuccessAuthResponse.class);
    String ssid = authResponse.getData().getSsid();
    assertNotEquals(ssid, null, "Ssid is null");
    sendAuthSsidToWebsocketServer(ssid);
    TimeUnit.SECONDS.sleep(messageAccumulationTimeout);
    Map responses = webSocketClient.getResponses();
    assertTrue(responses.keySet().stream().anyMatch(o -> o.equals("profile")),
        "Didn't get a profile");
    webSocketClient.clearResponses();
    sendAuthSsidToWebsocketServer(BAD_SSID);
    TimeUnit.SECONDS.sleep(messageAccumulationTimeout);
    webSocketClient.close();
    assertTrue(responses.keySet().stream().noneMatch(o -> o.equals("profile")),
        "Received a profile by sending a bad ssid");
  }

  /**
   * Negative test, send bad ssid to websocket. Flaky test, server may return {name:profile, msg:false},
   * but often return nothing. Didn't close connection after this test method, because connection
   * closed with status - 1006 and reason - 'Closed abnormally'.
   */
  @Test
  @Flaky
  @Description(useJavaDoc = true)
  public void negativeSendBadSsid() throws InterruptedException, IQOptionWebSocketClientException {
    webSocketClient.connectAndWait(websocketUrl);
    sendAuthSsidToWebsocketServer(BAD_SSID);
    TimeUnit.SECONDS.sleep(messageAccumulationTimeout);
    Object profile = webSocketClient.getResponses().get("profile");
    assertNull(profile, "Received a profile by sending a bad ssid");
  }
}
