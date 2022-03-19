package com.chain33.cn.huadaTest;

import org.junit.Test;

import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.AccountInfo;
import cn.chain33.javasdk.model.TransferBalanceRequest;
import cn.chain33.javasdk.model.enums.SignType;
import cn.chain33.javasdk.utils.HexUtil;
import cn.chain33.javasdk.utils.TransactionUtil;

public class huadaTest {
	
    RpcClient client = new RpcClient("172.22.16.179", 8801);
    
	  /**
     * 每隔1秒轉1個積分
     * @throws Exception 
     */
    @Test
    public void case1() throws Exception {
    	// 转出地址对应的私钥
    	String fromprivateKey = "CC38546E9E659D15E6B4893F0AB32A06D103931A8230B0BDE71459D2B27D6944";
    	String fromAddress = "14KEKbYtKKQm4wMthSK9J4La4nAiidGozt";
    	
    	for (int i = 0; i < 1000; i++) {
        	// 转1个积分
        	String to = "1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs";
        	transfer("转1个积分",1,to,fromprivateKey,fromAddress);
        	Thread.sleep(800);
    	}

    }
    
	  /**
     * 批量發送
     * @throws Exception 
     */
    @Test
    public void case2() throws Exception {
    	// 转出地址对应的私钥
    	String fromprivateKey = "CC38546E9E659D15E6B4893F0AB32A06D103931A8230B0BDE71459D2B27D6944";
    	String fromAddress = "14KEKbYtKKQm4wMthSK9J4La4nAiidGozt";
    	
    	for (int i = 0; i < 1000; i++) {
        	// 转1个积分
        	String to = "1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs";
        	transfer("转1个积分",1,to,fromprivateKey,fromAddress);
    	}

    }
    
    @Test
    public void test() throws Exception {
    	String privateKey = "fad9c8855b740a0b7ed4c221dbad0f33a83a49cad6b3fe8d5817ac83d38b6a19";
    	String publicKey = TransactionUtil.getHexPubKeyFromPrivKey(privateKey);
    	System.out.println(publicKey);
    	byte[] publicKeyByte = HexUtil.fromHexString(publicKey);
    	System.out.println(TransactionUtil.genAddress(publicKeyByte));
    	
    	publicKey = "049a7df67f79246283fdc93af76d4f8cdd62c4886e8cd870944e817dd0b97934fdd7719d0810951e03418205868a5c1b40b192451367f28e0088dd75e15de40c05";
    	publicKeyByte = HexUtil.fromHexString(publicKey);
    	System.out.println(TransactionUtil.genAddress(publicKeyByte));
    	    	
    }
    
    
	  /**
     * 發送錯誤交易
     * @throws Exception 
     */
    @Test
    public void case3() throws Exception {
    	// 转出地址对应的私钥
    	String fromprivateKey = "CC38546E9E659D15E6B4893F0AB32A06D103931A8230B0BDE71459D2B27D6944";
    	String fromAddress = "14KEKbYtKKQm4wMthSK9J4La4nAiidGozt";
    	
    	// 转1个积分
    	String to = "1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs";
    	transfer("转超过最大值的积分",100000001,to,fromprivateKey,fromAddress);

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
		String txHash = client.submitTransaction(createTransferTx);
		//System.out.println(txHash);
    }
}
