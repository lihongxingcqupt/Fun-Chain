package com.cqupt.art.order.feign;

import com.cqupt.art.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "artdev-author")
public interface NftWorksClient {

    @GetMapping("/author/nftBatchInfo/getNftName/{id}")
    R getNftName(@PathVariable("id") String id);
}
