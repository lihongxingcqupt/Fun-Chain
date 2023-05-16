package com.cqupt.art.author.entity.to;

import com.cqupt.art.author.entity.NftMetadata;
import lombok.Data;

@Data
public class CreateNftBatchInfoTo {
    private int num;
    private String authorName;
    private NftMetadata metadata;
}
