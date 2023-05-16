package com.cqupt.art.author.feign;

import com.cqupt.art.author.entity.to.CreateNftBatchInfoTo;
import com.cqupt.art.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//@FeignClient(value = "artmarket-conflux-chain",url = "http://10.16.94.33:8888")
@FeignClient(value = "artmarket-conflux-chain")
public interface ChainFeignService {
    @RequestMapping(value = "/api/chain/conflux/createNft/batch/once", method = RequestMethod.POST)
    R createNftBatchOnce(@RequestBody CreateNftBatchInfoTo to);
}
