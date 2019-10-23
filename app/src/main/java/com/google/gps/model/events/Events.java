package com.google.gps.model.events;

public class Events {

    public static class ServiceMainActivityLocation{

        private String Location;

        public ServiceMainActivityLocation(String Location) {
            this.Location=Location;
        }

        public String getLocation() {
            return Location;
        }
    }
}
