package com.cqupt.art.controller;

import com.cqupt.art.entity.PmTransferLog;
import com.cqupt.art.service.NftInfoService;
import com.cqupt.art.service.TransferLogService;
import com.cqupt.art.service.TransferService;
import com.cqupt.art.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trade/transfer")
public class TransforController {

    @Autowired
    private TransferService transferService;

    @Autowired
    private NftInfoService nftInfoService;

    @Autowired
    private TransferLogService transferLogService;

    //使用区块链地址或手机号转账
    @GetMapping("/transfer")
    public R transfer(@RequestParam(name = "toPhoneNum", required = false) String toPhoneNum,
                      @RequestParam(name = "toAddress", required = false) String toAddress,
                      @RequestParam("tokenId") Long tokenId) {

        transferService.transfer(toPhoneNum, toAddress, tokenId);
        return R.ok();
    }

    /**
     * @param toAddress 空投账户地址
     * @param artId     空投作品id
     * @return
     */
    @GetMapping("/airdrop")
    public R airdrop(@RequestParam String uid,
                     @RequestParam String toAddress,
                     @RequestParam String artId,
                     @RequestParam(required = false, defaultValue = "1") int num) {
        String txHash = nftInfoService.airdrop(uid, toAddress, artId, num);
        if (txHash != null) {
            return R.ok().put("data", txHash);
        }
        return R.error("空投失败！");
    }

    /**
     * 通过藏品的id来查询关于它的流转记录
     *
     * @param nftId
     * @return
     */
    @GetMapping("/getTransforLog")
    public R getTransforLog(@RequestParam("nftId") Long nftId) {
        List<PmTransferLog> transferLogs = transferLogService.getByNftId(nftId);
        return R.ok().put("data", transferLogs);
    }
}
