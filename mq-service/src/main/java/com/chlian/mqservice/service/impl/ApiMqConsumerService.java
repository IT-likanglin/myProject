package com.chlian.mqservice.service.impl;

import com.aliyun.openservices.ons.api.*;
import com.chlian.mqservice.domain.MqConsumerParam;
import com.chlian.mqservice.service.IApiMqConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;

@Service
public class ApiMqConsumerService implements IApiMqConsumerService {

    protected Logger logger = LoggerFactory.getLogger(ApiMqConsumerService.class);

    @Value("${file}")
    private String file;

    @Value("${ALIYUN.MQ.ACCESS_KEY}")
    private String accessKey;

    @Value("${ALIYUN.MQ.SECRET_KEY}")
    private String secertKey;

    @Value("${ALIYUN.MQ.ONSADDR}")
    private String onsaddr;

    @Override
    public void registerConsumer(MqConsumerParam mqConsumerParam, String ip) {

        logger.info("============开始注册服务，注册参数：" + mqConsumerParam);

        Properties properties = getProperties();

        properties.put(PropertyKeyConst.ConsumerId, mqConsumerParam.getConsumer_id());
        properties.put(PropertyKeyConst.MessageModel, mqConsumerParam.getMessage_model());
        properties.put(PropertyKeyConst.SuspendTimeMillis, StringUtils.isEmpty(mqConsumerParam.getSuspend_time_millis()) ?
                "100" : mqConsumerParam.getSuspend_time_millis());
        // 消息消费失败时的最大重试次数
        properties.put(PropertyKeyConst.MaxReconsumeTimes, StringUtils.isEmpty(mqConsumerParam.getMax_reconsume_times()) ?
                "20" : mqConsumerParam.getMax_reconsume_times());

        // 在订阅消息前，必须调用 start 方法来启动 Consumer，只需调用一次即可。
        Consumer consumer1 = ONSFactory.createConsumer(properties);

        consumer1.start();

        consumer1.subscribe(
                mqConsumerParam.getTopic(),
                "ALL".equals(mqConsumerParam.getTags()) ? "*" : mqConsumerParam.getTags(),
                new MessageListener() {
                    @Override
                    public Action consume(Message message, ConsumeContext consumeContext) {
                        logger.info("==========接收到消息，msgId是" + message.getMsgID(), " 消息对象：" + message);

                        RestTemplate restTemplate = new RestTemplate();
                        String msg = new String(message.getBody());
                        String topic = message.getTopic();
                        String msgID = message.getMsgID();
                        String shardingKey = message.getShardingKey();
                        String key = message.getKey();
                        String tag = message.getTag();

                        //TODO 返回的数据处理
                        //监听消息后回调地址（该地址由客户端调用者传入）
                        String port = mqConsumerParam.getMessage_callback_port();
                        String path = mqConsumerParam.getMessage_callback_path(); //.replace("%2", "/");

                        String url = "http://" + ip + ":" + port + "/" + path;

                        logger.info("======订阅消息监听后回调地址：" + url);
                        System.out.println("订阅消息监听后回调地址：" + url);


                        RestTemplate client = new RestTemplate();
                        HttpHeaders headers = new HttpHeaders();
                        //  请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
                        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                        //  封装参数，千万不要替换为Map与HashMap，否则参数无法传递
                        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
                        //  也支持中文
                        params.add("msg", msg);
                        params.add("topic", topic);
                        params.add("msg_id", msgID);
                        params.add("sharding_key", shardingKey);
                        params.add("key", key);
                        params.add("tag", tag);
                        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(params, headers);

                        try {
                            client.exchange(url, HttpMethod.POST, requestEntity, String.class);
                            logger.info("===============消息回调发送成功！！！！");

                            //消费成功
                            return Action.CommitMessage;
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.error("============调用远程回调出现异常，异常信息：" + e.getMessage());
                        }

                        //消费失败，告知服务器稍后再投递这条消息，继续消费其他消息
                        return Action.ReconsumeLater;
                    }
                });

    }

    private Properties getProperties() {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.AccessKey, accessKey);
        properties.put(PropertyKeyConst.SecretKey, secertKey);
        properties.put(PropertyKeyConst.ONSAddr, onsaddr);
        return properties;
    }

}
