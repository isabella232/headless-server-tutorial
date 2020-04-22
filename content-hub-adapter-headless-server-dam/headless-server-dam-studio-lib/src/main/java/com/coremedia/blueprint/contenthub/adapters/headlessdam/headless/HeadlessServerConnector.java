package com.coremedia.blueprint.contenthub.adapters.headlessdam.headless;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Optional;

/**
 *
 */
public class HeadlessServerConnector {
  private static final Logger LOG = LoggerFactory.getLogger(HeadlessServerConnector.class);
  public static final String GRAPH_QL_ENDPOINT = "graphql";

  private String headlessServerUrl;
  private RestTemplate restTemplate;

  public HeadlessServerConnector(@NonNull String headlessServerUrl) {
    this.headlessServerUrl = headlessServerUrl;
    this.restTemplate = restTemplate();
  }

  @NonNull
  private HttpEntity<String> buildRequestEntity(@NonNull String payload) {
    String s = payload.replaceAll("\n", " ");
    return new HttpEntity<>(s, buildHttpHeaders());
  }

  @NonNull
  private HttpHeaders buildHttpHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");
    headers.set("Accept", "application/json");
    headers.set("Accept-Charset", "utf-8");
    return headers;
  }

  @Nullable
  public CMContentDocument getContent(@NonNull String id) {
    Optional<QueryResponseDocument> queryResponseDocument = contentQuery(id);
    if (queryResponseDocument.isPresent()) {
      QueryResponseDocument responseDocument = queryResponseDocument.get();
      return responseDocument.getData().getContent();
    }
    return null;
  }

  @Nullable
  public CMContentDocument search(@NonNull String siteId, @NonNull String contentType) {
    Optional<QueryResponseDocument> queryResponseDocument = searchQuery(siteId, contentType);
    if (queryResponseDocument.isPresent()) {
      QueryResponseDocument responseDocument = queryResponseDocument.get();
      return responseDocument.getData().getContent();
    }
    return null;
  }

  private Optional<QueryResponseDocument> contentQuery(@NonNull String id) {
    String payload = new ContentQuery(id).toString();
    HttpEntity<String> httpEntity = buildRequestEntity(payload);
    return performRequest(httpEntity, QueryResponseDocument.class);
  }

  private Optional<QueryResponseDocument> searchQuery(@NonNull String siteId, String contentType) {
    String payload = new SearchQuery(siteId, contentType).toString();
    HttpEntity<String> httpEntity = buildRequestEntity(payload);
    return performRequest(httpEntity, QueryResponseDocument.class);
  }

  @NonNull
  private <T> Optional<T> performRequest(@NonNull HttpEntity<String> requestEntity,
                                         @NonNull Class<T> responseType) {
    Optional<ResponseEntity<T>> responseEntityOptional = makeExchange(requestEntity, responseType);
    if (responseEntityOptional.isEmpty()) {
      return Optional.empty();
    }
    ResponseEntity<T> responseEntity = responseEntityOptional.get();
    T responseBody = responseEntity.getBody();
    return Optional.ofNullable(responseBody);
  }


  @NonNull
  private <T> Optional<ResponseEntity<T>> makeExchange(@NonNull HttpEntity<String> requestEntity,
                                                       @NonNull Class<T> responseType) {
    try {
      String url = headlessServerUrl;
      if (!url.endsWith("/")) {
        url += "/";
      }
      URI uri = URI.create(url + GRAPH_QL_ENDPOINT);
      ResponseEntity<T> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, responseType);
      return Optional.of(responseEntity);
    } catch (HttpStatusCodeException ex) {
      LOG.error("Headless REST request failed: {}", ex.getResponseBodyAsString());
      HttpStatus statusCode = ex.getStatusCode();
      if (statusCode == HttpStatus.NOT_FOUND) {
        LOG.trace("Result from '{}' (response code: {}) will be interpreted as 'no result found'.", headlessServerUrl, statusCode);
        return Optional.empty();
      }

      if (statusCode == HttpStatus.FORBIDDEN) {
        LOG.warn("Forbidden, not allowed to make this request to URL " + headlessServerUrl + " with request entity " + requestEntity, ex);
        return Optional.empty();
      }

      LOG.warn("Headless call to '{}' failed. Exception:\n{}", headlessServerUrl, ex.getMessage());
      throw new UnsupportedOperationException(
              String.format("REST call to '%s' failed. Exception: %s", headlessServerUrl, ex.getMessage()), ex);
    }
  }

  public RestTemplate restTemplate() {
    try {
      // configure date (de)serialization
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
      objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

      // configure template
      HostnameVerifier PROMISCUOUS_VERIFIER = (s, sslSession) -> true;
      // Create a trust manager that does not validate certificate chains
      TrustManager[] trustAllCerts = new TrustManager[]{
              new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                  return null;
                }

                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
              }
      };
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());

      restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
      restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory() {
        @Override
        protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
          if (connection instanceof HttpsURLConnection) {
            ((HttpsURLConnection) connection).setSSLSocketFactory(sc.getSocketFactory());
            ((HttpsURLConnection) connection).setHostnameVerifier(PROMISCUOUS_VERIFIER);
          }
          super.prepareConnection(connection, httpMethod);
        }
      });
      MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
      messageConverter.setPrettyPrint(true);
      messageConverter.setObjectMapper(objectMapper);
      restTemplate.getMessageConverters().removeIf(m -> m.getClass().isAssignableFrom(MappingJackson2HttpMessageConverter.class));
      restTemplate.getMessageConverters().add(messageConverter);
    } catch (Exception e) {
      LOG.error("Failed to build rest template: {}", e.getMessage(), e);
    }
    return restTemplate;
  }
}
