package com.cqupt.art.activity.config;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class MQConfig {

    @Configuration
    static class Converter{
        @Bean
        public MessageConverter messageConverter() {
            return new Jackson2JsonMessageConverter();
        }
    }


    /**
     * @desc: 延时交换器，注意返回类型是CustomExchange
     * @return: org.springframework.amqp.core.TopicExchange
     * @auther: Michael Wong
     * @email:  michael_wong@yunqihui.net
     * @date:   2020/9/8 18:52
     * @update:
     */
    @Bean
    public CustomExchange customDlxExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
//        return new CustomExchange(MQKeyStatic.EXCHANGE_GOOD_CUSTOM_DLX,"x-delayed-message",true,false,args);
        return new CustomExchange("chouqianExchange","x-delayed-message",true,false,args);

    }

    /**
     * @desc: 活动上架通知抽签队列
     * @return: org.springframework.amqp.core.Queue
     * @auther: Michael Wong
     * @email:  michael_wong@yunqihui.net
     * @date:   2020/9/5 14:46
     * @update:
     */

    @Bean
    public Queue chouqainQueue() {
//        return new Queue(MQKeyStatic.QUEUE_GOOD_DLX_GOOD, true, false, false, null);
        return new Queue("chouqianDuilie", true, false, false, null);
    }

    @Bean
    public Binding dlxExchangeBindingGoodOnShelfDlx() {
        return BindingBuilder.bind(chouqainQueue()).to(customDlxExchange()).with("chouqianRouting").noargs();
    }

}
