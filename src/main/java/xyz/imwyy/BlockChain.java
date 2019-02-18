package xyz.imwyy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * create by stephen on 2018/5/2
 */
class BlockChain {

    public static final int DIFFICULTY = 5;   // difficulty of proof-of-work
    public static float MIN_TRANSACTION_MONEY = 0.1f;
    public static List<Block> BLOCK_CHAIN = new ArrayList<>();
    public static HashMap<String, TransactionOutput> ALL_UTXOs = new HashMap<>(); // all user's all unspent money

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(DIFFICULTY);
        BLOCK_CHAIN.add(newBlock);
    }

    public static Boolean isChainValid(Transaction genesisTX) {
        Block currentBlock;
        Block previousBlock;
        String hashPrefix = new String(new char[DIFFICULTY]).replace('\0', '0');
        HashMap<String, TransactionOutput> tempUTXOs = new HashMap<>();
        //a temporary working list of unspent transactions at a given block state.
        tempUTXOs.put(genesisTX.getTxOutput().get(0).getTransactionId(), genesisTX.getTxOutput().get(0));

        //loop through blockchain to check hashes:
        for (int i = 1; i < BLOCK_CHAIN.size(); i++) {
            currentBlock = BLOCK_CHAIN.get(i);
            previousBlock = BLOCK_CHAIN.get(i - 1);

            //compare registered hash and calculated hash:
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                System.out.println("Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
            //check if hash is solved
            if (!currentBlock.getHash().substring(0, DIFFICULTY).equals(hashPrefix)) {
                System.out.println("#his block hasn't been mined");
                return false;
            }

            //loop through transactions:
            TransactionOutput tempOutput;
            for (int t = 0; t < currentBlock.getTransactions().size(); t++) {
                Transaction currentTransaction = currentBlock.getTransactions().get(t);

                if (!currentTransaction.verifySignature()) {
                    System.out.println("#Signature on Transaction(" + t + ") is Invalid");
                    return false;
                }
                if (currentTransaction.getTransInputTotalMoney() != currentTransaction.getTransOutputTotalMoney()) {
                    System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                for (TransactionInput input : currentTransaction.getTxInput()) {
                    tempOutput = tempUTXOs.get(input.getTransactionId());

                    if (tempOutput == null) {
                        System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
                        return false;
                    }

                    if (input.getMoneyToSpend().getValue() != tempOutput.getValue()) {
                        System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.getTransactionId());
                }

                for (TransactionOutput output : currentTransaction.getTxOutput()) {
                    tempUTXOs.put(output.getTransactionId(), output);
                }

                if (currentTransaction.getTxOutput().get(0).getOwner() != currentTransaction.getRecipient()) {
                    System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
                    return false;
                }
                if (currentTransaction.getTxOutput().size() > 1 &&
                        currentTransaction.getTxOutput().get(1).getOwner() != currentTransaction.getSender()) {
                    System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
                    return false;
                }
            }
        }
        System.out.println("Blockchain is valid");
        return true;
    }
}
