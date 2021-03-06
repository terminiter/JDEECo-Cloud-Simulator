package cz.cuni.mff.d3s.jdeeco.cloudsimulator.administration.web.notlogged;

import javax.annotation.Resource;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import cz.cuni.mff.d3s.jdeeco.cloudsimulator.administration.security.PasswordHelper;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.administration.utils.ValidatorHelper;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.administration.web.notlogged.data.RegisterForm;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.common.extensions.StringEx;

/**
 * Validates registration form.
 */
public class RegisterValidator implements Validator {

	/**
	 * Register validation error codes.
	 */
	public static enum ErrorType {
		/**
		 * Form is not completely filled.
		 */
		NOT_FILLED,
		/**
		 * Email was not specified.
		 */
		EMAIL_NOT_SPECIFIED,
		/**
		 * Email has incorrect format.
		 */
		EMAIL_INVALID_VALUE,
		/**
		 * Password is too short.
		 */
		PASSWORD_TOO_SHORT,
		/**
		 * Password contains not alphanumeric characters.
		 */
		PASSWORD_NOT_ALPHANUMERIC,
		/**
		 * Password and passwordverify does not match.
		 */
		PASSWORD_VERIFY_NOT_MATCH,
	}
	
	@Resource
	private PasswordHelper passwordHelper;
	
	private static final String MISSING_EMAIL = "Email is not specified.";
	private static final String SHORT_PASSWORD = "Password must have at least 8 characters.";
	private static final String BAD_EMAIL_FORMAT = "E-mail address is in bad format. Correct format is name@domain.com (or other top level domain).";
	private static final String BAD_PASS_CHARS = "Password can contain only alphanumerical characters.";
	private static final String PASS_DONOT_MATCH = "Passwords does not match!";

	public static final String EMAIL_IN_USE = "Email address is already in use, for password reset use this %s.";
	private static final String FILL_ALL_ENTRIES = "You must fill all entries!";
	
	@Override
	public boolean supports(Class<?> clazz) {
		return RegisterForm.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		RegisterForm validatedObj = (RegisterForm) target;
		if (validatedObj == null) {
			errors.rejectValue(RegisterForm.EMAIL_FIELD, ErrorType.EMAIL_INVALID_VALUE.name(), MISSING_EMAIL);
			errors.rejectValue(RegisterForm.PASSWORD_FIELD, ErrorType.PASSWORD_TOO_SHORT.name(), SHORT_PASSWORD);
		} else {
			
			if (StringEx.isNullOrEmpty(validatedObj.getEmail())
				|| StringEx.isNullOrEmpty(validatedObj.getPassword())
				|| StringEx.isNullOrEmpty(validatedObj.getPasswordVerify())){
				errors.reject(ErrorType.NOT_FILLED.name(), FILL_ALL_ENTRIES);				
			}
			
			if (ValidatorHelper.isNotEmail(validatedObj.getEmail())) {
				errors.rejectValue(RegisterForm.EMAIL_FIELD, ErrorType.EMAIL_INVALID_VALUE.name(), BAD_EMAIL_FORMAT);
			}

			if (passwordHelper.isPasswordTooWeak(validatedObj.getPassword())) {
				errors.rejectValue(RegisterForm.PASSWORD_FIELD, ErrorType.PASSWORD_TOO_SHORT.name(), SHORT_PASSWORD);
			}

			if (passwordHelper.hasPasswordIncorrectCharacters(validatedObj.getPassword())) {
				errors.rejectValue(RegisterForm.PASSWORD_FIELD, ErrorType.PASSWORD_NOT_ALPHANUMERIC.name(), BAD_PASS_CHARS);
			}

			if (!validatedObj.getPassword().equals(validatedObj.getPasswordVerify())) {
				errors.rejectValue(RegisterForm.PASSWORD_VERIFY_FIELD, ErrorType.PASSWORD_VERIFY_NOT_MATCH.name(), PASS_DONOT_MATCH);
			}
			
			// Email collisions are not checked here. It must be checked on
			// database insert.
		}
	}
}
