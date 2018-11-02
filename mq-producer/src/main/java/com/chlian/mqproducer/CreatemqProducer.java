package com.chlian.mqproducer;

import com.chlian.mqproducer.domain.MqProducerParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

/**
 * 创建生产者客户端示例
 */
@RestController
@RequestMapping("/mq/producer")
@PropertySource("classpath:application.properties")
public class CreatemqProducer {

    protected Logger logger = LoggerFactory.getLogger(CreatemqProducer.class);

    @Value("${mq.service.ip}")
    private String mqServerIp;  //mq服务端IP地址

    @Value("${mq.service.port}")
    private String mqServerPort;  //mq服务端端口号

    /**
     * 创建生产者
     * @param request
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public String createProducer(HttpServletRequest request, MqProducerParam producerParam) {
        //localhost:8080/mq/producer?producer_id=PID_producttest1&msg=hello mq&tags=TagA&topic=cid_test

        //1. 接收
        Long delay_time = producerParam.getDelay_time();
        String interval_time = producerParam.getInterval_time();
        String msg = producerParam.getMsg();
        String msg_send_way = producerParam.getMsg_send_way();
        String producer_id = producerParam.getProducer_id();
        String sharding_key = producerParam.getSharding_key();
        String msg_key = producerParam.getMsg_key();
        String msg_type = producerParam.getMsg_type();
        String tags = producerParam.getTags();
        String topic = producerParam.getTopic();

        //mq服务层地址
        String url = "http://" + mqServerIp + ":" + mqServerPort + "/mq/producer";

        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        //  请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //  封装参数，千万不要替换为Map与HashMap，否则参数无法传递
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        //  也支持中文
        params.add("topic", topic);
        params.add("tags", tags);
        params.add("producer_id", producer_id);
        params.add("msg", msg);
        params.add("delay_time", StringUtils.isEmpty(delay_time) ? "" : delay_time.toString());
        params.add("interval_time", interval_time);
        params.add("msg_send_way", msg_send_way);
        params.add("sharding_key", sharding_key);
        params.add("msg_key", msg_key);
        params.add("msg_type", msg_type);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(params, headers);

        System.out.println("消费者正在消费");
        System.out.println("生产者正在生产");

        try {
            //  执行HTTP请求，提交创建生产者申请
            client.exchange(url, HttpMethod.POST, requestEntity, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("生产者发送失败，详细信息：" + e.getMessage());
            return "发送失败";
        }

        return "发送成功！";

    }
}
