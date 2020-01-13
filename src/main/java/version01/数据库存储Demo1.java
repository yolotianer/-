package version01;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author yolo
 * @date 2020/1/13-20:25
 * SHA-256
 */
public class 数据库存储Demo1 {
    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest=MessageDigest.getInstance("SHA-256");
        String s="你好世界";
        byte[] bytes = s.getBytes("UTF-8");
        messageDigest.update(bytes);
        byte[] result = messageDigest.digest();
        System.out.println(result.length);
        for (byte b : result) {
            System.out.printf("%02x",b);
        }
        System.out.println();
    }
}
