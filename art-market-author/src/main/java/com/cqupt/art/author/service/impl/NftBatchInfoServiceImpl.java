package com.cqupt.art.author.service.impl;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.art.constant.SeckillConstant;
import com.cqupt.art.author.entity.AuthorEntity;
import com.cqupt.art.author.entity.NftBatchInfoEntity;
import com.cqupt.art.author.entity.NftInfoEntity;
import com.cqupt.art.author.entity.NftMetadata;
import com.cqupt.art.author.entity.to.CreateNftBatchInfoTo;
import com.cqupt.art.author.entity.to.CreateNftBatchResultTo;
import com.cqupt.art.author.entity.to.NftDetailRedisTo;
import com.cqupt.art.author.entity.to.WorkQuery;
import com.cqupt.art.author.entity.vo.NftDetailVo;
import com.cqupt.art.author.entity.vo.SnapUpNftInfoVo;
import com.cqupt.art.author.feign.ChainFeignService;
import com.cqupt.art.author.dao.NftBatchInfoMapper;
import com.cqupt.art.author.service.AuthorService;
import com.cqupt.art.author.service.NftBatchInfoService;
import com.cqupt.art.author.service.NftInfoService;
import com.cqupt.art.utils.AliOssUtil;
import com.cqupt.art.utils.PageUtils;
import com.cqupt.art.utils.Query;
import com.cqupt.art.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.*;

