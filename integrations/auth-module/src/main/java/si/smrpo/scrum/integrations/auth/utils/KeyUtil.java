package si.smrpo.scrum.integrations.auth.utils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
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
    
}
