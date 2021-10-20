package com.chain33.cn.xtyTest;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.decode.DecodeRawTransaction;
import cn.chain33.javasdk.model.rpcresult.QueryTransactionResult;
import cn.chain33.javasdk.utils.HexUtil;
import cn.chain33.javasdk.utils.TransactionUtil;

public class SimpleStoreT {

	
	// 平行链节点IP
	String paraIp = "121.37.135.72";
	// 平行链服务端口
	int paraPort = 8801;
	RpcClient clientPara = new RpcClient(paraIp, paraPort);
	
	
	// 用户的地址和私钥
	String address = "1M8gvr1DZ1KKVjf6XW6aYR6pHGeDRspdCx";
    String privateKey = "452281167e7fa65cdd5bcb1e40565bf06a1aa3ce4fc8954848d0427a4cc27180";
    
	
	// 上链存证的内容(电子档案上链)
	//String content = "{\"档案编号@\":\"ID0000001\",\"企业代码\":\"QY0000001\",\"业务标识\":\"DA000001\",\"来源系统\":\"OA\", \"文档摘要\",\"0x93689a705ac0bb4612824883060d73d02534f8ba758f5ca21a343beab2bf7b47\"}";
	String content = "{\"abstracts\":\"转甘肃垄聚建筑工程有限公司设计费（结算100%）\",\"accountCode\":\"11221202\",\"accountName\":\"11221202\\应收账款\\应收勘测设计收入\\应收用户工程收入\",\"balanc\":3828.00,\"createDate\":1630487094000,\"createUser\":\"2020010909585152563120146\",\"createUserName\":\"付娟\",\"customerCode\":\"C02025869\",\"customerName\":\"甘肃垄聚建筑工程有限公司\",\"debitAmount\":3828.00,\"docDate\":1629216000000,\"docNum\":\"转-0022\",\"hashCode\":\"\",\"id\":\"2021090117045430302006791\",\"isDelete\":false,\"modifyDate\":1630487094000,\"modifyUser\":\"2020010909585152563120146\",\"modifyUserName\":\"付娟\",\"oppoAccountName\":\"主营业务收入/应交税费\",\"proCode\":\"2611222\",\"proName\":\"两当县部队营部至陕甘界（G316公路）国防战备公路改建工程\",\"resourceSys\":\"总账\",\"rowIndex\":0}";
	/**
	 * 内容存证上链
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	@Test
	public void writeParaData() throws IOException, InterruptedException {
		// 存证智能合约的名称（简单存证，固定就用这个名称）, user.p.mbaas.是平行链名称， user.write是合约名
		String execer = "user.p.hetongchain.user.write";
		// 合约地址
		String contractAddress = clientPara.convertExectoAddr(execer);
		String hash = null;
		String txEncode = null;
		for (int i = 0; i < 180; i++) {
			txEncode = TransactionUtil.createTransferTx(privateKey, contractAddress, execer, content.getBytes(),
					TransactionUtil.DEFAULT_FEE);
			
		    hash = clientPara.submitTransaction(txEncode);
		    Thread.sleep(1000);
		}


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
			
			System.out.println("交易所在的区块高度：" + result.getHeight());
			System.out.println("区块的打包时间：" + result.getBlocktime());
			System.out.println("从哪个用户发出：" + result.getFromaddr());
			System.out.print("上链的数据内容" + HexUtil.hexStringToString(result.getTx().getRawpayload()));
			break;
			
		}
		
		
	}
}
