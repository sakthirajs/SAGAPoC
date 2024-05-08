//package com.hso.sagaordermanager.service;
//
//import org.springframework.amqp.core.AcknowledgeMode;
//import org.springframework.amqp.core.Message;
//import org.springframework.amqp.core.MessageListener;
//
//import java.util.List;
//
//public class RabbitMQListener implements MessageListener {
//
//    public void onMessage(Message message) {
//        System.out.println("Consuming Message - " + new String(message.getBody()));
//    }
//
//    @Override
//    public void containerAckMode(AcknowledgeMode mode) {
//        MessageListener.super.containerAckMode(mode);
//    }
//
//    @Override
//    public boolean isAsyncReplies() {
//        return MessageListener.super.isAsyncReplies();
//    }
//
//    @Override
//    public void onMessageBatch(List<Message> messages) {
//        MessageListener.super.onMessageBatch(messages);
//    }
//
//}
//
