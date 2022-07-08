package com.borovyk.traininglargestpicture.service;

import com.borovyk.traininglargestpicture.entity.Photos;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static java.util.Comparator.comparing;

@Service
public class PicturesService {

    @SneakyThrows
    @Cacheable("largestPicture")
    public String getLargestPictureUrl(String apiKey, long sol) {
        var restTemplate = new RestTemplate();
        var factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build());
        restTemplate.setRequestFactory(factory);

        var url = UriComponentsBuilder.fromHttpUrl("https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos")
                .queryParam("sol", sol)
                .queryParam("api_key", apiKey)
                .build().toUri();
        var photos = restTemplate.getForObject(url, Photos.class);
        return photos.photos().parallelStream()
                .map(photo -> toUrlSizePair(restTemplate, photo.url()))
                .max(comparing(Pair::getValue))
                .map(Pair::getKey).orElse("nothing");
    }

    private Pair<String, Long> toUrlSizePair(RestTemplate restTemplate, String photoUrl) {
        var size = restTemplate.headForHeaders(photoUrl)
                .getFirst(HttpHeaders.CONTENT_LENGTH);
        return Pair.of(photoUrl, Long.parseLong(size));
    }

}
