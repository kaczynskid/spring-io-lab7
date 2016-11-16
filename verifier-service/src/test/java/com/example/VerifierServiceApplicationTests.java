package com.example;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = VerifierServiceApplication.class)
public class VerifierServiceApplicationTests {

	@Autowired
    WebApplicationContext applicationContext;

    @Before
    public void setup() {
        RestAssuredMockMvc.webAppContextSetup(applicationContext);
    }

	@Test
	public void contextLoads() {
	}
}
