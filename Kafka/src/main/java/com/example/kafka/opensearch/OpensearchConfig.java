package com.example.kafka.opensearch;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 * Configuration class for setting up the OpenSearch high-level REST client.
 * <p>
 * This client is used for connecting to the OpenSearch cluster with basic authentication
 * and a trust-all SSL context (intended for development or test environments).
 */
@Configuration
public class OpensearchConfig {
    /**
     * Creates and configures a {@link RestHighLevelClient} bean with basic authentication
     * and an insecure SSL context (trusts all certificates).
     * <p>
     * The OpenSearch endpoint is configured as <code>https://opensearch:9200</code> with
     * username "admin" and password "Aloha.32bit".
     * </p>
     *
     * @return a configured {@link RestHighLevelClient} instance
     * @throws NoSuchAlgorithmException if the TLS algorithm is not available
     * @throws KeyManagementException if initializing the SSL context fails
     */
    @Bean(destroyMethod = "close")
    public RestHighLevelClient restHighLevelClient() throws NoSuchAlgorithmException, KeyManagementException {

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("admin", "Aloha.32bit"));

        // Trust manager that accepts all certificates
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        final SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        RestClientBuilder builder = RestClient.builder(new HttpHost("opensearch", 9200, "https"))
                .setHttpClientConfigCallback(httpClientBuilder -> {
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    if ("https".equalsIgnoreCase("https")) {
                        httpClientBuilder.setSSLContext(sslContext);
                        httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
                    }
                    return httpClientBuilder;
                });

        return new RestHighLevelClient(builder);
    }
}
