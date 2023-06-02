# Fun-Chain（趣链）

* An NFT trading platform, to achieve the collection of the chain, first, trading and other functions. This source code is limited to exchange learning, do not do business, where the legal issues have nothing to do with myself.

* 一个 NFT 交易平台，实现了藏品的上链、首发、交易等功能。本源码仅限于交流学习，不做商用，凡涉及到法律问题与本人无关。



## 技术栈
<img src="https://img.shields.io/badge/Java-100%25-green" /> 

<img src="https://img.shields.io/badge/Spring%20Boot-100%25-yellow" />

<img src="https://img.shields.io/badge/%E6%B6%88%E6%81%AF%E4%B8%AD%E9%97%B4%E4%BB%B6-Rabbit%20MQ-red" />

<img src="https://img.shields.io/badge/%E6%95%B0%E6%8D%AE%E5%BA%93-MySQL-blue" />

<img src="https://img.shields.io/badge/%E7%BC%93%E5%AD%98-Redis-lightgrey" />



## 微服务功能
|  服务名  | 功能 |
| :----- | :----- |
| art-market-activity  | 登记抽签服务 |
| art-market-author   | 作者信息管理 |
| art-market-chain   | 区块链服务 |
| art-market-common   | 公共依赖服务 |
| art-market-gateway   | 网关服务 |
| art-market-order   | 订单服务 |
| art-market-seckill  | 秒杀服务 |
| aart-market-sms   | 短信服务 |
| art-market-trade   | 交易服务 |
| art-market-user   | 用户服务 |

## 特色功能
1、mq尝试次数过多后为不影响整体性能，不让mq再做无谓的重试，选择使用ACK并邮件通知管理员。
```
log.info("上链异常，拒绝消费消息......重试");
            if(redisTemplate.opsForValue().get(message.getMessageProperties().getMessageId()) == null){
                redisTemplate.opsForValue().set(message.getMessageProperties().getMessageId(),"1");
            }else{
                int times = Integer.parseInt(redisTemplate.opsForValue().get(message.getMessageProperties().getMessageId()));
                if(times > RabbitMqConstant.retryTimes){
                    // 尝试多次仍然未上链成功说明不是网络波动而是其他致命的原因了，这种情况，手动ACK并邮件通知工作人员，并且设置上链状态为出错
                    batchInfoEntity.setLanuchStatus(NftBatchStatusEnum.UP_ERROR.getCode());
                    batchInfoService.updateById(batchInfoEntity);

                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    // 邮件通知
                    boolean flag = send_qqmail(Arrays.asList(EmailConstant.administratorEmail), "上链重试次数过多", "上链已重复超过"+RabbitMqConstant.retryTimes + "请检查");
                    if(flag){
                        log.info("上链异常，已通知管理员");
                    }
                }else{
                    // 次数加一
                    redisTemplate.opsForValue().set(message.getMessageProperties().getMessageId(),times + 1 + "");
                }
            }
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
```
2、异步编排进行财产转移
```
/**
     * 支付成功，进行财产转移，包括本地转移和链上转移
     * @param order
     */
    @Transactional
    public void transferToUser(Order order) {
        /**
         * 封装链上财产转移的TO，远程调用区块链服务进行链上的财产转移
         */
        ChainTransferTo chainTransferTo = new ChainTransferTo();
        chainTransferTo.setFromUserId(order.getSellUserId());
        chainTransferTo.setToUserId(order.getBuyUserId());
        chainTransferTo.setArtId(order.getGoodsId());
        Integer localId = order.getLocalId();
        if (localId == null) {
            log.info("订单支付完成，生成本地id");
            R r = worksClient.getLocalId(order.getGoodsId(), order.getBuyUserId());
            localId = r.getData("data", new TypeReference<Integer>() {
            });
            log.info("生成的本地id为：{}", localId);
        }
        chainTransferTo.setLocalId(localId);

        // 链上转帐，远程调用的话会阻塞在这里，影响效率
        rabbitTemplate.convertAndSend("nft-order-event", "nft.order.chain.transfer", chainTransferTo);

        /**
         * 本地转移：
         * UserTokenItem：用户和藏品的一一对应
         * UserToken：用户拥有的藏品
         * TransferLog：交易日志
         */

        /**
         * 封装用户和藏品的对应信息，用于展示用户的全部藏品的详细信息
         */
        UserTokenItem item = new UserTokenItem();
        item.setPrice(order.getPrice());
        item.setLocalId(localId);
        // 藏品的状态,待链上确认
        item.setStatus(NftStatusEnum.WAIT_CHAIN_CONFIRM.getCode());
        //获取藏品的方式，有卖家ID就是二级市场，没有的话就是首发
        int gainType = order.getSellUserId().equals("0") ? NftGainTypeEnum.FIRST_BUY.getCode() : NftGainTypeEnum.TWO_BUY.getCode();
        item.setGainType(gainType);
        R r = worksClient.getNftInfo(order.getGoodsId());
        NftBatchInfoTo nftBatchInfoTo = null;
        if (r.getCode() == 200) {
            nftBatchInfoTo = r.getData("data", new TypeReference<NftBatchInfoTo>() {
            });
            item.setTokenType(nftBatchInfoTo.getType());
        }


        /**
         * 封装 userToken，用户和藏品的概览信息，用于展示，某某用户拥有 xx 等几件藏品
         */
        R result = tradeClient.getUserToken(order.getBuyUserId(), order.getGoodsId());
        UserToken userToken = null;
        if (result.getCode() == 200) {
            userToken = result.getData("data", new TypeReference<UserToken>() {});
        }
        if (userToken == null) {
            userToken = new UserToken();
            userToken.setUserId(order.getBuyUserId());
            userToken.setArtId(order.getGoodsId());
            userToken.setCount(order.getNum());
            userToken.setSail(0);

        } else {
            userToken.setCount(userToken.getCount() + order.getNum());
        }

        /**
         * 封装交易日志
         */
        TransferLog logTo = new TransferLog();
        logTo.setFromUid(order.getSellUserId());
        logTo.setToUid(order.getBuyUserId());
        logTo.setNftId(order.getGoodsId());
        logTo.setLocalId(order.getLocalId());
        logTo.setPrice(order.getPrice());

        logTo.setLocalId(localId);
        //保存交易日志
        if (order.getSellUserId().equals("0")) {
            logTo.setType(1);
        } else {
            logTo.setType(4);
        }


        /**
         * 自定义线程池
         */
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                5,
                5,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        /**
         * 使用 CompletableFuture 开启异步保存三张表，因为三张表都是跨服务保存，若不异步，性能会很差
         */
        // 保存 UserTokenItem
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
            tradeClient.saveUserTokenItem(item);
        }, executor);

        // 保存 UserToken
        UserToken finalUserToken = userToken;
        CompletableFuture<Void> voidCompletableFuture1 = CompletableFuture.runAsync(() -> {
            tradeClient.saveUserToken(finalUserToken);
        }, executor);

        // 保存 交易日志
        CompletableFuture<Void> voidCompletableFuture2 = CompletableFuture.runAsync(() -> {
            tradeClient.saveTransferLog(logTo);
        }, executor);
    }
```

