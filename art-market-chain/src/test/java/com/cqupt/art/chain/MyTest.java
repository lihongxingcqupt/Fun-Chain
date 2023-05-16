package com.cqupt.art.chain;

import conflux.web3j.Account;
import conflux.web3j.Cfx;
import conflux.web3j.contract.ContractCall;
import conflux.web3j.contract.abi.DecodeUtil;
import conflux.web3j.contract.abi.TupleDecoder;
import conflux.web3j.response.Transaction;
import conflux.web3j.response.events.NewHeadsNotification;
import conflux.web3j.types.Address;
import conflux.web3j.types.RawTransaction;
import conflux.web3j.types.SendTransactionResult;
import conflux.web3j.types.TransactionBuilder;
import io.reactivex.Flowable;
import org.junit.Test;
import org.springframework.stereotype.Component;
import org.web3j.abi.*;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.BatchResponse;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.websocket.WebSocketService;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyTest {

    @Test
    public void batchRpc() throws Exception {
        String addr = "cfxtest:aasu6pa7u3rugb4awcuu937pd52wnc1x2jb4zd916k";
        Cfx cfx = Cfx.create("https://test.confluxrpc.com");
        Web3j client = Web3j.build(new HttpService("https://test.confluxrpc.com"));
        Account account = Account.create(cfx, "0xc27da8e4c551cdd0324ee4913bade0bf520d00a6a096298175694dab70550f54");

        Account.Option option = new Account.Option();

        org.web3j.abi.datatypes.Address a1 = new Address("cfxtest:aasu6pa7u3rugb4awcuu937pd52wnc1x2jb4zd916k").getABIAddress();
        org.web3j.abi.datatypes.Address a2 = new Address("cfxtest:aasu6pa7u3rugb4awcuu937pd52wnc1x2jb4zd916k").getABIAddress();
        Utf8String uri1 = new Utf8String("https://nft-demo.oss-cn-zhangjiakou.aliyuncs.com/json/%E5%90%88%E7%BA%A6%E6%89%B9%E9%87%8F%E7%94%9F%E6%88%90.json");
        Utf8String uri2 = new Utf8String("https://nft-demo.oss-cn-zhangjiakou.aliyuncs.com/json/%E5%90%88%E7%BA%A6%E6%89%B9%E9%87%8F%E7%94%9F%E6%88%90.json");
        String contractAddress = "cfxtest:ace506v1phfdxt5d788aby849x7pgp2v4jbk4sdewg";
        ContractCall contract = new ContractCall(cfx, new Address(contractAddress));
        contract.buildFrom(new Address("cfxtest:aasu6pa7u3rugb4awcuu937pd52wnc1x2jb4zd916k"));

        List<org.web3j.abi.datatypes.Address> addresses = Arrays.asList(a1, a2);

        List<Utf8String> uris = Arrays.asList(uri1, uri2);

        List<Type> inputParameters = new ArrayList<>();

        inputParameters.add(new DynamicArray<>(org.web3j.abi.datatypes.Address.class, addresses));
        inputParameters.add(new DynamicArray<>(Utf8String.class, uris));

        Function func = new Function("AdminCreateNFTBatch", inputParameters, Arrays.<TypeReference<?>>asList(new TypeReference<Uint>() {
        }));
        String data = DefaultFunctionEncoder.encode(func);
        RawTransaction rtx = option.buildTx(cfx, account.getAddress(), account.getPoolNonce(), new Address(contractAddress), data);
        String sign = account.sign(rtx);

        String value = contract.call("userNFTs", new Address("cfxtest:aasu6pa7u3rugb4awcuu937pd52wnc1x2jb4zd916k")
                .getABIAddress()).sendAndGet();

        String createBatch = contract.call("AdminCreateNFTBatch", new DynamicArray<>(org.web3j.abi.datatypes.Address.class, addresses),
                new DynamicArray<>(Utf8String.class, uris)).sendAndGet();

        List<BigInteger> batchId = decodeResp(createBatch);
        System.out.println(batchId);

        List<Type> values = FunctionReturnDecoder.decode(value, Utils.convert(Arrays.asList(new TypeReference<Utf8String>() {
        }, new TypeReference<DynamicArray<Uint256>>() {
        })));

        List<BigInteger> bigIntegers = decodeResp(value);
        System.out.println(bigIntegers);
//        String s = cfx.sendRawTransaction(sign).sendAndGet();
//        System.out.println(s);

//        BatchResponse send = client.newBatch().add(cfx.sendRawTransaction(sign)).send();
//        System.out.println(send.getResponses().get(0).getResult());
    }

    @Test
    public void getDetailByHash() throws ConnectException {
        String addr = "cfxtest:aasu6pa7u3rugb4awcuu937pd52wnc1x2jb4zd916k";
        WebSocketService wsService = new WebSocketService("ws://test.confluxrpc.com:12535/", false);
        wsService.connect();
        Cfx cfx = Cfx.create(wsService);
        Flowable<NewHeadsNotification> events = cfx.subscribeNewHeads();
        events.subscribe(event -> {
            System.out.println(event.getParams().getResult());
        });
        Transaction transaction = cfx.getTransactionByHash("0x47063f2603efb0f56ae5bc4def67d636b83821110d1f5e6e51f362367e51ce48").sendAndGet().get();
        TypeReference<DynamicArray<Utf8String>> returnType = new TypeReference<DynamicArray<Utf8String>>() {
        };
        DecodeUtil.decode(transaction.getData(), returnType);
        System.out.println(transaction.getValue());
        System.out.println(transaction.getData());
    }

    public List<BigInteger> decodeResp(String encoded) {
        encoded = Numeric.cleanHexPrefix(encoded);
        TupleDecoder decoder = new TupleDecoder(encoded);
        //返回值有几个这里就跳几个
        decoder.nextUint256();
        int length = decoder.nextUint256().intValueExact();
//        int length = decoder.nextUint256().intValueExact();
        BigInteger[] values = new BigInteger[length];
        for (int i = 0; i < length; i++) {
            values[i] = decoder.nextUint256();
            System.out.println("values[" + i + "] =" + values[i].toString());
        }
        return Arrays.asList(values);
    }

    @Test
    public void decodeDataTest() {
        String data = "0x1b122263000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000000000000000000000000a000000000000000000000000000000000000000000000000000000000000000020000000000000000000000001a56ee3a821c9f85e326bab84a8b827bd1ad895a0000000000000000000000001a56ee3a821c9f85e326bab84a8b827bd1ad895a0000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000000000000000000000000e0000000000000000000000000000000000000000000000000000000000000007168747470733a2f2f6e66742d64656d6f2e6f73732d636e2d7a68616e676a69616b6f752e616c6979756e63732e636f6d2f6a736f6e2f2545352539302538382545372542412541362545362538392542392545392538372538462545372539342539462545362538382539302e6a736f6e000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000007168747470733a2f2f6e66742d64656d6f2e6f73732d636e2d7a68616e676a69616b6f752e616c6979756e63732e636f6d2f6a736f6e2f2545352539302538382545372542412541362545362538392542392545392538372538462545372539342539462545362538382539302e6a736f6e000000000000000000000000000000";

        List<Type> decodeList = DefaultFunctionReturnDecoder.decode(data, Arrays.asList(TypeReference.create(Type.class)));
        System.out.println(decodeList);
    }

    public static void main(String[] args) throws Exception {
        Cfx cfx = Cfx.create("https://test.confluxrpc.com");
        Account account = Account.create(cfx, "0xc27da8e4c551cdd0324ee4913bade0bf520d00a6a096298175694dab70550f54");
        Account.Option option = new Account.Option();
        option.withChainId(1L);
        System.out.println(account.getAddress());
        org.web3j.abi.datatypes.Address a1 = new Address("cfxtest:aasu6pa7u3rugb4awcuu937pd52wnc1x2jb4zd916k").getABIAddress();
        org.web3j.abi.datatypes.Address a2 = new Address("cfxtest:aasu6pa7u3rugb4awcuu937pd52wnc1x2jb4zd916k").getABIAddress();
        Utf8String uri1 = new Utf8String("https://nft-demo.oss-cn-zhangjiakou.aliyuncs.com/json/%E5%90%88%E7%BA%A6%E6%89%B9%E9%87%8F%E7%94%9F%E6%88%90.json");
        Utf8String uri2 = new Utf8String("https://nft-demo.oss-cn-zhangjiakou.aliyuncs.com/json/%E5%90%88%E7%BA%A6%E6%89%B9%E9%87%8F%E7%94%9F%E6%88%90.json");
        String contractAddress = "cfxtest:ace506v1phfdxt5d788aby849x7pgp2v4jbk4sdewg";

        List<org.web3j.abi.datatypes.Address> addresses = Arrays.asList(a1, a2);
        List<Utf8String> uris = Arrays.asList(uri1, uri2);
        List<Type> inputParameters = new ArrayList<>();
        inputParameters.add(new DynamicArray<>(org.web3j.abi.datatypes.Address.class, addresses));
        inputParameters.add(new DynamicArray<>(Utf8String.class, uris));

        Function func = new Function("AdminCreateNFTBatch", inputParameters, Arrays.<TypeReference<?>>asList(new TypeReference<Uint>() {
        }));
        String data = DefaultFunctionEncoder.encode(func);
        account.callWithData(new Address(contractAddress), data);

        RawTransaction rtx = option.buildTx(cfx, account.getAddress(), account.getPoolNonce(), new Address(contractAddress), data);
        String sign = account.sign(rtx);
        SendTransactionResult result = account.send(sign);
        System.out.println("交易hash" + result.getTxHash());
        System.out.println(result);
    }
}
