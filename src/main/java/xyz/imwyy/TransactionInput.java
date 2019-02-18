package xyz.imwyy;

/**
 * a transaction's input holds a reference to TransactionOutput
 * create by stephen on 2018/5/3
 */
public class TransactionInput {
    private String transactionId;
    private TransactionOutput moneyToSpend; // money that others transfer to me but i haven't transfer it to others.

    public TransactionInput(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public TransactionOutput getMoneyToSpend() {
        return moneyToSpend;
    }

    public void setMoneyToSpend(TransactionOutput moneyToSpend) {
        this.moneyToSpend = moneyToSpend;
    }
}