3、上链操作费时，异步处理
```
@RabbitHandler
    public void upToChain(NftBatchInfoEntity batchInfoEntity, Channel channel, Message message) {
        log.info("消费端收到消息：{}", JSON.toJSONString(batchInfoEntity));
        CreateNftBatchInfoTo infoTo = new CreateNftBatchInfoTo();
        AuthorEntity author = authorService.getById(batchInfoEntity.getAuthorId());
        log.info("查到的作者信息：{}", JSON.toJSONString(author));
        batchInfoEntity = batchInfoService.getOne(new QueryWrapper<NftBatchInfoEntity>().eq("author_id", author.getAuthorId()).eq("name", batchInfoEntity.getName()));
        infoTo.setAuthorName(author.getAuthorName());
        infoTo.setNum(batchInfoEntity.getTotalSupply());
        NftMetadata metadata = new NftMetadata();
        metadata.setName(batchInfoEntity.getName());
        metadata.setDescription(batchInfoEntity.getDescription());
        metadata.setImage(batchInfoEntity.getImageUrl());
        infoTo.setMetadata(metadata);
        R r = chainFeignService.createNftBatchOnce(infoTo);
        if (r.getCode() == 200) {
            CreateNftBatchResultTo resultTo = r.getData("data", new TypeReference<CreateNftBatchResultTo>() {
            });
            batchInfoEntity.setTxHash(resultTo.getTxHash());
            batchInfoEntity.setTokenUri(resultTo.getTokenUri());
            //更新batch的信息
            batchInfoService.update(batchInfoEntity, new QueryWrapper<NftBatchInfoEntity>().eq("author_id", author.getAuthorId()).eq("name", batchInfoEntity.getName()));
            //把tokenids放到表中
            NftInfoEntity infoEntity = new NftInfoEntity();
            BeanUtils.copyProperties(batchInfoEntity, infoEntity);
            infoEntity.setArtId(batchInfoEntity.getId().toString());
            infoEntity.setTokenName(batchInfoEntity.getName());
            Integer totalSupply = batchInfoEntity.getTotalSupply();
            List<BigInteger> tokenIds = resultTo.getTokenIds();
            List<NftInfoEntity> entiityList = new ArrayList<>();
            for (int i = 0; i < tokenIds.size(); i++) {
                NftInfoEntity entity = new NftInfoEntity();
                BeanUtils.copyProperties(infoEntity, entity);
                infoEntity.setTokenId(tokenIds.get(i).longValue());
                int localId = i + 1;
                log.info("localId---->{}", String.valueOf(localId));
                entity.setLocalId(localId);
                entiityList.add(entity);
            }
            log.info("插入数据库的数据： {}", JSON.toJSONString(entiityList));
            nftInfoService.saveBatch(entiityList);
            log.info("插入成功");
            //上链成功，给消息队列确认
            log.info("上链成功，手动ack消息");
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } 
```

