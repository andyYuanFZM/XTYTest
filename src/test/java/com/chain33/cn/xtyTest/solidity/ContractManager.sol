pragma solidity >=0.6.2 <0.7.0;

/**
* 合约生命周期管理伪代码，冻结，解冻，销毁
*/
contract ContractManager{
    address public owner;
    uint256 number;
    bool freezeFlg;

    constructor() public {
        owner = msg.sender;
        freezeFlg = false;
    }

    function freeze() external onlyOwner  {
        freezeFlg = true;
    }

    function unfreeze() external onlyOwner  {
        freezeFlg = false;
    }

    function store(uint256 num) external onlyUnFreeze {
        number = num;
    }

    function retrieve() external onlyUnFreeze view returns (uint256) {
        return number;
    }

    function destroyContract() external onlyOwner {
        selfdestruct(msg.sender);
    }

    modifier onlyOwner() {
        require(owner == msg.sender, 'You are not owner');
        _;
    }

    modifier onlyUnFreeze() {
        require(freezeFlg == false, 'This contract hash been frozen');
        _;
    }
}
