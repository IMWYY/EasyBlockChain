package xyz.imwyy;

import java.security.PublicKey;

/**
 * 转给我的金额 可以看作为收入
 * create by stephen on 2018/5/3
 */
public class TransactionToMe {
    public String id;
    public PublicKey owner;             //交易中这些coin的所有者
    public float value;                 //coin的数量
    public String parentTransactionId;  //表示创建这条转账的交易id


    public TransactionToMe(PublicKey owner, float value, String parentTransactionId) {
        this.owner = owner;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = Util.sha256(Util.getStringFromKey(owner) + Float.toString(value) + parentTransactionId);
    }

    /**
     * 检查这个coin是不是自己的
     *
     * @param publicKey 公钥
     */
    public boolean isMine(PublicKey publicKey) {
        return (publicKey == owner);
    }

    public String getParentTransactionId() {
        return parentTransactionId;
    }
}
