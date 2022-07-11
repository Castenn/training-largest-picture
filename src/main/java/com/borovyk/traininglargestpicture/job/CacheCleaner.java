package com.borovyk.traininglargestpicture.job;

import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class CacheCleaner {

    @Scheduled(cron = "@daily")
    @CacheEvict(cacheNames = "largestPicture")
    public void cleanLargestPictureCache() {
        log.debug("Largest picture cache cleaned");
    }

}
