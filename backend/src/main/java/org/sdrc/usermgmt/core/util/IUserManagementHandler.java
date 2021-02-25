package org.sdrc.usermgmt.core.util;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * @author Subham Ashish(subham@sdrc.co.in)
 *
 *This interface- implementation will be provided by client
 *
 *it helps in the following situation : 
 *
 *--extra parameters to be added in Session-object,
 *
 *--while creating user if any extra-parameter required
 *
 *--while updating user, logic will be defined by client application
 *
 */
public interface IUserManagementHandler {

	public Map<String, Object> sessionMap(Object account);
	
	public boolean saveAccountDetails(Map<String, Object> map, Object account);
	
	public boolean updateAccountDetails(Map<String, Object> map, Object account, Principal p);

	public List<?> getAllAuthorities();
	
}
