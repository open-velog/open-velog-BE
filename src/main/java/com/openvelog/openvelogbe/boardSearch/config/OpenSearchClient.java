package com.openvelog.openvelogbe.boardSearch.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class OpenSearchClient {

    private final OpenSearchProperties openSearchProperties;
    private final CredentialsProvider credentialsProvider;

    @Autowired
    public OpenSearchClient(OpenSearchProperties openSearchProperties) {
        this.openSearchProperties = openSearchProperties;

        credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(
                openSearchProperties.getRest().getUsername(), openSearchProperties.getRest().getPassword()));
    }

    @Bean(destroyMethod = "close")
    public RestClient getRestClient() throws URISyntaxException {
        URI uri = new URI(openSearchProperties.getRest().getUris());
        HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());

        RestClient restClient = RestClient.builder(host)
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
                .build();
        return restClient;
    }

    @Bean(destroyMethod = "close")
    public RestClient restClient() throws URISyntaxException {
        URI uri = new URI(openSearchProperties.getRest().getUris());
        HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        return RestClient.builder(host)
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
                .build();
    }
}

