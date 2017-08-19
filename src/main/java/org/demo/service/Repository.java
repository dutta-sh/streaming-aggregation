package org.demo.service;

import lombok.extern.log4j.Log4j2;
import org.demo.dto.Input;
import org.demo.dto.Output;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
    private NthSec[] nthSecs;

    private final int maxAge = 60; //in secs
    private final Lock lock = new ReentrantLock();
    private final ExecutorService pool = Executors.newFixedThreadPool(10);

    {
        init();
    }

    void init() {
        nthSecs = new NthSec[maxAge];
        for(int i = 0; i < maxAge; i++) {
            nthSecs[i] = new NthSec();
        }
    }

    public boolean insert(Input in) {

//        Callable<Boolean> callable = () -> {
//            long secsOld = (new Date().getTime() - in.getTsp()) / 1000;
//            if(!(secsOld >= 0 && secsOld < maxAge)) {
//                return false;
//            }
//
//            NthSec nth = nthSecs[(int) secsOld];
//
//            synchronized (nth) {
//                nth.cnt++;
//                nth.sum += in.getAmount();
//                if(in.getAmount() > nth.max)
//                    nth.max = in.getAmount();
//                if(in.getAmount() < nth.min)
//                    nth.min = in.getAmount();
//
//                reCompute = true;
//            }
//
//            return true;
//        };
//        return pool.submit(callable).get();

        lock.lock();

        long secsOld = (new Date().getTime() - in.getTsp()) / 1000;
        if(!(secsOld >= 0 && secsOld < maxAge)) {
            lock.unlock();
            return false;
        }

        NthSec nth = nthSecs[(int) secsOld];
        nth.cnt++;
        nth.sum += in.getAmount();
        if(in.getAmount() > nth.max)
            nth.max = in.getAmount();
        if(in.getAmount() < nth.min)
            nth.min = in.getAmount();

        reCompute = true;

        lock.unlock();

        return true;
    }

    public Output compute() {
        if(!reCompute)
            return out.clone();

        lock.lock();
        // Recheck because another thread might have acquired lock and changed state
        if(!reCompute) {
            lock.unlock();
            return out.clone();
        }

        Integer cnt = 0;
        Double sum = 0D;
        Double max = Double.MIN_VALUE;
        Double min = Double.MAX_VALUE;

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
            out = new Output();
        } else {
            out = Output.builder().sum(sum).avg(sum/cnt).max(max).min(min).cnt(cnt).build();
        }
        reCompute = false;

        lock.unlock();
        return out.clone();
    }

    public void shift() {
        lock.lock();

        reCompute = true;
        NthSec last = nthSecs[maxAge - 1];
        for(int i = maxAge-2; i >= 0; i--) {
            nthSecs[i+1] = nthSecs[i];
        }
        last.init();
        nthSecs[0] = last; //set 59th to 0th and reset it to default values. No new object is created or old object GCed

        lock.unlock();
    }
}
