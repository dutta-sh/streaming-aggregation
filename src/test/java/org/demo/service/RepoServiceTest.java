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
public class RepoServiceTest {

    @Autowired
    private RepoService repoService;
    @Autowired
    private Repository repo;

    @Before
    public void setup() {
        repo.init();
    }

    @Test
    public void processNewAddTest() throws Exception {
        Input input = Input.builder().amount(200D).tsp(new Date().getTime()).build();
        Boolean response = repoService.add(input).get();
        assertEquals(true, response);
    }

    @Test
    public void processOldAddTest() throws Exception {
        Input input = Input.builder().amount(200D).tsp(new Date().getTime() - 70000).build();
        Boolean response = repoService.add(input).get();
        assertEquals(false, response);
    }

    @Test
    public void processFutureAddTest() throws Exception {
        Input input = Input.builder().amount(200D).tsp(new Date().getTime() + 20000).build();
        Boolean response = repoService.add(input).get();
        assertEquals(false, response);
    }

    @Test
    public void processBadAddTest() throws Exception {
        Input input = Input.builder().amount(null).tsp(new Date().getTime()).build();
        Boolean response = repoService.add(input).get();
        assertEquals(false, response);
    }

    @Test
    public void getTest() throws Exception {
        for(int i = 0; i < 100; i ++) {
            Input input = Input.builder().amount(i * 100D).tsp(new Date().getTime() - i*1000).build();
            repoService.add(input).get();
        }

        Output output = repoService.get().get();
        assertEquals(new Integer(60), output.getCnt());
        assertEquals(new Double(2950), output.getAvg());
        assertEquals(new Double(5900), output.getMax());
        assertEquals(new Double(0), output.getMin());
        assertEquals(new Double(177000), output.getSum());

        Thread.sleep(10000);
        output = repoService.get().get();
        assertEquals(new Integer(50), output.getCnt());
        assertEquals(new Double(2450), output.getAvg());
        assertEquals(new Double(4900), output.getMax());
        assertEquals(new Double(0), output.getMin());
        assertEquals(new Double(122500), output.getSum());
    }
}
