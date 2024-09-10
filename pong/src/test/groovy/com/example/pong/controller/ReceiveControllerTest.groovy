package com.example.pong.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import spock.lang.Specification



@SpringBootTest
class ReceiveControllerTest extends Specification {

    @Autowired
    private ReceiveController receiveController;


    def "test receive success"() {
        given:
        def message = value
        when:
        Mono<ResponseEntity<String>> result = receiveController.receive(message)

        then:
        ResponseEntity<String> response=result.block()
        response == expected

        where:
        value | expected
        "hello"      | ResponseEntity.ok("world")

    }

}