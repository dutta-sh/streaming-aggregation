package org.demo.service;

import org.demo.dto.Input;
import org.demo.dto.Output;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//computes and stores statistics based on input during the
//last N seconds. Uses an array of N elements to store aggregate
//values for that second, and computes aggregates of aggregates
//when requested. Thus maintaining O(1) time and space complexity
//for all insert and retrieve operations.

@Component
public class Repository {
    //element of an array for each second
    //Any data coming in for that second will be
    //used to compute values for this object
    private class NthSec {
        Integer cnt; Double sum; Double max; Double min;

        private NthSec() {
            init();
        }

        //used to initialize or reset the object
        private void init() {
            cnt = 0; sum = 0D; max = Double.MIN_VALUE; min = Double.MAX_VALUE;
        }
    }

    private Output out;
    //used to avoid re-computation when no new data has come in
    //but under heavy load for compute request
    private boolean reCompute = true;
    private NthSec[] nthSecs;

    private final int maxAge = 60; //in secs
    private final Lock lock = new ReentrantLock();

    {
        init();
    }

    //used during start up and from test cases only
    //no need to make thread-safe
    void init() {
        nthSecs = new NthSec[maxAge];
        for(int i = 0; i < maxAge; i++) {
            nthSecs[i] = new NthSec();
        }
    }

    //called to insert data from a time in the past
    //recomputes one of the 60 array elements
    //locked to make thread-safe since during this time computation shouldn't take place
    //another option could have been to create 60 threads for each element, and allow
    //concurrent update as long as they updated different elements of the array
    public boolean insert(Input in) {
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

    //compute statistics based on snapshot of the array in point of time
    //locked to make thread-safe since during computation, insert shouldn't happen
    //and another process should also not compute, thus saving resource
    //returns a clone of the computed object, since the caller may try to modify the response
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

    //time has elapsed and hence the oldest element is no longer valid
    //shifting the array by 1 element to the right
    //the oldest element is reset and assigned to the newest position
    //otherwise new object needed to be created and the old object would eventually be GC-ed
    //no computation or insertion allowed during this operation
    public void shift() {
        lock.lock();

        reCompute = true;
        NthSec last = nthSecs[maxAge - 1];
        for(int i = maxAge-2; i >= 0; i--) {
            nthSecs[i+1] = nthSecs[i];
        }
        last.init();
        nthSecs[0] = last; //set 59th to 0th and reset it to default values

        lock.unlock();
    }
}
