package org.sdrc.usermgmt.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.sdrc.usermgmt.core.util.IUserManagementHandler;
import org.sdrc.usermgmt.domain.Account;
import org.sdrc.usermgmt.domain.AccountAuthorityMapping;
import org.sdrc.usermgmt.domain.AccountDesignationMapping;
import org.sdrc.usermgmt.domain.Authority;
import org.sdrc.usermgmt.domain.Designation;
import org.sdrc.usermgmt.exception.EmailMisMatchException;
import org.sdrc.usermgmt.exception.InvalidPropertyException;
import org.sdrc.usermgmt.exception.PasswordMismatchException;
import org.sdrc.usermgmt.exception.UserAlreadyExistException;
import org.sdrc.usermgmt.model.AuthorityControlType;
import org.sdrc.usermgmt.model.ChangePasswordModel;
import org.sdrc.usermgmt.model.ForgotPasswordModel;
import org.sdrc.usermgmt.model.Mail;
import org.sdrc.usermgmt.repository.AccountAuthorityMappingRepository;
import org.sdrc.usermgmt.repository.AccountDesignationMappingRepository;
import org.sdrc.usermgmt.repository.AccountRepository;
import org.sdrc.usermgmt.repository.AuthorityRepository;
import org.sdrc.usermgmt.repository.DesignationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author subham
 *
 */
@Service
@Slf4j
public class JPAUserManagementServiceImpl implements JPAUserManagementService {

	@Autowired(required = false)
	@Qualifier(value = "jpaAccountDesignationMappingRepository")
	private AccountDesignationMappingRepository accountDesignationMappingRepository;

	@Autowired(required = false)
	@Qualifier("jpaAccountRepository")
	private AccountRepository accountRepository;

	@Autowired
	private ConfigurableEnvironment configurableEnvironment;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired(required = false)
	@Qualifier("jpaDesignationRepository")
	private DesignationRepository designationRepository;

	@Autowired(required = false)
	private IUserManagementHandler iuserManagementHandler;

	@Autowired(required = false)
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired(required = false)
	private MailService mailService;

	@Autowired(required = false)
	@Qualifier("jpaAuthorityRepository")
	private AuthorityRepository authorityRepository;

	@Autowired(required = false)
	@Qualifier("jpaAccountAuthorityMappingRepository")
	private AccountAuthorityMappingRepository accountAuthorityMappingRepository;

