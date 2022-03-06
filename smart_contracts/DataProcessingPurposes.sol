//SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.5.0;
pragma experimental ABIEncoderV2;

contract DataProcessingPurposes {
    struct Purpose {
        uint8 id;
        string title;
        string description;
        uint16 version;
    }
    
    // TODO: Remove these if not needed, or create a constraint
    uint16 constant public MAX_PURPOSE_DESCRIPTION_SIZE = 500;
    uint8 constant public MAX_PURPOSE_TITLE_SIZE = 100;
    
    mapping (uint8 => Purpose) public purposeList;
    uint8[] public purposeIDs;
    
    function getAllPurposes() public view returns(
        	uint8[] memory,
        	string[] memory,
        	string[] memory,
        	uint16[] memory
    ) {
        uint8[] memory ids = new uint8[](purposeIDs.length);
        string[] memory titles = new string[](purposeIDs.length);
        string[] memory descs = new string[](purposeIDs.length);
        uint16[] memory vers = new uint16[](purposeIDs.length);
        
        Purpose memory tmp;
        for (uint i = 0; i < purposeIDs.length; i++) {
            tmp = purposeList[purposeIDs[i]];
            ids[i] = tmp.id;
            titles[i] = tmp.title;
            descs[i] = tmp.description;
            vers[i] = tmp.version;
        }
        
        return (ids, titles, descs, vers);
    }
    
    function getPurpose(uint8 purposeID) public view returns(
        string memory, string memory, uint16
    ) {
        Purpose memory tmp = purposeList[purposeID];
        return(tmp.title, tmp.description, tmp.version);
    }
    
    function createOrModifyPurpose(
        uint8 id,
        string memory title,
        string memory description
    ) public returns(uint16) {
        uint8 index;
        bool check = true;
        for (index = 0; index < purposeIDs.length; index++) {
            if(purposeIDs[index] == id) {
                check = false;
                break;
            }
        }
        if (check) {
        	purposeIDs.push(id);    
        }
        
        Purpose memory purpose = purposeList[id];
        purpose.id = id; //this is if it doesn't yet exist
        purpose.title = title;
        purpose.description = description;
        purpose.version += 1;
        purposeList[id] = purpose;  
        return purpose.version;        
    }
}
