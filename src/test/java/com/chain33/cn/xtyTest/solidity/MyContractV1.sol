pragma solidity >=0.6.2 <0.7.0;

contract MyContractV1 {
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
        counter = 0;
        owner = msg.sender;
    }

    function setNumber() isNotStopped public {
        counter += 2; 
    }

    function getNumber() isNotStopped public view returns (uint256){
        return counter;
    }

    function stopContract() isOwner public {
        stopped = true;
    }
}