package com.zzh.ping.push


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class MessageSendServerTest extends Specification {

    @Autowired
    private MessageSendServer messageSendServer

    def setup() {

    }

    def "Test pushMessages method with success request"() {
        given:

        when:
        messageSendServer.pongPushInit("hello", messageSendServer.getLockTry())

        then:
        print("world")

    }

    def "Test getLockTry"() {
        when:
        messageSendServer.getLockTry()
        then:
        print("getLockTry")
    }

    def "Test pushMessages"() {
        when:
        messageSendServer.sendMessages()
        then:
        print("pushMessages")
    }

    def "Test getFileLimit"() {
        when:
        messageSendServer.getFileLimit()
        then:
        print("getFileLimit")
    }

}