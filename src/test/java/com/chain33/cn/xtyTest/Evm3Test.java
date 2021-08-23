package com.chain33.cn.xtyTest;

import org.junit.Assert;
import org.junit.Test;

import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.rpcresult.QueryTransactionResult;
import cn.chain33.javasdk.utils.ByteUtil;
import cn.chain33.javasdk.utils.EvmUtil;
import cn.chain33.javasdk.utils.HexUtil;
import cn.chain33.javasdk.utils.TransactionUtil;

/**
 * 1对多转账样例，
 * ./合约文件见：sodity/ContractManager.sol
 * @author 
 *
 */
public class Evm3Test {

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
   
       
        // 合约代码：sodity/Token.sol
        String codes = "608060405234801561001057600080fd5b50600080546001600160a01b031916331790556002805460ff191690556102f78061003c6000396000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c8063092a5cce146100675780632e64cec1146100715780636057361d1461008b57806362a5af3b146100a85780636a28f000146100b05780638da5cb5b146100b8575b600080fd5b61006f6100dc565b005b610079610132565b60408051918252519081900360200190f35b61006f600480360360208110156100a157600080fd5b5035610194565b61006f6101f1565b61006f610253565b6100c06102b2565b604080516001600160a01b039092168252519081900360200190f35b6000546001600160a01b0316331461012f576040805162461bcd60e51b81526020600482015260116024820152702cb7ba9030b932903737ba1037bbb732b960791b604482015290519081900360640190fd5b33ff5b60025460009060ff161561018d576040805162461bcd60e51b815260206004820152601e60248201527f5468697320636f6e74726163742068617368206265656e2066726f7a656e0000604482015290519081900360640190fd5b5060015490565b60025460ff16156101ec576040805162461bcd60e51b815260206004820152601e60248201527f5468697320636f6e74726163742068617368206265656e2066726f7a656e0000604482015290519081900360640190fd5b600155565b6000546001600160a01b03163314610244576040805162461bcd60e51b81526020600482015260116024820152702cb7ba9030b932903737ba1037bbb732b960791b604482015290519081900360640190fd5b6002805460ff19166001179055565b6000546001600160a01b031633146102a6576040805162461bcd60e51b81526020600482015260116024820152702cb7ba9030b932903737ba1037bbb732b960791b604482015290519081900360640190fd5b6002805460ff19169055565b6000546001600160a01b03168156fea26469706673582212202fe66e104c4e6219c736c6392bc876628213b2d2bbf44ae70f746493e4667e0364736f6c634300060c0033";
        String abi = "[{\"inputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"inputs\":[],\"name\":\"destroyContract\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"freeze\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"owner\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"retrieve\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"num\",\"type\":\"uint256\"}],\"name\":\"store\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"unfreeze\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";

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
        
        // 调用合约
        int totalsupply = 100000;
    	byte[] store = EvmUtil.encodeParameter(abi, "store", totalsupply);
        txEncode = EvmUtil.callEvmContract(store,"", 0, contractAddress, privateKey, "");
        txhash = client.submitTransaction(txEncode);
        System.out.print("未冻结时，调用合约hash = " + txhash);
        Thread.sleep(5000);
        txResult = client.queryTransaction(txhash);
        System.out.println("; 执行结果 = " + txResult.getReceipt().getTyname());
        
        // 调用冻结合约
    	byte[] frozen = EvmUtil.encodeParameter(abi, "freeze");
        txEncode = EvmUtil.callEvmContract(frozen,"", 0, contractAddress, privateKey, "");
        txhash = client.submitTransaction(txEncode);
        System.out.print("调用冻结合约hash = " + txhash);
        Thread.sleep(5000);
        txResult = client.queryTransaction(txhash);
        System.out.println("; 执行结果 = " + txResult.getReceipt().getTyname());
        
        // 再次调用合约
        txEncode = EvmUtil.callEvmContract(store,"", 0, contractAddress, privateKey, "");
        txhash = client.submitTransaction(txEncode);
        System.out.print("合约被 冻结时，调用合约hash = " + txhash);
        Thread.sleep(5000);
        txResult = client.queryTransaction(txhash);
        System.out.println("; 执行结果 = " + txResult.getReceipt().getTyname());
        
        // 调用解冻合约
    	byte[] unfrozen = EvmUtil.encodeParameter(abi, "unfreeze");
        txEncode = EvmUtil.callEvmContract(unfrozen,"", 0, contractAddress, privateKey, "");
        txhash = client.submitTransaction(txEncode);
        System.out.print("调用解冻合约hash = " + txhash);
        Thread.sleep(5000);
        txResult = client.queryTransaction(txhash);
        System.out.println("; 执行结果 = " + txResult.getReceipt().getTyname());
        
        // 再次调用合约
        txEncode = EvmUtil.callEvmContract(store,"", 0, contractAddress, privateKey, "");
        txhash = client.submitTransaction(txEncode);
        System.out.print("合约解冻时，调用合约hash = " + txhash);
        Thread.sleep(5000);
        txResult = client.queryTransaction(txhash);
        System.out.println("; 执行结果 = " + txResult.getReceipt().getTyname());
        
        // 销毁合约
    	byte[] destroy = EvmUtil.encodeParameter(abi, "destroyContract");
        txEncode = EvmUtil.callEvmContract(destroy,"", 0, contractAddress, privateKey, "");
        txhash = client.submitTransaction(txEncode);
        System.out.print("调用销毁合约hash = " + txhash);
        Thread.sleep(5000);
        txResult = client.queryTransaction(txhash);
        System.out.println("; 执行结果 = " + txResult.getReceipt().getTyname());
        
        // 再次调用合约
        txEncode = EvmUtil.callEvmContract(store,"", 0, contractAddress, privateKey, "");
        txhash = client.submitTransaction(txEncode);
        System.out.print("合约被销毁时，调用合约hash = " + txhash);
        Thread.sleep(5000);
        txResult = client.queryTransaction(txhash);
        System.out.println("; 执行结果 = " + txResult.getReceipt().getTyname());
        
    }


}
