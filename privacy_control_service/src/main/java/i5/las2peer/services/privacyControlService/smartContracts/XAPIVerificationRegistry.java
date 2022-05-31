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
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tuples.generated.Tuple5;
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
public class XAPIVerificationRegistry extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b506108eb806100206000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c80631abce70714610051578063302293ff146100a357806330bad485146100d9578063ce75493c14610219575b600080fd5b6100746004803603604081101561006757600080fd5b5080359060200135610440565b60408051958652602086019490945260ff92831685850152911660608401526080830152519081900360a00190f35b6100d7600480360360808110156100b957600080fd5b5080359060208101359060ff60408201358116916060013516610490565b005b6100f6600480360360208110156100ef57600080fd5b5035610530565b6040518080602001806020018060200180602001858103855289818151815260200191508051906020019060200280838360005b8381101561014257818101518382015260200161012a565b50505050905001858103845288818151815260200191508051906020019060200280838360005b83811015610181578181015183820152602001610169565b50505050905001858103835287818151815260200191508051906020019060200280838360005b838110156101c05781810151838201526020016101a8565b50505050905001858103825286818151815260200191508051906020019060200280838360005b838110156101ff5781810151838201526020016101e7565b505050509050019850505050505050505060405180910390f35b6100d76004803603608081101561022f57600080fd5b810190602081018135600160201b81111561024957600080fd5b82018360208201111561025b57600080fd5b803590602001918460208302840111600160201b8311171561027c57600080fd5b9190808060200260200160405190810160405280939291908181526020018383602002808284376000920191909152509295949360208101935035915050600160201b8111156102cb57600080fd5b8201836020820111156102dd57600080fd5b803590602001918460208302840111600160201b831117156102fe57600080fd5b9190808060200260200160405190810160405280939291908181526020018383602002808284376000920191909152509295949360208101935035915050600160201b81111561034d57600080fd5b82018360208201111561035f57600080fd5b803590602001918460208302840111600160201b8311171561038057600080fd5b9190808060200260200160405190810160405280939291908181526020018383602002808284376000920191909152509295949360208101935035915050600160201b8111156103cf57600080fd5b8201836020820111156103e157600080fd5b803590602001918460208302840111600160201b8311171561040257600080fd5b91908080602002602001604051908101604052809392919081815260200183836020028082843760009201919091525092955061075b945050505050565b6000602052816000526040600020818154811061045957fe5b60009182526020909120600490910201805460018201546002830154600390930154919450925060ff808316926101009004169085565b610498610888565b506040805160a081018252858152602080820195865260ff9485168284019081529385166060830190815242608084019081526000988952888352938820805460018181018355918a5292909820925160049092029092019081559451958501959095559051600284018054955184166101000261ff00199290941660ff199096169590951716919091179092559051600390910155565b6000818152602081815260409182902054825181815281830281019092019092526060918291829182918290828015610573578160200160208202803883390190505b5090506060826040519080825280602002602001820160405280156105a2578160200160208202803883390190505b5090506060836040519080825280602002602001820160405280156105d1578160200160208202803883390190505b509050606084604051908082528060200260200182016040528015610600578160200160208202803883390190505b50905060005b8581101561074a5760008b815260208190526040902080548290811061062857fe5b90600052602060002090600402016001015485828151811061064657fe5b6020026020010181815250506000808c8152602001908152602001600020818154811061066f57fe5b906000526020600020906004020160020160009054906101000a900460ff1684828151811061069a57fe5b60ff90921660209283029190910182015260008c815290819052604090208054829081106106c457fe5b906000526020600020906004020160020160019054906101000a900460ff168382815181106106ef57fe5b60ff90921660209283029190910182015260008c8152908190526040902080548290811061071957fe5b90600052602060002090600402016003015482828151811061073757fe5b6020908102919091010152600101610606565b509299919850965090945092505050565b60005b84518110156108815761076f610888565b6040518060a0016040528087848151811061078657fe5b6020026020010151815260200186848151811061079f57fe5b602002602001015181526020018584815181106107b857fe5b602002602001015160ff1681526020018484815181106107d457fe5b602002602001015160ff1681526020014281525090506000808784815181106107f957fe5b60209081029190910181015182528181019290925260409081016000908120805460018181018355918352918490208551600490930201918255928401518184015590830151600282018054606086015160ff9081166101000261ff00199190941660ff1990921691909117169190911790556080909201516003909201919091550161075e565b5050505050565b6040805160a0810182526000808252602082018190529181018290526060810182905260808101919091529056fea265627a7a72315820cd210c89548d43d3ebd71d73f52f913f3e84215c3f8e3b284f03efe2456e8d5c64736f6c63430005110032";

    public static final String FUNC_ENTERLOGENTRIES = "enterLogEntries";

    public static final String FUNC_ENTERLOGENTRY = "enterLogEntry";

    public static final String FUNC_GETLOGENTRIES = "getLogEntries";

    public static final String FUNC_USERTOENTRIES = "userToEntries";

    @Deprecated
    protected XAPIVerificationRegistry(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected XAPIVerificationRegistry(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected XAPIVerificationRegistry(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected XAPIVerificationRegistry(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> enterLogEntries(List<byte[]> userIDs, List<byte[]> dataHashes, List<BigInteger> purposeCodes, List<BigInteger> purposeVersions) {
        final Function function = new Function(
                FUNC_ENTERLOGENTRIES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.datatypes.generated.Bytes32.class,
                        org.web3j.abi.Utils.typeMap(userIDs, org.web3j.abi.datatypes.generated.Bytes32.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.datatypes.generated.Bytes32.class,
                        org.web3j.abi.Utils.typeMap(dataHashes, org.web3j.abi.datatypes.generated.Bytes32.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint8>(
                        org.web3j.abi.datatypes.generated.Uint8.class,
                        org.web3j.abi.Utils.typeMap(purposeCodes, org.web3j.abi.datatypes.generated.Uint8.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint8>(
                        org.web3j.abi.datatypes.generated.Uint8.class,
                        org.web3j.abi.Utils.typeMap(purposeVersions, org.web3j.abi.datatypes.generated.Uint8.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> enterLogEntry(byte[] userID, byte[] dataHash, BigInteger purposeCode, BigInteger purposeVersion) {
        final Function function = new Function(
                FUNC_ENTERLOGENTRY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(userID), 
                new org.web3j.abi.datatypes.generated.Bytes32(dataHash), 
                new org.web3j.abi.datatypes.generated.Uint8(purposeCode), 
                new org.web3j.abi.datatypes.generated.Uint8(purposeVersion)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple4<List<byte[]>, List<BigInteger>, List<BigInteger>, List<BigInteger>>> getLogEntries(byte[] userID) {
        final Function function = new Function(FUNC_GETLOGENTRIES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(userID)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Bytes32>>() {}, new TypeReference<DynamicArray<Uint8>>() {}, new TypeReference<DynamicArray<Uint8>>() {}, new TypeReference<DynamicArray<Uint256>>() {}));
        return new RemoteFunctionCall<Tuple4<List<byte[]>, List<BigInteger>, List<BigInteger>, List<BigInteger>>>(function,
                new Callable<Tuple4<List<byte[]>, List<BigInteger>, List<BigInteger>, List<BigInteger>>>() {
                    @Override
                    public Tuple4<List<byte[]>, List<BigInteger>, List<BigInteger>, List<BigInteger>> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<List<byte[]>, List<BigInteger>, List<BigInteger>, List<BigInteger>>(
                                convertToNative((List<Bytes32>) results.get(0).getValue()), 
                                convertToNative((List<Uint8>) results.get(1).getValue()), 
                                convertToNative((List<Uint8>) results.get(2).getValue()), 
                                convertToNative((List<Uint256>) results.get(3).getValue()));
                    }
                });
    }

    public RemoteFunctionCall<Tuple5<byte[], byte[], BigInteger, BigInteger, BigInteger>> userToEntries(byte[] param0, BigInteger param1) {
        final Function function = new Function(FUNC_USERTOENTRIES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(param0), 
                new org.web3j.abi.datatypes.generated.Uint256(param1)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Uint8>() {}, new TypeReference<Uint8>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple5<byte[], byte[], BigInteger, BigInteger, BigInteger>>(function,
                new Callable<Tuple5<byte[], byte[], BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple5<byte[], byte[], BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple5<byte[], byte[], BigInteger, BigInteger, BigInteger>(
                                (byte[]) results.get(0).getValue(), 
                                (byte[]) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue());
                    }
                });
    }

    @Deprecated
    public static XAPIVerificationRegistry load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new XAPIVerificationRegistry(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static XAPIVerificationRegistry load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new XAPIVerificationRegistry(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static XAPIVerificationRegistry load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new XAPIVerificationRegistry(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static XAPIVerificationRegistry load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new XAPIVerificationRegistry(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<XAPIVerificationRegistry> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(XAPIVerificationRegistry.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<XAPIVerificationRegistry> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(XAPIVerificationRegistry.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<XAPIVerificationRegistry> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(XAPIVerificationRegistry.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<XAPIVerificationRegistry> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(XAPIVerificationRegistry.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
