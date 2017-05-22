package com.example.tsha.myapplication.modle;

import java.util.ArrayList;

/**
 * Created by ts.ha on 2017-04-26.
 */

public class Feature {

    Error error;
    String type;

    ArrayList<Features> features;

    public class Error {
        String gateway;
        String category;
        String id;
        String link;
        String code;
        String message;

        @Override
        public String toString() {
            return "error{" +
                    "gateway='" + gateway + '\'' +
                    ", category='" + category + '\'' +
                    ", id='" + id + '\'' +
                    ", link='" + link + '\'' +
                    ", code='" + code + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }


    public Error getError() {
        return error;
    }


    private class Features {
        String type;
        Properties properties;



        public class Properties {
            int totalTime;
            int totalDistance;

            public int getTotalTime() {
                return totalTime;
            }

            public void setTotalTime(int totalTime) {
                this.totalTime = totalTime;
            }

            public int getTotalDistance() {
                return totalDistance;
            }

            public void setTotalDistance(int totalDistance) {
                this.totalDistance = totalDistance;
            }

            @Override
            public String toString() {
                return "Properties{" +
                        "totalTime=" + totalTime +
                        ", totalDistance=" + totalDistance +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "Features{" +
                    "type='" + type + '\'' +
                    ", properties=" + properties +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Feature{" +
                "error=" + error +
                ", type='" + type + '\'' +
                ", features=" + features +
                '}';
    }
}
