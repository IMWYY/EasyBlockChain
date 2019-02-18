package xyz.imwyy;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * For each Transaction,there will be either a single input from a larger previous transaction or
 * multiple inputs combining smaller amounts, and at most two outputs: one for the payment,
 * and one returning the change, if any, back to the sender
 * create by stephen on 2018/5/3
 */
public class Transaction {

    private PublicKey recipient;
    private byte[] signature;       // sender uses his private key to generate this signature.
    // others can verify the transaction using sender's public key

    private String transactionId;    // hash of the transaction
    private float value;

    private PublicKey sender;

    private ArrayList<TransactionInput> txInput;
    private ArrayList<TransactionOutput> txOutput;
    private static int sequence = 0;

    /**
     * Each owner transfers the coin to the next by digitally signing a hash of the previous transaction
     * and the public key of the next owner and adding these to the end of the coin.
     * @param from    sender's public key
     * @param to      recipient's public key
     * @param value   money to transfer
     * @param txInput input transactions
     */
    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> txInput) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.txInput = txInput;
        this.txOutput = new ArrayList<>();
    }

    /**
     * For each transaction, it generates at most two outputs:
     * one for the payment,
     * and one returning the change, if any, back to the sender
     */
    public boolean processTransaction() {
        if (!verifySignature()) {
            System.out.println("Error! Transaction Signature failed to verify");
            return false;
        }

        for (TransactionInput i : txInput) {
            i.setMoneyToSpend(BlockChain.ALL_UTXOs.get(i.getTransactionId()));
        }

        if (getTransInputTotalMoney() < BlockChain.MIN_TRANSACTION_MONEY) {
            System.out.println("Error! Transaction Inputs to small: " + getTransInputTotalMoney());
            return false;
        }

        //generate TransactionOutput
        float leftOver = getTransInputTotalMoney() - value;
        this.transactionId = calculateHash();
        this.txOutput.add(new TransactionOutput(this.recipient, value, transactionId));
        if (leftOver > 0) {
            this.txOutput.add(new TransactionOutput(this.sender, leftOver, transactionId));
        }

        // add new output to global UTXO
        for (TransactionOutput o : this.txOutput) {
            BlockChain.ALL_UTXOs.put(o.getTransactionId(), o);
        }

        // remove spent money from global UTXO
        for (TransactionInput i : txInput) {
            if (i.getMoneyToSpend() == null) continue;
            BlockChain.ALL_UTXOs.remove(i.getMoneyToSpend().getTransactionId());
        }
        return true;
    }

    public float getTransInputTotalMoney() {
        float total = 0;
        for (TransactionInput i : txInput) {
            if (i.getMoneyToSpend() == null) continue;
            total += i.getMoneyToSpend().getValue();
        }
        return total;
    }

    public float getTransOutputTotalMoney() {
        float total = 0;
        for (TransactionOutput i : txOutput) {
            total += i.getValue();
        }
        return total;
    }

    private String calculateHash() {
        return Util.sha256(
                Util.getStringFromKey(sender)
                        + Util.getStringFromKey(recipient)
                        + Float.toString(value)
                        + Arrays.toString(signature) +
                        + sequence++
        );
    }

    /**
     * sender use this method to generate signature
     */
    public void generateSignature(PrivateKey privateKey) {
        String data = Util.getStringFromKey(sender)
                + Util.getStringFromKey(recipient)
                + Float.toString(value);
        this.signature = Util.ECDSASignature(privateKey, data);
    }

    /**
     * A payee can use sender's public key verify the signatures to verify the chain of ownership
     */
    public boolean verifySignature() {
        String data = Util.getStringFromKey(sender) + Util.getStringFromKey(recipient) + Float.toString(value);
        return Util.verifyECDSASignature(sender, data, signature);
    }


    public ArrayList<TransactionOutput> getTxOutput() {
        return txOutput;
    }

    public float getValue() {
        return value;
    }

    public PublicKey getRecipient() {
        return recipient;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public ArrayList<TransactionInput> getTxInput() {
        return txInput;
    }

    public PublicKey getSender() {
        return sender;
    }
}
