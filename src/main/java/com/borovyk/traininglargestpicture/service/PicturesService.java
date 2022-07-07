package com.borovyk.traininglargestpicture.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static java.util.Comparator.comparing;

@Service
public class PicturesService {

    @SneakyThrows
    public String printLargestPicture(String apiKey, long sol) {
        var restTemplate = new RestTemplate();
        var url = URI.create("https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?sol=" + sol + "&api_key=" + apiKey);
        var response = restTemplate.getForObject(url, String.class);
        return new ObjectMapper().readTree(response).findValuesAsText("img_src").parallelStream()
                .map(src -> toUrlSizePair(restTemplate, src))
                .max(comparing(Pair::getValue))
                .map(Pair::getKey).orElse("nothing");
    }

    private Pair<String, Long> toUrlSizePair(RestTemplate restTemplate, String pictureUrl) {
        var redirectUrl = restTemplate.headForHeaders(pictureUrl)
                .getFirst(HttpHeaders.LOCATION);
        var size = restTemplate.headForHeaders(redirectUrl)
                .getFirst(HttpHeaders.CONTENT_LENGTH);
        return Pair.of(pictureUrl, Long.parseLong(size));
    }

}
