package org.sdrc.mongoclientdetails;

import static java.util.Objects.isNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

@Document
public class MongoClientDetails implements ClientDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6693959601749417632L;
	
	@Id
	private String id;
	private String clientId;
	private String clientSecret;
	private Set<String> scope = Collections.emptySet();
	private Set<String> resourceIds = Collections.emptySet();
	private Set<String> authorizedGrantTypes = Collections.emptySet();
	private Set<String> registeredRedirectUris;
	private List<GrantedAuthority> authorities = Collections.emptyList();
	private Integer accessTokenValiditySeconds;
	private Integer refreshTokenValiditySeconds;
	private Map<String, Object> additionalInformation = new LinkedHashMap<>();
	private Set<String> autoApproveScopes;

	public MongoClientDetails() {
	}

	@PersistenceConstructor
	public MongoClientDetails(final String clientId, final String clientSecret, final Set<String> scope,
			final Set<String> resourceIds, final Set<String> authorizedGrantTypes,
			final Set<String> registeredRedirectUris, final List<GrantedAuthority> authorities,
			final Integer accessTokenValiditySeconds, final Integer refreshTokenValiditySeconds,
			final Map<String, Object> additionalInformation, final Set<String> autoApproveScopes) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.scope = scope;
		this.resourceIds = resourceIds;
		this.authorizedGrantTypes = authorizedGrantTypes;
		this.registeredRedirectUris = registeredRedirectUris;
		this.authorities = authorities;
		this.accessTokenValiditySeconds = accessTokenValiditySeconds;
		this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
		this.additionalInformation = additionalInformation;
		this.autoApproveScopes = autoApproveScopes;
	}

	public String getClientId() {
		return clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public Set<String> getScope() {
		return scope;
	}

	public Set<String> getResourceIds() {
		return resourceIds;
	}

	public Set<String> getAuthorizedGrantTypes() {
		return authorizedGrantTypes;
	}

	public List<GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public Integer getAccessTokenValiditySeconds() {
		return accessTokenValiditySeconds;
	}

	public Integer getRefreshTokenValiditySeconds() {
		return refreshTokenValiditySeconds;
	}

	public Map<String, Object> getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAutoApproveScopes(final Set<String> autoApproveScopes) {
		this.autoApproveScopes = autoApproveScopes;
	}

	public Set<String> getAutoApproveScopes() {
		return autoApproveScopes;
	}

	@Override
	public boolean isScoped() {
		return this.scope != null && !this.scope.isEmpty();
	}

	@Override
	public boolean isSecretRequired() {
		return this.clientSecret != null;
	}

	@Override
	public Set<String> getRegisteredRedirectUri() {
		return registeredRedirectUris;
	}

	@Override
	public boolean isAutoApprove(final String scope) {
		if (isNull(autoApproveScopes)) {
			return false;
		}
		for (String auto : autoApproveScopes) {
			if ("true".equals(auto) || scope.matches(auto)) {
				return true;
			}
		}
		return false;
	}

	public Set<String> getRegisteredRedirectUris() {
		return registeredRedirectUris;
	}

	public void setRegisteredRedirectUris(Set<String> registeredRedirectUris) {
		this.registeredRedirectUris = registeredRedirectUris;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public void setScope(Set<String> scope) {
		this.scope = scope;
	}

	public void setResourceIds(Set<String> resourceIds) {
		this.resourceIds = resourceIds;
	}

	public void setAuthorizedGrantTypes(Set<String> authorizedGrantTypes) {
		this.authorizedGrantTypes = authorizedGrantTypes;
	}

	public void setAuthorities(List<GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	public void setAccessTokenValiditySeconds(Integer accessTokenValiditySeconds) {
		this.accessTokenValiditySeconds = accessTokenValiditySeconds;
	}

	public void setRefreshTokenValiditySeconds(Integer refreshTokenValiditySeconds) {
		this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
	}

	public void setAdditionalInformation(Map<String, Object> additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
}