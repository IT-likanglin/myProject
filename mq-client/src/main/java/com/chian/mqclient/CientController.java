package com.chian.mqclient;

import com.chian.mqclient.domain.MqConsumerParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

/**
 * 创建消费者示例
 */
@RestController
@RequestMapping(value = "/consumer")
@PropertySource("classpath:application.properties")
public class CientController {

    protected Logger logger = LoggerFactory.getLogger(CientController.class);

    @Value("${mq.servce.path}")
    private String mqServerUrl;

    @Value("${server.port}")
    private String serverPort;

    /**
     * mq服务层监听到订阅消息后将数据返回该回调函数
     *
     * @param request
     */
    @RequestMapping(value = "callback", method = RequestMethod.POST)
    public void callback(HttpServletRequest request) {
        String msg_id = request.getParameter("msg_id");
        String msg = request.getParameter("msg");
        System.out.println("============监听到的消息的ID是：" + msg_id + "消息内容：" + msg);
        logger.info("SUCCESS!!!============监听到的消息的ID是：" + msg_id + "消息内容：" + msg);
    }


    /**
     * 测试：post方式提交，申请mq服务器注册监听服务
     *
     * @param request
     */
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public void test(HttpServletRequest request, MqConsumerParam consumerParam) {

        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        //  请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //  封装参数，千万不要替换为Map与HashMap，否则参数无法传递
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        //  也支持中文
        params.add("topic", consumerParam.getTopic());
        params.add("tags", consumerParam.getTags());
        params.add("message_callback_port", consumerParam.getMessage_callback_port());
        params.add("message_callback_path", consumerParam.getMessage_callback_path());

        params.add("consumer_id", consumerParam.getConsumer_id());
        params.add("message_model", consumerParam.getMessage_model());
        params.add("msg_type", consumerParam.getMsg_type());
        params.add("max_reconsume_times", consumerParam.getMax_reconsume_times());
        params.add("suspend_time_millis", consumerParam.getSuspend_time_millis());

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(params, headers);
        //  执行HTTP请求提交数据
        client.exchange(mqServerUrl, HttpMethod.POST, requestEntity, String.class);

        //该接口会一直处于监听中，不会返回数据
    }
}
