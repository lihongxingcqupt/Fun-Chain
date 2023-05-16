package com.cqupt.art.sms.entity.vo;

import com.cqupt.art.sms.entity.PmArticleCats;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ArticleCatsVo {
    List<PmArticleCats> items;
    Map<Integer, String> parentMap;
}
