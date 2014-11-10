package cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.security.authenticators;

import org.springframework.security.core.Authentication;

import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.data.models.User;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.security.CodeAuthenticationToken;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.security.CustomUserDetails;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.security.CustomUserDetailsImpl;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.security.SecurityService;

/**
 * Authenticating user against anonymous login code.
 */
public class AnonymousLoginCodeAuthenticator implements CodeAuthenticator {

	/**
	 * Service for retrieving users by anonymous code.
	 */
	private final SecurityService securityService;

	/**
	 * Constructor.
	 * 
	 * @param securityService
	 *            Service for finding anonymous users.
	 */
	public AnonymousLoginCodeAuthenticator(final SecurityService securityService) {
		this.securityService = securityService;
	}

	/**
	 * @param authentication
	 *            Authentication to verify.
	 * @return Authenticated token.
	 */
	@Override
	public final Authentication authenticate(final CodeAuthenticationToken authentication) {
		User user = securityService.findByAnonymousLoginCode(authentication.getCode());
		if (user == null) {
			return authentication;
		}
		CustomUserDetails principal = new CustomUserDetailsImpl(user);
		CodeAuthenticationToken authenticated = new CodeAuthenticationToken(authentication.getCodeType(),
				authentication.getCode(), principal, principal.getAuthorities());
		authenticated.setAuthenticated(true);
		return authenticated;
	}

}
