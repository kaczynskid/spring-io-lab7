package com.example;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
public class ReservationsVerificationTest {

    @Autowired ObjectMapper json;
    @Autowired MockMvc mvc;

    @Test
    public void should_allow_reservation_if_old_enough() throws Exception {
        mvc.perform(post("/reservations")
            .contentType(APPLICATION_JSON)
            .content(json.writeValueAsString(new ReservationRequest("John", 22))))
            .andExpect(status().isCreated());
    }

    @Test
    public void should_prevent_reservation_if_too_young() throws Exception {
        mvc.perform(post("/reservations")
            .contentType(APPLICATION_JSON)
            .content(json.writeValueAsString(new ReservationRequest("Jake", 17))))
            .andExpect(status().isExpectationFailed());
    }
}
