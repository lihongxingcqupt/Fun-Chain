package com.cqupt.art.author.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqupt.art.author.entity.NftBatchInfoEntity;
import com.cqupt.art.author.entity.vo.NftDetailVo;
import com.cqupt.art.author.entity.vo.SnapUpNftInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-20
 */
@Mapper
public interface NftBatchInfoMapper extends BaseMapper<NftBatchInfoEntity> {
    List<SnapUpNftInfoVo> getSnapUpList(@Param("start") int start, @Param("limit") Integer limit);

    NftDetailVo getNftDetail(@Param("id") String id);
}
