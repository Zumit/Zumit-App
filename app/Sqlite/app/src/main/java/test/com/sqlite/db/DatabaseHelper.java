package test.com.sqlite.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * ��ݿ����
 * 
 * */
public class DatabaseHelper extends SQLiteOpenHelper {

	private DatabaseHelper(Context context, String dbName,
			CursorFactory factory, int version) {
		super(context, dbName, factory, version);
	}

	public DatabaseHelper(Context context, String dbName, int version) {
		this(context, dbName, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		System.out.println("DBHelper onCreate");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.out.println("DBHelper onUpgrade");

	}
}
