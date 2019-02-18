package xyz.imwyy;

import com.auth0.jwt.internal.org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

/**
 * create by stephen on 2018/5/3
 */
public class Main {

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());

        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();
        Wallet coinBase = new Wallet();

        // create the first transaction
        Transaction genesisTx = new Transaction(coinBase.getPublicKey(), walletA.getPublicKey(), 100f, null);
        genesisTx.generateSignature(coinBase.getPrivateKey());
        TransactionOutput txOutput = new TransactionOutput(genesisTx.getRecipient(), genesisTx.getValue(), genesisTx.getTransactionId());
        genesisTx.getTxOutput().add(txOutput);
        BlockChain.ALL_UTXOs.put(txOutput.getTransactionId(), txOutput);

        System.out.println("Creating and Mining Genesis block... \n");

        Block genesis = new Block("0");
        genesis.addTransaction(genesisTx);
        BlockChain.addBlock(genesis);

        // normal case
        Block block1 = new Block(genesis.getHash());
        System.out.println();
        System.out.println("WalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());
        System.out.println("WalletA is Attempting to send funds 40 to WalletB...");
        block1.addTransaction(walletA.sendMoneyTo(walletB.getPublicKey(), 40f));
        BlockChain.addBlock(block1);
        System.out.println();
        System.out.println("WalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());
        System.out.println();

        // fail case
        Block block2 = new Block(block1.getHash());
        System.out.println("WalletA Attempting to send more funds 1000 than it has...");
        block2.addTransaction(walletA.sendMoneyTo(walletB.getPublicKey(), 1000f));
        BlockChain.addBlock(block2);
        System.out.println();
        System.out.println("WalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());
        System.out.println();

        Block block3 = new Block(block2.getHash());
        System.out.println("WalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.sendMoneyTo(walletA.getPublicKey(), 20));
        System.out.println();
        System.out.println("WalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());
        System.out.println();

        System.out.println("isChainValid: " + BlockChain.isChainValid(genesisTx));
    }
}
