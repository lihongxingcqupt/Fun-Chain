package com.cqupt.art.chain.entity.to;

import com.cqupt.art.chain.entity.NftMetadata;
import lombok.Data;

@Data
public class CreateNftBatchInfoTo {
    private int num;
    private String authorName;
    private NftMetadata metadata;
}
