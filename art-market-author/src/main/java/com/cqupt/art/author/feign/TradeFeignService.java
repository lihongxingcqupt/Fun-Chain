package com.cqupt.art.author.feign;

import com.cqupt.art.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "artmarket-trade")
public interface TradeFeignService {
    @GetMapping("/api/trade/transfer/getTransforLog")
    R getTransforLog(@RequestParam("nftId") Long nftId);
}
