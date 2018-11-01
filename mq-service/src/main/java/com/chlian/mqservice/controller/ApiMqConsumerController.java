package com.chlian.mqservice.controller;

import com.chlian.mqservice.domain.MqConsumerParam;
import com.chlian.mqservice.service.IApiMqConsumerService;
import com.chlian.mqservice.util.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 服务消费者API controller
 * @author liujiao
 */
@RestController
@RequestMapping("/mq/consumer")
public class ApiMqConsumerController extends BaseController{

    @Autowired
    private IApiMqConsumerService consumerService;

    /**
     * 注册消费者监听
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void registerConsumer(MqConsumerParam mqConsumerParam, HttpServletRequest request) {
        String ip = HttpUtils.getIp2(request);

        consumerService.registerConsumer(mqConsumerParam, ip);
    }



}
