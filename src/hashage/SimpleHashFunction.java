package hashage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SimpleHashFunction {
    public static String hashString(String input, String type) {
        try {
            MessageDigest md = MessageDigest.getInstance(type); // MD2, MD5, SHA-1, SHA-224
            byte[] hash = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
