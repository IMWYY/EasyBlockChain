/**
 * 定义区块链的类
 * create by stephen on 2018/5/2
 */
public class Block {

    private String hash;            //当前block的hash值
    private String previousHash;    //前一block的hash值
    private String data;            //当前block的数据
    private long timeStamp;         //当前block的时间戳
    private long nonce;             //记录挖矿时 Hash重算的次数

    public Block(String data, String previousHash) {
        this.previousHash = previousHash;
        this.data = data;
        this.timeStamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

    /**
     * 利用previousHash、timeStamp和data计算hash值
     */
    public String calculateHash() {
        return HashUtil.sha256(
                previousHash +
                        String.valueOf(timeStamp) +
                        String.valueOf(nonce) +
                        data
        );
    }

    /**
     * 挖矿 难度由MainChain.DIFFICULTY决定
     */
    public void mineBlock() {
        // target是一个DIFFICULTY长度的0数组
        String target = new String(new char[MainChain.DIFFICULTY]).replace('\0', '0');

        while (!hash.substring(0, MainChain.DIFFICULTY).equals(target)) {
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

    public String getData() {
        return data;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
