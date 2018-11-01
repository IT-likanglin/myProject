package com.chlian.mqproducer.domain;

/**
 * MQ APi服务层参数封装实体类
 */
public class MqParamEntity {

    private String topic; // 消息topic

    private String tags; //发送以及订阅的tag标签名，多个标签之间用||分割，如：TagA||TagB,消费者中，ALL代表订阅当前topic下的所有标签

    private String msg_key; //消息业务KEY字段

    private String msg_type = "ORDER"; //消息类型，四种：ORDER(顺序消息)、TRANSACTION(事务消息)、INTERVAL(定时消息)、DELAY延时消息，默认为顺序消息


    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getMsg_key() {
        return msg_key;
    }

    public void setMsg_key(String msg_key) {
        this.msg_key = msg_key;
    }

    public String getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(String msg_type) {
        this.msg_type = msg_type;
    }

    @Override
    public String toString() {
        return "MqParamEntity{" +
                "topic='" + topic + '\'' +
                ", tags='" + tags + '\'' +
                ", msg_key='" + msg_key + '\'' +
                ", msg_type='" + msg_type + '\'' +
                '}';
    }
}
