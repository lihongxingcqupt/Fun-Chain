package test;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = mqTest.class)

public class mqTest {

    RabbitTemplate rabbitTemplate = new RabbitTemplate();
    @Test
    public void fasong(){
        // 延迟队列，上下架
        JSONObject messageObject = new JSONObject();
        messageObject.put("nftId", 1);

        long expirStart = 3000;
        rabbitTemplate.convertAndSend("chouqianExchange","chouqianRouting",messageObject, message -> {
            // 这里的失效时间是long类型，普通的TTL方式的类型是String类型
            message.getMessageProperties().setHeader("x-delay",expirStart);
            return message;
        });

    }
}
