package com.chlian.mqservice.service.impl;

import com.aliyun.openservices.ons.api.*;
import com.aliyun.openservices.ons.api.order.OrderProducer;
import com.chlian.mqservice.domain.MqProducerParam;
import com.chlian.mqservice.domain.RestResult;
import com.chlian.mqservice.service.IApiMqProducerService;
import com.chlian.mqservice.util.UUIDUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * mq生产者生产消息逻辑层
 * @author liujiao
 *
 * @// TODO: 2018/10/29   事务消息发送
 * @// TODO: 2018/10/29 消息发送失败后的处理
 */
@Service
public class ApiMqProducerService implements IApiMqProducerService {

    @Value("${ALIYUN.MQ.ACCESS_KEY}")
    private String accessKey;

    @Value("${ALIYUN.MQ.SECRET_KEY}")
    private String secertKey;

    @Value("${ALIYUN.MQ.ONSADDR}")
    private String onsaddr;


    @Override
    public RestResult<?> createProducer(MqProducerParam producer) {

        RestResult restResult;

        //判断需要发送的消息类型，分别调用不同的方法进行处理
        //消息类型，四种：ORDER(顺序消息)、TRANSACTION(事务消息)、INTERVAL(定时消息)、DELAY延时消息，默认为顺序消息
        if ("TRANSACTION".equals(producer.getMsg_type())) {
            //TODO 事务消息发送--暂时为顺序消息发送
            restResult = this.sendOrderMessgae(producer);
            //restResult = this.sendTransactionMessage(producer);
        } else if ("INTERVAL".equals(producer.getMsg_type())) {
            //定时消息发送
            restResult = this.sendIntervalMessage(producer);
        } else if ("DELAY".equals(producer.getMsg_type())) {
            //延时消息发送
            restResult = this.sendDelayMessage(producer);
        } else {
            //顺序消息发送
            restResult = this.sendOrderMessgae(producer);
            //return new RestResult<>().error("消息类型不合法！");
        }

        return restResult;
    }


    /**
     * 发送顺序消息, 消息类型和发送方式必须都是顺序类型
     *
     * @param producerParam
     * @return
     */
    private RestResult<?> sendOrderMessgae(MqProducerParam producerParam) {

        Properties properties = getProperties();

        RestResult result = new RestResult();

        // 您在控制台创建的 Producer ID
        properties.put(PropertyKeyConst.ProducerId, producerParam.getProducer_id());

        OrderProducer producer = ONSFactory.createOrderProducer(properties);
        // 在发送消息前，必须调用 start 方法来启动 Producer，只需调用一次即可。
        producer.start();

        Message msg = getMessage(producerParam);

        // 设置代表消息的业务关键属性，请尽可能全局唯一。
        // 以方便您在无法正常收到消息情况下，可通过 MQ 控制台查询消息并补发。
        // 注意：不设置也不会影响消息正常收发
        String uuidKey = UUIDUtils.getUUID();
        msg.setKey(producerParam.getMsg_key() == null ? uuidKey : producerParam.getMsg_key());

        // 分区顺序消息中区分不同分区的关键字段，sharding key 于普通消息的 key 是完全不同的概念。
        // 全局顺序消息，该字段可以设置为任意非空字符串。
        String shardingKey = StringUtils.isEmpty(producerParam.getSharding_key()) ? UUIDUtils.getUUID() : producerParam.getSharding_key();

        //消息发送
        try {
            SendResult sendResult = producer.send(msg, shardingKey);

            // 发送消息，只要不抛异常就是成功result.setCode("200");
            if (sendResult != null) {
                result.setMsg("消息发送成功");
                Map<String, Object> data = new HashMap<>();
                data.put("msg_key", uuidKey);
                data.put("sharding_key", shardingKey);
                data.put("message_id", sendResult.getMessageId());
                data.put("topic", sendResult.getTopic());
                result.setData(data);

                String log = new Date() + " Send mq message success. Topic is:" + msg.getTopic() + " msgId is: " + sendResult.getMessageId();
                logger.info(log);
                System.out.println(log);
            }
        } catch (Exception e) {
            // 消息发送失败，需要进行重试处理，可重新发送这条消息或持久化这条数据进行补偿处理
            String log = new Date() + " Send mq message failed. Topic is:" + msg.getTopic();
            System.out.println(log);
            logger.error(log);

            e.printStackTrace();

            result.setCode("500");
            result.setMsg("消息发送失败，详细信息：" + e.getMessage());
        }

        return result;
    }


