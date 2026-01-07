package com.ocare.Ocare.adapter.in.web.healthRecord;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HealthDataRequest {

    private String recordkey;
    private String type;         // steps
    private String lastUpdate;
    private Data data;

    @Getter @Setter
    public static class Data {
        private String memo;
        private List<Entry> entries;
        private Source source;
    }

    @Getter @Setter
    public static class Entry {
        private String steps; // String으로 들어오므로 파싱 필요
        private Period period;
        private ValueUnit distance;
        private ValueUnit calories;
    }

    @Getter @Setter
    public static class Period {
        private String from; // 2024-11-14T23:00:00+0000
        private String to;
    }

    @Getter @Setter
    public static class ValueUnit {
        private Double value;
        private String unit;
    }

    @Getter @Setter
    public static class Source {
        private Product product;
        private Integer mode;
        private String name;
        private String type;
    }

    @Getter @Setter
    public static class Product {
        private String name;
        private String vender;
    }
}
