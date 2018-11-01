package com.chian.mqclient.domain;

public class MqProducerParam extends MqParamEntity {

    private String msg_send_way = "ORDER"; //消息发送方式：ORDER（可靠同步发送）、ASYNC（可靠异步发送）、ONEWAY（单向(Oneway)发送）, 默认为可靠同步方式

    private String producer_id; //生产者ID

    private String sharding_key; //分区关键字。分区顺序消息中区分不同分区的关键字段，sharding key 于普通消息的 key 是完全不同的概念。

    private String msg; //消息内容

    private Long delay_time; //针对延时消息有效，延时消息，单位毫秒（ms），在指定延迟时间（当前时间之后）进行投递，例如消息在 3 秒后投递

    private String interval_time; //针对定时任务。时间格式如下：2016-03-07 16:21:00

    public String getMsg_send_way() {
        return msg_send_way;
    }

    public void setMsg_send_way(String msg_send_way) {
        this.msg_send_way = msg_send_way;
    }

    public String getProducer_id() {
        return producer_id;
    }

    public void setProducer_id(String producer_id) {
        this.producer_id = producer_id;
    }

    public String getSharding_key() {
        return sharding_key;
    }

    public void setSharding_key(String sharding_key) {
        this.sharding_key = sharding_key;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getDelay_time() {
        return delay_time;
    }

    public void setDelay_time(Long delay_time) {
        this.delay_time = delay_time;
    }

    public String getInterval_time() {
        return interval_time;
    }

    public void setInterval_time(String interval_time) {
        this.interval_time = interval_time;
    }

    @Override
    public String toString() {
        return "MqProducerParam{" +
                "msg_send_way='" + msg_send_way + '\'' +
                ", producer_id='" + producer_id + '\'' +
                ", sharding_key='" + sharding_key + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
