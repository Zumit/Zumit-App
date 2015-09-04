package test.com.sqlite.dbtest;

import android.app.Activity;
import android.content.ContentValues;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import test.com.sqlite.R;
import test.com.sqlite.db.DatabaseHelper;
import test.com.sqlite.domain.UserEx;

public class MainActivity extends Activity {

	private final String db_name = "DBTest";
	private int rowNo = 0;

	java.util.Date date = new java.util.Date();
	long currentTime = date.getTime();
	private final String CREATE_TABLE = "create table if not exists user(id INTEGER PRIMARY KEY AUTOINCREMENT, sendTime integer, message varchar(500))";

	private EditText tableid, mmessage, mDeleteId;
	private TextView mResult;
	private UserEx userEx = null;

	private int dbVersion = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button btnCreateDB = (Button) findViewById(R.id.db_create);
		Button btnCreateTable = (Button) findViewById(R.id.table_create);
		Button btnInsertTable = (Button) findViewById(R.id.table_insert);
		Button btnUpdateTable = (Button) findViewById(R.id.table_update);
		Button btnDeleteTable = (Button) findViewById(R.id.table_delete);
		Button btnQueryTable = (Button) findViewById(R.id.table_query);

		btnCreateDB.setOnClickListener(new CreateDBListener());
		btnCreateTable.setOnClickListener(new CreateTableListener());
		btnInsertTable.setOnClickListener(new InsertTableListener());
		btnUpdateTable.setOnClickListener(new UpdateTableListener());
		btnDeleteTable.setOnClickListener(new DeleteTableListener());
		btnQueryTable.setOnClickListener(new QueryTableListener());

		tableid = (EditText) findViewById(R.id.tableid);
		mmessage = (EditText) findViewById(R.id.message);
		mDeleteId = (EditText) findViewById(R.id.deleteid);
		mResult = (TextView) findViewById(R.id.query_result);

		userEx = new UserEx(MainActivity.this);

		try {
			this.dbVersion = MainActivity.this.getPackageManager()
					.getPackageInfo(MainActivity.this.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ʵ��Ӧ�õ�ʱ��Ӧ���÷���������DatabaseHelper.java�в�����
	 * */
	class CreateDBListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			try {
				userEx.openDBConnect();

				show("successfully created!");
			} catch (Exception e) {
				e.printStackTrace();
				show("fail" + e.getMessage());
			} finally {
				userEx.closeDBConnect();
			}

		}

	}

	/**
	 * ʵ��Ӧ�õ�ʱ��Ӧ���÷���������DatabaseHelper.java�в�����
	 * */


	/**
	 * ʵ��Ӧ�õ�ʱ��Ӧ���÷���������DatabaseHelper.java�в�����
	 * */
	class CreateTableListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			DatabaseHelper dbHelper = null;
			SQLiteDatabase db = null;
			try {

				dbHelper = new DatabaseHelper(MainActivity.this, db_name,
						dbVersion);
				db = dbHelper.getWritableDatabase();

				db.execSQL(CREATE_TABLE);
				System.out.println("MainActivity  create table user");
				show("table created");
			} catch (Exception e) {
				e.printStackTrace();
				show("table fail" + e.getMessage());
			} finally {
				db.close();
				dbHelper.close();
			}
		}

	}

	class InsertTableListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			try {

				rowNo++;
				ContentValues values = new ContentValues();
				values.put("sendTime", "row" + rowNo);
				values.put("message", "row" + rowNo);

				userEx.Add(values);
				show("add ok");
				System.out.println("MainActivity  add ok" + rowNo);
			} catch (Exception e) {
				e.printStackTrace();
				show("fail" + e.getMessage());
			}

		}

	}

	class UpdateTableListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			try {

				ContentValues values = new ContentValues();
				values.put("name", mmessage.getText().toString());

				userEx.Update(values, "id=?", new String[] { tableid.getText()
						.toString() != null ? tableid.getText().toString()
						: "1" });
				show("update ok");
			} catch (Exception e) {
				e.printStackTrace();
				show("update fail" + e.getMessage());
			}
		}
	}

	class DeleteTableListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			try {
				userEx.Delete("id=?", new String[] { mDeleteId.getText()
						.toString() });

				System.out.println("MainActivity deleted"
						+ mDeleteId.getText().toString());
				show("deleted ok");
			} catch (Exception e) {
				e.printStackTrace();
				show("deleted fail" + e.getMessage());
			}
		}
	}

	class QueryTableListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			getNewVersionsers();
		}
	}

	private void getOldVersionUsers() {
		try {

			Cursor cursor;

			cursor = userEx.Query(new String[] { "id", "currentTime", "message" },
					null, null, null, null, null);

			String pre = "";

			if (cursor.getCount() > 0) {
				System.out.println("old version");
				while (cursor.moveToNext()) {

					String id = cursor.getString(cursor.getColumnIndex("id"));
					String name = cursor.getString(cursor
							.getColumnIndex("name"));
					String message = cursor.getString(cursor
							.getColumnIndex("message"));

					pre = pre + "\r\n" + "id : " + id + "; name : " + name
							+ "; message : " + message;

				}
				mResult.setText(pre);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			userEx.closeDBConnect();
		}
	}

	private void getNewVersionsers() {
		try {

			Cursor cursor;
			cursor = userEx.Query(new String[] { "id", "name", "message" },
					null, null, null, null, null);

			String pre = "";

			if (cursor.getCount() > 0) {
				System.out.println("new version");
				while (cursor.moveToNext()) {

					String id = cursor.getString(cursor.getColumnIndex("id"));
					String name = cursor.getString(cursor
							.getColumnIndex("name"));
					String message = cursor.getString(cursor
							.getColumnIndex("message"));

					pre = pre + "\r\n" + "id : " + id + "; name : " + name
							+ "; message : " + message + "; ";

				}
				mResult.setText(pre);
			} else {
				mResult.setText("û�в�ѯ����ݣ�");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			userEx.closeDBConnect();
		}
	}

	private void show(String t) {
		Toast.makeText(getApplicationContext(), t, Toast.LENGTH_LONG).show();
	}

}
