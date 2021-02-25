package org.sdrc.usermgmt.service;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.sdrc.usermgmt.domain.Account;
import org.sdrc.usermgmt.domain.Designation;
import org.sdrc.usermgmt.model.ChangePasswordModel;
import org.sdrc.usermgmt.model.ForgotPasswordModel;
import org.springframework.http.ResponseEntity;

public interface JPAUserManagementService {

	ResponseEntity<String> createUser(Map<String, Object> map, Principal p);

	ResponseEntity<String> changePassoword(ChangePasswordModel changePasswordModel, Principal p);

	ResponseEntity<String> validateOtp(String userName, String otp, String valueType);

	ResponseEntity<String> sendOtp(String userName, String valueType);

	ResponseEntity<String> forgotPassword(ForgotPasswordModel forgotPasswordModel);

	ResponseEntity<String> updateUser(Map<String, Object> updateUserMap, Principal p);

	ResponseEntity<String> enableUserName(String userId, Principal p);

	ResponseEntity<String> disableUserName(String userId,Principal p);

	List<Designation> getAllDesignations();

	Map<String, List<Account>> getDesgWiseAccount();

	ResponseEntity<Boolean> resetPassword(Map<String, Object> resetPasswordMap, Principal p);

	List<?> getAllAuthorities();

}
