package si.smrpo.scrum.persistence.auth;

import si.smrpo.scrum.persistence.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "signing_keys")
@NamedQueries({
    @NamedQuery(name = SigningKeyEntity.CHECK_KEY_EXISTS, query = "SELECT COUNT(k) FROM SigningKeyEntity k"),
    @NamedQuery(name = SigningKeyEntity.REMOVE_KEYS, query = "DELETE FROM SigningKeyEntity")
})
public class SigningKeyEntity extends BaseEntity {
    
    public static final String CHECK_KEY_EXISTS = "SigningKeyEntity.checkKeyExists";
    public static final String REMOVE_KEYS = "SigningKeyEntity.removeKeys";

    @Column(name = "algorithm", nullable = false)
    private String algorithm;
    
    @Column(name = "private_key", columnDefinition = "TEXT", nullable = false)
    private String privateKey;
    
    @Column(name = "public_key", columnDefinition = "TEXT", nullable = false)
    private String publicKey;
    
    public String getAlgorithm() {
        return algorithm;
    }
    
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
    
    public String getPrivateKey() {
        return privateKey;
    }
    
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
    
    public String getPublicKey() {
        return publicKey;
    }
    
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
