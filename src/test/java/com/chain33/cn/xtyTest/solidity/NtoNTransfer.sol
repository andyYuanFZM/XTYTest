pragma solidity ^0.6.0;


/**
* 信通院测试： 资产N对N转账
*/
contract Token {
    string public name; 
    string public symbol; 
    uint8 public decimals = 2; 
    uint256 public totalSupply; 

    mapping (address => uint256) public balanceOf;
    mapping (address => mapping (address => uint256)) public allowance;

    constructor() public {}

    /**
     * 初始化构造
     */
    function TokenCreate(uint256 initialSupply, string memory tokenName, string memory tokenSymbol) public {
        totalSupply = initialSupply * 10 ** uint256(decimals);  
        balanceOf[msg.sender] = totalSupply; 
        name = tokenName; 
        symbol = tokenSymbol; 
    }

    /**
     * 代币交易转移的内部实现
     */
    function _transfer(address _from, address _to, uint _value) internal {

        // 检查发送者余额
        require(balanceOf[_from] >= _value);
        // 确保转移为正数个
        require(balanceOf[_to] + _value > balanceOf[_to]);
        // Subtract from the sender
        balanceOf[_from] -= _value;
        // Add the same to the recipient
        balanceOf[_to] += _value;
    }

    /**
     *  代币交易转移
     *  从自己（创建交易者）账号发送`_value`个代币到 `_to`账号
     * @param _to 接收者地址
     * @param _value 转移数额
     */
    function transfer(address _to, uint256 _value) public {
        _transfer(msg.sender, _to, _value);
    }

    /**
     *  1对N批量转账
     *  从自己（创建交易者）账号发送`_value`个代币到一批 `_to`账号
     * @param _to 接收者地址
     * @param _value 转移数额
     */
    function batchtransfer(address[] memory _to, uint256 _value) public {
        require(_to.length > 0);
        for(uint i = 0; i < _to.length; i++){
            _transfer(msg.sender, _to[i], _value);
        }
    }

    /**
     *  N对N转账
     *  从from账号列表1对1发送`_value`个代币到一批 `_to`账号列表，发送方式一一对应，且from列表长度一定要等于to列表长度
     * @param _to 接收者地址
     * @param _value 转移数额
     */
    function batchNtoN(address[] memory _from, address[] memory _to, uint256 _value) public {
        require(_from.length == _to.length);
        for(uint i = 0; i < _from.length; i++){
            _transfer(_from[i], _to[i], _value);
        }
    }
}