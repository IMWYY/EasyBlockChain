package xyz.imwyy;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 钱包的实体类
 * 搜集余额（通过循环遍历UTXO列表来检查交易的输出是否是我的）并创建交易
 * 可以随时为钱包添加一些其他功能，例如记录您的交易历史记录。
 * <p>
 * create by stephen on 2018/5/3
 */
public class Wallet {
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public HashMap<String, TransactionToMe> UTXOs = new HashMap<>(); //这个钱包所拥有的UTXOs


    public Wallet() {
        generateKeyPair();
    }

    /**
     * 生成私钥和公钥
     */
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

    /**
     * 从这个钱包中生成并返回一个新交易
     * @param recipient 接受者
     * @param value 交易金额
     * @return Transaction
     */
    public Transaction sendFunds(PublicKey recipient, float value) {
        if (getBalance() < value) { //检查余额是否充足
            System.out.println("Error! Not Enough funds to send transaction. Transaction Discarded.");
            return null;
        }

        // 拿到满足value的TransactionInput
        ArrayList<TransactionToOthers> inputs = new ArrayList<>();
        float total = 0;
        for (Map.Entry<String, TransactionToMe> item : UTXOs.entrySet()) {
            TransactionToMe UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionToOthers(UTXO.id));
            if (total > value) break;
        }

        Transaction newTransaction = new Transaction(publicKey, recipient, value, inputs);
        newTransaction.generateSignature(privateKey);

        // 从自己钱包里UTXOs移除掉已经花掉的coin
        for (TransactionToOthers input : inputs) {
            UTXOs.remove(input.transactionToMeId);
        }
        return newTransaction;
    }

    /**
     * 遍历BitCoin中所有用户的UTXOs 保存属于自己的那部分
     *
     * @return 返回钱包的余额
     */
    public float getBalance() {
        float total = 0;
        for (Map.Entry<String, TransactionToMe> item : BitChain.UTXOs.entrySet()) {
            TransactionToMe UTXO = item.getValue();
            if (UTXO.isMine(publicKey)) { //如果coin属于我 就把它加入到自己wallet的UTXOs
                UTXOs.put(UTXO.id, UTXO);
                total += UTXO.value;
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
