package org.demo.service;

import lombok.extern.log4j.Log4j2;
import org.demo.dto.Input;
import org.demo.dto.Output;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Log4j2
public class RestServiceTest {
    @Autowired
    private RestService restService;
    @Autowired
    private Repository repo;

    @Before
    public void setup() {
        repo.init();
    }

    @Test
    public void healthCheckTest() {
        ResponseEntity resp = restService.healthCheck();
        assertEquals("service is up", resp.getBody());
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    public void postTransactionTest() throws Exception {
        Map<Integer, Integer> respMap = new HashMap<>();
        respMap.put(201, 0);
        respMap.put(204, 0);

        for(int i = 0; i < 100; i ++) {
            Input input = Input.builder().amount(i * 100D).tsp((new Date()).getTime() - i*1000).build();
            ResponseEntity resp = restService.postTransaction(input);
            respMap.put(resp.getStatusCodeValue(), respMap.get(resp.getStatusCodeValue()) + 1);
        }

        log.info(respMap);
        assertEquals(new Integer(60), respMap.get(201));
        assertEquals(new Integer(40), respMap.get(204));
    }

    @Test
    public void getStatisticsTest() throws Exception {
        for(int i = 0; i < 100; i ++) {
            Input input = Input.builder().amount(i * 100D).tsp(new Date().getTime() - i*1000).build();
            repo.insert(input);
        }

        ResponseEntity<Output> prev = null;
        ResponseEntity<Output> now = null;
        for(int i = 0; i < 70; i++) {
            log.info("Sleeping for " + (i*10/1000) + " secs");
            Thread.sleep(i * 10);
            prev = now;
            now = restService.getStatistics();
            if(prev != null) {
                assertTrue(now.getBody().getCnt() <= prev.getBody().getCnt());
                assertTrue(now.getBody().getAvg() <= prev.getBody().getAvg());
                assertTrue(now.getBody().getSum() <= prev.getBody().getSum());
                assertTrue(now.getBody().getMax() <= prev.getBody().getMax());
                assertTrue(now.getBody().getCnt() <= prev.getBody().getCnt());
            }
        }
    }
}
