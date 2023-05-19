package com.hobbi.service;

import java.util.List;

import com.hobbi.model.entities.Location;
import com.hobbi.model.entities.enums.LocationEnum;

public interface LocationService {
    List<Location> initLocations();

    Location getLocationByName(LocationEnum locationEnum);
}