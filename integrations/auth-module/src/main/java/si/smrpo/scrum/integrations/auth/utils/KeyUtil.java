package si.smrpo.scrum.integrations.auth.utils;

import com.mjamsek.rest.exceptions.RestException;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyUtil {
    
    private KeyUtil() {
    
    }
    
    public static String keyToString(Key key) {
        return keyToString(key.getEncoded());
    }
    
    public static String keyToString(byte[] key) {
        return new String(Base64.getEncoder().encode(key));
    }
    
    public static byte[] stringifiedKeyToBytes(String key) {
        return Base64.getDecoder().decode(key.getBytes(StandardCharsets.UTF_8));
    }
    
    public static PrivateKey loadPrivateKey(String privateKey, KeyFactory keyFactory) {
        try {
            byte[] privKeyBytes = KeyUtil.stringifiedKeyToBytes(privateKey);
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privKeyBytes);
            return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException("Invalid key specification!");
        }
    }
    
    public static PublicKey loadPublicKey(String publicKey, KeyFactory keyFactory) {
        try {
            byte[] pubKeyBytes = KeyUtil.stringifiedKeyToBytes(publicKey);
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(pubKeyBytes);
            return keyFactory.generatePublic(x509EncodedKeySpec);
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException("Invalid key specification!");
        }
    }
    
    public static String getRandomString(int length) {
        final int LEFT_LIMIT = 48; // char '0'
        final int RIGHT_LIMIT = 57; // char '9'
    
        SecureRandom random = new SecureRandom();
        
        return random.ints(LEFT_LIMIT, RIGHT_LIMIT + 1)
            .limit(length)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    }
    
    public static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            String base = Base64.getUrlEncoder().encodeToString(hash)
                .replaceAll("=", "")
                .replaceAll("/", "_")
                .replaceAll("\\+", "-");
            return base;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RestException("error.server");
        }
    }
}
