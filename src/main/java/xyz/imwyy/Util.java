package xyz.imwyy;

import java.security.*;
import java.util.ArrayList;
import java.util.Base64;

/**
 * hash计算、加密等的工具类
 * create by stephen on 2018/5/2
 */
public class Util {

    /**
     * 以字符串的形式计算Sha256的值
     *
     * @param input 输入
     * @return Sha256的值
     */
    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder(); // 十六进制保存hash值
            for (byte aHash : hash) {
                String hex = Integer.toHexString(0xff & aHash);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取Merkle Tree的根节点
     * @param transactions 交易列表
     * @return Merkle Tree的根节点
     */
    public static String getMerkleRoot(ArrayList<Transaction> transactions) {
        int count = transactions.size();
        ArrayList<String> previousTreeLayer = new ArrayList<>();
        for (Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.transactionId);
        }
        ArrayList<String> treeLayer = previousTreeLayer;
        while (count > 1) {
            treeLayer = new ArrayList<>();
            for (int i = 1; i < previousTreeLayer.size(); i++) {
                treeLayer.add(sha256(previousTreeLayer.get(i - 1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }
        return (treeLayer.size() == 1) ? treeLayer.get(0) : "";
    }

    /**
     * 根据私钥和需要加密的数据获取签名信息
     *
     * @param privateKey 私钥
     * @param input      需要加密的数据
     * @return 签名信息
     */
    public static byte[] ECDSASignature(PrivateKey privateKey, String input) {
        Signature dsa;
        byte[] output;
        try {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            output = dsa.sign();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return output;
    }

    /**
     * 根据公钥判断当前签名是否有效果
     *
     * @param publicKey 公钥
     * @param data      加密的数据
     * @param signature 签名信息
     * @return true有效 false无效
     */
    public static boolean verifyECDSASignature(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature instance = Signature.getInstance("ECDSA", "BC");
            instance.initVerify(publicKey);
            instance.update(data.getBytes());
            return instance.verify(signature);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取任何key的编码字符串
     *
     * @param key key
     * @return 编码字符串
     */
    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
}
