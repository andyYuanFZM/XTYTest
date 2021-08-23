package com.chain33.cn.xtyTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import cn.chain33.javasdk.client.Account;
import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.TransferBalanceRequest;
import cn.chain33.javasdk.model.enums.SignType;
import cn.chain33.javasdk.model.rpcresult.AccountAccResult;
import cn.chain33.javasdk.model.rpcresult.BlockOverViewResult;
import cn.chain33.javasdk.model.rpcresult.BlockResult;
import cn.chain33.javasdk.model.rpcresult.BlocksResult;
import cn.chain33.javasdk.model.rpcresult.QueryTransactionResult;
import cn.chain33.javasdk.utils.TransactionUtil;

/**
 * 3.1	数据操作
 * @author fkeit
 *
 */
public class Case3_1 {
	
    RpcClient client = new RpcClient(CommUtil.ip, CommUtil.port);

	Account account = new Account();

    /**
     * 3.1.1: 区块平均生成时间、总区块数
     * @throws Exception 
     */
    @Test
    public void case3_1_1() throws Exception {
    	
    	BlockResult blockResult = client.getLastHeader();
    	System.out.println("总区块数目为： " + blockResult.getHeight());
    	
    	int blockTime =  client.getBlockAverageTime();
    	System.out.println("平均出块时间为： " + blockTime + " 毫秒");
    	    	
    }
    
    /**
     * 3.1.2:查询指定区块业务数量
     * @throws Exception 
     */
    @Test
    public void case3_1_2() throws Exception {
    	// 取区块信息
    	List<BlocksResult> blockResultList = client.getBlocks(101l, 101l, true);
    	if (blockResultList.size() >= 0) {
    		System.out.println("区块中交易数目为： " + blockResultList.get(0).getBlock().getTxs().size());
    	} 
    }
    
    /**
     * 3.1.3:查询指定区块业务明细
     * @throws Exception 
     */
    @Test
    public void case3_1_3() throws Exception {
    	// 取区块详情
    	List<BlocksResult> blockResultList = client.getBlocks(101l, 101l, true);
    	if (blockResultList.size() >= 0) {
    		int txleng = blockResultList.get(0).getBlock().getTxs().size();
    		for (int i = 0; i < txleng; i++) {
    			System.out.println("第" + (i+1) + "笔交易详情为： " + blockResultList.get(0).getBlock().getTxs().get(i));
    		}
    	} 
    }
    
    /**
     * 3.1.4:点对点业务处理
     * @throws Exception 
     */
    @Test
    public void case3_1_4() throws Exception {
    	// 转出地址对应的私钥
    	String fromprivateKey = "CC38546E9E659D15E6B4893F0AB32A06D103931A8230B0BDE71459D2B27D6944";
    	String fromAddress = "14KEKbYtKKQm4wMthSK9J4La4nAiidGozt";
    	    	
    	// 转1个积分
    	String to = "1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs";
    	transfer("转1个积分",1,to,fromprivateKey,fromAddress);
    	
    	// 转超过最大值的积分
    	to = "1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs";
    	transfer("转超过最大值的积分",100000001,to,fromprivateKey,fromAddress);
    	
    	// 转向帐号非法（自己转自己）
    	to = "14KEKbYtKKQm4wMthSK9J4La4nAiidGozt";
    	transfer("自己转自己",1,to,fromprivateKey,fromAddress);
    	
    	// 转向帐号非法（地址不合法）
    	to = "14KEKbYtKKQm4wMthSK9J4La4nAiidabcd";
    	transfer("地址不合法",1,to,fromprivateKey,fromAddress);

    }
    
    
    /**
     * 3.1.5:业务溯源
     * @throws Exception 
     */
    @Test
    public void case3_1_5() throws Exception {
    	// 转指定区块的hash上一个区块的hash
    	List<BlocksResult> blockResultList = client.getBlocks(1l, 1l, true);
    	String blockHash = null;
    	if (blockResultList.size() >= 0) {
    		blockHash = blockResultList.get(0).getBlock().getParentHash();
    	} 
    	
    	// 根据block hash取交易hash
    	BlockOverViewResult blockOverView = client.getBlockOverview(blockHash);
    	List<String> txHashs = blockOverView.getTxHashes();
    	
    	// 根据txhas取交易详情
    	List<QueryTransactionResult> txs = client.GetTxByHashes(txHashs);
    	for (int i = 0; i < txs.size(); i++) {
        	System.out.println(txs.get(i));
    	}    
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
		String txHash = null;
		// 交易发往区块链
		try {
			txHash = client.submitTransaction(createTransferTx);
			System.out.println(txHash);
		} catch (Exception e) {
			System.out.println("RPC ERROR:" + e.getMessage());
		}

		List<String> list = new ArrayList<String>();
		list.add(fromAddress);
		list.add(to);
		

		for (int tick = 0; tick < 5; tick++){
			Thread.sleep(5000);
			QueryTransactionResult result = client.queryTransaction(txHash);
			if(result == null) {
				Thread.sleep(5000);
				continue;
			}

			System.out.println("执行结果:" + result.getReceipt().getTyname());
			
			List<AccountAccResult> queryBtyBalance;
			queryBtyBalance = client.getCoinsBalance(list, "coins");
			if (queryBtyBalance != null) {
				for (AccountAccResult accountAccResult : queryBtyBalance) {
					System.out.println(accountAccResult);
				}
			}
			
			break;
		}
		System.out.print("=========================" + note + " end ===============================\r\n\r\n");
    }

}
