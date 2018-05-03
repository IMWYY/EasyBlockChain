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

        Transaction firstTX = new Transaction(coinBase.getPublicKey(), walletA.getPublicKey(), 100f, null);
        firstTX.generateSignature(coinBase.getPrivateKey());
        firstTX.transactionId = "0";
        firstTX.getToMes().add(new TransactionToMe(firstTX.reciepient, firstTX.value, firstTX.transactionId));
        BitChain.UTXOs.put(firstTX.getToMes().get(0).id, firstTX.getToMes().get(0)); //把transaction保存在UTXOs


        System.out.println("Creating and Mining Genesis block... ");

        Block genesis = new Block("0");
        genesis.addTransaction(firstTX);
        BitChain.addBlock(genesis);

        Block block1 = new Block(genesis.getHash());
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds 40 to WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 40f));
        BitChain.addBlock(block1);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        // 交易失败的例子
        Block block2 = new Block(block1.getHash());
        System.out.println("\nWalletA Attempting to send more funds 1000 than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 1000f));
        BitChain.addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block3 = new Block(block2.getHash());
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.sendFunds(walletA.getPublicKey(), 20));
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        System.out.println("\nisChainValid: " + BitChain.isChainValid());
    }
}
