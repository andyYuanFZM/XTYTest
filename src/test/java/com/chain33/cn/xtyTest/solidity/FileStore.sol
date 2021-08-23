pragma solidity >=0.6.2 <0.7.0;

import "./BasicAuth.sol";

contract FileStore is BasicAuth {
    mapping(string => string) _fileStockByID;

    modifier validFileId(string memory fileId) {
        require(bytes(fileId).length > 0, "fileId is invalid!");
        _;
    }

    function setFileStockById(string memory fileId, string memory content) 
        onlyOwner validFileId(fileId) external {
        _fileStockByID[fileId] = content;
    }

    function getFileById(string memory fileId) external view  returns(string memory) {
        return _fileStockByID[fileId];
    }
}