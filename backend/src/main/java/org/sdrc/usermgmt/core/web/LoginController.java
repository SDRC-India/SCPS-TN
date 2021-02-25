package org.sdrc.usermgmt.core.web;

import java.security.Principal;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.sdrc.usermgmt.domain.Account;
import org.sdrc.usermgmt.model.UserModel;
import org.sdrc.usermgmt.mongodb.repository.AccountRepository;
import org.sdrc.usermgmt.mongodb.repository.LoginAuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@Transactional
public class LoginController {

	@Autowired(required = false)
	private TokenStore tokenStore;

	@Autowired
	private ConfigurableEnvironment configurableEnvironment;

	@Qualifier("mongoLoginAuditRepository")
	@Autowired(required = false)
	private LoginAuditRepository loginAuditRepo;

	@Qualifier("jpaLoginAuditRepository")
	@Autowired(required = false)
	private org.sdrc.usermgmt.repository.LoginAuditRepository loginAuditRepository;

	@Autowired(required = false)
	@Qualifier("mongoAccountRepository")
	private AccountRepository mongoAccountRepository;
	
	@Autowired(required = false)
	@Qualifier("jpaAccountRepository")
	private org.sdrc.usermgmt.repository.AccountRepository jpaAccountRepository;

	/**
	 * It extracts the user details from jwt access-token.
	 * 
	 * @param auth
	 * @return
	 */
	@ConditionalOnExpression("'${application.security.type}'=='jwt-both' OR "
			+ "'${application.security.type}'=='jwt-resserver' "
			+ "OR '${application.security.type}'=='jwt-oauthserver'")
	@RequestMapping(value = "/oauth/user", method = RequestMethod.GET)
	public Map<String, Object> getExtraInfo(OAuth2Authentication auth, Principal principal) {
		OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) auth.getDetails();
		OAuth2AccessToken accessToken = tokenStore.readAccessToken(details.getTokenValue());

		// save login details to db
		saveLoginAudit(principal.getName());

