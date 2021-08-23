contract BasicAuth {
    address public _owner;

    constructor() public {
        _owner = msg.sender;
    }

    function setOwner(address owner)
        public onlyOwner {
        _owner = owner;
    }

    modifier onlyOwner() { 
        require(msg.sender == _owner, "only authorized owner can store files.");
        _; 
    }
}