package xyz.imwyy;

import java.security.PublicKey;

/**
 * TransactionOutput holds money that has not spent
 * and those output can be use as TransactionInput.
 * create by stephen on 2018/5/3
 */
public class TransactionOutput {
    private String transactionId;
    private PublicKey owner;
    private float value;
    private String parentTransactionId;


    public TransactionOutput(PublicKey owner, float value, String parentTransactionId) {
        this.owner = owner;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.transactionId = Util.sha256(Util.getStringFromKey(owner) + Float.toString(value) + parentTransactionId);
    }

    public boolean isMine(PublicKey publicKey) {
        return (publicKey == owner);
    }

    public float getValue() {
        return value;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public PublicKey getOwner() {
        return owner;
    }
}
