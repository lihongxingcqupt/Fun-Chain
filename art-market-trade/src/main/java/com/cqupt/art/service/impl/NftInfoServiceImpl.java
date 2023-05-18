package com.cqupt.art.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cqupt.art.entity.PmNftInfo;
import com.cqupt.art.entity.PmTransferLog;
import com.cqupt.art.feign.ConfluxChainClient;
import com.cqupt.art.mapper.NftInfoMapper;
import com.cqupt.art.service.NftInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.art.service.TransferLogService;
import com.cqupt.art.utils.R;
import conflux.web3j.types.Address;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lihongxing
 * @since 2022-11-07
 */
@Slf4j
@Service
public class NftInfoServiceImpl extends ServiceImpl<NftInfoMapper, PmNftInfo> implements NftInfoService {

    @Autowired
    private ConfluxChainClient confluxChainClient;

    @Autowired
    private TransferLogService transferLogService;

    @Transactional
    @Override
    public String airdrop(String uid, String toAddress, String artId, int num) {
        log.info("空投作品：{}", artId);
        if (StringUtils.isNotBlank(uid) && Address.isValid(toAddress)) {
            List<PmNftInfo> nftInfos = baseMapper.selectList(new QueryWrapper<PmNftInfo>().eq("art_id", artId).eq("user_id", ""));
            PmNftInfo nftInfo = nftInfos.get(1);
            log.info(nftInfo.toString());
            //todo 此处应先对数据库做操作，改变状态，链上操作使用消息队列尽量保证数据一致性
            R r = confluxChainClient.adminTransfer(toAddress, BigInteger.valueOf(nftInfo.getTokenId()));
            if (r != null) {
                String txHash = (String) r.get("data");
                log.info("airdrop---txHash-->{}", txHash);
                if (StringUtils.isNotBlank(txHash)) {
                    //todo : 此处应该确认链上交易完成,可以考虑增加链上操作完成状态的标志位
                    nftInfo.setUserId(uid);
                    PmTransferLog log = new PmTransferLog();
                    log.setFromAddress("adminAddress");
                    log.setToAddress(toAddress);
                    log.setFromPhoneNum("");
                    log.setToPhoneNum("");
                    log.setCreateTime(new Date());
                    transferLogService.save(log);
                    baseMapper.updateById(nftInfo);
                    return txHash;
                }
            }
            return null;
        } else {
            throw new RuntimeException("用户id或钱包地址不合法！");
        }
    }
}
