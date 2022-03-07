package i5.las2peer.services.privacyControlService.smartContracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint16;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.4.1.
 */
@SuppressWarnings("rawtypes")
public class DataProcessingPurposes extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b50610b8c806100206000396000f3fe608060405234801561001057600080fd5b50600436106100575760003560e01c806306ec5ccf1461005c578063621df1a3146100b45780636e53e690146102025780637ba06e031461031c578063a583b05b1461034f575b600080fd5b61006461045c565b60408051602080825283518183015283519192839290830191858101910280838360005b838110156100a0578181015183820152602001610088565b505050509050019250505060405180910390f35b6101eb600480360360608110156100ca57600080fd5b60ff82351691908101906040810160208201356401000000008111156100ef57600080fd5b82018360208201111561010157600080fd5b8035906020019184600183028401116401000000008311171561012357600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929594936020810193503591505064010000000081111561017657600080fd5b82018360208201111561018857600080fd5b803590602001918460018302840111640100000000831117156101aa57600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295506104d3945050505050565b6040805161ffff9092168252519081900360200190f35b6102226004803603602081101561021857600080fd5b503560ff16610793565b604051808560ff1660ff16815260200180602001806020018461ffff1661ffff168152602001838103835286818151815260200191508051906020019080838360005b8381101561027d578181015183820152602001610265565b50505050905090810190601f1680156102aa5780820380516001836020036101000a031916815260200191505b50838103825285518152855160209182019187019080838360005b838110156102dd5781810151838201526020016102c5565b50505050905090810190601f16801561030a5780820380516001836020036101000a031916815260200191505b50965050505050505060405180910390f35b6103396004803603602081101561033257600080fd5b50356108da565b6040805160ff9092168252519081900360200190f35b61036f6004803603602081101561036557600080fd5b503560ff1661090b565b6040518080602001806020018461ffff1661ffff168152602001838103835286818151815260200191508051906020019080838360005b838110156103be5781810151838201526020016103a6565b50505050905090810190601f1680156103eb5780820380516001836020036101000a031916815260200191505b50838103825285518152855160209182019187019080838360005b8381101561041e578181015183820152602001610406565b50505050905090810190601f16801561044b5780820380516001836020036101000a031916815260200191505b509550505050505060405180910390f35b606060018054806020026020016040519081016040528092919081815260200182805480156104c857602002820191906000526020600020906000905b825461010083900a900460ff168152602060019283018181049485019490930390920291018084116104995790505b505050505090505b90565b6000805b60015460ff8216101561052b578460ff1660018260ff16815481106104f857fe5b60009182526020918290209181049091015460ff601f9092166101000a90041614156105235761052b565b6001016104d7565b60015460ff8216141561058b57600180548082018255600091909152602081047fb10e2d527612073b26eecdfd717e6a320cf44b4afac2b0732d9fcbe2b7fa0cf601805460ff808916601f9094166101000a938402930219169190911790555b610593610a90565b60ff86811660009081526020818152604091829020825160808101845281549094168452600180820180548551600261010094831615949094026000190190911692909204601f81018590048502830185019095528482529193858401939192918301828280156106455780601f1061061a57610100808354040283529160200191610645565b820191906000526020600020905b81548152906001019060200180831161062857829003601f168201915b5050509183525050600282810180546040805160206001841615610100026000190190931694909404601f810183900483028501830190915280845293810193908301828280156106d75780601f106106ac576101008083540402835291602001916106d7565b820191906000526020600020905b8154815290600101906020018083116106ba57829003601f168201915b50505091835250506003919091015461ffff90811660209283015260ff8981168085528484018a815260408087018b90526060870180516001908101909616905260009283528286529091208551815460ff191693169290921782555180519495508594919361074c93850192910190610abf565b5060408201518051610768916002840191602090910190610abf565b50606091820151600391909101805461ffff191661ffff909216919091179055015195945050505050565b600060208181529181526040908190208054600180830180548551600261010094831615949094026000190190911692909204601f810187900487028301870190955284825260ff90921694929390928301828280156108345780601f1061080957610100808354040283529160200191610834565b820191906000526020600020905b81548152906001019060200180831161081757829003601f168201915b50505060028085018054604080516020601f60001961010060018716150201909416959095049283018590048502810185019091528181529596959450909250908301828280156108c65780601f1061089b576101008083540402835291602001916108c6565b820191906000526020600020905b8154815290600101906020018083116108a957829003601f168201915b5050506003909301549192505061ffff1684565b600181815481106108e757fe5b9060005260206000209060209182820401919006915054906101000a900460ff1681565b6060806000610918610a90565b60ff85811660009081526020818152604091829020825160808101845281549094168452600180820180548551600261010094831615949094026000190190911692909204601f81018590048502830185019095528482529193858401939192918301828280156109ca5780601f1061099f576101008083540402835291602001916109ca565b820191906000526020600020905b8154815290600101906020018083116109ad57829003601f168201915b5050509183525050600282810180546040805160206001841615610100026000190190931694909404601f81018390048302850183019091528084529381019390830182828015610a5c5780601f10610a3157610100808354040283529160200191610a5c565b820191906000526020600020905b815481529060010190602001808311610a3f57829003601f168201915b50505091835250506003919091015461ffff1660209182015281015160408201516060909201519097919650945092505050565b6040518060800160405280600060ff1681526020016060815260200160608152602001600061ffff1681525090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10610b0057805160ff1916838001178555610b2d565b82800160010185558215610b2d579182015b82811115610b2d578251825591602001919060010190610b12565b50610b39929150610b3d565b5090565b6104d091905b80821115610b395760008155600101610b4356fea265627a7a72315820b8cfd54a62cbb9b22b0b981237e3003f45f8611eda60529b61c01aed276ad2c164736f6c634300050c0032";

    public static final String FUNC_CREATEORMODIFYPURPOSE = "createOrModifyPurpose";

    public static final String FUNC_GETALLPURPOSEIDS = "getAllPurposeIDs";

    public static final String FUNC_GETPURPOSE = "getPurpose";

    public static final String FUNC_PURPOSEIDS = "purposeIDs";

    public static final String FUNC_PURPOSELIST = "purposeList";

    @Deprecated
    protected DataProcessingPurposes(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected DataProcessingPurposes(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected DataProcessingPurposes(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected DataProcessingPurposes(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> createOrModifyPurpose(BigInteger id, String title, String description) {
        final Function function = new Function(
                FUNC_CREATEORMODIFYPURPOSE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint8(id), 
                new org.web3j.abi.datatypes.Utf8String(title), 
                new org.web3j.abi.datatypes.Utf8String(description)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<List> getAllPurposeIDs() {
        final Function function = new Function(FUNC_GETALLPURPOSEIDS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Uint8>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<Tuple3<String, String, BigInteger>> getPurpose(BigInteger purposeID) {
        final Function function = new Function(FUNC_GETPURPOSE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint8(purposeID)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint16>() {}));
        return new RemoteFunctionCall<Tuple3<String, String, BigInteger>>(function,
                new Callable<Tuple3<String, String, BigInteger>>() {
                    @Override
                    public Tuple3<String, String, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<String, String, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue());
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> purposeIDs(BigInteger param0) {
        final Function function = new Function(FUNC_PURPOSEIDS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Tuple4<BigInteger, String, String, BigInteger>> purposeList(BigInteger param0) {
        final Function function = new Function(FUNC_PURPOSELIST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint8(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint16>() {}));
        return new RemoteFunctionCall<Tuple4<BigInteger, String, String, BigInteger>>(function,
                new Callable<Tuple4<BigInteger, String, String, BigInteger>>() {
                    @Override
                    public Tuple4<BigInteger, String, String, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<BigInteger, String, String, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue());
                    }
                });
    }

    @Deprecated
    public static DataProcessingPurposes load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new DataProcessingPurposes(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static DataProcessingPurposes load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new DataProcessingPurposes(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static DataProcessingPurposes load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new DataProcessingPurposes(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static DataProcessingPurposes load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new DataProcessingPurposes(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<DataProcessingPurposes> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(DataProcessingPurposes.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<DataProcessingPurposes> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DataProcessingPurposes.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<DataProcessingPurposes> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(DataProcessingPurposes.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<DataProcessingPurposes> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DataProcessingPurposes.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
