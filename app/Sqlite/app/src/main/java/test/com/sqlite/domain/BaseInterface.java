package test.com.sqlite.domain;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * ҵ������ӿڣ����Զ����ʺ��Լ��Ĳ�ѯ����
 * 
 * */
public interface BaseInterface {

	/**
	 * ����
	 * 
	 * @param ��������
	 *            key-value
	 * */
	abstract void Add(ContentValues values);

	/**
	 * ����
	 * 
	 * @param values
	 *            �������� key-value
	 * @param whereClause
	 *            �������� ���磺id=?��?Ϊͨ���
	 * @param whereArgs
	 *            �������� ���磺new String[]{"1"}
	 * 
	 * */
	abstract void Update(ContentValues values, String whereClause,
						 String[] whereArgs);

	/**
	 * ɾ��
	 * 
	 * @param whereClause
	 *            ɾ������ ���磺id=?��?Ϊͨ���
	 * @param whereArgs
	 *            ɾ��� ���磺new String[]{"1"}
	 * 
	 * */
	abstract void Delete(String whereClause, String[] whereArgs);

	/**
	 * ��ѯ
	 * 
	 * ������Cursorʹ��֮�󣬲ſ��Թر���ݿ����ӡ� ���磺Cursor.moveToNext()ִ�е�ʱ�򣬲Ż�ȥ��ѯ��ݿ����Ƿ�����ݡ�
	 * 
	 * @param columns
	 *            ������
	 * @param selection
	 *            ��ѯ���� ���磺id=?��?Ϊͨ���
	 * @param selectionArgs
	 *            �������� ���磺 new String[]{"1"}
	 * @param groupBy
	 *            ����
	 * @param having
	 * @param orderBy
	 *            ����
	 * 
	 * */
	abstract Cursor Query(String[] columns, String selection,
						  String[] selectionArgs, String groupBy, String having,
						  String orderBy);

}
