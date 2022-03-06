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
    public static final String BINARY = "608060405234801561001057600080fd5b50610fe7806100206000396000f3fe608060405234801561001057600080fd5b506004361061007d5760003560e01c80637ba06e031161005b5780637ba06e03146100e357806388a9b8d2146100f6578063a583b05b146100fe578063bc16e343146101205761007d565b80630efd028414610082578063621df1a3146100a05780636e53e690146100c0575b600080fd5b61008a610138565b6040516100979190610e75565b60405180910390f35b6100b36100ae366004610bc7565b61013d565b6040516100979190610e67565b6100d36100ce366004610ba9565b6103ff565b6040516100979493929190610e83565b61008a6100f1366004610b83565b610546565b6100b3610577565b61011161010c366004610ba9565b61057d565b60405161009793929190610e33565b61012861072b565b6040516100979493929190610ddc565b606481565b60008060015b60015460ff8316101561019d578560ff1660018360ff168154811061016457fe5b60009182526020918290209181049091015460ff601f9092166101000a90041614156101925750600061019d565b600190910190610143565b80156101f657600180548082018255600091909152602081047fb10e2d527612073b26eecdfd717e6a320cf44b4afac2b0732d9fcbe2b7fa0cf601805460ff808a16601f9094166101000a938402930219169190911790555b6101fe610a49565b60ff87811660009081526020818152604091829020825160808101845281549094168452600180820180548551600261010094831615949094026000190190911692909204601f81018590048502830185019095528482529193858401939192918301828280156102b05780601f10610285576101008083540402835291602001916102b0565b820191906000526020600020905b81548152906001019060200180831161029357829003601f168201915b5050509183525050600282810180546040805160206001841615610100026000190190931694909404601f810183900483028501830190915280845293810193908301828280156103425780601f1061031757610100808354040283529160200191610342565b820191906000526020600020905b81548152906001019060200180831161032557829003601f168201915b50505091835250506003919091015461ffff90811660209283015260ff8a81168085528484018b815260408087018c90526060870180516001908101909616905260009283528286529091208551815460ff19169316929092178255518051949550859491936103b793850192910190610a78565b50604082015180516103d3916002840191602090910190610a78565b50606091820151600391909101805461ffff191661ffff90921691909117905501519695505050505050565b600060208181529181526040908190208054600180830180548551600261010094831615949094026000190190911692909204601f810187900487028301870190955284825260ff90921694929390928301828280156104a05780601f10610475576101008083540402835291602001916104a0565b820191906000526020600020905b81548152906001019060200180831161048357829003601f168201915b50505060028085018054604080516020601f60001961010060018716150201909416959095049283018590048502810185019091528181529596959450909250908301828280156105325780601f1061050757610100808354040283529160200191610532565b820191906000526020600020905b81548152906001019060200180831161051557829003601f168201915b5050506003909301549192505061ffff1684565b6001818154811061055357fe5b9060005260206000209060209182820401919006915054906101000a900460ff1681565b6101f481565b606080600061058a610a49565b60008060018760ff168154811061059d57fe5b600091825260208083208183040154601f92831661010090810a90910460ff908116865285830196909652604094850190932084516080810186528154909616865260018082018054875160029382161590970260001901169190910493840183900483028501830190955282845293858201939290918301828280156106655780601f1061063a57610100808354040283529160200191610665565b820191906000526020600020905b81548152906001019060200180831161064857829003601f168201915b5050509183525050600282810180546040805160206001841615610100026000190190931694909404601f810183900483028501830190915280845293810193908301828280156106f75780601f106106cc576101008083540402835291602001916106f7565b820191906000526020600020905b8154815290600101906020018083116106da57829003601f168201915b50505091835250506003919091015461ffff1660209182015281015160408201516060909201519097919650945092505050565b6060806060806060600180549050604051908082528060200260200182016040528015610762578160200160208202803883390190505b50905060606001805490506040519080825280602002602001820160405280156107a057816020015b606081526020019060019003908161078b5790505b50905060606001805490506040519080825280602002602001820160405280156107de57816020015b60608152602001906001900390816107c95790505b5090506060600180549050604051908082528060200260200182016040528015610812578160200160208202803883390190505b50905061081d610a49565b60005b600154811015610a39576000806001838154811061083a57fe5b600091825260208083208183040154601f92831661010090810a90910460ff908116865285830196909652604094850190932084516080810186528154909616865260018082018054875160029382161590970260001901169190910493840183900483028501830190955282845293858201939290918301828280156109025780601f106108d757610100808354040283529160200191610902565b820191906000526020600020905b8154815290600101906020018083116108e557829003601f168201915b5050509183525050600282810180546040805160206001841615610100026000190190931694909404601f810183900483028501830190915280845293810193908301828280156109945780601f1061096957610100808354040283529160200191610994565b820191906000526020600020905b81548152906001019060200180831161097757829003601f168201915b50505091835250506003919091015461ffff1660209091015280518751919350908790839081106109c157fe5b602002602001019060ff16908160ff168152505081602001518582815181106109e657fe5b60200260200101819052508160400151848281518110610a0257fe5b60200260200101819052508160600151838281518110610a1e57fe5b61ffff90921660209283029190910190910152600101610820565b5093989297509095509350915050565b6040518060800160405280600060ff1681526020016060815260200160608152602001600061ffff1681525090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10610ab957805160ff1916838001178555610ae6565b82800160010185558215610ae6579182015b82811115610ae6578251825591602001919060010190610acb565b50610af2929150610af6565b5090565b610b1091905b80821115610af25760008155600101610afc565b90565b600082601f830112610b2457600080fd5b8135610b37610b3282610ef6565b610ecf565b91508082526020830160208301858383011115610b5357600080fd5b610b5e838284610f3e565b50505092915050565b8035610b7281610f84565b92915050565b8035610b7281610f9b565b600060208284031215610b9557600080fd5b6000610ba18484610b67565b949350505050565b600060208284031215610bbb57600080fd5b6000610ba18484610b78565b600080600060608486031215610bdc57600080fd5b6000610be88686610b78565b935050602084013567ffffffffffffffff811115610c0557600080fd5b610c1186828701610b13565b925050604084013567ffffffffffffffff811115610c2e57600080fd5b610c3a86828701610b13565b9150509250925092565b6000610c508383610d8c565b9392505050565b6000610c638383610dc4565b505060200190565b6000610c638383610dd3565b6000610c8282610f24565b610c8c8185610f28565b935083602082028501610c9e85610f1e565b8060005b85811015610cd85784840389528151610cbb8582610c44565b9450610cc683610f1e565b60209a909a0199925050600101610ca2565b5091979650505050505050565b6000610cf082610f24565b610cfa8185610f28565b9350610d0583610f1e565b8060005b83811015610d33578151610d1d8882610c57565b9750610d2883610f1e565b925050600101610d09565b509495945050505050565b6000610d4982610f24565b610d538185610f28565b9350610d5e83610f1e565b8060005b83811015610d33578151610d768882610c6b565b9750610d8183610f1e565b925050600101610d62565b6000610d9782610f24565b610da18185610f28565b9350610db1818560208601610f4a565b610dba81610f7a565b9093019392505050565b610dcd81610f31565b82525050565b610dcd81610f38565b60808082528101610ded8187610d3e565b90508181036020830152610e018186610c77565b90508181036040830152610e158185610c77565b90508181036060830152610e298184610ce5565b9695505050505050565b60608082528101610e448186610d8c565b90508181036020830152610e588185610d8c565b9050610ba16040830184610dc4565b60208101610b728284610dc4565b60208101610b728284610dd3565b60808101610e918287610dd3565b8181036020830152610ea38186610d8c565b90508181036040830152610eb78185610d8c565b9050610ec66060830184610dc4565b95945050505050565b60405181810167ffffffffffffffff81118282101715610eee57600080fd5b604052919050565b600067ffffffffffffffff821115610f0d57600080fd5b506020601f91909101601f19160190565b60200190565b5190565b90815260200190565b61ffff1690565b60ff1690565b82818337506000910152565b60005b83811015610f65578181015183820152602001610f4d565b83811115610f74576000848401525b50505050565b601f01601f191690565b610f8d81610b10565b8114610f9857600080fd5b50565b610f8d81610f3856fea365627a7a723158204100540657dbf714fde1e5281be3d94aec099fb060193bd572ee74d556ac2f256c6578706572696d656e74616cf564736f6c634300050c0040";

    public static final String FUNC_MAX_PURPOSE_DESCRIPTION_SIZE = "MAX_PURPOSE_DESCRIPTION_SIZE";

    public static final String FUNC_MAX_PURPOSE_TITLE_SIZE = "MAX_PURPOSE_TITLE_SIZE";

    public static final String FUNC_CREATEORMODIFYPURPOSE = "createOrModifyPurpose";

    public static final String FUNC_GETALLPURPOSES = "getAllPurposes";

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

    public RemoteFunctionCall<BigInteger> MAX_PURPOSE_DESCRIPTION_SIZE() {
        final Function function = new Function(FUNC_MAX_PURPOSE_DESCRIPTION_SIZE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint16>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> MAX_PURPOSE_TITLE_SIZE() {
        final Function function = new Function(FUNC_MAX_PURPOSE_TITLE_SIZE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
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

    public RemoteFunctionCall<Tuple4<List<BigInteger>, List<String>, List<String>, List<BigInteger>>> getAllPurposes() {
        final Function function = new Function(FUNC_GETALLPURPOSES, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Uint8>>() {}, new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<DynamicArray<Uint16>>() {}));
        return new RemoteFunctionCall<Tuple4<List<BigInteger>, List<String>, List<String>, List<BigInteger>>>(function,
                new Callable<Tuple4<List<BigInteger>, List<String>, List<String>, List<BigInteger>>>() {
                    @Override
                    public Tuple4<List<BigInteger>, List<String>, List<String>, List<BigInteger>> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<List<BigInteger>, List<String>, List<String>, List<BigInteger>>(
                                convertToNative((List<Uint8>) results.get(0).getValue()), 
                                convertToNative((List<Utf8String>) results.get(1).getValue()), 
                                convertToNative((List<Utf8String>) results.get(2).getValue()), 
                                convertToNative((List<Uint16>) results.get(3).getValue()));
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
