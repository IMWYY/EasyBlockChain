import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * create by stephen on 2018/5/2
 */
public class MainChain {

    public static final int DIFFICULTY = 5;   //挖矿的难度

    public static List<Block> blockChain = new ArrayList<>();

    public static void main(String[] args) {
        blockChain.add(new Block("this is the first block", "0"));
        System.out.println("Trying to Mine block 1... ");
        blockChain.get(blockChain.size()-1).mineBlock();

        blockChain.add(new Block("this is the second block", blockChain.get(blockChain.size() - 1).getHash()));
        System.out.println("Trying to Mine block 2... ");
        blockChain.get(blockChain.size()-1).mineBlock();

        blockChain.add(new Block("this is the third block", blockChain.get(blockChain.size() - 1).getHash()));
        System.out.println("Trying to Mine block 3... ");
        blockChain.get(blockChain.size()-1).mineBlock();

        System.out.println("isChainValid : " + isChainValid(blockChain));

        String blockChainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockChain);
        System.out.println(blockChainJson);
    }


    /**
     * 检查区块链是否合法
     * 包括检查previousHash是否和前一个block的hash相等 当前block的hash是否计算正确
     */
    public static boolean isChainValid(List<Block> blocks) {
        Block currentBlock;
        Block previousBlock;

        for (int i = 1; i < blocks.size(); i++) {
            currentBlock = blocks.get(i);
            previousBlock = blocks.get(i - 1);

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


}
