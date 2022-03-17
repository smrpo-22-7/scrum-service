package si.smrpo.scrum.api.endpoints.examples;

public class UserExamples {
    
    public static final String USER_LIST_RESPONSE = "[ { \"username\": \"janezn\", \"firstName\": \"Janez\", \"lastName\": \"Novak\", \"email\": \"janez@mail.com\", \"phoneNumber\": \"+38641555666\", \"avatar\": \"https://www.images.com/janezn\", \"status\": \"ACTIVE\" } ]";
    public static final String USER_RESPONSE = "{ \"username\": \"janezn\", \"firstName\": \"Janez\", \"lastName\": \"Novak\", \"email\": \"janez@mail.com\", \"phoneNumber\": \"+38641555666\", \"avatar\": \"https://www.images.com/janezn\", \"status\": \"ACTIVE\", \"grantedRoles\": [\"user\"] }";
    
    public static final String USER_REGISTER_REQUEST = "{ \"username\": \"janezn\", \"password\": \"password123\", \"firstName\": \"Janez\", \"lastName\": \"Novak\", \"email\": \"janez@mail.com\", \"grantedRoles\": [\"admin\", \"user\"] }";
    public static final String USERNAME_CHECK_REQUEST = "{ \"username\": \"janezn\" }";
    public static final String USER_PROFILE_RESPONSE = "{ \"id\": \"026d8518-7b54-4574-961b-6b3ab5b3dbb9\", \"username\": \"janezn\", \"firstName\": \"Janez\", \"lastName\": \"Novak\", \"email\": \"janez@mail.com\", \"phoneNumber\": \"+38641555666\" }";
    public static final String CHANGE_PASSWORD_REQUEST = "{ \"password\": \"geslo123\", \"newPassword\": \"geslo456\" }";
}
