package com.cqupt.art.order.feign;

import com.cqupt.art.order.entity.UserToken;
import com.cqupt.art.order.entity.UserTokenItem;
import com.cqupt.art.order.entity.to.TransferLog;
import com.cqupt.art.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "art-market-trade")
public interface TradeClient {
    @PostMapping("/transferLog")
    R saveTransferLog(@RequestBody TransferLog transferLog);

    @GetMapping("/api/userToken/getUserToken")
    public R getUserToken(@RequestParam("userId") String userId, @RequestParam("artId") String artId);

    @PostMapping("/api/userToken/saveUserToken")
    public R saveUserToken(@RequestBody UserToken userToken);

    @PostMapping("/api/userToken/updateUserToken")
    public R updateUserToken(@RequestBody UserToken userToken);

    @PostMapping("/api/userToken/saveUserTokenItem")
    public R saveUserTokenItem(@RequestBody UserTokenItem item);

}
