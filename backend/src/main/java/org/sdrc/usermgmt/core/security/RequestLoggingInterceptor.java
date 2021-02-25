package org.sdrc.usermgmt.core.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.sdrc.usermgmt.core.util.UgmtClientCredentials;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;

/**
 * @author Subham Ashish(subham@sdrc.co.in) Created Date:26-Sep-2018 5:29:33 PM
 * 
 *         it gets excuted before calling the method postAccessToken() of
 *         TokenEndPoint class For authenticate user request
 */
@Component
@ConditionalOnExpression("'${application.security.type}'=='jwt-both' "
		+ "OR '${application.security.type}'=='jwt-oauthserver' OR '${application.security.type}'=='jwt-resserver' OR '${application.security.type}'=='oauth2-oauthserver' OR '${application.security.type}'=='oauth2-both'")
public class RequestLoggingInterceptor extends GenericFilterBean {

	/*
	 * You cannot use dependency injection from a filter out of the box.
	 * Although you are using GenericFilterBean your Servlet Filter is not
	 * managed by spring
	 * 
	 * @Autowired is not possible
	 */
	private UgmtClientCredentials ugmtClientCredentials;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		if (ugmtClientCredentials == null) {
			// we cannot expect spring to inject the service, but we can lazy
			// set it on the first call
			ServletContext servletContext = request.getServletContext();
			WebApplicationContext webApplicationContext = WebApplicationContextUtils
					.getWebApplicationContext(servletContext);
			ugmtClientCredentials = webApplicationContext.getBean(UgmtClientCredentials.class);
		}

		String clientId = ugmtClientCredentials.getClientId();

		String clientSecrte = ugmtClientCredentials.getClientPassword();

		HttpServletRequest httpRequest = (HttpServletRequest) request;

		/**
		 * verifying the grant type, shoud be password type
		 */
		if (httpRequest.getParameter("grant_type") != null && (httpRequest.getParameter("grant_type").equals("password")
				|| httpRequest.getParameter("grant_type").equals("refresh_token"))) {

			MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(httpRequest);

			String credentials = clientId + ":" + clientSecrte;
			String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

			mutableRequest.putHeader("Authorization", "Basic " + encodedCredentials);
			chain.doFilter(mutableRequest, response);

		} else {
			chain.doFilter(request, response);
		}

	}

}
