package org.demo.service;

import lombok.extern.log4j.Log4j2;
import org.demo.dto.Input;
import org.demo.dto.Output;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Log4j2
public class Repository {
    private class NthSec {
        Integer cnt; Double sum; Double max; Double min;

        private NthSec() {
            init();
        }

        private void init() {
            cnt = 0; sum = 0D; max = Double.MIN_VALUE; min = Double.MAX_VALUE;
        }
    }

    private Output out;
    private boolean reCompute = true;
    private static final int maxAge = 60; //in secs

    private NthSec[] nthSecs;
    {
        init();
    }

    public void init() {
        nthSecs = new NthSec[maxAge];
        for(int i = 0; i < maxAge; i++) {
            nthSecs[i] = new NthSec();
        }
    }

    public boolean insert(Input in) {
        long secsOld = (new Date().getTime() - in.getTsp()) / 1000;

        if(!(secsOld >= 0 && secsOld < maxAge))
            return false;

        NthSec nth = nthSecs[(int) secsOld];
        nth.cnt++;
        nth.sum += in.getAmount();
        if(in.getAmount() > nth.max)
            nth.max = in.getAmount();
        if(in.getAmount() < nth.min)
            nth.min = in.getAmount();

        reCompute = true;

        return true;
    }

    public Output compute() {
        if(!reCompute)
            return out.clone();

        Integer cnt = 0;
        Double sum = 0D;
        Double max = Double.MIN_VALUE;
        Double min = Double.MAX_VALUE;
        Double avg = 0D;

        for(NthSec nth : nthSecs) {
            if(nth.cnt == 0)
                continue;

            sum += nth.sum;
            cnt += nth.cnt;
            if(nth.max > max)
                max = nth.max;
            if(nth.min < min)
                min = nth.min;

        }
        if(cnt == 0) {
            sum = 0D; max = 0D; min = 0D; avg = 0D;
        } else {
            avg = sum/cnt;
        }
        reCompute = false;

        out = Output.builder().sum(sum).avg(avg).max(max).min(min).cnt(cnt).build();
        return out.clone();
    }

    public void shift() {
        reCompute = true;
        NthSec last = nthSecs[maxAge - 1];
        for(int i = maxAge-2; i >= 0; i--) {
            nthSecs[i+1] = nthSecs[i];
        }
        last.init();
        nthSecs[0] = last; //set 59th to 0th and reset it to default values. No new object is created or old object GCed
    }
}