	@Override
	@Transactional
	public ResponseEntity<String> createUser(Map<String, Object> map, Principal p) {

		Gson gson = new Gson();

		if (map.get("userName") == null || map.get("userName").toString().isEmpty())
			throw new RuntimeException("key : userName not found in map");

		if (map.get("designationIds") == null || map.get("designationIds").toString().isEmpty())
			throw new RuntimeException("key : designationIds not found in map");

		if (map.get("password") == null || map.get("password").toString().isEmpty())
			throw new RuntimeException("key : password not found in map");

		Account user = accountRepository.findByUserName(map.get("userName").toString());

		if (user != null) {

			throw new UserAlreadyExistException(configurableEnvironment.getProperty("username.duplicate.error"));

		}

		Account account = new Account();

		account.setUserName(map.get("userName").toString());
		account.setPassword(passwordEncoder.encode(map.get("password").toString()));

		/** checking existing mail for users */
		if (map.get("allowDuplicateMail") != null && map.get("allowDuplicateMail").toString().equals("false")) {
				if( map.get("email") != null && !map.get("email").toString().isEmpty()) {
			Account acc = accountRepository.findByEmail(map.get("email").toString());
			if (acc != null)
				throw new UserAlreadyExistException(configurableEnvironment.getProperty("email.duplicate.error"));
			account.setEmail(map.get("email").toString());
				}
		} else if (map.get("allowDuplicateMail") != null && map.get("allowDuplicateMail").toString().equals("true") && map.get("email") != null && !map.get("email").toString().isEmpty()) {
			account.setEmail(map.get("email").toString());
		} else if(map.get("allowDuplicateMail") == null  ) {
			account.setEmail(map.get("email").toString());
		}

		account = accountRepository.save(account);

		iuserManagementHandler.saveAccountDetails(map, account);

		List<Integer> designationIds = (List<Integer>) map.get("designationIds");

		List<Designation> designations = designationRepository.findByIdIn(designationIds);

		// check whether the user wanted to create admin user, if yes than does
		// he/she set the property 'allow.admin.creation' = true
		if ((!configurableEnvironment.containsProperty("allow.admin.creation"))
				|| configurableEnvironment.getProperty("allow.admin.creation").equals("false")) {
			designations.forEach(desgs -> {

				if (desgs.getName().equals("ADMIN")) {
					throw new RuntimeException("you do not have permission to create admin user!");
				}
			});
		}

		List<AccountDesignationMapping> admList = new ArrayList<>();

		for (Designation designation : designations) {

			AccountDesignationMapping adm = new AccountDesignationMapping();
			adm.setAccount(account);
			adm.setDesignation(designation);
			admList.add(adm);

		}
		accountDesignationMappingRepository.save(admList);

		/**
		 * if authorityIds present in the map than set its value in account
		 */
		if (map.get("authorityIds") != null && !map.get("authorityIds").toString().isEmpty()) {

			List<String> authorities = (List<String>) map.get("authorityIds");
			List<Authority> authority = authorityRepository.findByIdIn(authorities);

			List<AccountAuthorityMapping> accAuthList = new ArrayList<>();

			for (Authority auth : authority) {

				AccountAuthorityMapping accountAuthorityMapping = new AccountAuthorityMapping();
				accountAuthorityMapping.setAccount(account);
				accountAuthorityMapping.setAuthority(auth);
				accAuthList.add(accountAuthorityMapping);

			}

			accountAuthorityMappingRepository.save(accAuthList);
		}

		if (map.get("authority_control_type") != null && !map.get("authority_control_type").toString().isEmpty()) {

			if (!(map.get("authority_control_type").toString().equals("hybrid")
					|| map.get("authority_control_type").toString().equals("designation")
					|| map.get("authority_control_type").toString().equals("authority"))) {
				throw new java.lang.IllegalArgumentException(
						"invalid authority control type provided in api.allowed arguments are hybrid,authority,designation");
			}

			String controlType = map.get("authority_control_type").toString();
			account.setAuthorityControlType(controlType.equals("authority") ? AuthorityControlType.AUTHORITY
					: controlType.equals("designation") ? AuthorityControlType.DESIGNATION
							: AuthorityControlType.HYBRID);
			;

		} else {

			account.setAuthorityControlType(AuthorityControlType.DESIGNATION);
		}

		return new ResponseEntity<String>(gson.toJson(configurableEnvironment.getProperty("user.create.success")),
				HttpStatus.OK);
	}

