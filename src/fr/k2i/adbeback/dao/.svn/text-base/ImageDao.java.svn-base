package fr.k2i.adbeback.dao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import fr.k2i.adbeback.database.DbConnexion;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageDao extends AbstractDao{
	
	public ImageDao(DbConnexion dbConnexion) {
		super(dbConnexion);
	}

	private static final String SELECT_ALL;
	private static final String SELECT_BY_URL;
	private static final String INSERT_LOGO;
	private static final String UPDATE_LOGO;
	private static final String DELETE_ALL;
	 private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	static {
		SELECT_ALL = "SELECT img from LOGO";
		SELECT_BY_URL = "SELECT img from LOGO where url=?";
		INSERT_LOGO = "INSERT INTO LOGO(img,url) VALUES(?,?)";
		UPDATE_LOGO = "UPDATE LOGO SET img=? WHERE url=?";
		DELETE_ALL = "DELETE FROM LOGO WHERE 1=1";
	}


	/**
	 * return bitmap database with a specific url
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap getBitmapByUrl(String url) {
		precommand();
		String[] args = { url };
		Cursor cursor = db.rawQuery(SELECT_BY_URL, args);
		if (cursor.getCount() == 1) {
			cursor.moveToFirst();
			byte[] blob = cursor.getBlob(0);
			cursor.close();
			ByteArrayInputStream bInputStream = new ByteArrayInputStream(blob);
			return BitmapFactory.decodeStream(bInputStream);
		}
		cursor.close();
		return null;

	}

	/**
	 * if existe return true otherwise return false
	 * 
	 * @param url
	 * @return
	 */
	private Boolean isExistUrl(String url) {
		String[] args = { url };
		Cursor cursor = db.rawQuery(SELECT_BY_URL, args);
		int count = cursor.getCount();
		cursor.close();
		return (count == 1);
	}

	/**
	 * get String from inputStream
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private byte[] encodedBitamp(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		copy(input, output);
		return output.toByteArray();

		/*
		 * Bitmap bitmap = BitmapFactory.decodeStream(in); ByteArrayOutputStream
		 * bout = new ByteArrayOutputStream();
		 * bitmap.compress(CompressFormat.PNG, 100, bout); return
		 * bout.toByteArray();
		 */
	}

	private int copy(InputStream input, OutputStream output) throws IOException {
		long count = copyLarge(input, output);
		if (count > Integer.MAX_VALUE) {
			return -1;
		}
		return (int) count;
	}

	private long copyLarge(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	/**
	 * Save or Update Bitmap
	 * 
	 * @param url
	 * @param in
	 * @throws IOException
	 */
	public void saveBitmap(String url, InputStream in) throws IOException {
		precommand();
		Object[] args = { encodedBitamp(in), url };
		String query = null;
		if (isExistUrl(url)) {// update
			query = UPDATE_LOGO;
		} else {// insert
			query = INSERT_LOGO;
		}
		db.execSQL(query, args);
	}

	public List<Bitmap> getAll() {
		precommand();
		List<Bitmap> res = new ArrayList<Bitmap>();
		String[] args = {};
		Cursor cursor = db.rawQuery(SELECT_ALL, args);
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.move(i + 1);
			byte[] blob = null;
			try {
				blob = cursor.getBlob(0);
				ByteArrayInputStream bInputStream = new ByteArrayInputStream(
						blob);
				res.add(BitmapFactory.decodeStream(bInputStream));
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
		cursor.close();
		return res;
	}

	public void deleteAll() {
		precommand();
		db.execSQL(DELETE_ALL);
	}
}
