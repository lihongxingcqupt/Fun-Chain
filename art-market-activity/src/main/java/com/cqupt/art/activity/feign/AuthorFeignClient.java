package com.cqupt.art.activity.feign;

import com.cqupt.art.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
@FeignClient(value = "artdev-author",url = "10.16.26.87:8881")
public interface AuthorFeignClient {
    @GetMapping("/author/nftBatchInfo/getById")
    R getById(@RequestParam("id") Long id);
}
