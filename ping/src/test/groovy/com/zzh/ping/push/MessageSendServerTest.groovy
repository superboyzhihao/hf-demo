package com.zzh.ping.push


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

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