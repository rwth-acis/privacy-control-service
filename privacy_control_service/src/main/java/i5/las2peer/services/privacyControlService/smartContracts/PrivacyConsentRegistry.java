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
import org.web3j.abi.datatypes.generated.Uint16;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;
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
public class PrivacyConsentRegistry extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b50610501806100206000396000f3fe608060405234801561001057600080fd5b50600436106100415760003560e01c8063292f8f84146100465780638cd913c4146101045780638e570cd014610197575b600080fd5b610102600480360360a081101561005c57600080fd5b8135916020810135916040820135919081019060808101606082013564010000000081111561008a57600080fd5b82018360208201111561009c57600080fd5b803590602001918460208302840111640100000000831117156100be57600080fd5b9190808060200260200160405190810160405280939291908181526020018383602002808284376000920191909152509295505050903561ffff1691506101ef9050565b005b61012d6004803603606081101561011a57600080fd5b5080359060208101359060400135610295565b60405180806020018461ffff1661ffff168152602001838152602001828103825285818151815260200191508051906020019060200280838360005b83811015610181578181015183820152602001610169565b5050505090500194505050505060405180910390f35b6101c0600480360360608110156101ad57600080fd5b508035906020810135906040013561038e565b6040805195865260208601949094528484019290925261ffff1660608401526080830152519081900360a00190f35b6040805160c08101825286815260208082018781528284018781526060840187815261ffff871660808601524260a086015260008b81528085528681208b825285528681208a825285529590952084518155915160018301555160028201559251805192939261026592600385019201906103d1565b50608082015160048201805461ffff191661ffff90921691909117905560a0909101516005909101555050505050565b60606000806102a2610477565b6000878152602081815260408083208984528252808320888452825291829020825160c08101845281548152600182015481840152600282015481850152600382018054855181860281018601909652808652919492936060860193929083018282801561034d57602002820191906000526020600020906000905b825461010083900a900460ff1681526020600192830181810494850194909303909202910180841161031e5790505b5050509183525050600482015461ffff1660208201526005909101546040909101526060810151608082015160a09092015190999198509650945050505050565b600060208181529381526040808220855292815282812090935282529020805460018201546002830154600484015460059094015492939192909161ffff169085565b82805482825590600052602060002090601f016020900481019282156104675791602002820160005b8382111561043857835183826101000a81548160ff021916908360ff16021790555092602001926001016020816000010492830192600103026103fa565b80156104655782816101000a81549060ff0219169055600101602081600001049283019260010302610438565b505b506104739291506104ab565b5090565b6040805160c0810182526000808252602082018190529181018290526060808201526080810182905260a081019190915290565b6104c991905b8082111561047357805460ff191681556001016104b1565b9056fea265627a7a723158208b1b9f690744147b02897e3917f5f416749a7079483a49e007e9324737d67e4164736f6c63430005110032";

    public static final String FUNC_CONSENTDATABASE = "consentDatabase";

    public static final String FUNC_GETCONSENTINFO = "getConsentInfo";

    public static final String FUNC_STORECONSENT = "storeConsent";

    @Deprecated
    protected PrivacyConsentRegistry(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected PrivacyConsentRegistry(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected PrivacyConsentRegistry(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected PrivacyConsentRegistry(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<Tuple5<byte[], byte[], byte[], BigInteger, BigInteger>> consentDatabase(byte[] param0, byte[] param1, byte[] param2) {
        final Function function = new Function(FUNC_CONSENTDATABASE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(param0), 
                new org.web3j.abi.datatypes.generated.Bytes32(param1), 
                new org.web3j.abi.datatypes.generated.Bytes32(param2)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Uint16>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple5<byte[], byte[], byte[], BigInteger, BigInteger>>(function,
                new Callable<Tuple5<byte[], byte[], byte[], BigInteger, BigInteger>>() {
                    @Override
                    public Tuple5<byte[], byte[], byte[], BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple5<byte[], byte[], byte[], BigInteger, BigInteger>(
                                (byte[]) results.get(0).getValue(), 
                                (byte[]) results.get(1).getValue(), 
                                (byte[]) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue());
                    }
                });
    }

    public RemoteFunctionCall<Tuple3<List<BigInteger>, BigInteger, BigInteger>> getConsentInfo(byte[] userID, byte[] serviceID, byte[] internalID) {
        final Function function = new Function(FUNC_GETCONSENTINFO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(userID), 
                new org.web3j.abi.datatypes.generated.Bytes32(serviceID), 
                new org.web3j.abi.datatypes.generated.Bytes32(internalID)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Uint8>>() {}, new TypeReference<Uint16>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple3<List<BigInteger>, BigInteger, BigInteger>>(function,
                new Callable<Tuple3<List<BigInteger>, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple3<List<BigInteger>, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<List<BigInteger>, BigInteger, BigInteger>(
                                convertToNative((List<Uint8>) results.get(0).getValue()), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue());
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> storeConsent(byte[] userID, byte[] serviceID, byte[] internalID, List<BigInteger> purposes, BigInteger privacyVersion) {
        final Function function = new Function(
                FUNC_STORECONSENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(userID), 
                new org.web3j.abi.datatypes.generated.Bytes32(serviceID), 
                new org.web3j.abi.datatypes.generated.Bytes32(internalID), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint8>(
                        org.web3j.abi.datatypes.generated.Uint8.class,
                        org.web3j.abi.Utils.typeMap(purposes, org.web3j.abi.datatypes.generated.Uint8.class)), 
                new org.web3j.abi.datatypes.generated.Uint16(privacyVersion)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static PrivacyConsentRegistry load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new PrivacyConsentRegistry(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static PrivacyConsentRegistry load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new PrivacyConsentRegistry(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static PrivacyConsentRegistry load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new PrivacyConsentRegistry(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static PrivacyConsentRegistry load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new PrivacyConsentRegistry(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<PrivacyConsentRegistry> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(PrivacyConsentRegistry.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<PrivacyConsentRegistry> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(PrivacyConsentRegistry.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<PrivacyConsentRegistry> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(PrivacyConsentRegistry.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<PrivacyConsentRegistry> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(PrivacyConsentRegistry.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
