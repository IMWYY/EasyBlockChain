package xyz.imwyy;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * create by stephen on 2018/5/3
 */
public class Wallet {
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private HashMap<String, TransactionOutput> myUTXOs = new HashMap<>();

    public Wallet() {
        generateKeyPair();
    }

    public void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            keyGen.initialize(ecSpec, random);
            KeyPair keyPair = keyGen.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Transaction sendMoneyTo(PublicKey recipient, float value) {
        if (getBalance() < value) {
            System.out.println("Error! Not Enough funds to send transaction. Transaction Discarded.");
            return null;
        }

        ArrayList<TransactionInput> inputs = new ArrayList<>();
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item : myUTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            total += UTXO.getValue();
            inputs.add(new TransactionInput(UTXO.getTransactionId()));
            if (total > value) break;
        }

        Transaction newTransaction = new Transaction(this.publicKey, recipient, value, inputs);
        newTransaction.generateSignature(privateKey);

        // remove the money from your wallet
        for (TransactionInput input : inputs) {
            myUTXOs.remove(input.getTransactionId());
        }
        return newTransaction;
    }

    public float getBalance() {
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item : BlockChain.ALL_UTXOs.entrySet()) {
            TransactionOutput utxo = item.getValue();
            if (utxo.isMine(publicKey)) {
                myUTXOs.put(utxo.getTransactionId(), utxo);
                total += utxo.getValue();
            }
        }
        return total;
    }


    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
