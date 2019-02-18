package xyz.imwyy;

import java.util.ArrayList;

/**
 * create by stephen on 2018/5/2
 */
public class Block {

    private String hash;
    private String previousHash;
    private long timeStamp;
    private long nonce;             //used in mining
    private ArrayList<Transaction> transactions = new ArrayList<>();    // transactions that this block hold
    private String merkleRoot;


    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return Util.sha256(
                previousHash +
                        String.valueOf(timeStamp) +
                        String.valueOf(nonce) +
                        merkleRoot
        );
    }

    public boolean addTransaction(Transaction transaction) {
        //process transaction and check if valid, unless block is genesis block then ignore.
        if (transaction == null) return false;
        if (!"0".equals(previousHash)) {
            if ((!transaction.processTransaction())) {
                System.out.println("Error! Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
        return true;
    }

    public void mineBlock(int difficulty) {
        merkleRoot = Util.getMerkleRoot(transactions);
        // target is a array starting with n zeros
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined: " + hash + " Calculate times: " + nonce);
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }
}
