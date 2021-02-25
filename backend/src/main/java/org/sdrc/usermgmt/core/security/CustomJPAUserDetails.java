package org.sdrc.usermgmt.core.security;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sdrc.usermgmt.core.util.IUserManagementHandler;
import org.sdrc.usermgmt.domain.Account;
import org.sdrc.usermgmt.domain.AccountAuthorityMapping;
import org.sdrc.usermgmt.domain.AccountDesignationMapping;
import org.sdrc.usermgmt.domain.DesignationAuthorityMapping;
import org.sdrc.usermgmt.model.AuthorityControlType;
import org.sdrc.usermgmt.model.UserModel;
import org.sdrc.usermgmt.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author subham
 *
 */
@Component
public class CustomJPAUserDetails {

	@Autowired(required = false)
	private AccountRepository accountRepository;

	@Autowired(required = false)
	private IUserManagementHandler iuserManagementHandler;

	@Transactional
	public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {

		Account account = accountRepository.findByUserName(name);

		if (account == null) {
			throw new UsernameNotFoundException("Invalid username or password !");
		}

		// application user is responsible to provide its value
		Map<String, Object> sessionMap = iuserManagementHandler.sessionMap(account);

		Set<GrantedAuthority> grantedAuthority = new HashSet<>();

		/**
		 * As one user can have multiple roles
		 */
		Set<Object> designationIds = new HashSet<>();

		Set<String> designations = new HashSet<>();

		/**
		 * Adding authority like @PreAuthorize("hasAuthority('authorityvalue')")
		 * 
		 * if authority control type is mentioned designation than get all the
		 * authority mapped with the designations
		 */

		if (account.getAuthorityControlType() == AuthorityControlType.DESIGNATION
				|| account.getAuthorityControlType() == AuthorityControlType.HYBRID) {

			grantedAuthority = setGrantedAuthorityValueFromDesignation(account.getAccountDesignationMapping(),
					designationIds, designations, grantedAuthority);

		}

		/**
		 * Adding authority like @PreAuthorize("hasAuthority('authorityvalue')")
		 * 
		 * if authority control type is mentioned authority than get all the
		 * authority mapped.
		 */
		if (account.getAuthorityControlType() == AuthorityControlType.AUTHORITY
				|| account.getAuthorityControlType() == AuthorityControlType.HYBRID) {

			setGrantedAuthorityValueFromAuthority(account.getAccountAuthorityMapping(), grantedAuthority,
					designationIds, designations, account.getAccountDesignationMapping());

		}

		return new UserModel(account.getUserName(), account.getPassword(), account.isEnabled(), !account.isExpired(),
				!account.isCredentialexpired(), !account.isLocked(), grantedAuthority, account.getId(), designationIds,
				designations, sessionMap, account.getEmail(), null);
	}

	/**
	 * 
	 * @param accountAuthorityMapping
	 * @param grantedAuthority
	 * @param designationIds
	 * @param designations
	 * @param accountDesignationMapping
	 * @return
	 */
	private Set<GrantedAuthority> setGrantedAuthorityValueFromAuthority(
			List<AccountAuthorityMapping> accountAuthorityMapping, Set<GrantedAuthority> grantedAuthority,
			Set<Object> designationIds, Set<String> designations,
			List<AccountDesignationMapping> accountDesignationMapping) {

		accountDesignationMapping.forEach(ed -> {

			if (ed.getEnable()) {
				// adding role-ids
				designationIds.add(ed.getDesignation().getId());
				// adding rolename
				designations.add(ed.getDesignation().getName());
			}

		});

		accountAuthorityMapping.forEach(acc -> {

			grantedAuthority.add(new SimpleGrantedAuthority(acc.getAuthority().getAuthority()));

		});

		return grantedAuthority;
	}

	/**
	 * 
	 * @param accountDesignationMapping
	 * @param designationIds
	 * @param designations
	 * @param grantedAuthority
	 * @return
	 */
	private Set<GrantedAuthority> setGrantedAuthorityValueFromDesignation(
			List<AccountDesignationMapping> accountDesignationMapping, Set<Object> designationIds,
			Set<String> designations, Set<GrantedAuthority> grantedAuthority) {

		accountDesignationMapping.forEach(ed -> {

			if (ed.getEnable()) {
				List<DesignationAuthorityMapping> designationAuthorityMapping = ed.getDesignation()
						.getDesignationAuthorityMapping();

				designationAuthorityMapping.forEach(da -> {

					// adding role-ids
					designationIds.add(da.getDesignation().getId());

					// adding rolename
					designations.add(da.getDesignation().getName());
					grantedAuthority.add(new SimpleGrantedAuthority(da.getAuthority().getAuthority()));
				});
			}

		});

		return grantedAuthority;
	}

}
