package com.example.ping.push

import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Answers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import spock.lang.Specification

import java.util.logging.Logger

@SpringBootTest
class MessageSendServerTest extends Specification {

    @Autowired
    private MessageSendServer messageSendServer
//    @MockBean
//    private WebClient webClient

    def setup() {
        // 创建一个模拟的 WebClient
//        webClient =Mock(WebClient)
//        messageSendServer=new MessageSendServer()
    }

    def "Test pushMessages method with success request"() {
        given:

        when:
        messageSendServer.pongPushInit("hello",null)

        then:
        print("world")

    }

    def "Test getLockTry"() {
        when:
        messageSendServer.getLockTry()
        then:
        print("getLockTry")
    }



}