package fr.k2i.adbeback.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import fr.k2i.adbeback.business.user.UserBean;
import fr.k2i.adbeback.database.DbConnexion;

public class UserDao extends AbstractDao{
	public UserDao(DbConnexion dbConnexion) {
		super(dbConnexion);
	}

	private static final String SELECT;
	private static final String SELECT_DEFAULT;
	private static final String SELECT_EXIST;
	private static final String INSERT;
	private static final String UPDATE;
	private static final String DELETE;
	private static final String DELETE_ALL;

	static {
		SELECT = "SELECT pseudo,pwd,defaultprofile from USER";
		SELECT_DEFAULT = "SELECT pseudo,pwd,defaultprofile from USER where defaultprofile=1";
		SELECT_EXIST = "SELECT pseudo,pwd,defaultprofile from USER where pseudo=?";
		INSERT = "INSERT INTO USER(pwd,defaultprofile,pseudo) VALUES(?,?,?)";
		UPDATE = "UPDATE USER SET pwd=?, defaultprofile=? WHERE pseudo=?";
		DELETE = "DELETE USER WHERE pseudo=?";
		DELETE_ALL = "DELETE USER";
	}


	/**
	 * Get Default user
	 * 
	 * @return
	 */
	public UserBean getDefaultUser() {
		precommand();
		Cursor cursor = db.rawQuery(SELECT_DEFAULT, null);
		if (cursor.getCount() == 0)
			return null;
		cursor.moveToFirst();
		UserBean u = new UserBean();
		u.setPseudo(cursor.getString(0));
		u.setPwd(cursor.getString(1));
		u.setDefaultProfile((cursor.getInt(2) == 1));
		cursor.close();
		return u;
	}

	/**
	 * Get all users
	 * 
	 * @return
	 */
	public List<UserBean> getUsers() {
		precommand();
		List<UserBean> res = new ArrayList<UserBean>();
		Cursor cursor = db.rawQuery(SELECT, null);
		for (int i = 0; i < cursor.getCount(); i++) {
			if (cursor.move(i + 1)) {

				UserBean u = new UserBean();
				if (!cursor.isNull(0)) {
					u.setPseudo(cursor.getString(0));
				}
				if (!cursor.isNull(1)) {
					u.setPwd(cursor.getString(1));
				}
				if (cursor.isNull(2)) {
					u.setDefaultProfile(false);
				} else {
					u.setDefaultProfile(((2) == 1));
				}
				res.add(u);
			}
		}
		cursor.close();
		return res;
	}

	/**
	 * Set Default user
	 * 
	 * @param pseudo
	 */
	public void setDefaultProfile(String pseudo) {
		List<UserBean> users = getUsers();
		for (UserBean userBean : users) {
			if (userBean.getDefaultProfile() == true
					&& !userBean.getPseudo().equals(pseudo)) {
				userBean.setDefaultProfile(false);
				saveUser(userBean);
			} else if (userBean.getPseudo().equals(pseudo)
					&& userBean.getDefaultProfile() == false) {
				userBean.setDefaultProfile(true);
				saveUser(userBean);
			}
		}
	}

	/**
	 * return true if the user existe otherwise false
	 * 
	 * @param pseudo
	 * @return
	 */
	private Boolean isExist(String pseudo) {
		precommand();
		String[] args = { pseudo };
		Cursor cursor = db.rawQuery(SELECT_EXIST, args);
		int count = cursor.getCount();
		cursor.close();
		return (count == 1);
	}

	/**
	 * save user
	 * 
	 * @param user
	 */
	public void saveUser(UserBean user) {
		precommand();
		String[] args = { user.getPwd(),
				(user.getDefaultProfile()) ? "1" : "0", user.getPseudo() };
		String query = null;
		if (isExist(user.getPseudo())) {// update
			query = UPDATE;
		} else {// insert
			query = INSERT;
		}
		db.execSQL(query, args);
	}

	/**
	 * delete user
	 * 
	 * @param pseudo
	 */
	public void deleteProfile(String pseudo) {
		precommand();
		String[] args = { pseudo };
		db.execSQL(DELETE, args);
	}

	public void deleteAll() {
		precommand();
		db.delete("USER", "", null);
	}
}
