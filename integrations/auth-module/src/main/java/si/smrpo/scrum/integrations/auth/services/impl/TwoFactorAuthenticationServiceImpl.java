package si.smrpo.scrum.integrations.auth.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.rest.exceptions.RestException;
import com.mjamsek.rest.utils.DatetimeUtil;
import si.smrpo.scrum.integrations.auth.config.AuthConfig;
import si.smrpo.scrum.integrations.auth.services.TwoFactorAuthenticationService;
import si.smrpo.scrum.integrations.auth.utils.HttpUtil;
import si.smrpo.scrum.integrations.auth.utils.KeyUtil;
import si.smrpo.scrum.integrations.messaging.Message;
import si.smrpo.scrum.integrations.messaging.MessagingException;
import si.smrpo.scrum.integrations.messaging.MessagingService;
import si.smrpo.scrum.integrations.messaging.email.EmailMessageBuilder;
import si.smrpo.scrum.integrations.templating.TemplatingService;
import si.smrpo.scrum.persistence.auth.SessionEntity;
import si.smrpo.scrum.persistence.auth.TwoFactorCheckEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static si.smrpo.scrum.integrations.auth.AuthConstants.*;
import static si.smrpo.scrum.integrations.auth.ServletConstants.TWO_FA_SERVLET;

@RequestScoped
public class TwoFactorAuthenticationServiceImpl implements TwoFactorAuthenticationService {
    
    public static final Logger LOG = LogManager.getLogger(TwoFactorAuthenticationServiceImpl.class.getName());
    
    @Inject
    private EntityManager em;
    
    @Inject
    private MessagingService messagingService;
    
    @Inject
    private TemplatingService templatingService;
    
    @Inject
    private AuthConfig authConfig;
    
    @Override
    public Optional<TwoFactorCheckEntity> getByCode(String code) {
        TypedQuery<TwoFactorCheckEntity> query = em.createNamedQuery(TwoFactorCheckEntity.GET_BY_CODE, TwoFactorCheckEntity.class);
        query.setParameter("code", code);
    
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public void create2FAChallenge(SessionEntity session, Map<String, Object> params) {
        TwoFactorCheckEntity entity = new TwoFactorCheckEntity();
        entity.setSession(session);
        entity.setExpirationDate(DatetimeUtil.getMinutesAfterNow(10));
        String code = KeyUtil.getRandomString(6);
        String challenge = KeyUtil.sha256(createCodeVerifier(code, session.getId()));
        entity.setVerificationCode(code);
        
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            
            Map<String, Object> templateParams = new HashMap<>();
            templateParams.put("code", code);
            templateParams.put("verifyUrl", generateVerifyUrl(code, challenge, params));
            String htmlContent = templatingService.renderHtml("2fa-email", templateParams);
            
            Message message = EmailMessageBuilder.newBuilder()
                .subject("Verify your login")
                .recipient(session.getUser().getEmail())
                .content(htmlContent)
                .build();
    
            CompletableFuture.runAsync(() -> {
                messagingService.sendMessage(message);
            });
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        } catch (MessagingException e) {
            em.getTransaction().rollback();
            LOG.error("Error sending email message!", e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public boolean verify2FAChallenge(String code, String challenge, String sessionId) {
        var challengeEntityOpt = getByCode(code);
        if (challengeEntityOpt.isEmpty()) {
            return false;
        }
        
        TwoFactorCheckEntity entity = challengeEntityOpt.get();
    
        Date now = new Date();
        if (now.after(entity.getExpirationDate())) {
            return false;
        }
        
        var codeSession = entity.getSession().getId();
        
        String verifier = KeyUtil.sha256(createCodeVerifier(code, sessionId));
        String codeChallenge = challenge == null ?
            KeyUtil.sha256(createCodeVerifier(code, codeSession)) :
            challenge;
        
        return verifier.equals(codeChallenge);
    }
    
    private String generateVerifyUrl(String code, String challenge, Map<String, Object> params) {
        Map<String, String[]> urlParams = new HashMap<>();
        urlParams.put(CODE_2FA_PARAM, new String[]{code});
        urlParams.put(CHALLENGE_2FA_PARAM, new String[]{challenge});
        urlParams.put(REDIRECT_URI_PARAM, new String[]{(String) params.get(REDIRECT_URI_PARAM)});
        urlParams.put(REQUEST_ID_PARAM, new String[]{(String) params.get(REQUEST_ID_PARAM)});
        return authConfig.getBaseUrl() + TWO_FA_SERVLET + HttpUtil.formatQueryParams(urlParams);
    }
    
    private String createCodeVerifier(String code, String sessionId) {
        return code + ":" + sessionId;
    }
}
