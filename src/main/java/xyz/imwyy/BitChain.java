package xyz.imwyy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 模拟比特币
 * create by stephen on 2018/5/2
 */
public class BitChain {

    public static final int DIFFICULTY = 5;   //挖矿的难度
    public static float minimumTransaction = 0.1f;
    public static List<Block> blockChain = new ArrayList<>();
    public static HashMap<String, TransactionToMe> UTXOs = new HashMap<>(); //所有用户的没有被花掉的Transaction列表
    public static Transaction genesisTransaction;


    /**
     * 检查区块链是否合法
     * 包括检查previousHash是否和前一个block的hash相等 当前block的hash是否计算正确
     */
    public static boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;

        for (int i = 1; i < blockChain.size(); i++) {
            currentBlock = blockChain.get(i);
            previousBlock = blockChain.get(i - 1);

            // 当前block的hash是否计算正确
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                System.out.println("Current Hashes not equal!");
                return false;
            }

            // 检查previousHash是否和前一个block的hash相等
            if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
                System.out.println("Previous Hashes not equal!");
                return false;
            }
        }
        return true;
    }


    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(DIFFICULTY);
        blockChain.add(newBlock);
    }


//    public static Boolean isChainValid() {
//        Block currentBlock;
//        Block previousBlock;
//        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
//        HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>(); //a temporary working list of unspent transactions at a given block state.
//        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
//
//        //loop through blockchain to check hashes:
//        for(int i=1; i < blockchain.size(); i++) {
//
//            currentBlock = blockchain.get(i);
//            previousBlock = blockchain.get(i-1);
//            //compare registered hash and calculated hash:
//            if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
//                System.out.println("#Current Hashes not equal");
//                return false;
//            }
//            //compare previous hash and registered previous hash
//            if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
//                System.out.println("#Previous Hashes not equal");
//                return false;
//            }
//            //check if hash is solved
//            if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
//                System.out.println("#This block hasn't been mined");
//                return false;
//            }
//
//            //loop thru blockchains transactions:
//            TransactionOutput tempOutput;
//            for(int t=0; t <currentBlock.transactions.size(); t++) {
//                Transaction currentTransaction = currentBlock.transactions.get(t);
//
//                if(!currentTransaction.verifiySignature()) {
//                    System.out.println("#Signature on Transaction(" + t + ") is Invalid");
//                    return false;
//                }
//                if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
//                    System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
//                    return false;
//                }
//
//                for(TransactionInput input: currentTransaction.inputs) {
//                    tempOutput = tempUTXOs.get(input.transactionOutputId);
//
//                    if(tempOutput == null) {
//                        System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
//                        return false;
//                    }
//
//                    if(input.UTXO.value != tempOutput.value) {
//                        System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
//                        return false;
//                    }
//
//                    tempUTXOs.remove(input.transactionOutputId);
//                }
//
//                for(TransactionOutput output: currentTransaction.outputs) {
//                    tempUTXOs.put(output.id, output);
//                }
//
//                if( currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
//                    System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
//                    return false;
//                }
//                if( currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
//                    System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
//                    return false;
//                }
//
//            }
//
//        }
//        System.out.println("Blockchain is valid");
//        return true;
//    }

}
