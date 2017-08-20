package org.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//domain object to input thru
//REST call

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Input {
    private Double amount;
    private Long tsp;
}