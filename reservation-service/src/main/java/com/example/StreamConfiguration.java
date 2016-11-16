package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Profile("cloud")
@Configuration
@EnableBinding(Sink.class)
public class StreamConfiguration {
}

@Slf4j
@MessageEndpoint
class ReservationServiceActivator {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ObjectMapper json;

    @ServiceActivator(inputChannel = Sink.INPUT)
    public void createReservation(String reservationJson) throws Exception {
        log.info("Creating reservation from message: {}", reservationJson);
        Reservation reservation = json.readValue(reservationJson, Reservation.class);
        reservationRepository.save(reservation);
    }
}

