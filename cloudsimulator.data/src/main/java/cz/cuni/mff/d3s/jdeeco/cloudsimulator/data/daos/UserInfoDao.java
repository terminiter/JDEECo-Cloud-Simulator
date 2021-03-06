package cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.daos;

import cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.LoginCodeInfo;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.models.User;
import cz.cuni.mff.d3s.jdeeco.cloudsimulator.data.models.UserInfo;

public interface UserInfoDao extends BaseDao<UserInfo> {

	User findByActivationCode(String activationCode);

	User findByResetPasswordCode(String resetPasswordCode);

	LoginCodeInfo findByAnonymousLoginCode(String anonymousLoginCode);

	/**
	 * Retrieves reset password code or null if not exists.
	 * 
	 * @param user
	 * @return
	 */
	String getResetPasswordCode(User user);

	/**
	 * Stores reset password code for user as user info entry and delete all previous entries. To delete current entry
	 * call with resetCode == null.
	 * 
	 * @param user
	 * @param resetCode
	 */
	void setResetPasswordCode(User user, String resetCode);

	/**
	 * Retrieves activation code or null if not exists.
	 * 
	 * @param userId
	 * @return
	 */
	String getActivationCode(int userId);

	/**
	 * Stores activation code in user info and delete all previous entry for this users activation codes. Call with
	 * activationCode == null to delete activationCode entry.
	 * 
	 * @param user
	 * @param activationCode
	 */
	void setActivationCode(User user, String activationCode);

	/**
	 * Retrieves anonymous code for user or null if not set.
	 * 
	 * @param userId
	 *            User associated with code.
	 * @return Anonymous login code or null.
	 */
	LoginCodeInfo getAnonymousLoginCode(int userId);

	/**
	 * Stores anonymous login code in user info and delete all previous entry for this user's anonymous login codes.
	 * Call with anonymousLoginCode == null to delete current entry.
	 * 
	 * @param anonymousLoginCode
	 *            Code structure.
	 */
	void setAnonymousLoginCode(LoginCodeInfo anonymousLoginCode);
}