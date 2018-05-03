package xyz.imwyy;

import java.util.ArrayList;

/**
 * 定义区块链的类
 * create by stephen on 2018/5/2
 */
public class Block {

    private String hash;            //当前block的hash值
    private String previousHash;    //前一block的hash值
    private long timeStamp;         //当前block的时间戳
    private long nonce;             //记录挖矿时 Hash重算的次数
    private ArrayList<Transaction> transactions = new ArrayList<>();    //当前block的交易数据
    private String merkleRoot;      //Merkle Tree的根节点


    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

    /**
     * 利用previousHash、timeStamp、data和nonce计算hash值
     */
    public String calculateHash() {
        return Util.sha256(
                previousHash +
                        String.valueOf(timeStamp) +
                        String.valueOf(nonce) +
                        merkleRoot
        );
    }

    /**
     * 向这个区块添加交易记录
     * @param transaction 交易记录
     */
    public boolean addTransaction(Transaction transaction) {
        //process transaction and check if valid, unless block is genesis block then ignore.
        if (transaction == null) return false;
        if ((previousHash != "0")) {
            if ((!transaction.processTransaction())) {
                System.out.println("Error! Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
        return true;
    }

    /**
     * 挖矿 难度由MainChain.DIFFICULTY决定
     */
    public void mineBlock(int difficulty) {
        merkleRoot = Util.getMerkleRoot(transactions);
        // target是一个DIFFICULTY长度的0数组
        String target = new String(new char[difficulty]).replace('\0', '0');

        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;        //记录计算hash的次数
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
