package com.chlian.mqservice.service;

import com.chlian.mqservice.domain.MqConsumerParam;

public interface IApiMqConsumerService {
    /**
     * 注册监听
     * @param mqConsumerParam
     * @param request
     * @return
     */
    void registerConsumer(MqConsumerParam mqConsumerParam, String request);
}
