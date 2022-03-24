package si.smrpo.scrum.persistence.users;

import si.smrpo.scrum.lib.enums.SimpleStatus;
import si.smrpo.scrum.persistence.BaseEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "IDX_USERS_USERNAME_UNIQUE", columnList = "username", unique = true)
})
@NamedQueries({
    @NamedQuery(name = UserEntity.GET_BY_USERNAME, query = "SELECT u FROM UserEntity u WHERE u.username = :username"),
    @NamedQuery(name = UserEntity.GET_BY_EMAIL, query = "SELECT u FROM UserEntity u WHERE LOWER(TRIM(BOTH FROM u.email)) = :email")
})
public class UserEntity extends BaseEntity {
    
    public static final String GET_BY_USERNAME = "UserEntity.getByUsername";
    public static final String GET_BY_EMAIL = "UserEntity.getByEmail";
    
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "email", nullable = false)
    private String email;
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "avatar")
    private String avatar;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SimpleStatus status;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_login_at")
    private Date lastLoginAt;
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public Date getLastLoginAt() {
        return lastLoginAt;
    }
    
    public void setLastLoginAt(Date lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
    
    public SimpleStatus getStatus() {
        return status;
    }
    
    public void setStatus(SimpleStatus status) {
        this.status = status;
    }
}
