package com.chain33.cn.xtyTest;

import java.util.Arrays;

import org.junit.Test;

import cn.chain33.javasdk.client.Account;
import cn.chain33.javasdk.model.AccountInfo;
import cn.chain33.javasdk.utils.AesUtil;
import cn.chain33.javasdk.utils.HexUtil;

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

	@Test
	public void case3_8() {
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
		
		System.out.println("==================AES解密，拿到私钥匙明文==========================");
        String decrypt = AesUtil.decrypt(HexUtil.fromHexString(secretKey), aesKeyHex);
        System.out.println("AES解密结果：" + decrypt);
	}
}