    /**
     * 发送事务消息
     *
     * @param producer
     * @return
     */
    private RestResult sendTransactionMessage(MqProducerParam producer) {
        Properties properties = getProperties();

        //TODO
        return null;
    }

    /**
     * 发送定时消息
     *
     * @param producerParam
     * @return
     */
    private RestResult sendIntervalMessage(MqProducerParam producerParam) {

        Properties properties = getProperties();

        RestResult result = new RestResult();

        Producer producer = ONSFactory.createProducer(properties);
        // 在发送消息前，必须调用 start 方法来启动 Producer，只需调用一次即可。
        producer.start();

        Message msg = getMessage(producerParam);

        // 设置代表消息的业务关键属性，请尽可能全局唯一
        // 以方便您在无法正常收到消息情况下，可通过 MQ 控制台查询消息并补发。
        // 注意：不设置也不会影响消息正常收发
        String uuidKey = UUIDUtils.getUUID();
        msg.setKey(producerParam.getMsg_key() == null ? uuidKey : producerParam.getMsg_key());

        try {
            // 定时消息，单位毫秒（ms），在指定时间戳（当前时间之后）进行投递，例如 2016-03-07 16:21:00 投递。如果被设置成当前时间戳之前的某个时刻，消息将立刻投递给消费者。
            long timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(producerParam.getInterval_time()).getTime();
            msg.setStartDeliverTime(timeStamp);
            // 发送消息，只要不抛异常就是成功
            SendResult sendResult = producer.send(msg);

            String log = new Date() + " Send mq message success. Topic is:" + msg.getTopic() + " msgId is: " + sendResult.getMessageId();
            logger.info(log);
            System.out.println(log);

            result.setMsg("消息发送成功");
            Map<String, Object> data = new HashMap<>();
            data.put("msg_key", uuidKey);
            data.put("sharding_key", "");
            data.put("message_id", sendResult.getMessageId());
            data.put("topic", sendResult.getTopic());
            result.setData(data);
        } catch (Exception e) {
            // 消息发送失败，需要进行重试处理，可重新发送这条消息或持久化这条数据进行补偿处理
            String log = new Date() + " Send mq message failed. Topic is:" + msg.getTopic();
            logger.error(log);
            System.out.println(log);

            e.printStackTrace();

            result.setCode("500");
            result.setMsg("消息发送失败，详细信息：" + e.getMessage());
        }
        return result;
    }

