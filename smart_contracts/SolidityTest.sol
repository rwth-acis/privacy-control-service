// SPDX-License-Identifier: GPL-3.0

pragma solidity ^0.5.0;

contract SolidityTest {
    uint testCounter;
    
    constructor() public {
        testCounter = 0;
    }
    
    function getCounter() public view returns(uint) {
        return testCounter;
    }
    
    
    function incCounter() public {
        testCounter += 1;
    }
    
}