package com.pj.squashrestapp.config.security.method;

import com.pj.squashrestapp.repository.AdditionalMatchRepository;
import com.pj.squashrestapp.repository.BonusPointRepository;
import com.pj.squashrestapp.repository.LeagueRulesRepository;
import com.pj.squashrestapp.repository.LostBallRepository;
import com.pj.squashrestapp.repository.MatchRepository;
import com.pj.squashrestapp.repository.RoundRepository;
import com.pj.squashrestapp.repository.SeasonRepository;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;

/**
 * Class that allows to implement custom method security logic. Implementation is done by {@link
 * CustomMethodSecurityExpressionRoot} class.
 */
public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
    private ApplicationContext applicationContext;

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
            final Authentication authentication, final MethodInvocation invocation) {
        final CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(authentication);
        root.setThis(invocation.getThis());
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(this.trustResolver);
        root.setRoleHierarchy(getRoleHierarchy());
        root.setMatchRepository(applicationContext.getBean(MatchRepository.class));
        root.setRoundRepository(applicationContext.getBean(RoundRepository.class));
        root.setSeasonRepository(applicationContext.getBean(SeasonRepository.class));
        root.setBonusPointRepository(applicationContext.getBean(BonusPointRepository.class));
        root.setLostBallRepository(applicationContext.getBean(LostBallRepository.class));
        root.setLeagueRulesRepository(applicationContext.getBean(LeagueRulesRepository.class));
        root.setAdditionalMatchRepository(applicationContext.getBean(AdditionalMatchRepository.class));
        return root;
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) {
        super.setApplicationContext(applicationContext);
        this.applicationContext = applicationContext;
    }
}
