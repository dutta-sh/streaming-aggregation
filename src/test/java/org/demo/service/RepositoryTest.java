package org.demo.service;

import lombok.extern.log4j.Log4j2;
import org.demo.dto.Input;
import org.demo.dto.Output;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Log4j2
public class RepositoryTest {

    @Autowired
    private Repository repo;

    @Before
    public void setup() {
        repo.init();
    }

    @Test
    public void insertTest() {
        int trueCnt = 0;
        int falseCnt = 0;
        Date now = new Date();
        for(int i = 0; i < 100; i ++) {
            Date past = new Date(now.getTime() - i*1000);
            Input ip1 = Input.builder().amount(i*1000D).tsp(past.getTime()).build();
            Input ip2 = Input.builder().amount(i*1050D).tsp(past.getTime()).build();
            Boolean resp1 = repo.insert(ip1);
            Boolean resp2 = repo.insert(ip2);
            log.info("added: " + ip1 + " :: " + resp1);
            log.info("added: " + ip2 + " :: " + resp2);
            if(resp1)
                trueCnt++;
            else
                falseCnt++;
            if(resp2)
                trueCnt++;
            else
                falseCnt++;
        }

        assertEquals(120, trueCnt);
        assertEquals(80, falseCnt);
    }

    @Test
    public void computeTest() throws InterruptedException {
        repo.insert(Input.builder().amount(1000D).tsp(new Date().getTime()).build());
        repo.insert(Input.builder().amount(2000D).tsp(new Date().getTime()).build());
        Output out = repo.compute();
        log.info(out);
        assertEquals(new Integer(2), out.getCnt());
        assertEquals(new Double(2000), out.getMax());
        assertEquals(new Double(1000), out.getMin());
        assertEquals(new Double(1500), out.getAvg());
        assertEquals(new Double(3000), out.getSum());

        log.info("Sleeping for 5 secs");
        Thread.sleep(5000);
        repo.insert(Input.builder().amount(3000D).tsp(new Date().getTime()).build());
        repo.insert(Input.builder().amount(4000D).tsp(new Date().getTime()).build());
        out = repo.compute();
        log.info(out);
        assertEquals(new Integer(4), out.getCnt());
        assertEquals(new Double(4000), out.getMax());
        assertEquals(new Double(1000), out.getMin());
        assertEquals(new Double(2500), out.getAvg());
        assertEquals(new Double(10000), out.getSum());

        log.info("Sleeping for 57 secs");
        Thread.sleep(57000);
        out = repo.compute();
        log.info(out);
        assertEquals(new Integer(2), out.getCnt());
        assertEquals(new Double(4000), out.getMax());
        assertEquals(new Double(3000), out.getMin());
        assertEquals(new Double(3500), out.getAvg());
        assertEquals(new Double(7000), out.getSum());

        log.info("Sleeping for 4 secs");
        Thread.sleep(4000);
        out = repo.compute();
        log.info(out);
        assertEquals(new Integer(0), out.getCnt());
        assertEquals(new Double(0), out.getMax());
        assertEquals(new Double(0), out.getMin());
        assertEquals(new Double(0), out.getAvg());
        assertEquals(new Double(0), out.getSum());
    }
}