	/**
	 * Updating password in database
	 */
	@Override
	@Transactional
	public ResponseEntity<String> changePassoword(ChangePasswordModel changePasswordModel, Principal p) {
		/**
		 * using it to parse string as json
		 */
		Gson gson = new Gson();
		Account user = accountRepository.findByUserName(changePasswordModel.getUserName());
		try {
			/**
			 * check newpassword and confirm password is same or nots
			 */
			checkIfNewPasswordAndDbPasswordAreSame(changePasswordModel.getNewPassword(),
					changePasswordModel.getConfirmPassword(), user);

			/**
			 * check user has entered correct old password or not
			 */
			checkCorrectOldPassword(changePasswordModel.getOldPassword(), user.getPassword(), user);
			/**
			 * check new password is same as db password or not if same
			 */
			checkIfNewPasswordAndDbPasswordAreSame(changePasswordModel.getNewPassword(), user.getPassword(), user);
			/**
			 * encoding password
			 */
			user.setPassword(bCryptPasswordEncoder.encode(changePasswordModel.getNewPassword()));
			/**
			 * updating db
			 */
			accountRepository.save(user);

			log.info(configurableEnvironment.getProperty("password.update.success") + " for user : "
					+ user.getUserName());
			return new ResponseEntity<>(gson.toJson(configurableEnvironment.getProperty("password.update.success")),
					HttpStatus.OK);

		} catch (PasswordMismatchException e) {
			log.error("Action : change-password By {}: error while updating password! ", user.getUserName(), e);
			throw new PasswordMismatchException(e.getMessage());
		} catch (Exception e) {
			log.error("Action : change-password By {}: error while updating password! ", user.getUserName(), e);
			return new ResponseEntity<>(gson.toJson(configurableEnvironment.getProperty("password.update.failure")),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * check new password is same as db password or not if same
	 * 
	 * @param newPassword
	 * @param password
	 * @param user
	 */
	private void checkIfNewPasswordAndDbPasswordAreSame(String newPassword, String password, Account user) {
		if (bCryptPasswordEncoder.matches(newPassword, password)) {
			log.error(
					"Action : change-password By {}: error while updating password! : message : "
							+ configurableEnvironment.getProperty("new.password.previous.password"),
					user.getUserName());

			throw new PasswordMismatchException(configurableEnvironment.getProperty("new.password.previous.password"));
		}
	}

	/**
	 * check user has entered correct old password or not
	 * 
	 * @param oldPassword
	 * @param password
	 * @param user
	 */
	private void checkCorrectOldPassword(String oldPassword, String password, Account user) {
		if (!bCryptPasswordEncoder.matches(oldPassword, password)) {
			log.error("Action : change-password By {} : error while updating password! : message : ",
					user.getUserName(), configurableEnvironment.getProperty("password.not.matching"));
			throw new PasswordMismatchException(configurableEnvironment.getProperty("password.not.matching"));
		}
	}

	@Override
	@Transactional
	public ResponseEntity<String> validateOtp(String userNameOrMail, String otp, String valueType) {
		/**
		 * using it to parse string as json
		 */
		Gson gson = new Gson();

		Account user = null;

		if (valueType == null || valueType.equals("email")) {
			user = accountRepository.findByEmail(userNameOrMail);
		} else if (valueType.equals("uName")) {
			user = accountRepository.findByUserName(userNameOrMail);
		} else {
			throw new InvalidPropertyException("Invalid valueType property");
		}
		/**
		 * validating time of otp
		 */
		if ((user.getOtpGeneratedDateTime().getTime() + 10 * 60000) <= new Date().getTime()) {
			log.error("Action : validate-OTP By {} : error while validating Otp {} ", user.getUserName(),
					configurableEnvironment.getProperty("password.reset.time.expire"));
			return new ResponseEntity<>(gson.toJson(configurableEnvironment.getProperty("password.reset.time.expire")),
					HttpStatus.BAD_REQUEST);
		}
		/**
		 * validating number of attempts exceed 10 or not
		 */
		if (user.getInvalidAttempts() == 3) {
			log.error("Action : validate-OTP By {} : error while validating Otp {} ", user.getUserName(),
					configurableEnvironment.getProperty("password.reset.time.expire"));
			return new ResponseEntity<>(gson.toJson(configurableEnvironment.getProperty("invalid.otp.exceed.limit")),
					HttpStatus.BAD_REQUEST);
		}

		/**
		 * validating otp entered by user is correct or not
		 */
		if (user.getOtp().equals(otp)) {
			log.info("Action : validate-otp :  OTP Validated for user : {}", user.getUserName());
			return new ResponseEntity<>(gson.toJson(configurableEnvironment.getProperty("opt.valid")), HttpStatus.OK);
		} else {
			user.setInvalidAttempts((short) (user.getInvalidAttempts() + 1));

			if (3 - user.getInvalidAttempts() != 0) {
				if (3 - user.getInvalidAttempts() == 1)
					return new ResponseEntity<>(
							gson.toJson(
									"Invalid OTP! You have " + (3 - user.getInvalidAttempts()) + " available attempt"),
							HttpStatus.BAD_REQUEST);
				else
					return new ResponseEntity<>(
							gson.toJson(
									"Invalid OTP! You have " + (3 - user.getInvalidAttempts()) + " available attempts"),
							HttpStatus.BAD_REQUEST);
			} else {
				log.error("Action : validate-OTP By {} : error while validating Otp {} ", user.getUserName(),
						configurableEnvironment.getProperty("password.reset.time.expire"));
				return new ResponseEntity<>(
						gson.toJson(configurableEnvironment.getProperty("invalid.otp.exceed.limit")),
						HttpStatus.BAD_REQUEST);
			}
		}
	}

	@Override
	@Transactional
	public ResponseEntity<String> sendOtp(String userNameOrMail, String valueType) {
		/**
		 * using it to parse string as json
		 */
		Gson gson = new Gson();

		Account user = null;

		if (valueType == null || valueType.equals("email")) {
			user = accountRepository.findByEmail(userNameOrMail);
		} else if (valueType.equals("uName")) {
			user = accountRepository.findByUserName(userNameOrMail);
		} else {
			throw new InvalidPropertyException("Invalid valueType property");
		}

		try {

			if (user == null) {
				return new ResponseEntity<>(gson.toJson(configurableEnvironment.getProperty("emailid.not.exist")),
						HttpStatus.NOT_FOUND);
			}
			/**
			 * generating 4 digit random otp
			 */
			Random random = new Random();
			String otp = String.format("%04d", random.nextInt(10000));

			/**
			 * updating user table with generated otp
			 */
			user.setOtp(otp);
			user.setOtpGeneratedDateTime(new Date());
			user.setInvalidAttempts((short) 0);

			accountRepository.save(user);

			/**
			 * creating mail object and seeting its required value to send mail
			 */
			Mail mail = new Mail();
			List<String> emailId = new ArrayList<String>();
			emailId.add(user.getEmail());
			mail.setToEmailIds(emailId);
			mail.setToUserName(user.getUserName());
			mail.setSubject("Forgot Password OTP");
			mail.setMessage(configurableEnvironment.getProperty("otp.send.message") + otp);
			mail.setFromUserName("Administrator");
			/**
			 * sending mail
			 */
			mailService.sendMail(mail);
			return new ResponseEntity<>(gson.toJson(configurableEnvironment.getProperty("otp.sent.success")),
					HttpStatus.OK);

		} catch (Exception e) {
			log.error("Action : generate-otp By {} : error while generating OTP! : ", user.getUserName(), e);
			return new ResponseEntity<>(gson.toJson(configurableEnvironment.getProperty("error.otp")),
					HttpStatus.CONFLICT);
		}

	}

	@Override
	@Transactional
	public ResponseEntity<String> forgotPassword(ForgotPasswordModel forgotPasswordModel) {
		/**
		 * using it to parse string as json
		 */
		Gson gson = new Gson();

		Account user = null;

		if (forgotPasswordModel.getValueType() == null || forgotPasswordModel.getValueType().equals("email")) {
			user = accountRepository.findByEmail(forgotPasswordModel.getEmailId());
		} else if (forgotPasswordModel.getValueType() == null || forgotPasswordModel.getValueType().equals("uName")) {
			user = accountRepository.findByUserName(forgotPasswordModel.getEmailId());
		} else {
			throw new InvalidPropertyException("Invalid valueType property");
		}

		try {

			/**
			 * validating otp entered by user is correct or not
			 */
			ResponseEntity<String> validateOtpMess = validateOtp(forgotPasswordModel.getEmailId(),
					forgotPasswordModel.getOtp(), forgotPasswordModel.getValueType());

			if (validateOtpMess.getStatusCodeValue() == 200) {

				/**
				 * check if new password is equal to confirm password
				 */
				if (!forgotPasswordModel.getNewPassword().equals(forgotPasswordModel.getConfirmPassword())) {
					log.error("Action : change-password  : error while updating password! : message : ",
							configurableEnvironment.getProperty("password.not.matching"));
					throw new PasswordMismatchException(
							configurableEnvironment.getProperty("new.password.confirm.password.not.matching"));
				}
				user.setPassword(bCryptPasswordEncoder.encode(forgotPasswordModel.getNewPassword()));
				accountRepository.save(user);
				log.info("Action : forgot-password  message :: password updated successfull for user {}",
						user.getUserName());

				return new ResponseEntity<>(gson.toJson(configurableEnvironment.getProperty("password.forgot.success")),
						HttpStatus.OK);
			} else {
				return validateOtpMess;
			}
		} catch (Exception e) {
			log.error("Action : forgot-password By {}: error while creating new user with payload {} ",
					user.getUserName(), forgotPasswordModel, e);
			return new ResponseEntity<>(gson.toJson(configurableEnvironment.getProperty("password.reset.failure")),
					HttpStatus.CONFLICT);

		}
	}

	@Override
	@Transactional
	public ResponseEntity<String> updateUser(Map<String, Object> updateUserMap, Principal principal) {

		/**
		 * checking if account-id exist
		 */
		if (updateUserMap.get("id") == null || updateUserMap.get("id").toString().isEmpty()) {
			throw new RuntimeException("key : id not found in updateUserMap");
		}

		Account account = accountRepository.findById((Integer) updateUserMap.get("id"));
		if (account == null) {
			throw new EntityNotFoundException("Cannot find such user with id:" + updateUserMap.get("id").toString());
		}

		/**
		 * checking if email-id already exist
		 */
		if (updateUserMap.get("allowDuplicateMail") != null
				&& updateUserMap.get("allowDuplicateMail").toString().equals("false")) {
			if (updateUserMap.get("email") != null && !updateUserMap.get("email").toString().isEmpty()) {
				Account accountByEmail = accountRepository.findByEmail(updateUserMap.get("email").toString());
				if (accountByEmail != null && !accountByEmail.getId().equals(account.getId()))
					throw new EmailMisMatchException(configurableEnvironment.getProperty("email.duplicate.error"));
				account.setEmail(updateUserMap.get("email").toString());
			}
		}  else if (updateUserMap.get("allowDuplicateMail") != null && updateUserMap.get("allowDuplicateMail").toString().equals("true") && updateUserMap.get("email") != null && !updateUserMap.get("email").toString().isEmpty()) {
			account.setEmail(updateUserMap.get("email").toString());
		} else if(updateUserMap.get("allowDuplicateMail") == null  ) {
			account.setEmail(updateUserMap.get("email").toString());
		}

		Gson gson = new Gson();
		try {

			iuserManagementHandler.updateAccountDetails(updateUserMap, account, principal);
			log.info("Action : update-user :  user information updated successfully  for user : {}",
					account.getUserName());
			return new ResponseEntity<>(gson.toJson(configurableEnvironment.getProperty("user.update.success")),
					HttpStatus.OK);

		} catch (Exception e) {
			log.error("Action : update user : error : ", e);
			throw new RuntimeException(e);
		}

	}

	@Override
	@Transactional
	public ResponseEntity<String> enableUserName(String userId, Principal p) {
		Gson gson = new Gson();
		Account user = accountRepository.findById(Integer.valueOf(userId));
		try {
			user.setEnabled(true);
			accountRepository.save(user);
			log.info("Action : enable-user ::{} for user name : {}",
					configurableEnvironment.getProperty("user.enable.success"), user.getUserName());
			return new ResponseEntity<String>(gson.toJson(configurableEnvironment.getProperty("user.enable.success")),
					HttpStatus.OK);

		} catch (Exception e) {
			log.error("Action : enable-user By{}: error while creating new user with userId {}", user.getUserName(),
					user.getId(), e);
			throw new RuntimeException();
		}
	}

	@Override
	@Transactional
	public ResponseEntity<String> disableUserName(String userId, Principal p) {
		Gson gson = new Gson();
		Account user = accountRepository.findById(Integer.valueOf(userId));
		try {
			user.setEnabled(false);
			accountRepository.save(user);
			return new ResponseEntity<String>(gson.toJson(configurableEnvironment.getProperty("user.disable.success")),
					HttpStatus.OK);
		} catch (Exception e) {
			log.error("Action : enable-user By{}: error while creating new user with userId {}", user.getUserName(),
					user.getId(), e);
			throw new RuntimeException();
		}
	}

	@Override
	public List<Designation> getAllDesignations() {

		/**
		 * Fetching all the designations from DB in asc order
		 */
		List<Designation> desgList = designationRepository.findAllByOrderByIdAsc();

		if ((!configurableEnvironment.containsProperty("allow.admin.creation"))
				|| (configurableEnvironment.getProperty("allow.admin.creation").equals("false"))) {

			/**
			 * filtering the role-name of ADMIN
			 */
			desgList = desgList.stream().filter(desgName -> !"ADMIN".equals(desgName.getName()))
					.collect(Collectors.toList());
		}

		return desgList;
	}

	/**
	 * 'allow.admin.creation'-> to be set true in application.properties to allow
	 * admin user to updates its own information.
	 */
	@Override
	public Map<String, List<Account>> getDesgWiseAccount() {

		Map<String, List<Account>> admMap = new LinkedHashMap<>();

		List<Account> userList = new ArrayList<>();

		List<AccountDesignationMapping> accountDesignationMappingList = accountDesignationMappingRepository.findAll();

		// ignore admin role
		if ((!configurableEnvironment.containsProperty("allow.admin.creation"))
				|| configurableEnvironment.getProperty("allow.admin.creation").equals("false")) {

			accountDesignationMappingList = accountDesignationMappingList.stream()
					.filter(roleName -> !"ADMIN".equals(roleName.getDesignation().getName()))
					.collect(Collectors.toList());

		}

		for (AccountDesignationMapping adm : accountDesignationMappingList) {

			if (admMap.containsKey(adm.getDesignation().getName())) {

				admMap.get(adm.getDesignation().getName()).add(adm.getAccount());

			} else {

				userList = new ArrayList<>();
				userList.add(adm.getAccount());
				admMap.put(adm.getDesignation().getName(), userList);
			}

		}

		return admMap;
	}

	@Override
	@Transactional
	public ResponseEntity<Boolean> resetPassword(Map<String, Object> resetPasswordMap, Principal p) {

		if (resetPasswordMap.get("userId") == null || resetPasswordMap.get("userId").toString().isEmpty())
			throw new RuntimeException("key : userId not found in map");

		if (resetPasswordMap.get("newPassword") == null || resetPasswordMap.get("newPassword").toString().isEmpty())
			throw new RuntimeException("key : newPassword not found in map");

		try {
			Account user = accountRepository.findById((int) resetPasswordMap.get("userId"));

			user.setPassword(bCryptPasswordEncoder.encode(resetPasswordMap.get("newPassword").toString()));

			accountRepository.save(user);

			return new ResponseEntity<Boolean>(true, HttpStatus.OK);

		} catch (Exception e) {
			log.error("Action : while resetting password with payload {}", resetPasswordMap, e);
			throw new RuntimeException();
		}

	}

	@Override
	public List<?> getAllAuthorities() {
		// TODO Auto-generated method stub
		return iuserManagementHandler.getAllAuthorities();
	}

}
