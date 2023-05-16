package com.cqupt.art.author.controller;

import com.alibaba.fastjson.JSON;
import com.cqupt.art.author.config.mq.MyNftMqConfig;
import com.cqupt.art.author.entity.AuthorEntity;
import com.cqupt.art.author.entity.NftBatchInfoEntity;
import com.cqupt.art.author.entity.to.NftDetailRedisTo;
import com.cqupt.art.author.entity.to.WorkQuery;
import com.cqupt.art.author.entity.vo.NftDetailVo;
import com.cqupt.art.author.entity.vo.SnapUpNftInfoVo;
import com.cqupt.art.author.service.AuthorService;
import com.cqupt.art.author.service.NftBatchInfoService;
import com.cqupt.art.utils.PageUtils;
import com.cqupt.art.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/author/nftBatchInfo")
@CrossOrigin
@Slf4j
public class NftBatchInfoController {

    @Autowired
    NftBatchInfoService nftBatchInfoService;
    @Autowired
    AuthorService authorService;
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 到此处只是后台填写信息后上传到数据库保存信息而没有真正的上架和生成单个的藏品
     *
     * @param batchInfoEntity
     * @return
     */

    @Validated
    @PostMapping("/save")
    public R save(@RequestBody @Valid NftBatchInfoEntity batchInfoEntity) {
        //合约地址是死的
        batchInfoEntity.setContractAddress("cfxtest:acacwaz9gt4jx4cpf4mrbur9usjz2cnp26zgh6d77k");
        batchInfoEntity.setCreateTime(new Date());
        //初始的库存就是发行总数
        batchInfoEntity.setInventory(batchInfoEntity.getTotalSupply());
        nftBatchInfoService.save(batchInfoEntity);
        //todo 这里应该去处理上链的逻辑了
        rabbitTemplate.convertAndSend(MyNftMqConfig.MINT_EXCHANGE, MyNftMqConfig.MINT_PRODUCT_ROUTING_KEY, batchInfoEntity);
        return R.ok().put("data", batchInfoEntity);
    }

    @GetMapping("/upToChain")
    public R upToChain(@RequestParam("id") Long id) {
        NftBatchInfoEntity nftBatchInfoEntity = nftBatchInfoService.upToChain(id);
        return R.ok().put("page", nftBatchInfoEntity);
    }

    /**
     * 条件查询作品并分页展示
     *
     * @param params
     * @return
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = nftBatchInfoService.queryPage(params);
        List<AuthorEntity> authors = authorService.list();
        Map<Long, String> authorMap = authors.stream().collect(Collectors.toMap(AuthorEntity::getAuthorId, AuthorEntity::getAuthorName, (k1, k2) -> k1));
        return R.ok().put("page", page).put("authorMap", authorMap);
    }

    /**
     * 模糊搜索
     */
    @GetMapping("/list/query")
    public R listQuery(WorkQuery query) {
        log.info("查询条件->{}", query.toString());
        PageUtils pageUtils = nftBatchInfoService.listQuery(query);
        return R.ok().put("page", pageUtils);
    }

    /**
     * 编辑介绍图片
     */
    @PostMapping("/update")
    public R updateDes(@RequestBody NftBatchInfoEntity nftBatchInfo) {
        nftBatchInfoService.updateDes(nftBatchInfo);
        return R.ok();
    }

    /**
     * 删除这一批（包含这一批中的若干个藏品）
     *
     * @param id
     * @return
     */
    @GetMapping("delete")
    public R deleteBatch(@RequestParam("id") Long id) {
        nftBatchInfoService.deleteBatch(id);
        return R.ok();
    }

    @PostMapping("uploadNftImage")
    public R uploadNftImage(MultipartFile file) {
        if (file.isEmpty()) {
            return R.error("上传NFT图片失败！");
        }
        try {
            String imgUrl = nftBatchInfoService.uploadNftImage(file);
            return R.ok().put("imgUrl", imgUrl);
        } catch (IOException e) {
            log.error(e.getMessage());
            return R.error("上传图片到OSS失败！");
        }
    }

    /**
     * 抢购上架
     *
     * @param workId 作品Id
     */

    @GetMapping("launch")
    public R lanunchForKill(@RequestParam(name = "workId") Long workId) {
        nftBatchInfoService.launch(workId);
        return R.ok("上架成功！");
    }

    /**
     * 首发藏品列表
     *
     * @return
     */
    @GetMapping("listInfo/{curPage}/{limit}")
    public R snapUpListInfo(@PathVariable Integer curPage, @PathVariable Integer limit) {
        List<SnapUpNftInfoVo> snapUpNftInfoVos = nftBatchInfoService.snapUpListInfo(curPage, limit);
        return R.ok("获取NFT列表成功！").put("list", snapUpNftInfoVos);
    }

    /**
     * 首发页详情信息
     */
    @PostMapping("nftDetail/seckillInfo")
    public R nftSecKillDetail(@RequestBody Map<String, String> params) {
        log.info("收到的参数：{}", JSON.toJSONString(params));
        NftDetailRedisTo to = nftBatchInfoService.secKillDetail(params.get("id"), params.get("name"));
        log.info("查到的数据：{}", JSON.toJSONString(to));
        if (to != null) {
            return R.ok("获取数据成功").put("data", to);
        } else {
            return R.error("服务异常！");
        }
    }

    @GetMapping("nftDetail/{id}")
    public R nftDetail(@PathVariable("id") String id) {
        NftDetailVo vo = nftBatchInfoService.nftDetail(id);
        if (vo != null) {
            return R.ok().put("data", vo);
        }
        return R.error("系统异常！");
    }


    @GetMapping("/getNftName/{id}")
    public R getNftName(@PathVariable("id") String id) {
        String name = nftBatchInfoService.getById(id).getName();
        return R.ok().put("data", name);
    }

    @GetMapping("getById")
    public R getById(@RequestParam("id") Long id){
        NftBatchInfoEntity byId = nftBatchInfoService.getById(id);
        return R.ok().put("data",byId);
    }
}
