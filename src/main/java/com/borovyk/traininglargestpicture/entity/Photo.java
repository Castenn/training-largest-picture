package com.borovyk.traininglargestpicture.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Photo(@JsonProperty("img_src") String url) {}
