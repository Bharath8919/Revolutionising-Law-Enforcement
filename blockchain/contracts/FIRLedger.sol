// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

contract FIRLedger {
    struct FIRRecord {
        uint256 id;
        string descriptionHash;
        string location;
        uint256 timestamp;
        address filedBy;
    }

    mapping(uint256 => FIRRecord) public records;
    uint256[] public recordIds;

    event FIRStored(uint256 indexed id, string descriptionHash, address filedBy);

    function storeFIR(uint256 _id, string memory _descriptionHash, string memory _location) public {
        records[_id] = FIRRecord(_id, _descriptionHash, _location, block.timestamp, msg.sender);
        recordIds.push(_id);
        emit FIRStored(_id, _descriptionHash, msg.sender);
    }

    function getRecordCount() public view returns (uint256) {
        return recordIds.length;
    }
}
