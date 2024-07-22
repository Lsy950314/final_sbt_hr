package com.example.sbt_final_hr.domain.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleMapsService {

    private final GeoApiContext geoApiContext;

    public GoogleMapsService(@Value("${google.maps.api.key}") String apiKey) {
        this.geoApiContext = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
    }

    public GeocodingResult[] getGeocoding(String address) throws Exception {
        return GeocodingApi.geocode(geoApiContext, address).await();
    }
}