		return accessToken.getAdditionalInformation();
	}

	/**
	 * It retrieves the user deatils.
	 * 
	 * @param principal
	 * @return
	 */
	@ConditionalOnExpression("'${application.security.type}'=='oauth2-both' OR "
			+ "'${application.security.type}'=='basic' " + "OR '${application.security.type}'=='oauth2-oauthserver'")
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public UserModel principal(Principal principal) {

		UserModel user = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		// save login details to db
		saveLoginAudit(principal.getName());

		return user;

	}

	private boolean saveLoginAudit(String userName) {

		String dataSourceType = configurableEnvironment.getProperty("app.datasource.type");

		switch (dataSourceType) {

		case "SQL": {

			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
					.getRequest();

			org.sdrc.usermgmt.domain.LoginAudit audit = new org.sdrc.usermgmt.domain.LoginAudit();
			audit.setActive(true);

			audit.setIpAddress(request.getRemoteAddr());

			audit.setLoggedInDate(new Date());

			Account userDomain =jpaAccountRepository.findByUserName(userName);

			audit.setAccount(userDomain);

			audit.setUserAgent(request.getHeader("User-Agent"));

			try {
				audit.setActualUserAgent(browserInformation(request.getHeader("User-Agent")));
			} catch (Exception e) {
				audit.setActualUserAgent("E-Parse ex : " + request.getHeader("User-Agent"));
			}

			audit.setUsername(userName);

			audit = loginAuditRepository.save(audit);

		}

			break;

		case "MONGO": {

			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
					.getRequest();

			org.sdrc.usermgmt.mongodb.domain.LoginAudit loginAud = new org.sdrc.usermgmt.mongodb.domain.LoginAudit();
			loginAud.setActive(true);

			loginAud.setIpAddress(request.getRemoteAddr());

			loginAud.setLoggedInDate(new Date());

			org.sdrc.usermgmt.mongodb.domain.Account userDomain = mongoAccountRepository.findByUserName(userName);

			loginAud.setAccount(userDomain);

			loginAud.setUserAgent(request.getHeader("User-Agent"));

			try {
				loginAud.setActualUserAgent(browserInformation(request.getHeader("User-Agent")));
			} catch (Exception e) {
				loginAud.setActualUserAgent("E-Parse ex : " + request.getHeader("User-Agent"));
			}

			loginAud.setUsername(userName);

			loginAud = loginAuditRepo.save(loginAud);

		}
			break;

		}
		return true;
	}

	/**
	 * it returns the actual browser agent
	 * 
	 * @param browserDetails
	 * 
	 */
	private String browserInformation(String browserDetails) {

		String browserInfo = browserDetails;
		String browser = "";

		if (browserInfo.contains("Mobi")) {
			if (browserInfo.contains("Edge") || browserInfo.contains("MSIE")) {
				browser = "Mobile: " + browserDetails
						.substring(browserDetails.indexOf("("), browserDetails.indexOf(")") + 1).concat(" ")
						.concat((browserDetails.substring(browserDetails.indexOf("Edge")).split(" ")[0]));
			} else if (browserInfo.contains("Gecko") && browserInfo.contains("rv:")) {
				browser = "Mobile: " + browserDetails
						.substring(browserDetails.indexOf("("), browserDetails.indexOf(")") + 1).concat(" ")
						.concat((browserDetails.substring(browserDetails.indexOf("Firefox")).split(" ")[0]));
			} else if (browserInfo.contains("Safari") && browserInfo.contains("AppleWebKit")
					&& !browserInfo.contains("Chrome")) {
				browser = "Mobile: " + browserDetails
						.substring(browserDetails.indexOf("("), browserDetails.indexOf(")") + 1).concat(" ")
						.concat((browserDetails.substring(browserDetails.indexOf("Safari")).split(" ")[0]));
			} else if (browserInfo.contains("Mini")) {
				browser = "Mobile: " + browserDetails
						.substring(browserDetails.indexOf("("), browserDetails.indexOf(")") + 1).concat(" ")
						.concat((browserDetails.substring(browserDetails.indexOf("Opera Mini")).split(" ")[0]));
			} else if (browserInfo.contains("Chrome")) {
				browser = "Mobile: " + browserDetails
						.substring(browserDetails.indexOf("("), browserDetails.indexOf(")") + 1).concat(" ")
						.concat(browserDetails.substring(browserDetails.indexOf("Chrome")).split(" ")[0]);
			} else
				browser = "Mobile: "
						+ browserDetails.substring(browserDetails.indexOf("("), browserDetails.indexOf(")") + 1);
		} else if (browserInfo.contains("Nexus") && browserInfo.contains("Chrome")) {
			browser = "Mobile: "
					+ browserDetails.substring(browserDetails.indexOf("("), browserDetails.indexOf(")") + 1).concat(" ")
							.concat((browserDetails.substring(browserDetails.indexOf("Chrome")).split(" ")[0]));
		} else if (browserInfo.contains("Nexus") && browserInfo.contains("OPR")) {
			browser = "Mobile: "
					+ browserDetails.substring(browserDetails.indexOf("("), browserDetails.indexOf(")") + 1)
							.concat(((browserDetails.substring(browserDetails.indexOf("OPR")).split(" ")[0]))
									.replace("OPR", "Opera"));
		} else if (browserInfo.contains("Nexus")) {
			browser = "Mobile: "
					+ browserDetails.substring(browserDetails.indexOf("("), browserDetails.indexOf(")") + 1);
		} else if (browserInfo.contains("Tab") || browserInfo.contains("TAB")) {
			browser = "Mobile: "
					+ browserDetails.substring(browserDetails.indexOf("("), browserDetails.indexOf(")") + 1);
		} else if (browserInfo.contains("Edge")) {
			browser = "Computer: " + (browserDetails.substring(browserDetails.indexOf("Edge")).split(" ")[0]);
		} else if (browserInfo.contains("MSIE")) {
			browser = "Computer: " + (browserDetails.substring(browserDetails.indexOf("MSIE")).split(" ")[0]);
		} else if (browserInfo.contains("Gecko") && browserInfo.contains("rv:")) {
			browser = "Computer: " + (browserDetails.substring(browserDetails.indexOf("Firefox")).split(" ")[0]);

		} else if (browserInfo.contains("Safari") && browserInfo.contains("AppleWebKit")
				&& !browserInfo.contains("Chrome")) {
			// Safari Computer Browser
			browser = "Computer: "
					+ browserDetails.substring(browserDetails.indexOf("("), browserDetails.indexOf(")") + 1)
							.concat((browserDetails.substring(browserDetails.indexOf("Safari")).split(" ")[0]));
		} else if (browserInfo.contains("Opera") || browserInfo.contains("OPR")) {
			if (browserInfo.contains("OPR")) {
				// desktop
				browser = "Computer: " + ((browserDetails.substring(browserDetails.indexOf("OPR")).split(" ")[0]))
						.replace("OPR", "Opera");
			} else
				browser = browserInfo;

		} else if (browserInfo.contains("Chrome")) {
			browser = "Computer: " + (browserDetails.substring(browserDetails.indexOf("Chrome")).split(" ")[0]);
		} else if ((browserInfo.indexOf("Mozilla/7.0") > -1) || (browserInfo.indexOf("Netscape6") != -1)
				|| (browserInfo.indexOf("Mozilla/4.7") != -1) || (browserInfo.indexOf("Mozilla/4.78") != -1)
				|| (browserInfo.indexOf("Mozilla/4.08") != -1) || (browserInfo.indexOf("Mozilla/3") != -1)) {

			browser = "Netscape-?";

		} else if (browserInfo.contains("rv")) {
			browser = "IE";
		} else {
			browser = "UnKnown, More-Info: " + browserDetails;
		}

		return browser;

	}
}
