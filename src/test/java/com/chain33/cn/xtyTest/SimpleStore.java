package com.chain33.cn.xtyTest;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.decode.DecodeRawTransaction;
import cn.chain33.javasdk.model.rpcresult.QueryTransactionResult;
import cn.chain33.javasdk.utils.HexUtil;
import cn.chain33.javasdk.utils.TransactionUtil;

public class SimpleStore {

	
	// 平行链节点IP
	String paraIp = "18";
	// 平行链服务端口
	int paraPort = 8801;
	RpcClient clientPara = new RpcClient(paraIp, paraPort);
	
	
	// 用户的地址和私钥
	String address = "1M8gvr1DZ1KKVjf6XW6aYR6pHGeDRspdCx";
    String privateKey = "452281167e7fa65cdd5bcb1e40565bf06a1aa3ce4fc8954848d0427a4cc27180";
    
    // 代扣地址和私钥
    String withholdAddress = "1FpJG4PHJARQNpyXkF6E6f9rvBx97zDaaJ";
    String withholdPrivateKey = "2d5cd98d637033028c7ee7ca78e5dd71d8aaaada0e8b3244f18bba7b6a75ba8c";
	
	// 上链存证的内容(电子档案上链)
	String content = "{\"档案编号\":\"ID0000001\",\"企业代码\":\"QY0000001\",\"业务标识\":\"DA000001\",\"来源系统\":\"OA\", \"文档摘要\",\"0x93689a705ac0bb4612824883060d73d02534f8ba758f5ca21a343beab2bf7b47\"}";
	
	/**
	 * 内容存证上链
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	@Test
	public void writeParaData() throws IOException, InterruptedException {
		// 存证智能合约的名称（简单存证，固定就用这个名称）, user.p.mbaas.是平行链名称， user.write是合约名
		String execer = "user.write";
		// 合约地址
		String contractAddress = clientPara.convertExectoAddr(execer);

		String txEncode = TransactionUtil.createTransferTx(privateKey, contractAddress, execer, content.getBytes(),
				TransactionUtil.DEFAULT_FEE);
		// 构造代扣交易
		String createNoBalanceTx = clientPara.createNoBalanceTx(txEncode, "");
		// 解析交易
	    List<DecodeRawTransaction> decodeRawTransactions = clientPara.decodeRawTransaction(createNoBalanceTx);

	    String hexString = TransactionUtil.signDecodeTx(decodeRawTransactions, contractAddress, privateKey, withholdPrivateKey);
	    String hash = clientPara.submitTransaction(hexString);

	    // 此处返回的交易hash是代扣的hash，而不是实际存证的hash
		System.out.println(hash);
		
		
		// 查询
		Thread.sleep(5000);
		for (int tick = 0; tick < 5; tick++){
			QueryTransactionResult result = clientPara.queryTransaction(hash);
			if(result == null) {
				Thread.sleep(5000);
				continue;
			}

			// 使用代扣交易时，实际是上链了两笔交易，一笔是代扣的，一笔是实际存证的。  实际存证的交易hash取得方式如下：
			System.out.println("实际在平行链上存证的交易hash:" + result.getTx().getNext());
			
			
			System.out.println("交易所在的区块高度：" + result.getHeight());
			System.out.println("区块的打包时间：" + result.getBlocktime());
			System.out.println("从哪个用户发出：" + result.getFromaddr());
			System.out.print("上链的数据内容" + HexUtil.hexStringToString(result.getTx().getRawpayload()));
			
			break;
		}
		
		
	}
}
