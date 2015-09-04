package test.com.sqlite.domain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Userҵ�����
 * 
 * @author С���� 2014��2��25�� 09:22:45 QQ��1065885952
 * */
public class UserEx extends BaseEx {

	private final static String TABLENAME = "user";

	public UserEx(Context context) {
		super(context);
	}

	@Override
	public void Add(ContentValues values) {
		try {
			openDBConnect();

			getDb().insert(TABLENAME, null, values);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDBConnect();
		}
	}

	@Override
	public void Update(ContentValues values, String whereClause,
			String[] whereArgs) {
		try {
			openDBConnect();

			getDb().update(TABLENAME, values, whereClause, whereArgs);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDBConnect();
		}
	}

	@Override
	public void Delete(String whereClause, String[] whereArgs) {
		try {
			openDBConnect();

			getDb().delete(TABLENAME, whereClause, whereArgs);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeDBConnect();
		}
	}

	// �÷��������޸ķ���ֵ����ΪList<T>�������Զ��巵��ֵ��ע��ر���ݿ����ӡ�
	@Override
	public Cursor Query(String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy) {
		Cursor cursor = null;
		try {
			openDBConnect();

			cursor = getDb().query(TABLENAME, columns, selection,
					selectionArgs, groupBy, having, orderBy);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// TODO:�ر���ݿ����ӵĶ���(super.stopDBConnect())������Cursorʹ�ý���֮��ִ�С�
		}

		return cursor;
	}

}
