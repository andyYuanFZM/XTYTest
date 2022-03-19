package com.chain33.cn.xtyTest;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.alibaba.fastjson.JSONObject;

import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.rpcresult.QueryTransactionResult;
import cn.chain33.javasdk.utils.ByteUtil;
import cn.chain33.javasdk.utils.EvmUtil;
import cn.chain33.javasdk.utils.HexUtil;
import cn.chain33.javasdk.utils.TransactionUtil;
 
  
public class NftShow extends JFrame  implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JLabel lb_backgroud;
	JLabel jLabel1,jLabel2,jLabel3,jLabel4,jLabel5,jLabel6,jLabel7,jLabel8;
	JTextArea evmCode, evmAbi;
	JTextField nftNumber, toaddress, nftNum;
	JPanel jPanel,jPanel2,jPanel3;
	JButton deploy,create,transfer;
	
	// 区块链IP
	String ip = "121.37.71.179";
	// 区块链服务端口
	int port = 8801;
	RpcClient client = new RpcClient(ip, port);
	
	
	// 合约部署人对应的区块链地址和私钥
	String address = "1M8gvr1DZ1KKVjf6XW6aYR6pHGeDRspdCx";
	String privateKey = "452281167e7fa65cdd5bcb1e40565bf06a1aa3ce4fc8954848d0427a4cc27180";
	
	// 合約地址
	String contractAddress = "";
		    
    String codes = "";
    String abi = "";

	public NftShow() {
		
		Image logo = Toolkit.getDefaultToolkit().getImage("src/5.png");
		setIconImage(logo);
		TrayIcon icon = new TrayIcon(logo);
		icon.setImageAutoSize(true);
		SystemTray systemTray = SystemTray.getSystemTray();
		try {
			systemTray.add(icon);
		} catch (AWTException e) {
			e.printStackTrace();
		}
		
		jLabel1 = new JLabel("EVM CODE");
		jLabel2 = new JLabel("EVM ABI");
		jLabel3 = new JLabel();
		
		evmCode = new JTextArea();
		evmAbi = new JTextArea();
		
		jLabel1.setBounds(30, 90, 100, 40);
		jLabel1.setFont(new Font("宋体",Font.BOLD, 20));
		jLabel2.setBounds(30, 210, 100, 40);
		jLabel2.setFont(new Font("宋体",Font.BOLD, 20));

		
		evmCode.setBounds(140, 90, 2100, 100);
		evmCode.setFont(new Font("宋体",Font.BOLD, 20));
		evmAbi.setBounds(140, 210, 2100, 100);
		evmAbi.setFont(new Font("宋体",Font.BOLD, 20));

		deploy = new JButton("部署合约");
		deploy.setFont(new Font("宋体",Font.BOLD, 20));
		deploy.setBounds(30, 310, 200, 40);
		
		jLabel3.setBounds(250, 310, 1000, 40);
		jLabel3.setFont(new Font("宋体",Font.BOLD, 20));
		jLabel3.setForeground(new Color(255,0,0));
		deploy.addActionListener(this);
		
		// 生成NFT
		jLabel4 = new JLabel("NFT編號");
		jLabel4.setBounds(30, 390, 100, 40);
		jLabel4.setFont(new Font("宋体",Font.BOLD, 20));
		
		nftNumber =new JTextField();
		nftNumber.setBounds(140, 390, 150, 40);
		nftNumber.setFont(new Font("宋体",Font.BOLD, 20));
		
		create = new JButton("生成NFT");
		create.setFont(new Font("宋体",Font.BOLD, 20));
		create.setBounds(310, 390, 200, 40);
		create.addActionListener(this);
		
		jLabel5 = new JLabel();
		jLabel5.setBounds(520, 390, 1000, 40);
		jLabel5.setFont(new Font("宋体",Font.BOLD, 20));
		jLabel5.setForeground(new Color(255,0,0));
		
		// 轉移NFT
		jLabel6 = new JLabel("去向地址");
		jLabel6.setBounds(30, 445, 100, 40);
		jLabel6.setFont(new Font("宋体",Font.BOLD, 20));
		
		toaddress =new JTextField();
		toaddress.setBounds(140, 445, 150, 40);
		toaddress.setFont(new Font("宋体",Font.BOLD, 20));
		
		jLabel7 = new JLabel("轉移的NFT編號");
		jLabel7.setBounds(300, 445, 150, 40);
		jLabel7.setFont(new Font("宋体",Font.BOLD, 20));
		
		nftNum =new JTextField();
		nftNum.setBounds(460, 445, 120, 40);
		nftNum.setFont(new Font("宋体",Font.BOLD, 20));
		
		transfer = new JButton("轉移NFT");
		transfer.setFont(new Font("宋体",Font.BOLD, 20));
		transfer.setBounds(590, 445, 150, 40);
		transfer.addActionListener(this);
		
		jLabel8 = new JLabel();
		jLabel8.setBounds(750, 445, 1800, 40);
		jLabel8.setFont(new Font("宋体",Font.BOLD, 20));
		jLabel8.setForeground(new Color(255,0,0));
		

		
		this.setLayout(null);
		this.add(jLabel1);
		this.add(jLabel2);
		this.add(jLabel3);
		
		this.add(evmCode);
		this.add(evmAbi);
		this.add(deploy);
		
		this.add(jLabel4);
		this.add(nftNumber);
		this.add(create);
		this.add(jLabel5);
		this.add(jLabel6);
		this.add(jLabel7);
		this.add(jLabel8);
		this.add(toaddress);
		this.add(transfer);
		this.add(nftNum);
		
		this.setTitle("EVM合约部署演示");
		this.setSize(2304,640);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setResizable(false);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new NftShow();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == "部署合约") {
			if(evmCode.getText().isEmpty() || evmAbi.getText().isEmpty()) {
				JOptionPane.showMessageDialog(null, "evmCode和evmABI是必须项","提示消息",JOptionPane.WARNING_MESSAGE);
			} else {
				codes = evmCode.getText();
				abi = evmAbi.getText();
				try {
					deployEvmContract(codes, abi);
				} catch (IOException | InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		} else if(e.getActionCommand() == "生成NFT") {
			if(nftNumber.getText().isEmpty() || nftNumber.getText().isEmpty()) {
				JOptionPane.showMessageDialog(null, "NFT編號是必须项","提示消息",JOptionPane.WARNING_MESSAGE);
			} else {
				String nftN = nftNumber.getText();
				String[] nftNs = nftN.split(",");
				int[] nftNsArrays = Arrays.stream(nftNs).mapToInt(Integer::parseInt).toArray();
				int length = nftNsArrays.length;
		        int[] amounts = new int[length];
		        for (int i = 0; i < length; i++) {
		        	amounts[i] = 1;
		        }
				try {
					createNFT(nftNsArrays, amounts);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		} else if(e.getActionCommand() == "轉移NFT") {
			if(toaddress.getText().isEmpty() || nftNum.getText().isEmpty()) {
				JOptionPane.showMessageDialog(null, "轉向地址和轉移的NFT編號是必须项","提示消息",JOptionPane.WARNING_MESSAGE);
			} else {
				String toaddr = toaddress.getText();
				String nftN = nftNum.getText();
				
				try {
					transferNFT(toaddr, nftN);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	private void deployEvmContract(String codes, String abi) throws IOException, InterruptedException {
		
        // 部署合约
        String txEncode;
        String txhash = "";
        
        QueryTransactionResult txResult;
        // 部署合约
        byte[] code = ByteUtil.merge(HexUtil.fromHexString(codes), abi.getBytes());

        long gas = 100000000;
        System.out.println("Gas fee is:" + gas);
        txEncode = EvmUtil.createEvmContract(code, "", "evm-sdk-test", privateKey, "", gas);
        txhash = client.submitTransaction(txEncode);
        System.out.print("部署合约交易hash = " + txhash);
        Thread.sleep(5000);
        txResult = client.queryTransaction(txhash);
        for (int tick = 0; tick < 500; tick++){
			txResult = client.queryTransaction(txhash);
			if(txResult == null) {
				Thread.sleep(5000);
				continue;
			}
	        System.out.println("; 执行结果 = " + txResult.getReceipt().getTyname());
	        break;
		}
        
        // 计算合约地址
        contractAddress = TransactionUtil.convertExectoAddr(address + txhash.substring(2));
        if ("ExecOk".equals(txResult.getReceipt().getTyname())) {
            jLabel3.setText("合约部署成功,合约地址:" + contractAddress);
        } else {
        	jLabel3.setText("合约部署失敗，請檢查");
        }
	}

	
	private void createNFT(int[] ids, int[] amounts) throws Exception {
		byte[] initNFT = EvmUtil.encodeParameter(abi, "initArtNFT", ids, amounts);
    	
        long gas = 100000000;
        
        String txEncode = EvmUtil.callEvmContract(initNFT,"", 0, contractAddress, privateKey, "", gas);
        String txhash = client.submitTransaction(txEncode);
        
        QueryTransactionResult txResult = null;
		for (int tick = 0; tick < 500; tick++){
			txResult = client.queryTransaction(txhash);
			if(txResult == null) {
				Thread.sleep(5000);
				continue;
			}
	        System.out.println("; 执行结果 = " + txResult.getReceipt().getTyname());
	        break;
		}
		
        if ("ExecOk".equals(txResult.getReceipt().getTyname())) {
        	byte[] packAbiGet;
        	JSONObject query;
        	JSONObject output;
        	String rawData;
        	String result = "用户" + "1M8gvr1DZ1KKVjf6XW6aYR6pHGeDRspdCx" + " 中NFT資產余额： ";
        	for (int i = 0; i < ids.length; i++) {
           	 	// 查询用户A和用户B地址下的资产余额
    	        packAbiGet = EvmUtil.encodeParameter(abi, "balanceOf", "1M8gvr1DZ1KKVjf6XW6aYR6pHGeDRspdCx", ids[i]);
    	        query = client.callEVMAbi(contractAddress, HexUtil.toHexString(packAbiGet));
    	        output = query.getJSONObject("result");
    	        rawData = output.getString("rawData");
    	        result = result + "[" + ids[i] +":"+  HexUtil.hexStringToAlgorism(HexUtil.removeHexHeader(rawData)) + "]";
        	}

            jLabel5.setText(result);
        } else {
        	jLabel5.setText("合约部署失敗，請檢查");
        }
	}
	
	private void transferNFT(String userToaddress, String id) throws Exception {
		
        String traceInfo = "華僑大學演示，区块链上留证";
        byte[] transfer = EvmUtil.encodeParameter(abi, "transferArtNFT", userToaddress, id, 1, traceInfo);
        String txEncode = EvmUtil.callEvmContract(transfer,"", 0, contractAddress, privateKey, "",10000000);
        String txhash = client.submitTransaction(txEncode);
        System.out.print("转NFT合约hash = " + txhash);
        QueryTransactionResult txResult = null;
		for (int tick = 0; tick < 500; tick++){
			txResult = client.queryTransaction(txhash);
			if(txResult == null) {
				Thread.sleep(5000);
				continue;
			}
	        System.out.println("; 执行结果 = " + txResult.getReceipt().getTyname());
	        break;
		}
		
        if ("ExecOk".equals(txResult.getReceipt().getTyname())) {
        	byte[] packAbiGet;
        	JSONObject query;
        	JSONObject output;
        	String rawData;
        	String result = "用户" + "1M8gvr1DZ1KKVjf6XW6aYR6pHGeDRspdCx" + " 中NFT資產余额： ";
        	
       	 	// 查询用户A地址下的资产余额
	        packAbiGet = EvmUtil.encodeParameter(abi, "balanceOf", "1M8gvr1DZ1KKVjf6XW6aYR6pHGeDRspdCx", id);
	        query = client.callEVMAbi(contractAddress, HexUtil.toHexString(packAbiGet));
	        output = query.getJSONObject("result");
	        rawData = output.getString("rawData");
	        result = result + "[" + id +":"+  HexUtil.hexStringToAlgorism(HexUtil.removeHexHeader(rawData)) + "]";
	        
	        
       	 	// 查询用户B地址下的资产余额
	        packAbiGet = EvmUtil.encodeParameter(abi, "balanceOf", userToaddress, id);
	        query = client.callEVMAbi(contractAddress, HexUtil.toHexString(packAbiGet));
	        output = query.getJSONObject("result");
	        rawData = output.getString("rawData");
	        result = result + "\r\n用户" + userToaddress + " 中NFT資產余额： " + "[" + id +":"+  HexUtil.hexStringToAlgorism(HexUtil.removeHexHeader(rawData)) + "]";
        	
	        jLabel8.setText(result);
        
        }
	}
}