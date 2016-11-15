package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.hateoas.Resource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@JsonTest
public class ReservationsRepresentationTest {

    @Autowired JacksonTester<Resource<Reservation>> json;

    @Test
    public void should_serialize_reservation() throws Exception {
        // given
        Reservation reservation = new Reservation("John");
        Resource<Reservation> resource = new Resource<>(reservation);
        resource = new ReservationResourceProcessor().process(resource);

        // when
        JsonContent<Resource<Reservation>> result = json.write(resource);

        // then
        assertThat(result).extractingJsonPathStringValue("@.name").startsWith("John");
        assertThat(result).extractingJsonPathStringValue("@.links[0].href")
            .isEqualTo("https://www.google.pl/search?tbm=isch&q=John");
    }
}
