package com.cqupt.art.feign;

import com.cqupt.art.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigInteger;

@FeignClient("artmarket-conflux-chain")
public interface ChainClient {

    @GetMapping("/chain/account/createAccount/{pwd}")
    R createAccount(@PathVariable("pwd") String pwd);

    @GetMapping("/transfer/{to}/{tokenId}")
    R adminTransfer(@PathVariable("to") String toAddress, @PathVariable("tokenId") BigInteger tokenId);
}
