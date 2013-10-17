package fr.k2i.adbeback.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbConnexion extends SQLiteOpenHelper{
	private static final String DATABASE_NAME = "adbeback";
	private static final int DATABASE_VERSION = 1;

	private String[]creates = {
			"CREATE TABLE LOGO (id INTEGER PRIMARY KEY,url TEXT,img BLOB)",
			"CREATE TABLE USER (pseudo TEXT PRIMARY KEY,pwd TEXT,defaultprofile INTEGER)"
	};
	
	public DbConnexion(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (String createTable : creates) {
			db.execSQL(createTable);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
