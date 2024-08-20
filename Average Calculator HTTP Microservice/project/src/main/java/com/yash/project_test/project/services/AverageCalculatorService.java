package com.yash.project_test.project.services;

import com.yash.project_test.project.models.AverageResponse;
import com.yash.project_test.project.models.Number;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AverageCalculatorService {
    private final RestTemplate restTemplate;
    private final String testServerUrl;
    private final String bearerToken;

    private final int windowSize = 10;
    private LinkedList<Integer> slidingWindow = new LinkedList<>();

    @Autowired
    public AverageCalculatorService(RestTemplateBuilder restTemplateBuilder,
                                    @Value("${test.server.url}") String testServerUrl,
                                    @Value("${test.server.auth.token}") String bearerToken) {
        this.restTemplate = restTemplateBuilder.build();
        this.testServerUrl = testServerUrl;
        this.bearerToken = bearerToken;
    }

    public AverageResponse calculateAverage(String numberId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Number> response = restTemplate.exchange(testServerUrl + "/" + numberId,
                HttpMethod.GET,
                entity,
                Number.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            List<Integer> receivedNumbers = response.getBody().getNumbers();
            List<Integer> uniqueNumbers = receivedNumbers.stream()
                    .distinct()
                    .collect(Collectors.toList());

            List<Integer> previousWindow = new ArrayList<>(slidingWindow);

            for (Integer number : uniqueNumbers) {
                if (!slidingWindow.contains(number)) {
                    if (slidingWindow.size() == windowSize) {
                        slidingWindow.removeFirst();
                    }
                    slidingWindow.add(number);
                }
            }

            double average = slidingWindow.stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0);

            AverageResponse averageResponse = new AverageResponse();
            averageResponse.setNumbers(uniqueNumbers);
            averageResponse.setWindowPrevState(previousWindow);
            averageResponse.setWindowCurrState(new ArrayList<>(slidingWindow));
            averageResponse.setAvg(average);

            return averageResponse;
        } else {
            // Handle error scenario
            throw new RuntimeException("Failed to fetch data from test server");
        }
    }
}

