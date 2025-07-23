package com.rgonzaleso.restclient.externalapi;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/api/v1")
public interface AlgoliaApiClient {

    @GetExchange("/search_by_date")
    String getHints(@RequestParam String query,
                    @RequestParam(required = false, defaultValue = "0") Integer page,
                    @RequestParam(required = false) Integer hitsPerPage);
}
