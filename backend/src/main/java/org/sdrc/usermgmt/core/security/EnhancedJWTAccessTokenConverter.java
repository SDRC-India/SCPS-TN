package org.sdrc.usermgmt.core.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sdrc.usermgmt.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
/*
 * @author azar
 * 
 * This class overrides the default accesstoken converter and sets the extra information,
 * retrieving the information from UserModel.
 */
public class EnhancedJWTAccessTokenConverter extends JwtAccessTokenConverter {
	
	@Autowired
	private CustomUserDetails customUserDetails;
	
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		
		UserModel user;
		
			try{
				user = (UserModel) authentication.getPrincipal();
			}catch (ClassCastException e){
				user =(UserModel)customUserDetails.loadUserByUsername((String)authentication.getPrincipal());
			}

			final Map<String, Object> additionalInfo = new HashMap<String, Object>();

			additionalInfo.put("sessionMap", user.getSessionMap());
			additionalInfo.put("userId", user.getUserId());
			additionalInfo.put("designationIds", user.getDesignationIds());
			additionalInfo.put("emailId", user.getEmail());
			additionalInfo.put("designationNames", user.getDesignations());
			
			List<String> authorityNames = new ArrayList<>();
			for(GrantedAuthority authority : user.getAuthorities()) {
				authorityNames.add(authority.getAuthority());
			}
			additionalInfo.put("authorities", authorityNames);
			
			if(user.getDesgSlugId()!=null)
				additionalInfo.put("desgSlugId", user.getDesgSlugId());
			
			((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);

		accessToken = super.enhance(accessToken, authentication);
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(new HashMap<>());
		return accessToken;
	}
}
