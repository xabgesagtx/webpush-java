package nl.martijndwars.webpush;

import org.jose4j.lang.JoseException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class PushService extends AbstractPushService<PushService> {


    public PushService(HttpClient httpClient) {
        super(httpClient);
    }

    public PushService(HttpClient httpClient, String gcmApiKey) {
        super(httpClient, gcmApiKey);
    }

    public PushService(HttpClient httpClient, KeyPair keyPair) {
        super(httpClient, keyPair);
    }

    public PushService(HttpClient httpClient, KeyPair keyPair, String subject) {
        super(httpClient, keyPair, subject);
    }

    public PushService(HttpClient httpClient, String publicKey, String privateKey) throws GeneralSecurityException {
        super(httpClient, publicKey, privateKey);
    }

    public PushService(HttpClient httpClient, String publicKey, String privateKey, String subject) throws GeneralSecurityException {
        super(httpClient, publicKey, privateKey, subject);
    }

    /**
     * Send a notification and wait for the response.
     *
     * @param notification
     * @param encoding
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     * @throws JoseException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public HttpResponse<String> send(Notification notification, Encoding encoding) throws GeneralSecurityException, IOException, JoseException, ExecutionException, InterruptedException {
        return sendAsync(notification, encoding).get();
    }

    public HttpResponse<String> send(Notification notification) throws GeneralSecurityException, IOException, JoseException, ExecutionException, InterruptedException {
        return send(notification, Encoding.AES128GCM);
    }

    /**
     * Send a notification, but don't wait for the response.
     *
     * @param notification
     * @param encoding
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     * @throws JoseException
     *
     * @deprecated Use {@link PushAsyncService#send(Notification, Encoding)} instead.
     */
    @Deprecated
    public Future<HttpResponse<String>> sendAsync(Notification notification, Encoding encoding) throws GeneralSecurityException, IOException, JoseException, InterruptedException {
        var httpPost = preparePost(notification, encoding);


        return httpClient.sendAsync(httpPost.build(), HttpResponse.BodyHandlers.ofString());
    }

    /**
     * @deprecated Use {@link PushAsyncService#send(Notification)} instead.
     */
    @Deprecated
    public Future<HttpResponse<String>> sendAsync(Notification notification) throws GeneralSecurityException, IOException, JoseException, InterruptedException {
        return sendAsync(notification, Encoding.AES128GCM);
    }

    /**
     * Prepare a HttpPost for Apache async http client
     *
     * @param notification
     * @param encoding
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     * @throws JoseException
     */
    public java.net.http.HttpRequest.Builder preparePost(Notification notification, Encoding encoding) throws GeneralSecurityException, IOException, JoseException {
        HttpRequest request = prepareRequest(notification, encoding);
        var httpPost = java.net.http.HttpRequest.newBuilder(URI.create(request.getUrl()));
        request.getHeaders().forEach(httpPost::header);
        if (request.getBody() != null) {
            java.net.http.HttpRequest.Builder post = httpPost.POST(java.net.http.HttpRequest.BodyPublishers.ofByteArray(request.getBody()));
        }
        return httpPost;
    }
}
