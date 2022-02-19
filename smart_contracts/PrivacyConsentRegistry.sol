// SPDX-License-Identifier: GPL-3.0

pragma solidity ^0.5.0;

contract PrivacyConsentRegistry {
    struct Consent {
        bytes32 userID;
        bytes32 serviceID;
        bytes32 internalID;
        uint8[] purposes;
        uint16 privacyVersion;
        uint256 timestamp;
    }
    
    // Mapping UserID -> ServiceID -> InternalID
    mapping(bytes32 => mapping(bytes32 => mapping(bytes32 => Consent))) public consentDatabase;
    
    
    function getConsentInfo(bytes32 userID, bytes32 serviceID, bytes32 internalID)
    	public view returns(uint8[] memory, uint16, uint256) {
        Consent memory tmp = consentDatabase[userID][serviceID][internalID];
        return (tmp.purposes, tmp.privacyVersion, tmp.timestamp);
    }
    
    function storeConsent(bytes32 userID, bytes32 serviceID, bytes32 internalID,
        uint8[] memory purposes, uint16 privacyVersion
    ) public {
        consentDatabase[userID][serviceID][internalID] = Consent(userID, serviceID, internalID, purposes, privacyVersion, now);
    }
    
}