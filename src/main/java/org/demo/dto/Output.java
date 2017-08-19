package org.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Output implements Cloneable {
    private Double sum;
    private Double avg;
    private Double max;
    private Double min;
    private Integer cnt;

    public Output clone() {
        return Output.builder().sum(sum).avg(avg).max(max).min(min).cnt(cnt).build();
    }
}