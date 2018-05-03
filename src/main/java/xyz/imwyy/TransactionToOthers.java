package xyz.imwyy;

/**
 * 转给他人的记录 可以理解为支出 引用尚未使用的TransactionToMe
 * transactionToMeId将用于查找相关的TransactionToMe，允许矿工检查您的所有权。
 * create by stephen on 2018/5/3
 */
public class TransactionToOthers {
    public String transactionToMeId;  //用于查找相关的TransactionOutput
    public TransactionToMe UTXO;        //包含没有花的transaction，即别人转给我但是我没有转给别人的

    public TransactionToOthers(String transactionToMeId) {
        this.transactionToMeId = transactionToMeId;
    }


}
