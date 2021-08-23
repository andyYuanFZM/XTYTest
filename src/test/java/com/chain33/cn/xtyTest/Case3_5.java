package com.chain33.cn.xtyTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

import cn.chain33.javasdk.client.Account;
import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.AccountInfo;
import cn.chain33.javasdk.model.TransferBalanceRequest;
import cn.chain33.javasdk.model.enums.SignType;
import cn.chain33.javasdk.model.rpcresult.AccountAccResult;
import cn.chain33.javasdk.model.rpcresult.QueryTransactionResult;
import cn.chain33.javasdk.utils.ByteUtil;
import cn.chain33.javasdk.utils.EvmUtil;
import cn.chain33.javasdk.utils.HexUtil;
import cn.chain33.javasdk.utils.TransactionUtil;

public class Case3_5 {
	
//	RpcClient client = new RpcClient(CommUtil.ip, CommUtil.port);
	RpcClient client = new RpcClient("localhost", 8801);

	Account account = new Account();
	
	/**
	 * 3.5.1 智能合约执行结果
	 * 3.5.2 查询支持
	 * @throws Exception 
	 */
	@Test
	public void Case3_5_1() throws Exception {
		
    	// 转出地址对应的私钥
    	String fromprivateKey = "CC38546E9E659D15E6B4893F0AB32A06D103931A8230B0BDE71459D2B27D6944";
    	String fromAddress = "14KEKbYtKKQm4wMthSK9J4La4nAiidGozt";
    	
    	String to = null;
    	List<String> addressList = new ArrayList<String>();
    	for (int i = 0; i < 10; i++) {
        	AccountInfo accountInfo = account.newAccountLocal();
    		to = accountInfo.getAddress();
    		addressList.add(to);
        	transfer("第" + i + "笔转账交易",1,to,fromprivateKey,fromAddress);
    	}
    	
    	List<AccountAccResult> queryBtyBalance;
    	addressList.add(fromAddress);
		queryBtyBalance = client.getCoinsBalance(addressList, "coins");
		if (queryBtyBalance != null) {
			for (AccountAccResult accountAccResult : queryBtyBalance) {
				System.out.println(accountAccResult);
			}
		}
	}
	
	
	/**
	 * 3.5.3 智能合约升级
	 * 合约见： ./solidity/MyContractV1.sol 和 ./solidity/MyContractV2.sol
	 * 测试部署：MyContractV1.sol合约存在bug， 在运行一端时间后，升级成MyContractV2.sol, 同时停止MyContractV1，并且将MyContractV1中的值一直到MyContractV2中
	 * @throws Exception 
	 */
	 @Test
	 public void Case3_5_3() throws Exception {
    	
	     // 合约部署人对应的区块链地址和私钥
		String address = "1M8gvr1DZ1KKVjf6XW6aYR6pHGeDRspdCx";
	    String privateKey = "452281167e7fa65cdd5bcb1e40565bf06a1aa3ce4fc8954848d0427a4cc27180";
        
        // 对应代码：MyContractV1.sol
        String codesOld = "60806040526000805460ff60201b1916905534801561001d57600080fd5b506000805465010000000000330264ff00000001600160c81b03199091161790556102308061004d6000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c806312253a6c146100515780634154b2431461005b57806361bc221a14610063578063f2c9ecd814610084575b600080fd5b61005961009e565b005b610059610114565b61006b610189565b6040805163ffffffff9092168252519081900360200190f35b61008c610195565b60408051918252519081900360200190f35b6000546501000000000090046001600160a01b031633146100fd576040805162461bcd60e51b815260206004820152601460248201527329b2b73232b91034b9903737ba1037bbb732b91760611b604482015290519081900360640190fd5b6000805464ff000000001916640100000000179055565b600054640100000000900460ff161561016b576040805162461bcd60e51b815260206004820152601460248201527321b7b73a3930b1ba1034b99039ba37b83832b21760611b604482015290519081900360640190fd5b6000805463ffffffff8082166002011663ffffffff19909116179055565b60005463ffffffff1681565b60008054640100000000900460ff16156101ed576040805162461bcd60e51b815260206004820152601460248201527321b7b73a3930b1ba1034b99039ba37b83832b21760611b604482015290519081900360640190fd5b5060005463ffffffff169056fea26469706673582212202813efa9ea976eede47fcbca2254b38775773d746760c22ad98b16215c826b9164736f6c634300060c0033";
        String abiOld = "[{\"inputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"inputs\":[],\"name\":\"counter\",\"outputs\":[{\"internalType\":\"uint32\",\"name\":\"\",\"type\":\"uint32\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"getNumber\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"setNumber\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"stopContract\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";

        // 部署合约
        String txEncode;
        String txhash = "";
   
        QueryTransactionResult txResult;
        
        //  =============================================老合约 Start============================================
        // 部署合约
        try {
            byte[] code = ByteUtil.merge(HexUtil.fromHexString(codesOld), abiOld.getBytes());

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
        String contractAddressOld = TransactionUtil.convertExectoAddr(address + txhash.substring(2));
        System.out.println("部署好的合约地址 = " + contractAddressOld);
        
        // 调用合约setNumber做累加
    	byte[] store = EvmUtil.encodeParameter(abiOld, "setNumber");
        txEncode = EvmUtil.callEvmContract(store,"", 0, contractAddressOld, privateKey, "");
        txhash = client.submitTransaction(txEncode);
        System.out.print("老合约调用setNumber合约hash = " + txhash);
        Thread.sleep(5000);
        txResult = client.queryTransaction(txhash);
        System.out.println("; 执行结果 = " + txResult.getReceipt().getTyname());
        
        // 查询
        byte[] packAbiGet = EvmUtil.encodeParameter(abiOld, "getNumber");

        JSONObject query = client.callEVMAbi(contractAddressOld, HexUtil.toHexString(packAbiGet));
        Assert.assertNotNull(query);
        JSONObject output = query.getJSONObject("result");
        List<?> result = EvmUtil.decodeOutput(abiOld, "getNumber", output);
        int number = Integer.parseInt(String.valueOf(result.get(0)));
        System.out.println("从链上的查询结果：" + number);
        
        // 停止老合约
    	byte[] stopContract = EvmUtil.encodeParameter(abiOld, "stopContract");
        txEncode = EvmUtil.callEvmContract(stopContract,"", 0, contractAddressOld, privateKey, "");
        txhash = client.submitTransaction(txEncode);
        System.out.print("调用停止合约的hash = " + txhash);
        Thread.sleep(5000);
        txResult = client.queryTransaction(txhash);
        System.out.println("; 执行结果 = " + txResult.getReceipt().getTyname());
        //  =============================================老合约 End============================================
        
        //  =============================================新合约 Start============================================
        
        // 对应代码：MyContractV1.sol
        String codesNew = "60806040526000805460ff60201b1916905534801561001d57600080fd5b5060008054650100000000003302600160281b600160c81b03199091161790556102d98061004c6000396000f3fe608060405234801561001057600080fd5b50600436106100575760003560e01c806312253a6c1461005c5780634154b2431461006657806361bc221a1461006e5780637ad5e5741461008f578063f2c9ecd8146100b2575b600080fd5b6100646100cc565b005b610064610142565b6100766101b7565b6040805163ffffffff9092168252519081900360200190f35b610064600480360360208110156100a557600080fd5b503563ffffffff166101c3565b6100ba61023e565b60408051918252519081900360200190f35b6000546501000000000090046001600160a01b0316331461012b576040805162461bcd60e51b815260206004820152601460248201527329b2b73232b91034b9903737ba1037bbb732b91760611b604482015290519081900360640190fd5b6000805464ff000000001916640100000000179055565b600054640100000000900460ff1615610199576040805162461bcd60e51b815260206004820152601460248201527321b7b73a3930b1ba1034b99039ba37b83832b21760611b604482015290519081900360640190fd5b6000805463ffffffff8082166001011663ffffffff19909116179055565b60005463ffffffff1681565b6000546501000000000090046001600160a01b03163314610222576040805162461bcd60e51b815260206004820152601460248201527329b2b73232b91034b9903737ba1037bbb732b91760611b604482015290519081900360640190fd5b6000805463ffffffff191663ffffffff92909216919091179055565b60008054640100000000900460ff1615610296576040805162461bcd60e51b815260206004820152601460248201527321b7b73a3930b1ba1034b99039ba37b83832b21760611b604482015290519081900360640190fd5b5060005463ffffffff169056fea2646970667358221220184ccac60a3d777f766c9d9cfcb7a766c8819faaf226d0dde8ecd88299c4afa464736f6c634300060c0033";
        String abiNew = "[{\"inputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"inputs\":[],\"name\":\"counter\",\"outputs\":[{\"internalType\":\"uint32\",\"name\":\"\",\"type\":\"uint32\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"getNumber\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"uint32\",\"name\":\"_counter\",\"type\":\"uint32\"}],\"name\":\"initNumber\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"setNumber\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"stopContract\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";

        // 部署合约
        try {
            byte[] code = ByteUtil.merge(HexUtil.fromHexString(codesNew), abiNew.getBytes());

            txEncode = EvmUtil.createEvmContract(code, "", "evm-sdk-test", privateKey, "");
            txhash = client.submitTransaction(txEncode);
            System.out.print("部署新合约交易hash = " + txhash);
            Thread.sleep(5000);
            txResult = client.queryTransaction(txhash);
            Assert.assertEquals(txResult.getReceipt().getTyname(), "ExecOk");
            System.out.println("; 执行结果 = " + txResult.getReceipt().getTyname());
                        
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        
        // 计算合约地址
        String contractAddressNew = TransactionUtil.convertExectoAddr(address + txhash.substring(2));
        System.out.println("部署好的合约地址 = " + contractAddressNew);
        
        // 老合约中的值复制给新合约
    	store = EvmUtil.encodeParameter(abiNew, "initNumber", number);
        txEncode = EvmUtil.callEvmContract(store,"", 0, contractAddressNew, privateKey, "");
        txhash = client.submitTransaction(txEncode);
        System.out.print("升级后合约，调用initNumber合约初始化数据hash = " + txhash);
        Thread.sleep(5000);
        txResult = client.queryTransaction(txhash);
        System.out.println("; 执行结果 = " + txResult.getReceipt().getTyname());
        
        
        // 调用合约setNumber做累加
    	store = EvmUtil.encodeParameter(abiNew, "setNumber");
        txEncode = EvmUtil.callEvmContract(store,"", 0, contractAddressNew, privateKey, "");
        txhash = client.submitTransaction(txEncode);
        System.out.print("升级后合约，调用合约hash = " + txhash);
        Thread.sleep(5000);
        txResult = client.queryTransaction(txhash);
        System.out.println("; 执行结果 = " + txResult.getReceipt().getTyname());
        
        // 查询
        packAbiGet = EvmUtil.encodeParameter(abiNew, "getNumber");

        query = client.callEVMAbi(contractAddressNew, HexUtil.toHexString(packAbiGet));
        output = query.getJSONObject("result");
        result = EvmUtil.decodeOutput(abiOld, "getNumber", output);
        System.out.println("从链上的查询结果：" + result);
        
        
        //  =============================================新合约 End============================================
        
        // 再次调用老合约
    	store = EvmUtil.encodeParameter(abiOld, "setNumber");
        txEncode = EvmUtil.callEvmContract(store,"", 0, contractAddressOld, privateKey, "");
        txhash = client.submitTransaction(txEncode);
        System.out.print("调用已经被 停止的老合约返回的hash = " + txhash);
        Thread.sleep(5000);
        txResult = client.queryTransaction(txhash);
        System.out.println("; 执行结果 = " + txResult.getReceipt().getTyname());
	 }
	 
	/**
	 * 3.5.4合约冻结,解冻，终止
	 * @throws Exception 
	 */
	 @Test
	 public void Case3_5_4() throws Exception {
		 
			// 合约部署人对应的区块链地址和私钥
			String address = "1M8gvr1DZ1KKVjf6XW6aYR6pHGeDRspdCx";
		    String privateKey = "452281167e7fa65cdd5bcb1e40565bf06a1aa3ce4fc8954848d0427a4cc27180";

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
	 
	
	/**
     * 转账交易
     * @param note
     * @param amount
     * @param to
     * @param privateKey
     * @param fromAddress
	 * @throws Exception 
     */
    private void transfer(String note, long amount, String to, String privateKey, String fromAddress) throws Exception {
    	
    	TransferBalanceRequest transferBalanceRequest = new TransferBalanceRequest();
    	// 转账说明
		transferBalanceRequest.setNote(note);
		// 转主积分的情况下，默认填""
		transferBalanceRequest.setCoinToken("");
		// 转账数量 ， 以下代表转1个积分
		transferBalanceRequest.setAmount(amount * 100000000L);
		// 转到的地址
		transferBalanceRequest.setTo(to);
		// 签名私私钥,对应的测试地址是：14KEKbYtKKQm4wMthSK9J4La4nAiidGozt
		transferBalanceRequest.setFromPrivateKey(privateKey);
		// 执行器名称，主链主积分固定为coins
		transferBalanceRequest.setExecer("coins");
		// 签名类型 (支持SM2, SECP256K1, ED25519)
		transferBalanceRequest.setSignType(SignType.SECP256K1);
		// 构造好，并本地签好名的交易
		String createTransferTx = TransactionUtil.transferBalanceMain(transferBalanceRequest);
		
		System.out.print("=========================" + note + " start ===============================\r\n");
		// 交易发往区块链
		String txHash = client.submitTransaction(createTransferTx);
		System.out.println(txHash);
		
		Thread.sleep(3000);

		for (int tick = 0; tick < 5; tick++){
			QueryTransactionResult result = client.queryTransaction(txHash);
			if(result == null) {
				Thread.sleep(5000);
				continue;
			}

			System.out.println("执行结果:" + result.getReceipt().getTyname());			
			break;
		}
		System.out.print("=========================" + note + " end ===============================\r\n\r\n");
    }

}
