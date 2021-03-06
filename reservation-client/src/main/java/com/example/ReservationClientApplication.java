package com.example;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.EXPECTATION_FAILED;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.messaging.support.MessageBuilder.withPayload;

import java.util.List;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableCircuitBreaker
@EnableBinding(ReservationsBinding.class)
public class ReservationClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationClientApplication.class, args);
	}

	@Bean
	@ConditionalOnProperty(name = "spring.cloud.discovery.enabled", havingValue = "true", matchIfMissing = true)
	public ApplicationRunner discoveryClientDemo(DiscoveryClient discovery) {
		return args -> {
			try {
				log.info("------------------------------");
				log.info("DiscoveryClient Example");

				discovery.getInstances("reservationservice").forEach(instance -> {
					log.info("Reservation service: ");
					log.info("  ID: {}", instance.getServiceId());
					log.info("  URI: {}", instance.getUri());
					log.info("  Meta: {}", instance.getMetadata());
				});

				log.info("------------------------------");
			} catch (Exception e) {
				log.error("DiscoveryClient Example Error!", e);
			}
		};
	}

	@Bean @LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}

interface ReservationsBinding {

    @Output("createReservation")
    MessageChannel createReservation();

}

@FeignClient(name = "verifierservice")
interface VerifierClient {

    @RequestMapping(method = RequestMethod.POST, path = "/check", consumes = APPLICATION_JSON_VALUE)
    VerificationResult check(ReservationRequest request);

}

@NoArgsConstructor
@AllArgsConstructor
@Data
class VerificationResult {

    boolean eligible;

}

@NoArgsConstructor
@AllArgsConstructor
@Data
class ReservationRequest {

	String name;
	int age;

}

@FeignClient(name = "reservationservice", fallback = ReservationsClientFallback.class)
interface ReservationsClient {

	@RequestMapping(path = "/reservations", method = RequestMethod.GET)
	Resources<Reservation> listReservations();

    @RequestMapping(path = "/reservations", method = RequestMethod.POST)
    ResponseEntity<Void> createReservation(@RequestBody Reservation reservation);
}

@Component
class ReservationsClientFallback implements ReservationsClient {

	@Override
	public Resources<Reservation> listReservations() {
		return new Resources<>(asList("This is fallback".split(" ")).stream()
			.map(Reservation::new)
			.collect(toList()));
	}

    @Override
    public ResponseEntity<Void> createReservation(Reservation reservation) {
        return ResponseEntity.status(SERVICE_UNAVAILABLE).build();
    }
}

@Slf4j
@RestController
@RequestMapping("/reservations")
class ReservationsController {

	private final RestTemplate rest;
	private final ReservationsClient client;
    private final VerifierClient verifier;
    private final ReservationsBinding binding;
    private final ObjectMapper json;

    public ReservationsController(RestTemplate rest, ReservationsClient client,
                                  VerifierClient verifier, ReservationsBinding binding,
                                  ObjectMapper json) {
		this.rest = rest;
		this.client = client;
        this.verifier = verifier;
        this.binding = binding;
        this.json = json;
    }

	@GetMapping("/names")
	public List<String> names() {
		log.info("Calling names...");
		ParameterizedTypeReference<Resources<Reservation>> responseType =
			new ParameterizedTypeReference<Resources<Reservation>>() {};
		ResponseEntity<Resources<Reservation>> exchange =
			rest.exchange("http://reservationservice/reservations", GET, null, responseType);
		return exchange.getBody().getContent().stream()
			.map(Reservation::getName)
			.collect(toList());
	}

	@GetMapping("/feign-names")
	public List<String> feignNames() {
		log.info("Calling feign-names...");
		return client.listReservations().getContent().stream()
			.map(Reservation::getName)
			.collect(toList());
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody ReservationRequest request) throws Exception {
        log.info("Calling create...");
        VerificationResult result = verifier.check(request);
        if (result.isEligible()) {
            log.info("Eligible");
            if (binding.createReservation().send(
                withPayload(json.writeValueAsString(new Reservation(request.name))).build())) {
                return ResponseEntity.status(CREATED).build();
            } else {
                return ResponseEntity.status(SERVICE_UNAVAILABLE).build();
            }
        } else {
            log.info("Not eligible");
            return ResponseEntity.status(EXPECTATION_FAILED).build();
        }
	}
}

@NoArgsConstructor
@AllArgsConstructor
@Data
class Reservation {

	String name;

}
