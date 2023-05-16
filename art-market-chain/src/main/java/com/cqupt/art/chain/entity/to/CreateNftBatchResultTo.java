package com.cqupt.art.chain.entity.to;

import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class CreateNftBatchResultTo {
    private String txHash;
    //在本次铸造之前总共铸造了这么多份
    private List<BigInteger> tokenIds;

    private String tokenUri;
}