    /**
     * 发送延时消息
     *
     * @param producerParam
     * @return
     */
    private RestResult sendDelayMessage(MqProducerParam producerParam) {

        Properties properties = getProperties();

        RestResult result = new RestResult();

        Producer producer = ONSFactory.createProducer(properties);
        // 在发送消息前，必须调用 start 方法来启动 Producer，只需调用一次即可。

        producer.start();

        Message msg = getMessage(producerParam);

        // 设置代表消息的业务关键属性，请尽可能全局唯一。
        // 以方便您在无法正常收到消息情况下，可通过 MQ 控制台查询消息并补发。
        // 注意：不设置也不会影响消息正常收发
        String uuidKey = UUIDUtils.getUUID();
        msg.setKey(producerParam.getMsg_key() == null ? uuidKey : producerParam.getMsg_key());

        // 分区顺序消息中区分不同分区的关键字段，sharding key 于普通消息的 key 是完全不同的概念。
        // 全局顺序消息，该字段可以设置为任意非空字符串。
        String shardingKey = StringUtils.isEmpty(producerParam.getSharding_key()) ? UUIDUtils.getUUID() : producerParam.getSharding_key();

        try {

            // 设置消息需要被投递的时间
            System.out.println("延迟时间：" + producerParam.getDelay_time());
            Long delayTime = producerParam.getDelay_time();
            msg.setStartDeliverTime(System.currentTimeMillis() + delayTime);
            System.out.println("延迟时间：" + msg.getStartDeliverTime());


            SendResult sendResult = null;

            //final String msgId = "";

            //消息发送方式：ORDER（可靠同步发送）、ASYNC（可靠异步发送）、ONEWAY（单向(Oneway)发送）, 默认为可靠同步方式
            if ("ASYNC".equals(producerParam.getMsg_send_way())) {
                //可靠异步发送
                producer.sendAsync(msg, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        //消息发送成功
                        result.setCode("200");
                        result.setMsg("消息发送成功");
                        Map<String, Object> data = new HashMap<>();
                        data.put("msg_key", uuidKey);
                        data.put("sharding_key", shardingKey);
                        data.put("message_id", sendResult == null ? "" : sendResult == null ? "" : sendResult.getMessageId());
                        data.put("topic", sendResult == null ? "" : producerParam.getTopic());
                        result.setData(data);

                        String log = new Date() + " Send mq message success. Topic is:" + msg.getTopic() + " msgId is: " + sendResult == null ? "" : sendResult.getMessageId();
                        logger.info(log);
                        System.out.println(log);
                    }

                    @Override
                    public void onException(OnExceptionContext onExceptionContext) {
                        // 消息发送失败，需要进行重试处理，可重新发送这条消息或持久化这条数据进行补偿处理
                    }
                });
            } else if ("ONEWAY".equals(producerParam.getMsg_send_way())) {
                // 单向(Oneway)发送.由于在 oneway 方式发送消息时没有请求应答处理，一旦出现消息发送失败，则会因为没有重试而导致数据丢失。
                // 若数据不可丢，建议选用可靠同步或可靠异步发送方式。
                producer.sendOneway(msg);
            } else {
                //可靠同步发送
                sendResult = producer.send(msg);
            }

            result.setCode("200");
            result.setMsg("消息发送成功");
            Map<String, Object> data = new HashMap<>();
            data.put("msg_key", uuidKey);
            data.put("sharding_key", shardingKey);
            data.put("message_id", sendResult == null ? "" : sendResult == null ? "" : sendResult.getMessageId());
            data.put("topic", sendResult == null ? "" : producerParam.getTopic());
            result.setData(data);

            String log = new Date() + " Send mq message success，messageID is：" + msg.getMsgID();
            logger.info(log);
            System.out.println(log);
        } catch (Exception e) {
            // 消息发送失败，需要进行重试处理，可重新发送这条消息或持久化这条数据进行补偿处理
            String log = new Date() + " Send mq message failed. Topic is:" + msg.getTopic();
            logger.error(log);
            System.out.println(log);
            e.printStackTrace();

            result.setCode("500");
            result.setMsg("消息发送失败，topic为：" + msg.getTopic() + "详细信息：" + e.getMessage());

            sendFaildHandler(msg);
        }

        return result;
    }

    /**
     * 消息发送失败后的处理
     * @param msg
     */
    private void sendFaildHandler(Message msg) {
        //TODO
    }


    /**
     * 获取Message对象
     * @param producerParam
     * @return
     */
    private Message getMessage(MqProducerParam producerParam) {
        Message msg = new Message( //
                // 您在控制台创建的 Topic
                producerParam.getTopic(),
                // Message Tag, 可理解为 Gmail 中的标签，对消息进行再归类，方便 Consumer 指定过滤条件在 MQ 服务器过滤
                producerParam.getTags(),
                // Message Body 可以是任何二进制形式的数据， MQ 不做任何干预，需要 Producer 与 Consumer 协商好一致的序列化和反序列化方式
                producerParam.getMsg().getBytes());
        return msg;
    }

    private Properties getProperties() {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.AccessKey, accessKey);
        properties.put(PropertyKeyConst.SecretKey, secertKey);
        properties.put(PropertyKeyConst.ONSAddr, onsaddr);
        return properties;
    }

}
