package com.cqupt.art.service;

import com.cqupt.art.entity.PmTransferLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lihongxing
 * @since 2022-11-03
 */
public interface TransferLogService extends IService<PmTransferLog> {

    List<PmTransferLog> getByNftId(Long nftId);
}
