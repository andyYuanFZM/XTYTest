package com.chain33.cn.xtyTest;

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
 * 1对多转账样例，
 * ./合约文件见：sodity/Token.sol
 * @author 
 *
 */
public class Evm2Test {

	// 区块链IP
	String ip = "localhost";
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
   
        // 积分owner的地址和私钥
        String ownerAddress = "1FpJG4PHJARQNpyXkF6E6f9rvBx97zDaaJ";
        String ownerPrivateKey = "2d5cd98d637033028c7ee7ca78e5dd71d8aaaada0e8b3244f18bba7b6a75ba8c";
        
        // 合约代码：sodity/Token.sol
        String codes = "60806040526002805460ff19168117905534801561001c57600080fd5b506106bd8061002c6000396000f3fe608060405234801561001057600080fd5b50600436106100935760003560e01c806370a082311161006657806370a082311461028357806395d89b41146102a9578063a9059cbb146102b1578063dd62ed3e146102dd578063e6fa9afe1461030b57610093565b806306fdde031461009857806318160ddd14610115578063313ce5671461012f5780636b129d4c1461014d575b600080fd5b6100a06103b0565b6040805160208082528351818301528351919283929083019185019080838360005b838110156100da5781810151838201526020016100c2565b50505050905090810190601f1680156101075780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b61011d61043e565b60408051918252519081900360200190f35b610137610444565b6040805160ff9092168252519081900360200190f35b6102816004803603606081101561016357600080fd5b8135919081019060408101602082013564010000000081111561018557600080fd5b82018360208201111561019757600080fd5b803590602001918460018302840111640100000000831117156101b957600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929594936020810193503591505064010000000081111561020c57600080fd5b82018360208201111561021e57600080fd5b8035906020019184600183028401116401000000008311171561024057600080fd5b91908080601f01602080910402602001604051908101604052809392919081815260200183838082843760009201919091525092955061044d945050505050565b005b61011d6004803603602081101561029957600080fd5b50356001600160a01b031661049a565b6100a06104ac565b610281600480360360408110156102c757600080fd5b506001600160a01b038135169060200135610506565b61011d600480360360408110156102f357600080fd5b506001600160a01b0381358116916020013516610515565b6102816004803603604081101561032157600080fd5b81019060208101813564010000000081111561033c57600080fd5b82018360208201111561034e57600080fd5b8035906020019184602083028401116401000000008311171561037057600080fd5b9190808060200260200160405190810160405280939291908181526020018383602002808284376000920191909152509295505091359250610532915050565b6000805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156104365780601f1061040b57610100808354040283529160200191610436565b820191906000526020600020905b81548152906001019060200180831161041957829003601f168201915b505050505081565b60035481565b60025460ff1681565b60025460ff16600a0a830260038190553360009081526004602090815260408220929092558351610480928501906105f4565b5080516104949060019060208401906105f4565b50505050565b60046020526000908152604090205481565b60018054604080516020600284861615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156104365780601f1061040b57610100808354040283529160200191610436565b610511338383610577565b5050565b600560209081526000928352604080842090915290825290205481565b600082511161054057600080fd5b60005b82518110156105725761056a3384838151811061055c57fe5b602002602001015184610577565b600101610543565b505050565b6001600160a01b03831660009081526004602052604090205481111561059c57600080fd5b6001600160a01b038216600090815260046020526040902054818101116105c257600080fd5b6001600160a01b0392831660009081526004602052604080822080548490039055929093168352912080549091019055565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061063557805160ff1916838001178555610662565b82800160010185558215610662579182015b82811115610662578251825591602001919060010190610647565b5061066e929150610672565b5090565b5b8082111561066e576000815560010161067356fea2646970667358221220147129555337f98e6ac2b4018213f4b2b3b6330db658a725a849ad51018e71f564736f6c634300060c0033";
        String abi = "[{\"inputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"initialSupply\",\"type\":\"uint256\"},{\"internalType\":\"string\",\"name\":\"tokenName\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"tokenSymbol\",\"type\":\"string\"}],\"name\":\"TokenCreate\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"name\":\"allowance\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"name\":\"balanceOf\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address[]\",\"name\":\"_to\",\"type\":\"address[]\"},{\"internalType\":\"uint256\",\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"batchtransfer\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"decimals\",\"outputs\":[{\"internalType\":\"uint8\",\"name\":\"\",\"type\":\"uint8\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"name\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"symbol\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"totalSupply\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"_to\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"transfer\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";

