package cz.cuni.mff.d3s.jdeeco.cloudsimulator.administration.web.notlogged;

import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import cz.cuni.mff.d3s.jdeeco.cloudsimulator.administration.AppContext;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.models.User;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.administration.security.UserHelper;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.administration.services.UserOperationErrorType;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.administration.services.UserOperationException;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.administration.services.UserService;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.administration.utils.HTMLHelper;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.administration.web.MappingSettings;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.administration.web.ViewParameters;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.administration.web.notlogged.data.RegisterForm;

/**
 * Register form for users.
 */
@Controller
public class RegisterController {

	/**
	 * Logging.
	 */
	private Logger logger = LoggerFactory.getLogger(RegisterController.class);

	/**
	 * Name of register page template.
	 */
	private static final String REGISTER_VIEW = "notlogged/register";

	/**
	 * State of registration. Can have values: 0 - no submitted form 1 - submitted form contains errors 2 - activation
	 * email link sent
	 */
	private static final String REGISTER_STATE_VAR = "registerState";

	/**
	 * Logged user.
	 */
	@SuppressWarnings("unused")
	private static final String LOGGED_USER_VAR = "user";

	private static final int FORM_NOT_SUBMITTED_STATE = 0;
	private static final int FORM_ERROR_STATE = 1;
	private static final int FORM_SUBMITTED_STATE = 2;

	
	/**
	 * Validation of register form results.
	 */
	@Resource
	private RegisterValidator registerValidator;

	/**
	 * User operations.
	 */
	@Resource
	private UserService userService;

	/**
	 * The application context.
	 */
	@Resource
	private AppContext appContext;

	@RequestMapping(value = MappingSettings.REGISTRATION, method = RequestMethod.GET)
	public ModelAndView showForm() throws UnsupportedEncodingException {
		if (NotLoggedHelper.isUserLoggedIn(userService)) {
			return NotLoggedHelper.redirectToMainModel();
		}

		return defaultModel().addObject(REGISTER_STATE_VAR, FORM_NOT_SUBMITTED_STATE);
	}

	@RequestMapping(value = MappingSettings.REGISTRATION, method = RequestMethod.POST)
	public ModelAndView validateForm(@ModelAttribute RegisterForm registerForm, BindingResult result) {

		User loggedUser = UserHelper.getAuthenticatedUser();
		if (NotLoggedHelper.isUserLoggedIn(userService)) {
			logger.debug("Already activated user with id '{}' is trying to register.", loggedUser.getId());
			return NotLoggedHelper.redirectToMainModel();
		}
		
		registerValidator.validate(registerForm, result);
		if (result.hasErrors()) {
			return renderErrors(registerForm, result);
		}
		try {
			User user = userService.registerUser(registerForm.getEmail(), registerForm.getPassword());

			logger.info("User {} has been succesfully registered.", user.getEmail());
		} catch (UserOperationException ex) {
			return renderRegisterError(registerForm, result, ex.getErrorType());
		} catch (Exception ex) {
			// Some error happens, probably email is already used.
			logger.error("Error registering user", ex);
		}
		return renderSuccess();
	}

	/**
	 * @return ModelAndView of success register page.
	 */
	private ModelAndView renderSuccess() {
		return defaultModel().addObject(REGISTER_STATE_VAR, FORM_SUBMITTED_STATE);
	}

	/**
	 * Renders errors in values.
	 *
	 * @param form
	 *            Posted form.
	 * @param result
	 *            Validation results.
	 * @return Model and view.
	 */
	private ModelAndView renderErrors(final RegisterForm form, final BindingResult result) {
		ModelAndView model = defaultModel().addObject(REGISTER_STATE_VAR, FORM_ERROR_STATE);
		model.addObject(ViewParameters.MODEL_VAR, form);
		FieldError er = result.getFieldError();
		model.addObject(ViewParameters.ERROR_MSG_VAR, er.getDefaultMessage());
		return model;
	}

	/**
	 * Renders registering exception error.
	 *
	 * @param form
	 *            Posted form.
	 * @param result
	 *            Results of validation.
	 * @param errorType
	 *            Type of registering error.
	 * @return ModelAndView with errors.
	 */
	private ModelAndView renderRegisterError(final RegisterForm form, BindingResult result,
			final UserOperationErrorType errorType) {
		if (errorType.equals(UserOperationErrorType.EMAIL_ALREADY_REGISTERED)) {
			String forgottenPasswordLink = HTMLHelper.createLink(MappingSettings.FORGOTTENPASSWORD, "site");
			result.rejectValue(RegisterForm.EMAIL_FIELD, "error.not-already-used", String.format(RegisterValidator.EMAIL_IN_USE, forgottenPasswordLink));
			return renderErrors(form, result);
		}
		return renderSuccess();
	}
	
	/**
	 * @return New instance of default register ModelAndView.
	 */
	private ModelAndView defaultModel() {
		return new ModelAndView(REGISTER_VIEW).addObject(ViewParameters.CANCEL_URI, appContext.getSiteRoot());
	}
}
