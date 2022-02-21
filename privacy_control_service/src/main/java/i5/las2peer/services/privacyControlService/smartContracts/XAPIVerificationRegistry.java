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
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple3;
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
    public static final String BINARY = "608060405234801561001057600080fd5b50610561806100206000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c8063025fd89e146100515780631abce7071461017a57806330bad485146101bb578063b9a579ab14610271575b600080fd5b6101786004803603604081101561006757600080fd5b81019060208101813564010000000081111561008257600080fd5b82018360208201111561009457600080fd5b803590602001918460208302840111640100000000831117156100b657600080fd5b919080806020026020016040519081016040528093929190818152602001838360200280828437600092019190915250929594936020810193503591505064010000000081111561010657600080fd5b82018360208201111561011857600080fd5b8035906020019184602083028401116401000000008311171561013a57600080fd5b919080806020026020016040519081016040528093929190818152602001838360200280828437600092019190915250929550610294945050505050565b005b61019d6004803603604081101561019057600080fd5b5080359060200135610353565b60408051938452602084019290925282820152519081900360600190f35b6101d8600480360360208110156101d157600080fd5b5035610392565b604051808060200180602001838103835285818151815260200191508051906020019060200280838360005b8381101561021c578181015183820152602001610204565b50505050905001838103825284818151815260200191508051906020019060200280838360005b8381101561025b578181015183820152602001610243565b5050505090500194505050505060405180910390f35b6101786004803603604081101561028757600080fd5b50803590602001356104ac565b60005b825181101561034e576102a861050c565b60405180606001604052808584815181106102bf57fe5b602002602001015181526020018484815181106102d857fe5b602002602001015181526020014281525090506000808584815181106102fa57fe5b60209081029190910181015182528181019290925260409081016000908120805460018181018355918352918490208551600390930201918255928401518184015592015160029092019190915501610297565b505050565b6000602052816000526040600020818154811061036c57fe5b600091825260209091206003909102018054600182015460029092015490935090915083565b600081815260208181526040918290205482518181528183028101909201909252606091829182908280156103d1578160200160208202803883390190505b509050606082604051908082528060200260200182016040528015610400578160200160208202803883390190505b50905060005b838110156104a057600087815260208190526040902080548290811061042857fe5b90600052602060002090600302016001015483828151811061044657fe5b602002602001018181525050600080888152602001908152602001600020818154811061046f57fe5b90600052602060002090600302016002015482828151811061048d57fe5b6020908102919091010152600101610406565b50909350915050915091565b6104b461050c565b5060408051606081018252838152602080820193845242828401908152600095865285825292852080546001818101835591875291909520915160039091029091019081559151928201929092559051600290910155565b60408051606081018252600080825260208201819052918101919091529056fea265627a7a72315820a9b09619843451b44f765b94a2089d1a9dab502c6a551b45a0cf687a3c51f2c364736f6c63430005110032";

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

    public RemoteFunctionCall<TransactionReceipt> enterLogEntries(List<byte[]> userIDs, List<byte[]> dataHashes) {
        final Function function = new Function(
                FUNC_ENTERLOGENTRIES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.datatypes.generated.Bytes32.class,
                        org.web3j.abi.Utils.typeMap(userIDs, org.web3j.abi.datatypes.generated.Bytes32.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.datatypes.generated.Bytes32.class,
                        org.web3j.abi.Utils.typeMap(dataHashes, org.web3j.abi.datatypes.generated.Bytes32.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> enterLogEntry(byte[] userID, byte[] dataHash) {
        final Function function = new Function(
                FUNC_ENTERLOGENTRY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(userID), 
                new org.web3j.abi.datatypes.generated.Bytes32(dataHash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple2<List<byte[]>, List<BigInteger>>> getLogEntries(byte[] userID) {
        final Function function = new Function(FUNC_GETLOGENTRIES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(userID)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Bytes32>>() {}, new TypeReference<DynamicArray<Uint256>>() {}));
        return new RemoteFunctionCall<Tuple2<List<byte[]>, List<BigInteger>>>(function,
                new Callable<Tuple2<List<byte[]>, List<BigInteger>>>() {
                    @Override
                    public Tuple2<List<byte[]>, List<BigInteger>> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<List<byte[]>, List<BigInteger>>(
                                convertToNative((List<Bytes32>) results.get(0).getValue()), 
                                convertToNative((List<Uint256>) results.get(1).getValue()));
                    }
                });
    }

    public RemoteFunctionCall<Tuple3<byte[], byte[], BigInteger>> userToEntries(byte[] param0, BigInteger param1) {
        final Function function = new Function(FUNC_USERTOENTRIES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(param0), 
                new org.web3j.abi.datatypes.generated.Uint256(param1)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple3<byte[], byte[], BigInteger>>(function,
                new Callable<Tuple3<byte[], byte[], BigInteger>>() {
                    @Override
                    public Tuple3<byte[], byte[], BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<byte[], byte[], BigInteger>(
                                (byte[]) results.get(0).getValue(), 
                                (byte[]) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue());
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
