package com.anthem.nimbus.platform.core.process.mq;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MessageReceiver {

    public void receiveMessage(byte[] message) {
    	System.out.println("output message - " + new String(message));
        log.info("Received message .. ", new String(message));
    }
}