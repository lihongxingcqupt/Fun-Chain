package com.cqupt.art.order.feign;

import com.cqupt.art.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigInteger;

@FeignClient(value = "artmarket-conflux-chain")
public interface ChainClient {

    @GetMapping("/api/chain/conflux/transfer/{from}/{to}/{tokenId}")
    R transfer(@PathVariable("from")
                              String from,
                      @PathVariable("to")
                              String to,
                      @PathVariable("tokenId")
                              BigInteger tokenId);
}
