package xyz.imwyy;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

/**
 * 交易信息的实体类
 * create by stephen on 2018/5/3
 */
public class Transaction {
    public String transactionId;    // 交易的hash值
    public PublicKey sender;        // 发送方地址即发送人公钥
    public PublicKey reciepient;    // 接受方地址即接受人公钥
    public float value;             // 交易的金额
    public byte[] signature;        // 签名用来保证只有货币的拥有者才可以用来发送自己的货币，阻止其他人试图篡改提交的交易。

    public ArrayList<TransactionToOthers> toOthers = new ArrayList<>();
    public ArrayList<TransactionToMe> toMes = new ArrayList<>();

    private static int sequence = 0; // 一个计数器 记录交易生成的次数

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionToOthers> ToOthers) {
        this.sender = from;
        this.reciepient = to;
        this.value = value;
        this.toOthers = ToOthers;
    }


    /**
     * 处理交易 如果新的交易可以被创建返回true
     *
     * @return 如果新的交易可以被创建返回true
     */
    public boolean processTransaction() {
        if (!verifiySignature()) {
            System.out.println("Error! Transaction Signature failed to verify");
            return false;
        }

        // 将所有的TransactionToOthers利用BitCoin.UTXOs和transactionToMe关联
        for (TransactionToOthers i : toOthers) {
            i.UTXO = BitChain.UTXOs.get(i.transactionToMeId);
        }

        //检查交易是否合法 交易额不得小于minimumTransaction
        if (getTransToOthersValue() < BitChain.minimumTransaction) {
            System.out.println("Error! Transaction Inputs to small: " + getTransToOthersValue());
            return false;
        }

        //生成TransactionOutput
        float leftOver = getTransToOthersValue() - value; //扣除交易value后的余额
        transactionId = calulateHash();
        toMes.add(new TransactionToMe(this.reciepient, value, transactionId)); //接受者增加value
        toMes.add(new TransactionToMe(this.sender, leftOver, transactionId));  //发送者减少value

        //把所有的output加入UTXOs
        for (TransactionToMe o : toMes) {
            BitChain.UTXOs.put(o.id, o);
        }

        //将已经花费的transaction从UTXO中移除
        for (TransactionToOthers i : toOthers) {
            if (i.UTXO == null) continue;
            BitChain.UTXOs.remove(i.UTXO.id);
        }

        return true;
    }

    /**
     * 获得转给别人的coin总额
     *
     * @return 转给别人的coin总额
     */
    public float getTransToOthersValue() {
        float total = 0;
        for (TransactionToOthers i : toOthers) {
            if (i.UTXO == null) continue;
            total += i.UTXO.value;
        }
        return total;
    }

    /**
     * 获得所有TransactionOutput的coin和
     *
     * @return 所有TransactionOutput的coin和
     */
    public float getTransToMeValue() {
        float total = 0;
        for (TransactionToMe o : toMes) {
            total += o.value;
        }
        return total;
    }

    /**
     * 计算Transaction的hash 用作Transaction的id
     *
     * @return hash值
     */
    private String calulateHash() {
        sequence++; // 加入sequence因子 减少hash碰撞
        return Util.sha256(
                Util.getStringFromKey(sender) +
                        Util.getStringFromKey(reciepient) +
                        Float.toString(value) + sequence
        );
    }


    /**
     * 将交易中所有不想被篡改的信息加密为签名
     *
     * @param privateKey 私钥
     */
    public void generateSignature(PrivateKey privateKey) {
        String data = Util.getStringFromKey(sender) + Util.getStringFromKey(reciepient) + Float.toString(value);
        signature = Util.ECDSASignature(privateKey, data);
    }

    /**
     * 验证信息是否被篡改（是否有效）
     * 签名将由矿工验证，只有签名验证成功后交易才能被添加到区块中去。
     */
    public boolean verifiySignature() {
        String data = Util.getStringFromKey(sender) + Util.getStringFromKey(reciepient) + Float.toString(value);
        return Util.verifyECDSASignature(sender, data, signature);
    }

    public ArrayList<TransactionToOthers> getToOthers() {
        return toOthers;
    }

    public ArrayList<TransactionToMe> getToMes() {
        return toMes;
    }

    public PublicKey getReciepient() {
        return reciepient;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public PublicKey getSender() {
        return sender;
    }

    public float getValue() {
        return value;
    }
}