        QueryTransactionResult txResult;
        // 部署合约
        try {
            byte[] code = ByteUtil.merge(HexUtil.fromHexString(codes), abi.getBytes());

            txEncode = EvmUtil.createEvmContract(code, "", "evm-sdk-test", privateKey, "");
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
        
        // 调用合约，生成初始资产
        int totalsupply = 100000;
        String tokenName = "信通院测试积分";
        String tokenSymbol = "XTY";
    	byte[] setOwner = EvmUtil.encodeParameter(abi, "TokenCreate", totalsupply,tokenName,tokenSymbol);
        txEncode = EvmUtil.callEvmContract(setOwner,"", 0, contractAddress, ownerPrivateKey, "");
        txhash = client.submitTransaction(txEncode);
        System.out.print("发行积分交易hash = " + txhash);
        Thread.sleep(5000);
        txResult = client.queryTransaction(txhash);
        System.out.println("; 执行结果 = " + txResult.getReceipt().getTyname());
        
        // 查询owner地址下有多少积分
        byte[] packAbiGet = EvmUtil.encodeParameter(abi, "balanceOf", ownerAddress);

        JSONObject query = client.callEVMAbi(contractAddress, HexUtil.toHexString(packAbiGet));
        Assert.assertNotNull(query);
        JSONObject output = query.getJSONObject("result");
        String rawData = output.getString("rawData");
        System.out.println(HexUtil.removeHexHeader(rawData));
        System.out.println("转账前ownerAddres" + ownerAddress + " 中积分余额： " + HexUtil.hexStringToAlgorism(HexUtil.removeHexHeader(rawData)));

        
        // 从owner地址批量转账给他人
        String[] to = {"1AqfN1QwvXAmczozYf4kEKny5ytM9Qetkv","1EjxeeyKLDNb31YRUHEovsZCg4V7Y1T4WU","1GoW17bVLzaPMRyWnQcGNmeieT4669rAjf"};
    	// 調用batchtransfer方法來存證，使用非权限用户签名
    	byte[] batchtransfer = EvmUtil.encodeParameter(abi, "batchtransfer", to, 100);
        txEncode = EvmUtil.callEvmContract(batchtransfer,"", 0, contractAddress, ownerPrivateKey, "");
        txhash = client.submitTransaction(txEncode);
        System.out.print("批量转账的的交易hash = " + txhash);
        Thread.sleep(5000);
        txResult = client.queryTransaction(txhash);
        System.out.println("; 执行结果 = " + txResult.getReceipt().getTyname());
        
        packAbiGet = EvmUtil.encodeParameter(abi, "balanceOf", ownerAddress);

        query = client.callEVMAbi(contractAddress, HexUtil.toHexString(packAbiGet));
        output = query.getJSONObject("result");
        rawData = output.getString("rawData");
        System.out.println("转账后ownerAddres" + ownerAddress + " 中积分余额： " + HexUtil.hexStringToAlgorism(HexUtil.removeHexHeader(rawData)));
        
        packAbiGet = EvmUtil.encodeParameter(abi, "balanceOf", "1AqfN1QwvXAmczozYf4kEKny5ytM9Qetkv");

        query = client.callEVMAbi(contractAddress, HexUtil.toHexString(packAbiGet));
        output = query.getJSONObject("result");
        rawData = output.getString("rawData");
        System.out.println("1AqfN1QwvXAmczozYf4kEKny5ytM9Qetkv 积分余额： " + HexUtil.hexStringToAlgorism(HexUtil.removeHexHeader(rawData)));

        packAbiGet = EvmUtil.encodeParameter(abi, "balanceOf", "1EjxeeyKLDNb31YRUHEovsZCg4V7Y1T4WU");

        query = client.callEVMAbi(contractAddress, HexUtil.toHexString(packAbiGet));
        output = query.getJSONObject("result");
        rawData = output.getString("rawData");
        System.out.println("1EjxeeyKLDNb31YRUHEovsZCg4V7Y1T4WU 积分余额： " + HexUtil.hexStringToAlgorism(HexUtil.removeHexHeader(rawData)));
        
        packAbiGet = EvmUtil.encodeParameter(abi, "balanceOf", "1GoW17bVLzaPMRyWnQcGNmeieT4669rAjf");

        query = client.callEVMAbi(contractAddress, HexUtil.toHexString(packAbiGet));
        output = query.getJSONObject("result");
        rawData = output.getString("rawData");
        System.out.println("1GoW17bVLzaPMRyWnQcGNmeieT4669rAjf 积分余额： " + HexUtil.hexStringToAlgorism(HexUtil.removeHexHeader(rawData)));
    }


}
