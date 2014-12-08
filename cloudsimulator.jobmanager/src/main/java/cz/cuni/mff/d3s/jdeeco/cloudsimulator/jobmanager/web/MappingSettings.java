package cz.cuni.mff.d3s.jdeeco.cloudsimulator.jobmanager.web;

public class MappingSettings {

	/* *************************** not logged in *************************** */
	public static final String INDEX = "";

	public static final String LOGIN_ANONYM = "/anonym/login";

	public static final String FORGOTTENPASSWORD = "/client/forgottenpassword";
	public static final String ACTIVATION = "/client/activate";
	public static final String AUTHENTICATION = "/client/auth";
	public static final String REGISTRATION = "/client/register";
	public static final String RESETPASSWORD = "/client/resetpassword";

	
	/* *************************** logged in *************************** */
	public static final String ACTION = "/action";
	public static final String MAIN = "/main";

	
	public static final String NEWPASSWORD = ACTION + "/client/newpassword";
	public static final String CHANGEPASSWORD = ACTION + "/client/changepassword";
	public static final String LOGOUT = ACTION + "/client/logout";


	public static final String PROJECT_ROOT = MAIN + "/project";
	public static final String PROJECT = PROJECT_ROOT + "/{projectId}";
	public static final String PROJECT_CREATE = PROJECT_ROOT + "/create";
	public static final String PROJECT_EDIT = PROJECT + "/edit";
	public static final String PROJECT_HIDE = PROJECT + "/hide";
	public static final String PROJECT_CONFIGUREVISIBILITY = PROJECT_ROOT + "/configurevisibility";

	public static final String CONFIGURATION_ROOT = MAIN + "/configuration";
	public static final String CONFIGURATION = CONFIGURATION_ROOT + "/{configurationId}";
	public static final String CONFIGURATION_ADD = CONFIGURATION_ROOT + "/add/{projectId}";
	public static final String CONFIGURATION_EDIT = CONFIGURATION + "/edit";
	public static final String CONFIGURATION_CONFIGUREVISIBILITY = CONFIGURATION_ROOT + "/configurevisibility/{projectId}";

	public static final String EXECUTION_ROOT = MAIN + "/execution";
	public static final String EXECUTION = EXECUTION_ROOT + "/{executionId}";
	public static final String EXECUTION_RUN = EXECUTION_ROOT + "/run/{configurationId}";
	public static final String EXECUTION_STOP = EXECUTION + "/stop";
	public static final String EXECUTION_EDIT = EXECUTION + "/edit";


	public static final String INFRASTRUCTURE = MAIN + "/infrastructure";
	public static final String SIMULATIONQUEUE = MAIN + "/simulationqueue";
	
	public static String GetFullUri(String root, String subPage) {
		if (root.endsWith("/") && subPage.startsWith("/")){
			return root + subPage.substring(1); // we don't want double slash
		}
		return root + subPage;
	}
	
}
