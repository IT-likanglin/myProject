package com.chlian.mqservice.delay;

import com.aliyun.openservices.ons.api.*;

import java.util.Date;
import java.util.Properties;

/**
 * 延迟消息生产者
 */
public class DelayProducer {

    public static void main(String[] args) {
        Properties properties = new Properties();
        // AccessKey 阿里云身份验证，在阿里云服务器管理控制台创建
        properties.put(PropertyKeyConst.AccessKey, "LTAIDSbm44GHI4Lg");
        // SecretKey 阿里云身份验证，在阿里云服务器管理控制台创建
        properties.put(PropertyKeyConst.SecretKey, "ECX29NlSRpzxnXySdS3HNwxDL6puNm");
        // 设置 TCP 接入域名，进入 MQ 控制台的生产者管理页面，在左侧操作栏单击获取接入点获取
        // 此处以公共云生产环境为例
        properties.put(PropertyKeyConst.ONSAddr,
                "http://onsaddr-internet.aliyun.com/rocketmq/nsaddr4client-internet");
        Producer producer = ONSFactory.createProducer(properties);
        // 在发送消息前，必须调用 start 方法来启动 Producer，只需调用一次即可。
        producer.start();
        Message msg = new Message( //
                // 您在控制台创建的 Topic
                "cid_test",
                // Message Tag, 可理解为 Gmail 中的标签，对消息进行再归类，方便 Consumer 指定过滤条件在 MQ 服务器过滤
                "TagA",
                // Message Body 可以是任何二进制形式的数据， MQ 不做任何干预，需要 Producer 与 Consumer 协商好一致的序列化和反序列化方式
                "Hello MQ".getBytes());
        // 设置代表消息的业务关键属性，请尽可能全局唯一。
        // 以方便您在无法正常收到消息情况下，可通过 MQ 控制台查询消息并补发。
        // 注意：不设置也不会影响消息正常收发
        msg.setKey("ORDERID_100");
        try {
            // 延时消息，单位毫秒（ms），在指定延迟时间（当前时间之后）进行投递，例如消息在 3 秒后投递
            long delayTime = System.currentTimeMillis() + 3000;
            // 设置消息需要被投递的时间
            msg.setStartDeliverTime(delayTime);
            SendResult sendResult = producer.send(msg);
            // 同步发送消息，只要不抛异常就是成功
            if (sendResult != null) {
                System.out.println(new Date() + " Send mq message success. Topic is:" + msg.getTopic() + " msgId is: " + sendResult.getMessageId());
            }
        } catch (Exception e) {
            // 消息发送失败，需要进行重试处理，可重新发送这条消息或持久化这条数据进行补偿处理
            System.out.println(new Date() + " Send mq message failed. Topic is:" + msg.getTopic());
            e.printStackTrace();
        }
        // 在应用退出前，销毁 Producer 对象<br>
        // 注意：如果不销毁也没有问题
        producer.shutdown();
    }

}
