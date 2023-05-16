package com.cqupt.art.chain.service;

import com.cqupt.art.chain.entity.NftMetadata;
import com.cqupt.art.chain.entity.to.CreateNftBatchResultTo;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public interface ConfluxNftService {

    // 批量铸造NFT，一次交易
    CreateNftBatchResultTo createNftBatch(int num, NftMetadata metadata, String authorName) throws Exception;

    //批量铸造NFT，多次交易
    List<String> creatNftBatch(int num, NftMetadata metadata) throws Exception;

    //为用户设置允许转增特定nft
    String approve(String to, BigInteger tokenId) throws Exception;

    //允许用户转增所有的已拥有nft
    String setApproveAll(String to, boolean isApproveAll) throws Exception;

    //链上资产转移（转增、交易完成后转到用户钱包）
    String transfer(String from, String to, BigInteger tokenId) throws Exception;

    BigInteger totalSupply();


    String adminTransfer(String toAddress, BigInteger tokenId);

    String adminTransferBatch(List<String> addressList, List<BigInteger> tokenIds);
}
