package com.chlian.mqproducer.domain;

public class MqConsumerParam extends MqParamEntity {

    private String consumer_id = "CID_liujiao"; //必须。您在控制台创建的 Consumer ID

    private String max_reconsume_times; //非必须，默认为100。消息消费失败时的最大重试次数

    private String suspend_time_millis; //非必须，默认为20。顺序消息消费失败进行重试前的等待时间，单位(毫秒)

    private String message_model = "CLUSTERING"; //，非必须，订阅方式，分为集群订阅（CLUSTERING）和广播方式（BROADCASTING）订阅两种，默认为集群订阅

    private String message_callback_path; //必须，监听到订阅消息后回调地址

    private String message_callback_port; //必须，监听到订阅消息后回调端口号

    public String getConsumer_id() {
        return consumer_id;
    }

    public void setConsumer_id(String consumer_id) {
        this.consumer_id = consumer_id;
    }

    public String getMax_reconsume_times() {
        return max_reconsume_times;
    }

    public void setMax_reconsume_times(String max_reconsume_times) {
        this.max_reconsume_times = max_reconsume_times;
    }

    public String getSuspend_time_millis() {
        return suspend_time_millis;
    }

    public void setSuspend_time_millis(String suspend_time_millis) {
        this.suspend_time_millis = suspend_time_millis;
    }


    public void setMessage_model(String message_model) {
        this.message_model = message_model;
    }

    public String getMessage_model() {
        return message_model;
    }


    public String getMessage_callback_path() {
        return message_callback_path;
    }

    public void setMessage_callback_path(String message_callback_path) {
        this.message_callback_path = message_callback_path;
    }

    public String getMessage_callback_port() {
        return message_callback_port;
    }

    public void setMessage_callback_port(String message_callback_port) {
        this.message_callback_port = message_callback_port;
    }

    @Override
    public String toString() {
        return "MqConsumerParam{" +
                "consumer_id='" + consumer_id + '\'' +
                ", max_reconsume_times='" + max_reconsume_times + '\'' +
                ", suspend_time_millis='" + suspend_time_millis + '\'' +
                ", message_model='" + message_model + '\'' +
                ", message_callback_path='" + message_callback_path + '\'' +
                ", message_callback_port='" + message_callback_port + '\'' +
                '}';
    }
}
