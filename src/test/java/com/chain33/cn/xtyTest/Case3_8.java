package com.chain33.cn.xtyTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import cn.chain33.javasdk.client.Account;
import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.AccountInfo;
import cn.chain33.javasdk.model.decode.DecodeRawTransaction;
import cn.chain33.javasdk.utils.AesUtil;
import cn.chain33.javasdk.utils.HexUtil;
import cn.chain33.javasdk.utils.TransactionUtil;

/**
 * 密钥的软件管理
 * 测试思路： 
 * 1. 提供AES对称加密算法
 * 2. 通过SDK申请用户私钥等信息， 用上述对称加密算法加密后保存
 * 3. 使用私钥前，需要先通过AES解密
 * @author fkeit
 *
 */
public class Case3_8 {
	
	RpcClient client = new RpcClient(CommUtil.ip, CommUtil.port);
	
	String content = "{\"档案编号\":\"ID0000001\",\"企业代码\":\"QY0000001\",\"业务标识\":\"DA000001\",\"来源系统\":\"OA\", \"文档摘要\",\"0x93689a705ac0bb4612824883060d73d02534f8ba758f5ca21a343beab2bf7b47\"}";
    
	@Test
	public void case3_8() throws IOException {
		System.out.println("==================申请用户私私钥 ==========================");
		// 获取签名用的私钥
		Account account = new Account();
		AccountInfo accountInfo = account.newAccountLocal();
		String privateKey = accountInfo.getPrivateKey();
		
		System.out.println("私钥明文:" + privateKey);
		
		System.out.println("==================AES加密==========================");
		// 生成AES加密KEY
		String aesKeyHex = "ba940eabdf09ee0f37f8766841eee763";
		//可用该方法生成 AesUtil.generateDesKey(128);
		byte[] key = HexUtil.fromHexString(aesKeyHex);
		// 生成iv
		byte[] iv = AesUtil.generateIv();
		// 对明文进行加密
		byte[] encrypt = AesUtil.encrypt(privateKey, key, iv);
		String secretKey = HexUtil.toHexString(encrypt);
		
		System.out.println("私钥密文，由byte数组转string存储:" + secretKey);
		
		System.out.println("==================用加密后的私钥签名交易==========================");
		String execer = "user.write";
		// 合约地址
		String contractAddress = client.convertExectoAddr(execer);
		String txEncode = "";
		String hash = "";
		try {
			txEncode = TransactionUtil.createTransferTx(secretKey, contractAddress, execer, content.getBytes(),
					TransactionUtil.DEFAULT_FEE);
		    hash = client.submitTransaction(txEncode);
		} catch (Exception e) {
            System.out.println("用加密后的私钥匙签名会出错:" + e.toString());
        }

		System.out.println("==================AES解密，拿到私钥匙明文==========================");
        String decrypt = AesUtil.decrypt(HexUtil.fromHexString(secretKey), aesKeyHex);
        System.out.println("AES解密结果：" + decrypt);
        
		System.out.println("==================用解密后的私钥签名交易==========================");
        txEncode = TransactionUtil.createTransferTx(decrypt, contractAddress, execer, content.getBytes(),
				TransactionUtil.DEFAULT_FEE);
	    hash = client.submitTransaction(txEncode);
		System.out.println("用解密后的私钥签名，交易正常发送：" + hash);
        
        

	}
}
