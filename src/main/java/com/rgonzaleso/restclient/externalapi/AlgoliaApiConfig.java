package com.rgonzaleso.restclient.externalapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class AlgoliaApiConfig {

    @Value("${algolia.api.base-url}")
    private String baseUrl;

    @Bean
    public AlgoliaApiClient algoliaApiClient(){
        assert baseUrl != null;

        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultStatusHandler(HttpStatusCode::isError,
                        (request, response) ->
                        {throw new ResponseStatusException(response.getStatusCode());})
                .build();

        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(AlgoliaApiClient.class);
    }
}
