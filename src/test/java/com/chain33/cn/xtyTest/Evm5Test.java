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
 * N对N转账
 * 流程描述：
 *    1. 创建一个用户，通过这个用户创建一定数量的积分
 *    2. 从以上用户，分别转一定数量的积分到三个新用户下
 *    3. 再从这三个用户下，多对多的且一一对应的将一定数量的积分转到另外三个用户地址下
 * ./合约文件见：sodity/NtoNTransfer.sol
 * @author 
 *
 */
public class Evm5Test {

	// 区块链IP
//	String ip = "121.36.222.205";
	String ip = "172.22.17.194";
	// 区块链服务端口
	int port = 8801;
	RpcClient client = new RpcClient(ip, port);

	// 合约创建人的地址和私钥,合约的管理人
	String address = "1M8gvr1DZ1KKVjf6XW6aYR6pHGeDRspdCx";
    String privateKey = "452281167e7fa65cdd5bcb1e40565bf06a1aa3ce4fc8954848d0427a4cc27180";
    
    // 积分owner的地址和私钥
    String ownerAddress = "1FpJG4PHJARQNpyXkF6E6f9rvBx97zDaaJ";
    String ownerPrivateKey = "2d5cd98d637033028c7ee7ca78e5dd71d8aaaada0e8b3244f18bba7b6a75ba8c";
    
    long gas = 100000000;
    
    // 合约代码：sodity/Token.sol
    String codes = "60806040526002805460ff19168117905534801561001c57600080fd5b5061082c8061002c6000396000f3fe608060405234801561001057600080fd5b506004361061009e5760003560e01c806395d89b411161006657806395d89b41146102b0578063a176cb68146102b8578063a9059cbb146103dd578063dd62ed3e14610409578063e6fa9afe146104375761009e565b806306fdde03146100a357806318160ddd14610120578063313ce5671461013a5780636b129d4c1461015857806370a082311461028a575b600080fd5b6100ab6104da565b6040805160208082528351818301528351919283929083019185019080838360005b838110156100e55781810151838201526020016100cd565b50505050905090810190601f1680156101125780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b610128610568565b60408051918252519081900360200190f35b61014261056e565b6040805160ff9092168252519081900360200190f35b6102886004803603606081101561016e57600080fd5b81359190810190604081016020820135600160201b81111561018f57600080fd5b8201836020820111156101a157600080fd5b803590602001918460018302840111600160201b831117156101c257600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295949360208101935035915050600160201b81111561021457600080fd5b82018360208201111561022657600080fd5b803590602001918460018302840111600160201b8311171561024757600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929550610577945050505050565b005b610128600480360360208110156102a057600080fd5b50356001600160a01b03166105c4565b6100ab6105d6565b610288600480360360608110156102ce57600080fd5b810190602081018135600160201b8111156102e857600080fd5b8201836020820111156102fa57600080fd5b803590602001918460208302840111600160201b8311171561031b57600080fd5b9190808060200260200160405190810160405280939291908181526020018383602002808284376000920191909152509295949360208101935035915050600160201b81111561036a57600080fd5b82018360208201111561037c57600080fd5b803590602001918460208302840111600160201b8311171561039d57600080fd5b9190808060200260200160405190810160405280939291908181526020018383602002808284376000920191909152509295505091359250610630915050565b610288600480360360408110156103f357600080fd5b506001600160a01b038135169060200135610683565b6101286004803603604081101561041f57600080fd5b506001600160a01b0381358116916020013516610692565b6102886004803603604081101561044d57600080fd5b810190602081018135600160201b81111561046757600080fd5b82018360208201111561047957600080fd5b803590602001918460208302840111600160201b8311171561049a57600080fd5b91908080602002602001604051908101604052809392919081815260200183836020028082843760009201919091525092955050913592506106af915050565b6000805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156105605780601f1061053557610100808354040283529160200191610560565b820191906000526020600020905b81548152906001019060200180831161054357829003601f168201915b505050505081565b60035481565b60025460ff1681565b60025460ff16600a0a8302600381905533600090815260046020908152604082209290925583516105aa92850190610763565b5080516105be906001906020840190610763565b50505050565b60046020526000908152604090205481565b60018054604080516020600284861615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156105605780601f1061053557610100808354040283529160200191610560565b815183511461063e57600080fd5b60005b83518110156105be5761067b84828151811061065957fe5b602002602001015184838151811061066d57fe5b6020026020010151846106e6565b600101610641565b61068e3383836106e6565b5050565b600560209081526000928352604080842090915290825290205481565b60008251116106bd57600080fd5b60005b82518110156106e1576106d93384838151811061066d57fe5b6001016106c0565b505050565b6001600160a01b03831660009081526004602052604090205481111561070b57600080fd5b6001600160a01b0382166000908152600460205260409020548181011161073157600080fd5b6001600160a01b0392831660009081526004602052604080822080548490039055929093168352912080549091019055565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106107a457805160ff19168380011785556107d1565b828001600101855582156107d1579182015b828111156107d15782518255916020019190600101906107b6565b506107dd9291506107e1565b5090565b5b808211156107dd57600081556001016107e256fea264697066735822122015d9c041c10b3ae076d3191770534d3dd45d7325de65386cb56cce9775442ba164736f6c634300060c0033";
    String abi = "[{\"inputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"initialSupply\",\"type\":\"uint256\"},{\"internalType\":\"string\",\"name\":\"tokenName\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"tokenSymbol\",\"type\":\"string\"}],\"name\":\"TokenCreate\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"name\":\"allowance\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"name\":\"balanceOf\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address[]\",\"name\":\"_from\",\"type\":\"address[]\"},{\"internalType\":\"address[]\",\"name\":\"_to\",\"type\":\"address[]\"},{\"internalType\":\"uint256\",\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"batchNtoN\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address[]\",\"name\":\"_to\",\"type\":\"address[]\"},{\"internalType\":\"uint256\",\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"batchtransfer\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"decimals\",\"outputs\":[{\"internalType\":\"uint8\",\"name\":\"\",\"type\":\"uint8\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"name\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"symbol\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"totalSupply\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"_to\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"transfer\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";

