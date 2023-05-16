package com.cqupt.art.order.app;

import com.alipay.api.AlipayApiException;
import com.cqupt.art.order.config.AlipayTemplate;
import com.cqupt.art.order.entity.vo.PayVo;
import com.cqupt.art.order.service.OrderService;
import com.cqupt.art.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/pay")
@Slf4j
public class PayAppController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AlipayTemplate alipayTemplate;

    @GetMapping("/payOrder/{orderSn}")
    public R payOrder(@PathVariable("orderSn") String orderSn) {
        PayVo payVo = orderService.getOrderPay(orderSn);
        try {
            String pay = alipayTemplate.pay(payVo);
            return R.ok().put("result", pay);
        } catch (AlipayApiException e) {
            log.error(e.getErrMsg());
            return R.error("支付异常，请刷新重试！");
        }
    }
}