@Service("nftBatchInfoService")
@Slf4j
public class NftBatchInfoServiceImpl extends ServiceImpl<NftBatchInfoMapper, NftBatchInfoEntity> implements NftBatchInfoService {
    @Autowired
    ChainFeignService chainFeignService;
    @Autowired
    AuthorService authorService;
    @Autowired
    NftInfoService nftInfoService;
    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Override
    public NftBatchInfoEntity upToChain(Long id) {
        NftBatchInfoEntity byId = this.getById(id);
        CreateNftBatchInfoTo createNftBatchInfoTo = new CreateNftBatchInfoTo();
        createNftBatchInfoTo.setNum(byId.getTotalSupply());
        String authorName = authorService.getById(byId.getAuthorId()).getAuthorName();
        createNftBatchInfoTo.setAuthorName(authorName);
        NftMetadata nftMetadata = new NftMetadata();
        nftMetadata.setImage(byId.getImageUrl());

        if (byId.getDescription() == null) {
            log.error("请先添加图片描述");
        } else {
            nftMetadata.setDescription(byId.getDescription());
        }
        nftMetadata.setName(byId.getName());
        createNftBatchInfoTo.setMetadata(nftMetadata);

        R r = chainFeignService.createNftBatchOnce(createNftBatchInfoTo);
        CreateNftBatchResultTo createNftBatchResultTo = null;
        if (r.getCode() != 0) {
            log.error("上链失败");
        } else {
            createNftBatchResultTo = r.getData("data", new TypeReference<CreateNftBatchResultTo>() {
            });
        }
        byId.setTokenUri(createNftBatchResultTo.getTokenUri());
        byId.setTxHash(createNftBatchResultTo.getTxHash());
        //上链生成的全部藏品ID
        List<BigInteger> tokenIds = createNftBatchResultTo.getTokenIds();
        for (int i = 0; i < tokenIds.size(); i++) {
            NftInfoEntity nftInfoEntity = new NftInfoEntity();
            nftInfoEntity.setTotalSupply(byId.getTotalSupply());
            nftInfoEntity.setAuthorId(byId.getAuthorId());
            nftInfoEntity.setType(byId.getType());
            nftInfoEntity.setContractAddress(byId.getContractAddress());
            nftInfoEntity.setLocalId(i + 1);
            nftInfoEntity.setTokenName(nftMetadata.getName());
            nftInfoEntity.setImageUrl(nftMetadata.getImage());
            nftInfoEntity.setDescription(nftMetadata.getDescription());
            nftInfoEntity.setTxHash(byId.getTxHash());
            nftInfoEntity.setTokenId(tokenIds.get(i).longValue());
            nftInfoEntity.setUserId("");
            nftInfoEntity.setArtId(byId.getId() + "");
            nftInfoService.save(nftInfoEntity);
        }
        this.baseMapper.updateById(byId);
        return byId;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<NftBatchInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (key != null && key.length() != 0) {
            queryWrapper.like("name", key);
        }
        String type = (String) params.get("type");
        if (type != null && type.length() != 0) {
            queryWrapper.and(q -> {
                q.eq("type", type);
            });
        }
        IPage<NftBatchInfoEntity> page = this.page(
                new Query<NftBatchInfoEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    @Override
    public void updateDes(NftBatchInfoEntity nftBatchInfo) {
        //更新批量这个表
        this.updateById(nftBatchInfo);
        //更新藏品的单个表
        UpdateWrapper<NftInfoEntity> up = new UpdateWrapper<>();
        up.set("description", nftBatchInfo.getDescription());
        up.eq("art_id", nftBatchInfo.getId() + "");
        nftInfoService.update(up);
    }


    @Override
    public void deleteBatch(Long id) {
        this.baseMapper.deleteById(id);
        //再去删除一批中的每一个藏品
        Map<String, Object> param = new HashMap<>();
        param.put("art_id", id + "");
        nftInfoService.getBaseMapper().deleteByMap(param);
    }

    @Override
    public PageUtils listQuery(WorkQuery query) {
        QueryWrapper<NftBatchInfoEntity> wrapper = new QueryWrapper<>();

        if (StringUtils.isNotBlank(query.getKeyword())) {
            String keyword = query.getKeyword();
            if ("0x".equals(keyword.substring(0, 2))) {
                wrapper.eq("tx_hash", keyword);
            } else {
                wrapper.like("name", keyword);
            }
        }
        if (query.getAuthorId() != null) {
            wrapper.eq("author_id", query.getAuthorId());
        }
        Page<NftBatchInfoEntity> ipage = new Page<>(query.getCurPage(), query.getLimit());
        ipage.addOrder(OrderItem.desc("issue_time"));
        IPage<NftBatchInfoEntity> page = this.page(ipage, wrapper);
        return new PageUtils(page);
    }

    @Override
    public String uploadNftImage(MultipartFile file) throws IOException {
        String originName = file.getOriginalFilename();
        InputStream is = file.getInputStream();
        String objectName = "img/nft/" + originName;
        log.info(objectName);
        String imgUrl = AliOssUtil.uploadFile(is, objectName);
        log.info("上传NFT图片链接为：{}", imgUrl);
        return imgUrl;
    }

    @Override
    public void launch(Long workId) {
        NftBatchInfoEntity entity = baseMapper.selectById(workId);
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SeckillConstant.SECKILL_DETAIL_PREFIX);
        String key = entity.getName() + "-" + entity.getId();
        if (!ops.hasKey(key)) {
            AuthorEntity author = authorService.getById(entity.getAuthorId());
            NftDetailRedisTo to = new NftDetailRedisTo();

            BeanUtils.copyProperties(entity, to);

            to.setAuthorName(author.getAuthorName());
            to.setAuthorDesc(author.getAuthorDesc());
            to.setAvatarUrl(author.getAvatarUrl());
            Date startTime = entity.getIssueTime();
            to.setStartTime(startTime);
            //秒杀时间为30分钟
            to.setEndTime(new Date(startTime.getTime() + 30 * 60 * 1000));
            String token = UUID.randomUUID().toString().replace("-", "");
            to.setToken(token);
            String redisJson = JSON.toJSONString(to);
            ops.put(key, redisJson);
            //使用信号量设置库存
            RSemaphore semaphore = redissonClient.getSemaphore(SeckillConstant.SECKILL_SEMAPHORE + token);
            semaphore.trySetPermits(entity.getInventory());
            entity.setLanuchStatus(2);
            baseMapper.updateById(entity);
        }
    }

    @Override
    public List<SnapUpNftInfoVo> snapUpListInfo(Integer curPage, Integer limit) {
        int start = (curPage - 1) * limit;
        List<SnapUpNftInfoVo> snapUpNftInfos = baseMapper.getSnapUpList(start, limit);
        return snapUpNftInfos;
    }

    @Override
    public NftDetailRedisTo secKillDetail(String id, String nftName) {
        String key = nftName + "-" + id;
        String json = (String) redisTemplate.opsForHash().get(SeckillConstant.SECKILL_DETAIL_PREFIX, key);
        if (StringUtils.isNotBlank(json)) {
            log.info("秒杀商品 redis中查询到的数据：{}", json);
            NftDetailRedisTo to = JSON.parseObject(json, NftDetailRedisTo.class);
            String s = redisTemplate.opsForValue().get(SeckillConstant.SECKILL_SEMAPHORE + to.getToken());
            log.info("库存：{}", s);

            //秒杀需要验证token，若未到开始时间，不能把token给出去
            long startTime = to.getStartTime().getTime();
            if (System.currentTimeMillis() < startTime) {
                to.setToken(null);
            }
            return to;
        }
        return null;
    }

    @Override
    public NftDetailVo nftDetail(String id) {
        NftDetailVo vo = baseMapper.getNftDetail(id);
        long time = vo.getStartTime().getTime();
        time += 30 * 60 * 1000;
        vo.setEndTime(new Date(time));
        return vo;
    }


}
