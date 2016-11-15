package com.example;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
public class ReservationServiceApplicationTests {

    @Autowired MockMvc mvc;

	@Test
	public void should_return_photo_link_with_reservation_resource() throws Exception {
        // when
		mvc.perform(get("/reservations/1"))
			.andDo(print())

        // then
			.andExpect(jsonPath("@.name").value(startsWith("Bartek")))
			.andExpect(jsonPath("@._links.photo.href").exists());
	}
}
