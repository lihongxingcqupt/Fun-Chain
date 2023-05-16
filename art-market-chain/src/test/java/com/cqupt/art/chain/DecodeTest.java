package com.cqupt.art.chain;


import conflux.web3j.Account;
import conflux.web3j.Cfx;
import conflux.web3j.request.LogFilter;
import conflux.web3j.response.Log;
import conflux.web3j.response.events.LogNotification;
import conflux.web3j.types.Address;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import org.junit.Test;
import org.web3j.abi.DefaultFunctionEncoder;
import org.web3j.abi.DefaultFunctionReturnDecoder;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.websocket.WebSocketService;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DecodeTest {


    @Test
    public void decode() throws Exception {
        Cfx cfx = Cfx.create("https://test.confluxrpc.com");
        Account account = Account.create(cfx, "0xc27da8e4c551cdd0324ee4913bade0bf520d00a6a096298175694dab70550f54");
        Account.Option option = new Account.Option();

        org.web3j.abi.datatypes.Address a1 = new Address("cfxtest:aasu6pa7u3rugb4awcuu937pd52wnc1x2jb4zd916k").getABIAddress();
        org.web3j.abi.datatypes.Address a2 = new Address("cfxtest:aasu6pa7u3rugb4awcuu937pd52wnc1x2jb4zd916k").getABIAddress();
        Utf8String uri1 = new Utf8String("https://nft-demo.oss-cn-zhangjiakou.aliyuncs.com/json/%E5%90%88%E7%BA%A6%E6%89%B9%E9%87%8F%E7%94%9F%E6%88%90.json");
        Utf8String uri2 = new Utf8String("https://nft-demo.oss-cn-zhangjiakou.aliyuncs.com/json/%E5%90%88%E7%BA%A6%E6%89%B9%E9%87%8F%E7%94%9F%E6%88%90.json");
        String contractAddress = "cfxtest:ace506v1phfdxt5d788aby849x7pgp2v4jbk4sdewg";

        DynamicArray<org.web3j.abi.datatypes.Address> address = new DynamicArray<org.web3j.abi.datatypes.Address>(org.web3j.abi.datatypes.Address.class, Arrays.asList(a1, a2));
        DynamicArray<Utf8String> urls = new DynamicArray<>(Utf8String.class, Arrays.asList(uri1, uri2));
        Function func = new Function(
                "AdminCreateNFTBatch",
                Arrays.asList(address, urls),
                Collections.<TypeReference<?>>emptyList()
        );
        String encode = DefaultFunctionEncoder.encode(func);
        System.out.println(encode);

        String data = account.callWithData(new Address(contractAddress), encode);
        subLog();
        System.out.println(data);

        cfx.getLogs(new LogFilter());

        TypeReference ts = TypeReference.create(Uint256.class);
        List<Type> list = DefaultFunctionReturnDecoder.decode(data, Arrays.asList(ts));

        System.out.println(list);

    }


    public void subLog() throws ConnectException {
        System.out.println("开始订阅");
        WebSocketService wsService = new WebSocketService("wss://test.confluxrpc.com/ws", false);
        wsService.connect();
        Cfx cfx = Cfx.create(wsService);
        LogFilter filter = new LogFilter();
        List<Address> toFilterAddress = new ArrayList<Address>();
        toFilterAddress.add(new Address("cfxtest:aasu6pa7u3rugb4awcuu937pd52wnc1x2jb4zd916k"));
        filter.setAddress(toFilterAddress);

        List<List<String>> topics = new ArrayList<>();

        List<String> sigTopic = new ArrayList<>();

        String creatBatch = EventEncoder.buildEventSignature("CreateBatch(address, address, uint256[])");
        sigTopic.add(creatBatch);
        topics.add(sigTopic);

        filter.setTopics(topics);
        final Flowable<LogNotification> events2 = cfx.subscribeLogs(filter);
        final Disposable disposable2 = events2.subscribe(event -> {
            Log log = event.getParams().getResult();
            System.out.println("log--->");
            System.out.println(log);
            String data = log.getData();
            TypeReference uint256TypeReference = TypeReference.create(Uint256.class);
            List<TypeReference<Type>> decodeTypes = Arrays.asList(uint256TypeReference);
            List<Type> list = DefaultFunctionReturnDecoder.decode(data, decodeTypes);
            System.out.println(list);
            // get and decode log topic or data
            // https://github.com/conflux-fans/crypto-knowledge/blob/main/blogs/java-sdk-abi-encode.md#event-%E7%BC%96%E8%A7%A3%E7%A0%81
        });
        System.out.println("结束订阅");
        disposable2.dispose();
    }


    @Test
    public void decodeGreetingResult() {
        String helloWorldData = "0x0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000b68656c6c6f20776f726c64000000000000000000000000000000000000000000";
        TypeReference stringTypeReference = TypeReference.create(Utf8String.class);
        // 使用  DefaultFunctionReturnDecoder 来解析 data,
        List<Type> list = DefaultFunctionReturnDecoder.decode(helloWorldData, Arrays.asList(stringTypeReference));
        System.out.println(list.get(0).getValue());
    }

    @Test
    public void decodeTxData() {
        // ERC721 transferFrom data
        String data0 = "0x42842e0e000000000000000000000000167cd6a8c5b31e348a634b4f0788fa2990a180e40000000000000000000000001deffe0e4aab25767f62e0f71424f8d6375899da0000000000000000000000000000000000000000000000000000000000008ba9";
        Cfx cfx = Cfx.create("https://test.confluxrpc.com");
        String data = cfx.getTransactionByHash("0xf9a36a69c0678562192cb460d2044952eaf8c59769b41ca92fa9c3c33604da7a").sendAndGet().get().getData();
        System.out.println(data.substring(10));
        System.out.println("=============================");
        System.out.println(data0.substring(10));
        TypeReference fromTypeReference = TypeReference.create(org.web3j.abi.datatypes.Address.class);
        TypeReference toTypeReference = TypeReference.create(org.web3j.abi.datatypes.Address.class);
        TypeReference tokenIdTypeReference = TypeReference.create(Uint256.class);
        // use DefaultFunctionReturnDecoder to data,
        List<Type> list = DefaultFunctionReturnDecoder.decode(data.substring(10), Arrays.asList(fromTypeReference, toTypeReference, tokenIdTypeReference));
        System.out.println(list.get(2).getValue());
    }

    public void eventDecode() {
        List<TypeReference<?>> paramList = new ArrayList<>();
        paramList.add(TypeReference.create(org.web3j.abi.datatypes.Address.class, true));
        paramList.add(TypeReference.create(org.web3j.abi.datatypes.Address.class, true));
        paramList.add(TypeReference.create(DynamicArray.class, false));
        Event createBatch = new Event("CreateBatch", paramList);
        String signature = EventEncoder.encode(createBatch);

    }
}
