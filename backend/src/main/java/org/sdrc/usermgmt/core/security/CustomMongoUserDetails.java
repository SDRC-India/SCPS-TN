package org.sdrc.usermgmt.core.security;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sdrc.usermgmt.core.util.IUserManagementHandler;
import org.sdrc.usermgmt.model.AuthorityControlType;
import org.sdrc.usermgmt.model.UserModel;
import org.sdrc.usermgmt.mongodb.domain.Account;
import org.sdrc.usermgmt.mongodb.domain.AssignedDesignations;
import org.sdrc.usermgmt.mongodb.domain.Authority;
import org.sdrc.usermgmt.mongodb.domain.Designation;
import org.sdrc.usermgmt.mongodb.domain.DesignationAuthorityMapping;
import org.sdrc.usermgmt.mongodb.repository.AccountRepository;
import org.sdrc.usermgmt.mongodb.repository.AuthorityRepository;
import org.sdrc.usermgmt.mongodb.repository.DesignationAuthorityMappingRepository;
import org.sdrc.usermgmt.mongodb.repository.DesignationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Subham Ashish (subham@sdrc.co.in)
 *
 */
@ConditionalOnExpression("'${app.datasource.type}'=='MONGO'")
@Component
public class CustomMongoUserDetails {

	@Autowired(required = false)
	@Qualifier("mongoAccountRepository")
	private AccountRepository accountRepository;

	@Autowired(required = false)
	private IUserManagementHandler iuserManagementHandler;

	@Autowired(required = false)
	@Qualifier("mongoDesignationAuthorityMappingRepository")
	private DesignationAuthorityMappingRepository designationAuthorityMappingRepository;

	@Autowired(required = false)
	@Qualifier("mongoDesignationRepository")
	private DesignationRepository designationRepository;

	@Autowired(required = false)
	@Qualifier("mongoAuthorityRepository")
	private AuthorityRepository authorityRepository;

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

		Set<Integer> desgSlugId = new HashSet<>();

		/**
		 * Adding authority like @PreAuthorize("hasAuthority('authorityvalue')")
		 * 
		 * if authority control type is mentioned designation than get all the
		 * authority mapped with the designations
		 */

		if (account.getAuthorityControlType() == AuthorityControlType.DESIGNATION
				|| account.getAuthorityControlType() == AuthorityControlType.HYBRID) {

			grantedAuthority = setGrantedAuthorityValueFromDesignation(account.getAssignedDesignations(),
					designationIds, designations, desgSlugId, grantedAuthority);

		}

		/**
		 * Adding authority like @PreAuthorize("hasAuthority('authorityvalue')")
		 * 
		 * if authority control type is mentioned authority than get all the
		 * authority mapped.
		 */
		if (account.getAuthorityControlType() == AuthorityControlType.AUTHORITY
				|| account.getAuthorityControlType() == AuthorityControlType.HYBRID) {

			grantedAuthority = setGrantedAuthorityValueFromAuthority(account.getAuthorityIds(), grantedAuthority,
					account.getAssignedDesignations(), designationIds, designations, desgSlugId);

		}

		return new UserModel(account.getUserName(), account.getPassword(), account.isEnabled(), !account.isExpired(),
				!account.isCredentialexpired(), !account.isLocked(), grantedAuthority, account.getId(), designationIds,
				designations, sessionMap, account.getEmail(), desgSlugId);
	}

	/**
	 * This method fetches the assigned authority values and sets it in
	 * grantedAuthoriy.
	 * 
	 * @param authorityIds
	 * @param grantedAuthority
	 * @return grantedAuthority values
	 */
	private Set<GrantedAuthority> setGrantedAuthorityValueFromAuthority(List<String> authorityIds,
			Set<GrantedAuthority> grantedAuthority, List<AssignedDesignations> assignedDesignations,
			Set<Object> designationIds, Set<String> designations, Set<Integer> desgSlugId) {

		List<String> desgList = new ArrayList<>();

		assignedDesignations.forEach(asdg -> {

			if (asdg.getEnable()) {

				desgList.add(asdg.getDesignationIds());

			}

		});

		List<Designation> desgs = designationRepository.findByIdIn(desgList);

		desgs.forEach(da -> {
			// adding role-ids
			designationIds.add(da.getId());

			// // adding rolename
			designations.add(da.getName());

			// add slug id
			desgSlugId.add(da.getSlugId());

		});

		List<Authority> authorities = authorityRepository.findByIdIn(authorityIds);

		authorities.forEach(da -> {

			grantedAuthority.add(new SimpleGrantedAuthority(da.getAuthority()));
		});

		return grantedAuthority;
	}

	/**
	 * This method retrieves the the ids of designation, fetch all the
	 * authorities assigned to those designation and finally sets it in
	 * grantedauthority.
	 * 
	 * @param assignedDesignations
	 * @param desgSlugId
	 * @param designations
	 * @param designationIds
	 * @param grantedAuthority
	 * 
	 * @return grantedAuthority values
	 */
	private Set<GrantedAuthority> setGrantedAuthorityValueFromDesignation(
			List<AssignedDesignations> assignedDesignations, Set<Object> designationIds, Set<String> designations,
			Set<Integer> desgSlugId, Set<GrantedAuthority> grantedAuthority) {

		List<String> desgList = new ArrayList<>();

		assignedDesignations.forEach(asdg -> {

			if (asdg.getEnable()) {

				desgList.add(asdg.getDesignationIds());

			}

		});

		List<Designation> desgs = designationRepository.findByIdIn(desgList);

		// fecth all the DesignationAuthorityMapping
		List<DesignationAuthorityMapping> designationAuthorityMapping = designationAuthorityMappingRepository
				.findByDesignationIn(desgs);

		designationAuthorityMapping.forEach(da -> {
			// adding role-ids
			designationIds.add(da.getDesignation().getId());

			// // adding rolename
			designations.add(da.getDesignation().getName());

			// add slug id
			desgSlugId.add(da.getDesignation().getSlugId());

			grantedAuthority.add(new SimpleGrantedAuthority(da.getAuthority().getAuthority()));
		});
		return grantedAuthority;
	}

}
