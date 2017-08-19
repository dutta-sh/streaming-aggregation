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
    private Double sum = 0D;
    private Double avg = 0D;
    private Double max = 0D;
    private Double min = 0D;
    private Integer cnt = 0;

    public Output clone() {
        return Output.builder().sum(sum).avg(avg).max(max).min(min).cnt(cnt).build();
    }
}