    @Test
    public void testEvmContract() throws Exception {

        // 部署合约
        String txEncode;
        String txhash = "";
   

        QueryTransactionResult txResult;
        // 部署合约
        byte[] code = ByteUtil.merge(HexUtil.fromHexString(codes), abi.getBytes());

        txEncode = EvmUtil.createEvmContract(code, "", "evm-sdk-test", privateKey, "", gas);
        txhash = client.submitTransaction(txEncode);
        System.out.print("部署合约交易hash = " + txhash);
        Thread.sleep(5000);
        txResult = client.queryTransaction(txhash);
        Assert.assertEquals(txResult.getReceipt().getTyname(), "ExecOk");
        System.out.println("; 执行结果 = " + txResult.getReceipt().getTyname());
                        
        
        // 计算合约地址
        String contractAddress = TransactionUtil.convertExectoAddr(address + txhash.substring(2));
        System.out.println("部署好的合约地址 = " + contractAddress);
        
        // 调用合约，生成初始资产
        int totalsupply = 100000;
        String tokenName = "信通院测试积分";
        String tokenSymbol = "XTY";
    	byte[] setOwner = EvmUtil.encodeParameter(abi, "TokenCreate", totalsupply,tokenName,tokenSymbol);
        txEncode = EvmUtil.callEvmContract(setOwner,"", 0, contractAddress, ownerPrivateKey, "", gas);
        txhash = client.submitTransaction(txEncode);
        System.out.print("发行积分交易hash = " + txhash);
        Thread.sleep(5000);
        txResult = client.queryTransaction(txhash);
        System.out.println("; 执行结果 = " + txResult.getReceipt().getTyname());
        
       
        // 从owner地址批量转账给他人
        String[] addressList1 = {"1AqfN1QwvXAmczozYf4kEKny5ytM9Qetkv","1EjxeeyKLDNb31YRUHEovsZCg4V7Y1T4WU","1GoW17bVLzaPMRyWnQcGNmeieT4669rAjf"};
        String[] addressList2 = {"1PYePqkjXPax7pVCeNetoCP1beCX3qcjj7","19UW8z8cVBvW8FWEQVwh9xzBVnXpgoJNfW","1BZ9Tn9PHpTuYMBLQ4AiDrGUsnC1nWjiCY"};

    	// 調用batchtransfer方法來存證，使用非权限用户签名
    	byte[] batchtransfer = EvmUtil.encodeParameter(abi, "batchtransfer", addressList1, 100);
        txEncode = EvmUtil.callEvmContract(batchtransfer,"", 0, contractAddress, ownerPrivateKey, "", gas);
        txhash = client.submitTransaction(txEncode);
        System.out.print("1对多批量转账的的交易hash = " + txhash);
        Thread.sleep(5000);
        txResult = client.queryTransaction(txhash);
        System.out.println("; 执行结果 = " + txResult.getReceipt().getTyname());
                
        int from1 = getBalance(addressList1[0], contractAddress);
        int from2 = getBalance(addressList1[1], contractAddress);
        int from3 = getBalance(addressList1[2], contractAddress);
        
        int to1 = getBalance(addressList2[0], contractAddress);
        int to2 = getBalance(addressList2[1], contractAddress);
        int to3 = getBalance(addressList2[2], contractAddress);

        System.out.println("=======================多对多发送交易前各地址余额 ===============================");
        System.out.println("来源地址1： 1AqfN1QwvXAmczozYf4kEKny5ytM9Qetkv 积分余额： " + from1);
        System.out.println("来源地址2： 1EjxeeyKLDNb31YRUHEovsZCg4V7Y1T4WU 积分余额： " + from2);
        System.out.println("来源地址3： 1GoW17bVLzaPMRyWnQcGNmeieT4669rAjf 积分余额： " + from3);
        
        System.out.println("去向地址1： 1PYePqkjXPax7pVCeNetoCP1beCX3qcjj7 积分余额： " + to1);
        System.out.println("去向地址2： 19UW8z8cVBvW8FWEQVwh9xzBVnXpgoJNfW 积分余额： " + to2);
        System.out.println("去向地址3： 1BZ9Tn9PHpTuYMBLQ4AiDrGUsnC1nWjiCY 积分余额： " + to3);
        
       	// 調用batchNtoN方法來存證，使用合约创建人的私钥签名
    	batchtransfer = EvmUtil.encodeParameter(abi, "batchNtoN", addressList1, addressList2, 15);
        txEncode = EvmUtil.callEvmContract(batchtransfer,"", 0, contractAddress, privateKey, "", gas);
        txhash = client.submitTransaction(txEncode);
        System.out.print("多对多批量转账的的交易hash = " + txhash);
        Thread.sleep(5000);
        txResult = client.queryTransaction(txhash);
        System.out.println("; 执行结果 = " + txResult.getReceipt().getTyname());
                
        from1 = getBalance(addressList1[0], contractAddress);
        from2 = getBalance(addressList1[1], contractAddress);
        from3 = getBalance(addressList1[2], contractAddress);
        
        to1 = getBalance(addressList2[0], contractAddress);
        to2 = getBalance(addressList2[1], contractAddress);
        to3 = getBalance(addressList2[2], contractAddress);
        
        System.out.println("=======================多对多发送交易后各地址余额,每个地址各转15个积分===============================");
        System.out.println("来源地址1： 1AqfN1QwvXAmczozYf4kEKny5ytM9Qetkv 积分余额： " + from1);
        System.out.println("来源地址2： 1EjxeeyKLDNb31YRUHEovsZCg4V7Y1T4WU 积分余额： " + from2);
        System.out.println("来源地址3： 1GoW17bVLzaPMRyWnQcGNmeieT4669rAjf 积分余额： " + from3);
        
        System.out.println("去向地址1： 1PYePqkjXPax7pVCeNetoCP1beCX3qcjj7 积分余额： " + to1);
        System.out.println("去向地址2： 19UW8z8cVBvW8FWEQVwh9xzBVnXpgoJNfW 积分余额： " + to2);
        System.out.println("去向地址3： 1BZ9Tn9PHpTuYMBLQ4AiDrGUsnC1nWjiCY 积分余额： " + to3);


    }
    
    /**
     * 获取地址下积分余额
     * @param address
     * @return
     * @throws Exception
     */
    private int getBalance(String address, String contractAddress) throws Exception {
    	 // 查询owner地址下有多少积分
        byte[] packAbiGet = EvmUtil.encodeParameter(abi, "balanceOf", address);

        JSONObject query = client.callEVMAbi(contractAddress, HexUtil.toHexString(packAbiGet));
        Assert.assertNotNull(query);
        JSONObject output = query.getJSONObject("result");
        String rawData = output.getString("rawData");
        return HexUtil.hexStringToAlgorism(HexUtil.removeHexHeader(rawData));
    }


}
