package com.borovyk.traininglargestpicture.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

import static java.util.Comparator.comparing;

@Service
public class PicturesService {

    record Photos(List<Photo> photos) {}
    record Photo(@JsonProperty("img_src") String imgSrc) {}

    @SneakyThrows
    @Cacheable("largestPicture")
    public String getLargestPictureUrl(String apiKey, long sol) {
        var restTemplate = new RestTemplate();
        var url = URI.create("https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?sol=" + sol + "&api_key=" + apiKey);
        var photos = restTemplate.getForObject(url, Photos.class);
        return photos.photos.parallelStream()
                .map(photo -> toUrlSizePair(restTemplate, photo.imgSrc))
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
