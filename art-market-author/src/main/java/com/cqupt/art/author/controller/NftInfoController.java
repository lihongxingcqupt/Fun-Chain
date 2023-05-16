package com.cqupt.art.author.controller;

import com.cqupt.art.author.entity.to.TransferLogTo;
import com.cqupt.art.author.entity.vo.NftAndUserVo;
import com.cqupt.art.author.service.NftInfoService;
import com.cqupt.art.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/author/nftInfo")
public class NftInfoController {
    /**
     * 条件查询藏品并分页展示
     *
     * @param params
     * @return
     */

    @Autowired
    NftInfoService nftInfoService;

    /**
     * 查询全部的藏品并分页
     *
     * @param params
     * @return 所需那一页的集合，以及这个集合的个数，可以通过以下参数查询
     * 1、artName 藏品名字
     * 2、state 该藏品状态，详见实体
     * 3、getWay 该藏品的获取方式
     * 4、phone 用户手机号
     * 5、curPage 当前页数
     * 6、capacity 每页显示条数
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        List<NftAndUserVo> nftAndUserVoList = nftInfoService.queryPage(params);
        return R.ok().put("items", nftAndUserVoList).put("total", nftAndUserVoList.size());
    }

    /**
     * 通过该藏品的id远程调用交易服务获取该藏品的流转记录
     *
     * @param id
     * @return
     */
    @GetMapping("/getTransforLog")
    public R transforLog(@RequestParam("id") Long id) {
        List<TransferLogTo> transferLogTos = nftInfoService.getTransforLog(id);
        return R.ok().put("data", transferLogTos);
    }


}
