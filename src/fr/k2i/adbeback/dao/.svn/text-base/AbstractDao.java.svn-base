package fr.k2i.adbeback.dao;

import android.database.sqlite.SQLiteDatabase;
import fr.k2i.adbeback.database.DbConnexion;

public abstract class AbstractDao {
	protected DbConnexion dbConnexion;
	protected SQLiteDatabase db;
	
	public AbstractDao(DbConnexion dbConnexion) {
		this.dbConnexion = dbConnexion;
		db = this.dbConnexion.getReadableDatabase();
	}
	
	
	public void close(){
		dbConnexion.close();
	}
	
	protected void precommand(){
		if(!db.isOpen()){
			db = dbConnexion.getReadableDatabase();
		}
	}
}
