//DTO që kthen formatin e DataTables:
//
//draw
//
//data
//
//recordsTotal
//
//recordsFiltered
//
//Ky është kyç që tabela të shfaqë saktë rreshtat/paginimin.
package com.example.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class DataTableResponse<T> {
    private int draw;
    private long recordsTotal;
    private long recordsFiltered;
    private List<T> data;
}
