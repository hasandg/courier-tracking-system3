package com.hasandag.courier.tracking.system.utils;

import org.springframework.stereotype.Component;

@Component
public class GeoUtils {

    // Earth radius in kilometers
    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Calculate distance between two points using the Haversine formula
     *
     * @param lat1 Latitude of point 1
     * @param lon1 Longitude of point 1
     * @param lat2 Latitude of point 2
     * @param lon2 Longitude of point 2
     * @return Distance in meters
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Convert to radians
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        // Haversine formula
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Distance in kilometers
        double distanceKm = EARTH_RADIUS_KM * c;

        // Convert to meters
        return distanceKm * 1000;
    }

    /**
     * Check if a point (courier location) is within a specified radius of another point (store location)
     *
     * @param courierLat   Courier latitude
     * @param courierLon   Courier longitude
     * @param storeLat     Store latitude
     * @param storeLon     Store longitude
     * @param radiusMeters Radius in meters
     * @return true if within radius, otherwise false
     */
    public static boolean isWithinRadius(double courierLat, double courierLon,
                                         double storeLat, double storeLon,
                                         double radiusMeters) {
        double distance = calculateDistance(courierLat, courierLon, storeLat, storeLon);
        return distance <= radiusMeters;
    }
}