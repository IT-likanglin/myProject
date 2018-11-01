package com.chlian.mqservice.controller;

import com.chlian.mqservice.domain.MqProducerParam;
import com.chlian.mqservice.domain.RestResult;
import com.chlian.mqservice.service.IApiMqProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 生产者服务API controller
 * @author liujiao
 */
@RestController
@RequestMapping("/mq/producer")
public class ApiMqProducerController extends BaseController {

    @Autowired
    private IApiMqProducerService mqService;

    @Value("#{file}")
    private static String fileName;

    /**
     * 创建生产者服务
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public RestResult createProducer(MqProducerParam producer) {
        try {
            RestResult<?> restResult= mqService.createProducer(producer);
            return restResult;
        } catch (Exception e) {
            logger.error("服务端创建生产者失败，详细信息：" + e.getMessage());
            System.out.println(e.getMessage());
        }
        return new RestResult().error("创建生产者失败");
     }


}
