package com.cqupt.art.feign;

import com.cqupt.art.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("artmarket-trade")
public interface TradeClient {

    @GetMapping("/api/trade/transfer/airdrop")
    R airdrop(@RequestParam String uid,
              @RequestParam String toAddress,
              @RequestParam String artId,
              @RequestParam(required = false, defaultValue = "1") int num);
}
