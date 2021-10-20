package com.chain33.cn.xtyTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import cn.chain33.javasdk.client.Account;
import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.AccountInfo;
import cn.chain33.javasdk.model.TransferBalanceRequest;
import cn.chain33.javasdk.model.enums.SignType;
import cn.chain33.javasdk.model.rpcresult.AccountAccResult;
import cn.chain33.javasdk.model.rpcresult.BlockOverViewResult;
import cn.chain33.javasdk.model.rpcresult.BlockResult;
import cn.chain33.javasdk.model.rpcresult.BlocksResult;
import cn.chain33.javasdk.model.rpcresult.CryptoResult;
import cn.chain33.javasdk.model.rpcresult.NetResult;
import cn.chain33.javasdk.model.rpcresult.PeerResult;
import cn.chain33.javasdk.model.rpcresult.QueryTransactionResult;
import cn.chain33.javasdk.model.rpcresult.VersionResult;
import cn.chain33.javasdk.utils.TransactionUtil;

public class RpcTest {

	String paraIp = "172.22.16.206";
	int paraPort = 8801;
	RpcClient client = new RpcClient(paraIp, paraPort);
	
	String address = "1M8gvr1DZ1KKVjf6XW6aYR6pHGeDRspdCx";
    String privateKey = "452281167e7fa65cdd5bcb1e40565bf06a1aa3ce4fc8954848d0427a4cc27180";
    
    @Test
    public void getPeerInfo() throws IOException {
    	// ��ѯԶ�̵�ַ��Ϣ�б�
    	List<PeerResult> peerResult = client.getPeerInfo();
    	System.out.println("peerResult is :"  + peerResult );
    }
    
    @Test
    public void getNetInfo() throws IOException {
    	// ��ѯԶ�̵�ַ��Ϣ�б�
    	NetResult netResult = client.getNetInfo();
    	System.out.println("netResult is :"  + netResult );
    }
    
    @Test
    public void gettCryptoInfo() throws IOException {
    	// ��ѯϵͳ֧�ֵ�ǩ���б�
    	List<CryptoResult> result  = client.getCryptoResult(); 
    	System.out.println("CryptoResult is :"  + result );
    }
    
    @Test
    public void isSync() throws IOException {
    	// ��ѯ�ڵ��ͬ��״̬��
    	Boolean result = client.isSync();
    	System.out.println("sync result is :"  + result );
    }
    
    @Test
    public void getVersion() throws IOException {
    	// ��ȡԶ�̵�ַ��Ϣ�б�
    	VersionResult result  = client.getVersion();
    	System.out.println("version result is :"  + result );
    }
    
    @Test
    public void transfer() throws InterruptedException, IOException {

    		TransferBalanceRequest transferBalanceRequest = new TransferBalanceRequest();

    		// ת��˵��
    		transferBalanceRequest.setNote("ת��˵��");
    		// ת�����ֵ�����£�Ĭ����""
    		transferBalanceRequest.setCoinToken("");
    		// ת������ �� ���´���ת1������
    		transferBalanceRequest.setAmount(1 * 100000000L);
    		// ת���ĵ�ַ
    		transferBalanceRequest.setTo("1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs");
    		// ǩ��˽˽Կ
    		transferBalanceRequest.setFromPrivateKey("CC38546E9E659D15E6B4893F0AB32A06D103931A8230B0BDE71459D2B27D6944");
    		// ִ�������ƣ����������̶ֹ�Ϊcoins
    		transferBalanceRequest.setExecer("coins");
    		// ǩ������ (֧��SM2, SECP256K1, ED25519)
    		transferBalanceRequest.setSignType(SignType.SECP256K1);
    		// ����ã�������ǩ�����Ľ���
    		String createTransferTx = TransactionUtil.transferBalanceMain(transferBalanceRequest);
    		// ���׷���������
    		String txHash = null;
    		try {
    			txHash = client.submitTransaction(createTransferTx);
    			System.out.println(txHash);
    		} catch (Exception e) {
    			System.out.println("RPC ERROR:" + e.getMessage());
    		}

    		List<String> list = new ArrayList<String>();
    		list.add("1CbEVT9RnM5oZhWMj4fxUrJX94VtRotzvs");
    		list.add("14KEKbYtKKQm4wMthSK9J4La4nAiidGozt");

    		// һ��1��һ������
    		Thread.sleep(10000);
    		QueryTransactionResult queryTransaction1;
    		for (int i = 0; i < 5; i++) {
    			queryTransaction1 = client.queryTransaction(txHash);
    			if (null == queryTransaction1) {
    				Thread.sleep(3000);
    			} else {
    				System.out.println("����ִ�н����" + queryTransaction1.getReceipt().getTyname());
    				break;
    			}
    		}

    		List<AccountAccResult> queryBtyBalance;
    		queryBtyBalance = client.getCoinsBalance(list, "coins");
    		if (queryBtyBalance != null) {
    			for (AccountAccResult accountAccResult : queryBtyBalance) {
    				System.out.println(accountAccResult);
    			}
    		}
    }
    
    @Test
    public void getblockResult() throws IOException {
    	// ��������߶ȷ�Χȡ������Ϣ
    	List<BlocksResult> blockResult  = client.getBlocks(1L,1L,true);
    	System.out.println("blockResult is :"  + blockResult );
    }
    
    @Test
    public void getblockHeader() throws IOException {
    	// ��ȡ����ͷ��Ϣ
    	 BlockResult blockResult  = client.getLastHeader();
    	System.out.println("blockheaderResult is :"  + blockResult );
    }
    
    @Test
    public void getblockHash() throws IOException {
    	// ��ȡ����hash
    	String blockhash = client.getBlockHash(1L);
    	System.out.println("blockhash is :"  + blockhash );
    }
    
    @Test
    public void getblockOverview() throws IOException {
    	// ��ȡ����hash
    	String blockhash = client.getBlockHash(1L);
    	System.out.println("blockhash is :"  + blockhash );
    	
    	// ��ѯָ����ϣֵ��������ϸ��Ϣ��
    	BlockOverViewResult blockView = client.getBlockOverview(blockhash);
    	System.out.println("BlockOverViewResult is :"  + blockView );
    }
    
    
    @Test
    public void convertExecer() throws IOException {
    	// ���ݺ�Լ���ƻ�ȡ��Լ����������ַ��
    	String address = client.convertExectoAddr("token");
    	
    	System.out.println("convert address is :"  + address );
    }
    
    @Test
    public void newAccount() throws IOException {
    	Account account = new Account();
    	// ��ȡ������˽Կ����Կ����ַ��
    	AccountInfo accountInfo = account.newAccountLocal();
    	System.out.println("˽Կ:" + accountInfo.getPrivateKey());
    	System.out.println("��Կ:" + accountInfo.getPublicKey());
    	System.out.println("��ַ:" + accountInfo.getAddress());
    	
    	//У���ַ�ĺϷ��ԡ�
    	boolean validAddressResult = TransactionUtil.validAddress(accountInfo.getAddress());
    	System.out.printf("validate result is:%s", validAddressResult);
    }

}
