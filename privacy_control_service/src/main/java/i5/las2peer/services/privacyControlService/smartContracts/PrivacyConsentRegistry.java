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
public class PrivacyConsentRegistry extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b50610607806100206000396000f3fe608060405234801561001057600080fd5b50600436106100415760003560e01c80635500556a146100465780638cd913c4146101825780638e570cd01461024b575b600080fd5b610180600480360360a081101561005c57600080fd5b8135916020810135916040820135919081019060808101606082013564010000000081111561008a57600080fd5b82018360208201111561009c57600080fd5b803590602001918460208302840111640100000000831117156100be57600080fd5b919080806020026020016040519081016040528093929190818152602001838360200280828437600092019190915250929594936020810193503591505064010000000081111561010e57600080fd5b82018360208201111561012057600080fd5b8035906020019184602083028401116401000000008311171561014257600080fd5b91908080602002602001604051908101604052809392919081815260200183836020028082843760009201919091525092955061029a945050505050565b005b6101ab6004803603606081101561019857600080fd5b508035906020810135906040013561033e565b604051808060200180602001848152602001838103835286818151815260200191508051906020019060200280838360005b838110156101f55781810151838201526020016101dd565b50505050905001838103825285818151815260200191508051906020019060200280838360005b8381101561023457818101518382015260200161021c565b505050509050019550505050505060405180910390f35b6102746004803603606081101561026157600080fd5b50803590602081013590604001356104a0565b604080519485526020850193909352838301919091526060830152519081900360800190f35b6040805160c081018252868152602080820187815282840187815260608401878152608085018790524260a086015260008b81528085528681208b825285528681208a825285529590952084518155915160018301555160028201559251805192939261030d92600385019201906104d7565b50608082015180516103299160048401916020909101906104d7565b5060a082015181600501559050505050505050565b606080600061034b61057d565b6000878152602081815260408083208984528252808320888452825291829020825160c0810184528154815260018201548184015260028201548185015260038201805485518186028101860190965280865291949293606086019392908301828280156103f657602002820191906000526020600020906000905b825461010083900a900460ff168152602060019283018181049485019490930390920291018084116103c75790505b505050505081526020016004820180548060200260200160405190810160405280929190818152602001828054801561046c57602002820191906000526020600020906000905b825461010083900a900460ff1681526020600192830181810494850194909303909202910180841161043d5790505b5050509183525050600591909101546020909101526060810151608082015160a09092015190999198509650945050505050565b6000602081815293815260408082208552928152828120909352825290208054600182015460028301546005909301549192909184565b82805482825590600052602060002090601f0160209004810192821561056d5791602002820160005b8382111561053e57835183826101000a81548160ff021916908360ff1602179055509260200192600101602081600001049283019260010302610500565b801561056b5782816101000a81549060ff021916905560010160208160000104928301926001030261053e565b505b506105799291506105b1565b5090565b6040805160c0810182526000808252602082018190529181018290526060808201819052608082015260a081019190915290565b6105cf91905b8082111561057957805460ff191681556001016105b7565b9056fea265627a7a72315820be5ad58866be4bcf87102b0a08035b3be95a04abbd5d53d83f5d44748f2bea5d64736f6c63430005110032";

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

    public RemoteFunctionCall<Tuple4<byte[], byte[], byte[], BigInteger>> consentDatabase(byte[] param0, byte[] param1, byte[] param2) {
        final Function function = new Function(FUNC_CONSENTDATABASE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(param0), 
                new org.web3j.abi.datatypes.generated.Bytes32(param1), 
                new org.web3j.abi.datatypes.generated.Bytes32(param2)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple4<byte[], byte[], byte[], BigInteger>>(function,
                new Callable<Tuple4<byte[], byte[], byte[], BigInteger>>() {
                    @Override
                    public Tuple4<byte[], byte[], byte[], BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<byte[], byte[], byte[], BigInteger>(
                                (byte[]) results.get(0).getValue(), 
                                (byte[]) results.get(1).getValue(), 
                                (byte[]) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue());
                    }
                });
    }

    public RemoteFunctionCall<Tuple3<List<BigInteger>, List<BigInteger>, BigInteger>> getConsentInfo(byte[] userID, byte[] serviceID, byte[] courseID) {
        final Function function = new Function(FUNC_GETCONSENTINFO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(userID), 
                new org.web3j.abi.datatypes.generated.Bytes32(serviceID), 
                new org.web3j.abi.datatypes.generated.Bytes32(courseID)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Uint8>>() {}, new TypeReference<DynamicArray<Uint8>>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple3<List<BigInteger>, List<BigInteger>, BigInteger>>(function,
                new Callable<Tuple3<List<BigInteger>, List<BigInteger>, BigInteger>>() {
                    @Override
                    public Tuple3<List<BigInteger>, List<BigInteger>, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<List<BigInteger>, List<BigInteger>, BigInteger>(
                                convertToNative((List<Uint8>) results.get(0).getValue()), 
                                convertToNative((List<Uint8>) results.get(1).getValue()), 
                                (BigInteger) results.get(2).getValue());
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> storeConsent(byte[] userID, byte[] serviceID, byte[] courseID, List<BigInteger> purposes, List<BigInteger> purposeVersions) {
        final Function function = new Function(
                FUNC_STORECONSENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(userID), 
                new org.web3j.abi.datatypes.generated.Bytes32(serviceID), 
                new org.web3j.abi.datatypes.generated.Bytes32(courseID), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint8>(
                        org.web3j.abi.datatypes.generated.Uint8.class,
                        org.web3j.abi.Utils.typeMap(purposes, org.web3j.abi.datatypes.generated.Uint8.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint8>(
                        org.web3j.abi.datatypes.generated.Uint8.class,
                        org.web3j.abi.Utils.typeMap(purposeVersions, org.web3j.abi.datatypes.generated.Uint8.class))), 
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
