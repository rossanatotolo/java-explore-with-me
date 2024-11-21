package ru.practicum;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class BaseClient {

    protected final RestTemplate restTemplate;

    public BaseClient(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    protected ResponseEntity<Object> get(final String path,
                                         @Nullable final Map<String, Object> parameters) {
        return makeRequest(HttpMethod.GET, path, parameters, null);
    }

    protected <T> ResponseEntity<Object> post(final String path,
                                              final T body) {
        return makeRequest(HttpMethod.POST, path, null, body);
    }

    private <T> ResponseEntity<Object> makeRequest(final HttpMethod method,
                                                   final String path,
                                                   @Nullable final Map<String, Object> parameters,
                                                   @Nullable final T body) {
        HttpEntity<T> requestEntity = body == null ? null : new HttpEntity<>(body);

        ResponseEntity<Object> statsServiceResponse;
        try {
            if (parameters != null) {
                statsServiceResponse = restTemplate.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                statsServiceResponse = restTemplate.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }

        if (statsServiceResponse.getStatusCode().is2xxSuccessful()) {
            return statsServiceResponse;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(statsServiceResponse.getStatusCode());

        if (statsServiceResponse.hasBody()) {
            return responseBuilder.body(statsServiceResponse.getBody());
        }

        return responseBuilder.build();
    }
}