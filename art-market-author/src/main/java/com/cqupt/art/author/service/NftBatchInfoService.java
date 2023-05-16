package com.cqupt.art.author.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.art.author.entity.NftBatchInfoEntity;
import com.cqupt.art.author.entity.to.NftDetailRedisTo;
import com.cqupt.art.author.entity.to.WorkQuery;
import com.cqupt.art.author.entity.vo.NftDetailVo;
import com.cqupt.art.author.entity.vo.SnapUpNftInfoVo;
import com.cqupt.art.utils.PageUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public interface NftBatchInfoService extends IService<NftBatchInfoEntity> {

    NftBatchInfoEntity upToChain(Long id);

    PageUtils queryPage(Map<String, Object> params);

    void updateDes(NftBatchInfoEntity nftBatchInfo);

    void deleteBatch(Long id);

    PageUtils listQuery(WorkQuery query);

    String uploadNftImage(MultipartFile file) throws IOException;

    void launch(Long workId);

    List<SnapUpNftInfoVo> snapUpListInfo(Integer curPage, Integer limit);

    NftDetailRedisTo secKillDetail(String id, String nftName);

    NftDetailVo nftDetail(String id);
}
