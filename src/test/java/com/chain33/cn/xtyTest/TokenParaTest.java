package com.chain33.cn.xtyTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.chain33.javasdk.client.RpcClient;
import org.junit.Test;

import cn.chain33.javasdk.model.decode.DecodeRawTransaction;
import cn.chain33.javasdk.model.rpcresult.AccountAccResult;
import cn.chain33.javasdk.model.rpcresult.QueryTransactionResult;
import cn.chain33.javasdk.model.rpcresult.TokenResult;
import cn.chain33.javasdk.utils.TransactionUtil;

public class TokenParaTest {

	
	String ip = "localhost";
	int port = 8901;
    RpcClient client = new RpcClient(ip, port);
    
	String paraName = "user.p.FilmChain.";
	
	String superManagerPk = "0x4e92bda2477ded0e7c07a9e3acd2370de8d7401c68cc83ee8376806db3121e77";
    
    @Test
    public void createBlackList() throws Exception {

    	String execerName = paraName + "manage";
    	String key = "token-blacklist";
    	String value = "BTC";
    	String op = "add";
    	String txEncode = TransactionUtil.createManage(key, value, op, superManagerPk, execerName);
    	String hash = client.submitTransaction(txEncode);
    	System.out.print(hash);
    }
    
    @Test
    public void createTokenFinisher() throws Exception {

    	String execerName = paraName + "manage";
    	String key = "token-finisher";
    	String value = "1N2ABERwHgxGhebVw6fVSwaQ5uLAysmGEu";
    	String op = "add";
    	String txEncode = TransactionUtil.createManage(key, value, op, superManagerPk, execerName);
    	String hash = client.submitTransaction(txEncode);
    	System.out.print(hash);
    	
    }
    
	@Test
	public void preCreateTokenLocal() throws IOException {
	   long total = 19900000000000000L;
	   String name = "DEVELOP COINS";
	   String symbol = "COINSDEVX";
	   String introduction = "�����߱�";
	   Long price = 0L;
	   Integer category = 0;
	   String execer = paraName + "token";
	   String owner = "1EHWKLEixvfanTHWmnF7mYMuDDXTCorZd7";
	   String managerPrivateKey = "3990969DF92A5914F7B71EEB9A4E58D6E255F32BF042FEA5318FC8B3D50EE6E8";
	   String precreateTx = TransactionUtil.createPrecreateTokenTx(execer, name, symbol, introduction, total, price,
	           owner, category, managerPrivateKey);
	   String submitTransaction = client.submitTransaction(precreateTx);
	   System.out.println(submitTransaction);
	}
	
	@Test
	public void createTokenFinishLocal() throws IOException {
	   String symbol = "COINSDEVX";
	   String execer = paraName + "token";
	   String managerPrivateKey = "3990969DF92A5914F7B71EEB9A4E58D6E255F32BF042FEA5318FC8B3D50EE6E8";
	   String owner = "1EHWKLEixvfanTHWmnF7mYMuDDXTCorZd7";
	   String hexData = TransactionUtil.createTokenFinishTx(symbol, execer, owner, managerPrivateKey);
	   String submitTransaction = client.submitTransaction(hexData);
	   System.out.println(submitTransaction);
	}
	
    @Test
    public void createTokenTransfer() throws InterruptedException, IOException {
        String note = "测试币";
        String coinToken = "COINSDEVX";
        Long amount = 10000 * 100000000L;// 1 = real amount
        String to = "1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs";
        String fromAddressPriveteKey = "55637b77b193f2c60c6c3f95d8a5d3a98d15e2d42bf0aeae8e975fc54035e2f4";
        String execer = paraName + "token";
        String txEncode = TransactionUtil.createTokenTransferTx(fromAddressPriveteKey, to, execer, amount, coinToken, note);
        
        String createNoBalanceTx = client.createNoBalanceTx(txEncode, "");
        
	    List<DecodeRawTransaction> decodeRawTransactions = client.decodeRawTransaction(createNoBalanceTx);
	    String withHoldPrivateKey = "3990969DF92A5914F7B71EEB9A4E58D6E255F32BF042FEA5318FC8B3D50EE6E8";
	    
	    String contranctAddress = client.convertExectoAddr(execer);
	    String hexString = TransactionUtil.signDecodeTx(decodeRawTransactions, contranctAddress, fromAddressPriveteKey, withHoldPrivateKey);
	    String submitTransaction = client.submitTransaction(hexString);
	    System.out.println("submitTransaction:" + submitTransaction);

		Thread.sleep(5000);
		for (int tick = 0; tick < 5; tick++){
			QueryTransactionResult result = client.queryTransaction(submitTransaction);
			if(result == null) {
				Thread.sleep(5000);
				continue;
			}

			System.out.println("next:" + result.getTx().getNext());
			QueryTransactionResult nextResult = client.queryTransaction(result.getTx().getNext());
			System.out.println("ty:" + nextResult.getReceipt().getTyname());
			break;
		}
    }
    
    @Test
    public void queryCreateTokens() throws IOException {
        String execer = paraName + "token";
        Integer status = 1;
        List<TokenResult> queryCreateTokens;
        queryCreateTokens = client.queryCreateTokens(status,execer);
        for (TokenResult tokenResult : queryCreateTokens) {
            System.out.println(tokenResult);
        }
    }
    
    @Test
    public void getTokenBalace() throws IOException {
        String execer = paraName + "token";
        
        List<String> addressList = new ArrayList<>();
        addressList.add("1EHWKLEixvfanTHWmnF7mYMuDDXTCorZd7");
        addressList.add("1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs");
        List<AccountAccResult> queryBtyBalance;
        queryBtyBalance = client.getTokenBalance(addressList, execer, "COINSDEVX");
        for (AccountAccResult accountAccResult : queryBtyBalance) {
            System.out.println(accountAccResult);
        }
    }

}
