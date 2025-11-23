package nl.martijndwars.webpush;

import org.jose4j.lang.JoseException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.util.concurrent.CompletableFuture;


public class PushAsyncService extends AbstractPushService<PushAsyncService> {


    public PushAsyncService() {
    }

    public PushAsyncService(String gcmApiKey) {
        super(gcmApiKey);
    }

    public PushAsyncService(KeyPair keyPair) {
        super(keyPair);
    }

    public PushAsyncService(KeyPair keyPair, String subject) {
        super(keyPair, subject);
    }

    public PushAsyncService(String publicKey, String privateKey) throws GeneralSecurityException {
        super(publicKey, privateKey);
    }

    public PushAsyncService(String publicKey, String privateKey, String subject) throws GeneralSecurityException {
        super(publicKey, privateKey, subject);
    }

    /**
     * Send a notification asynchronously.
     *
     * @param notification
     * @param encoding
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     * @throws JoseException
     */
    public CompletableFuture<HttpResponse<String>> send(Notification notification, Encoding encoding) throws GeneralSecurityException, IOException, JoseException {
        var httpPost = preparePost(notification, encoding);
        return httpClient.sendAsync(httpPost.build(), BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> send(Notification notification) throws GeneralSecurityException, IOException, JoseException {
        return send(notification, Encoding.AES128GCM);
    }

    /**
     * Prepare a POST request for AHC.
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
            httpPost.POST(BodyPublishers.ofByteArray(request.getBody()));
        } else {
            httpPost.POST(BodyPublishers.noBody());
        }
        return httpPost;
    }
}
