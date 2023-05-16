package com.cqupt.art.service.impl;

import com.cqupt.art.entity.PmTransferLog;
import com.cqupt.art.mapper.PmTransferLogMapper;
import com.cqupt.art.service.TransferLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-03
 */
@Service
public class TransferLogServiceImpl extends ServiceImpl<PmTransferLogMapper, PmTransferLog> implements TransferLogService {

    @Override
    public List<PmTransferLog> getByNftId(Long nftId) {
        Map<String, Object> map = new HashMap<>();
        map.put("nft_id", nftId);
        List<PmTransferLog> pmTransferLogs = this.baseMapper.selectByMap(map);
        return pmTransferLogs;
    }
}
