package com.chain33.cn.xtyTest;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.rpcresult.QueryTransactionResult;
import cn.chain33.javasdk.utils.ByteUtil;
import cn.chain33.javasdk.utils.EvmUtil;
import cn.chain33.javasdk.utils.HexUtil;
import cn.chain33.javasdk.utils.TransactionUtil;

/**
 * 两个合约之间方法调用， A合约是权限合约，B合约是存证合约，在B合约中调用A合约中的权限校验方法，成功能才访问B合约，否则提示无权限
 * 合约见： ./xtyTest/BasicAuth.sol, ./xtyTest/FileStore.sol
 * @author 
 *
 */
public class Evm6Test {

	// 区块链IP
	String ip = "124.71.60.111";
	// 区块链服务端口
	int port = 8801;
	RpcClient client = new RpcClient(ip, port);

	// 合约部署人对应的区块链地址和私钥
	String address = "1M8gvr1DZ1KKVjf6XW6aYR6pHGeDRspdCx";
    String privateKey = "452281167e7fa65cdd5bcb1e40565bf06a1aa3ce4fc8954848d0427a4cc27180";

    @Test
    public void testEvmContract() throws Exception {

        // 部署合约
        String txEncode;
        String txhash = "";
   
        // 存证合约owner的address和private key
        String ownerAddress = "1FpJG4PHJARQNpyXkF6E6f9rvBx97zDaaJ";
        String ownerPrivateKey = "2d5cd98d637033028c7ee7ca78e5dd71d8aaaada0e8b3244f18bba7b6a75ba8c";
        
        // 合约代码：https://bty33.oss-cn-shanghai.aliyuncs.com/chain33Dev/BAAS/%E5%AD%98%E8%AF%81%E5%90%88%E7%BA%A6.zip
        String codes = "608060405234801561001057600080fd5b506040516100f83803806100f88339818101604052602081101561003357600080fd5b505160005560b2806100466000396000f3fe6080604052348015600f57600080fd5b506004361060325760003560e01c80632e64cec11460375780636057361d14604f575b600080fd5b603d606b565b60408051918252519081900360200190f35b606960048036036020811015606357600080fd5b50356071565b005b60005490565b60008054909101905556fea2646970667358221220ae83fde96808bc7b246777f8c2c2873dea9be279707d944fd84638b6cdeb107f64736f6c634300060c0033";
        String abi = "[{\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"_a\",\"type\":\"uint256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"inputs\":[],\"name\":\"retrieve\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"num\",\"type\":\"uint256\"}],\"name\":\"store\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";

        QueryTransactionResult txResult;
        // 部署合约
        try {
        	byte[] evmWithCode = EvmUtil.encodeContructor(abi, 100);
            byte[] code = ByteUtil.merge(HexUtil.fromHexString(codes), evmWithCode);

            txEncode = EvmUtil.createEvmContract(code, "", "evm-sdk-test", privateKey, "", 10000000);
            txhash = client.submitTransaction(txEncode);
            System.out.print("部署合约交易hash = " + txhash);
            Thread.sleep(5000);
            txResult = client.queryTransaction(txhash);
            Assert.assertEquals(txResult.getReceipt().getTyname(), "ExecOk");
            System.out.println("; 执行结果 = " + txResult.getReceipt().getTyname());
                        
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        
        // 计算合约地址
        String contractAddress = TransactionUtil.convertExectoAddr(address + txhash.substring(2));
        System.out.println("部署好的合约地址 = " + contractAddress);
        
               
        // 使用非授权的地址调用存证合约，此笔交易会执行失败，在交易体中会打印错误信息
    	// 調用setFileStockById方法來存證，使用非权限用户签名
    	byte[] setFileStock = EvmUtil.encodeParameter(abi, "store", 15);
        txEncode = EvmUtil.callEvmContract(setFileStock,"", 0, contractAddress, privateKey, "", 10000000);
        txhash = client.submitTransaction(txEncode);
        System.out.print("使用非授权地址调用存证合约的交易hash = " + txhash);
        Thread.sleep(5000);
        txResult = client.queryTransaction(txhash);
        System.out.println("; 执行结果 = " + txResult.getReceipt().getTyname());
        
        
        byte[] packAbiGet = EvmUtil.encodeParameter(abi, "retrieve");

        JSONObject query = client.callEVMAbi(contractAddress, HexUtil.toHexString(packAbiGet));
        Assert.assertNotNull(query);
        JSONObject output = query.getJSONObject("result");
        List<?> result = EvmUtil.decodeOutput(abi, "retrieve", output);
        System.out.println("根据ID从链上的查询结果：" + result);
       
    }


}
