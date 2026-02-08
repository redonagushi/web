//DTO që merr parametrat e DataTables:
//
//draw, start, length, search, order, columns
package com.example.platform.dto;

import lombok.Data;
import java.util.List;

@Data
public class DataTableRequest {
    private int draw;
    private int start;
    private int length;
    private Search search;
    private List<Order> order;
    private List<Column> columns; // ✅ SHTO KETE

    @Data
    public static class Search {
        private String value;
    }

    @Data
    public static class Order {
        private int column; // index
        private String dir; // asc/desc
    }

    @Data
    public static class Column {
        private String data; // p.sh: "id", "emri", "email"
        private String name;
        private boolean searchable;
        private boolean orderable;
        private Search search;
    }
}
