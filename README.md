# WebPush

**THIS IS A FORK OF https://github.com/web-push-libs/webpush-java FOR USE IN MY OWN PROJECTS**

A Web Push library for Java 21. Supports payloads and VAPID.


## Installation

For Gradle, add the following dependency to `build.gradle`:

```groovy
compile 'com.github.xabgesagtx:web-push:0.1'
```

For Maven, add the following dependency to `pom.xml`:

```xml
<dependency>
    <groupId>com.github.xabgesagtx</groupId>
    <artifactId>web-push</artifactId>
    <version>0.1</version>
</dependency>
```

This library depends on BouncyCastle, which acts as a Java Cryptography Extension (JCE) provider. BouncyCastle's JARs are signed, and depending on how you package your application, you may need to include BouncyCastle yourself as well.

## Building

To assemble all archives in the project:

```sh
./gradlew assemble
```

## Usage

This library is meant to be used as a Java API. However, it also exposes a CLI to easily generate a VAPID keypair and send a push notification.

### API

First, make sure you add the BouncyCastle security provider:

```java
Security.addProvider(new BouncyCastleProvider());
```

Then, create an instance of the push service, either `nl.martijndwars.webpush.PushService` for synchronous blocking HTTP calls, or `nl.martijndwars.webpush.PushAsyncService` for asynchronous non-blocking HTTP calls:

```java
PushService pushService = new PushService(...);
```

Then, create a notification based on the user's subscription:

```java
Notification notification = new Notification(...);
```

To send a push notification:

```java
pushService.send(notification);
```

## Testing

The integration tests use [Web Push Testing Service (WPTS)](https://github.com/GoogleChromeLabs/web-push-testing-service) to handle the Selenium and browser orchestrating. We use a forked version that fixes a bug on macOS. To install WPTS:

```
npm i -g github:MartijnDwars/web-push-testing-service#bump-selenium-assistant
```

Then start WPTS:

```
web-push-testing-service start wpts
```

Then run the tests:

```
./gradlew clean test
```

Finally, stop WPTS:

```
web-push-testing-service stop wpts
```

## FAQ

### Why does encryption take multiple seconds?

There may not be enough entropy to generate a random seed, which is common on headless servers. There exist two ways to overcome this problem:

- Install [haveged](http://stackoverflow.com/a/31208558/368220), a _"random number generator that remedies low-entropy conditions in the Linux random device that can occur under some workloads, especially on headless servers."_ [This](https://www.digitalocean.com/community/tutorials/how-to-setup-additional-entropy-for-cloud-servers-using-haveged) tutorial explains how to install haveged on different Linux distributions.

- Change the source for random number generation in the JVM from `/dev/random` to `/dev/urandom`. [This](https://docs.oracle.com/cd/E13209_01/wlcp/wlss30/configwlss/jvmrand.html) page offers some explanation.

## Credit

To give credit where credit is due, the PushService is mostly a Java port of marco-c/web-push. The HttpEce class is mostly a Java port of martinthomson/encrypted-content-encoding.

## Resources

### Specifications

- [Generic Event Delivery Using HTTP Push](https://tools.ietf.org/html/draft-ietf-webpush-protocol-11)
- [Message Encryption for Web Push](https://tools.ietf.org/html/draft-ietf-webpush-encryption-08)
- [Encrypted Content-Encoding for HTTP](https://tools.ietf.org/html/draft-ietf-httpbis-encryption-encoding-02)

### Miscellaneous

- [Voluntary Application Server Identification for Web Push](https://tools.ietf.org/html/draft-ietf-webpush-vapid-01)
- [Web Push Book](https://web-push-book.gauntface.com/)
- [Simple Push Demo](https://gauntface.github.io/simple-push-demo/)
- [Web Push: Data Encryption Test Page](https://jrconlin.github.io/WebPushDataTestPage/)
- [Push Companion](https://web-push-codelab.appspot.com/)

## Related

The web-push-libs organization hosts implementations of the Web Push protocol in several languages:

- For PHP, see [web-push-libs/web-push-php](https://github.com/web-push-libs/web-push-php)
- For NodeJS, see [web-push-libs/web-push](https://github.com/web-push-libs/web-push)
- For Python, see [web-push-libs/pywebpush](https://github.com/web-push-libs/pywebpush)
- For C#, see [web-push-libs/web-push-csharp](https://github.com/web-push-libs/web-push-csharp)
- For Scala, see [zivver/web-push](https://github.com/zivver/web-push)

