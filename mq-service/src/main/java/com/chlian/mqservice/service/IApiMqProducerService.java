package com.chlian.mqservice.service;


import com.chlian.mqservice.domain.MqProducerParam;
import com.chlian.mqservice.domain.RestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface IApiMqProducerService {

    Logger logger = LoggerFactory.getLogger(IApiMqProducerService.class);

    /**
     * 创建生产者
     * @param producer
     * @return
     */
    public RestResult<?> createProducer(MqProducerParam producer);

}
