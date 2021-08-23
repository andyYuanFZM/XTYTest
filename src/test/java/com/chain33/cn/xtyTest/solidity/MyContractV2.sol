pragma solidity >=0.6.2 <0.7.0;

contract MyContractV2 {
    uint32 public counter;
    bool private stopped = false;
    address private owner;
   
    /**
    * 合约中止，中止后无法再次访问
    */
    modifier isNotStopped {
        require(!stopped, 'Contract is stopped.');
        _;
    }
 
    modifier isOwner {
        require(msg.sender == owner, 'Sender is not owner.');
        _;
    }
    
    constructor() public {
        owner = msg.sender;
    }

    /**
    * 合约升级，老数据移植到新合约中
    */
    function initNumber(uint32 _counter) isOwner public {
        // 在构造函数中接受传参，将V1版本中的值传递进来，使前后数据能衔接
        counter = _counter; 
    }

    function setNumber() isNotStopped public {
        counter++; 
    }

    function getNumber() isNotStopped public view returns (uint256){
        return counter;
    }

    function stopContract() isOwner public {
        stopped = true;
    }
}