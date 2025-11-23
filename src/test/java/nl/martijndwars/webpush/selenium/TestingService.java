package nl.martijndwars.webpush.selenium;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Java wrapper for interacting with the Web Push Testing Service.
 */
public class TestingService {
    private final String baseUrl;
    private final HttpClient httpClient;

    public TestingService(HttpClient httpClient, String baseUrl) {
        this.httpClient = httpClient;
        this.baseUrl = baseUrl;
    }

    /**
     * Start a new test suite.
     *
     * @return
     */
    public int startTestSuite() throws IOException, InterruptedException {
        String startTestSuite = request(baseUrl + "start-test-suite/");

        JsonElement root = JsonParser.parseString(startTestSuite);

        return root.getAsJsonObject().get("data").getAsJsonObject().get("testSuiteId").getAsInt();
    }

    /**
     * Get a test ID and subscription for the given test case.
     *
     * @param testSuiteId
     * @param configuration
     * @return
     * @throws IOException
     */
    public JsonObject getSubscription(int testSuiteId, Configuration configuration) throws IOException, InterruptedException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("testSuiteId", testSuiteId);
        jsonObject.addProperty("browserName", configuration.browser);
        jsonObject.addProperty("browserVersion", configuration.version);

        if (configuration.gcmSenderId != null) {
            jsonObject.addProperty("gcmSenderId", configuration.gcmSenderId);
        }

        if (configuration.publicKey != null) {
            jsonObject.addProperty("vapidPublicKey", configuration.publicKey);
        }

        String getSubscription = request(baseUrl + "get-subscription/", jsonObject);

        return getData(getSubscription);
    }

    /**
     * Get the notification status for the given test case.
     *
     * @param testSuiteId
     * @param testId
     * @return
     * @throws IOException
     */
    public JsonArray getNotificationStatus(int testSuiteId, int testId) throws IOException, InterruptedException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("testSuiteId", testSuiteId);
        jsonObject.addProperty("testId", testId);


        String notificationStatus = request(baseUrl + "get-notification-status/", jsonObject);

        return getData(notificationStatus).get("messages").getAsJsonArray();
    }

    /**
     * End the given test suite.
     *
     * @return
     */
    public boolean endTestSuite(int testSuiteId) throws IOException, InterruptedException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("testSuiteId", testSuiteId);

        String endTestSuite = request(baseUrl + "end-test-suite/", jsonObject);

        return getData(endTestSuite).get("success").getAsBoolean();
    }

    /**
     * Perform HTTP request and return response.
     *
     * @param uri
     * @return
     */
    protected String request(String uri) throws IOException, InterruptedException {
        return request(uri, null);
    }

    /**
     * Perform HTTP request and return response.
     *
     * @param uri
     * @return
     */
    protected String request(String uri, JsonObject entity) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(uri));
        if (entity != null) {
            builder.POST(HttpRequest.BodyPublishers.ofString(entity.toString())).header("Content-Type", "application/json");
        } else {
            builder.POST(HttpRequest.BodyPublishers.noBody());
        }
        java.net.http.HttpRequest request = builder.build();
        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String json = httpResponse.body();

        if (httpResponse.statusCode() != 200) {
            JsonElement root = JsonParser.parseString(json);
            JsonObject error = root.getAsJsonObject().get("error").getAsJsonObject();

            String errorId = error.get("id").getAsString();
            String errorMessage = error.get("message").getAsString();


            throw new IllegalStateException("Error while requesting " + uri + " with body " + entity + " (" + errorId + ": " + errorMessage);
        }

        return json;
    }

    /**
     * Get the a JSON object of the data in the JSON response.
     *
     * @param response
     */
    protected JsonObject getData(String response) {
        JsonElement root = JsonParser.parseString(response);

        return root.getAsJsonObject().get("data").getAsJsonObject();
    }

}
