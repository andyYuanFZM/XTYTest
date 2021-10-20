package com.chain33.cn.xtyTest;

import java.util.List;
import org.junit.Test;
import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.decode.DecodeRawTransaction;
import cn.chain33.javasdk.utils.TransactionUtil;

public class SignTest {

	// 区块链IP
		String ip = "localhost";
		// 区块链服务端口
		int port = 8901;
		RpcClient client = new RpcClient(ip, port);
		String paraName = "user.p.mbaas.";

		// 代扣地址和私钥
		String withHoldAddress = "1M8gvr1DZ1KKVjf6XW6aYR6pHGeDRspdCx";
	    String withHoldPrivateKey = "452281167e7fa65cdd5bcb1e40565bf06a1aa3ce4fc8954848d0427a4cc27180";
	    
	    @Test
	    public void testSign() throws Exception {
	        String note = "代扣转账";
	        String coinToken = "";
	        Long amount = 1 * 10000000L;
	        String to = "1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs";
	        byte[] payload = TransactionUtil.createTransferPayLoad(to, amount, coinToken, note);
	        
	        // 实际交易的私钥
	        String fromAddressPriveteKey = "CC38546E9E659D15E6B4893F0AB32A06D103931A8230B0BDE71459D2B27D6944";
	        String execer = paraName + "coins";
	        String contranctAddress = client.convertExectoAddr(execer);
	        String createTransferTx = TransactionUtil.createTransferTx(fromAddressPriveteKey, contranctAddress, execer, payload);
	        
	        String createNoBalanceTx = client.createNoBalanceTx(createTransferTx, "");
	        
	        // 解析交易
	        List<DecodeRawTransaction> decodeRawTransactions = client.decodeRawTransaction(createNoBalanceTx);
	        String hexString = TransactionUtil.signDecodeTx(decodeRawTransactions, contranctAddress, fromAddressPriveteKey, withHoldPrivateKey);
	        String submitTransaction = client.submitTransaction(hexString);
	        
	        System.out.println("submitTransaction:" + submitTransaction);
	    }
}
