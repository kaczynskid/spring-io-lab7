package com.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@RestController
@RequestMapping("/message")
@EnableConfigurationProperties(MyMessage.class)
public class MessageController {
//    NO REFRESH!!!
//    private String message;
//
//    public MessageController(@Value("${message}") String message) {
//        this.message = message;
//    }
//
//    @GetMapping
//    public MyMessage message() {
//        return new MyMessage(message);
//    }


//    private final Environment environment;
//
//    public MessageController(Environment environment) {
//        this.environment = environment;
//    }
//
//    @GetMapping
//    public MyMessage message() {
//        return new MyMessage(environment.getProperty("message"));
//    }


    private final MyMessage message;

    public MessageController(MyMessage message) {
        this.message = message;
    }

    @GetMapping
    public MyMessage message() {
        return new MyMessage(message.message);
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties
class MyMessage {

    String message;

}
