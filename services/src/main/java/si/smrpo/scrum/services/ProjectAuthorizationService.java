package si.smrpo.scrum.services;

import com.mjamsek.rest.exceptions.ForbiddenException;

public interface ProjectAuthorizationService {
    
    /**
     * Checks if user is present in a project, with any role
     * @return <code>true</code> if user is part of a project, <code>false</code> otherwise
     */
    boolean isInProject(String projectId, String userId);
    
    /**
     * Checks if user is present in a project, with any role and throws exception otherwise
     * @throws ForbiddenException if user is not part of a project
     */
    void isInProjectOrThrow(String projectId, String userId) throws ForbiddenException;
    
    /**
     * Checks if user is present in a project and has a member role
     * @return <code>true</code> if user is part of a project and has member role, <code>false</code> otherwise
     */
    boolean isProjectMember(String projectId, String userId);
    
    /**
     * Checks if user is present in a project and has member role, or throws exception otherwise
     * @throws ForbiddenException if user is not part of a project or doesn't have member role
     */
    void isProjectMemberOrThrow(String projectId, String userId) throws ForbiddenException;
    
    /**
     * Checks if user is present in a project and has a product owner role
     * @return <code>true</code> if user is part of a project and has member role, <code>false</code> otherwise
     */
    boolean isProductOwner(String projectId, String userId);
    
    /**
     * Checks if user is present in a project and has product owner role, or throws exception otherwise
     * @throws ForbiddenException if user is not part of a project or doesn't have product owner role
     */
    void isProductOwnerOrThrow(String projectId, String userId) throws ForbiddenException;
    
    /**
     * Checks if user is present in a project and has a scrum master role
     * @return <code>true</code> if user is part of a project and has scrum master role, <code>false</code> otherwise
     */
    boolean isScrumMaster(String projectId, String userId);
    
    /**
     * Checks if user is present in a project and has scrum master role, or throws exception otherwise
     * @throws ForbiddenException if user is not part of a project or doesn't have scrum master role
     */
    void isScrumMasterOrThrow(String projectId, String userId) throws ForbiddenException;
    
    /**
     * Checks if user is present in a project and has a scrum master or product owner role
     * @return <code>true</code> if user is part of a project and has scrum master or product owner role, <code>false</code> otherwise
     */
    boolean isProjectAdmin(String projectId, String userId);
    
    /**
     * Checks if user is present in a project and has scrum master or product owner role, or throws exception otherwise
     * @throws ForbiddenException if user is not part of a project or doesn't have scrum master or product owner role
     */
    void isProjectAdminOrThrow(String projectId, String userId) throws ForbiddenException;
}
