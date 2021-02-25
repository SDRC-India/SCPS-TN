package org.sdrc.usermgmt.core.security;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

/**
 * @author Subham Ashish(subham@sdrc.co.in)
 * 
 *         This controller class delete the token from database
 */

@RestController
public class Oauth2Logout {

	@Autowired(required=false)
	private ConsumerTokenServices consumerTokenServices;

	@RequestMapping(method = RequestMethod.DELETE, value = "/oauth/logout")
	@ResponseBody
	public void logout(WebRequest request) {

		String bearer = "bearer";

		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		// log.info("authorization header: {}", authorizationHeader);

		if (authorizationHeader != null && StringUtils.containsIgnoreCase(authorizationHeader, bearer)) {

			String accessTokenID = authorizationHeader.substring(bearer.length() + 1);
			// log.info("access_token: {}", accessTokenID);
			consumerTokenServices.revokeToken(accessTokenID);

		}

	}
}
