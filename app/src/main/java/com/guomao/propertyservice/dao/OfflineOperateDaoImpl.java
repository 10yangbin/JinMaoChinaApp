package com.guomao.propertyservice.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.guomao.propertyservice.config.CommenAction;
import com.guomao.propertyservice.model.offlineoperate.OfflineOperateDao;
import com.guomao.propertyservice.model.offlineoperate.Operation;
import com.guomao.propertyservice.util.L;
import com.guomao.propertyservice.util.StringUtil;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;


/**
 * 离线报事的dao接口实现
 * @author Administrator
 *
 */
public class OfflineOperateDaoImpl extends AbstractDao implements
		OfflineOperateDao {

	public static final String OFFLINE_OPERATE_TABLE = "offlineoperate";

	public static final String ID_COLUMN = "_id";
	public static final String OPERATE_TASKID_COLUMN = "operate_task_id";
	public static final String OPERATE_A_COLUMN = "operate_a";
	public static final String OPERATE_P_COLUMN = "operate_p";
	public static final String OPERATE_FILES_COLUMN = "operate_files";
	public static final String OPERATE_STATUS_COLUMN = "operate_status";
	public static final String SUBMIT_TIME_COLUMN = "submit_time";
	public static final String CREATE_TIME_COLUMN = "create_time";
	public static final String OPERATE_RESULT_COLUMN = "operate_result";

	public static final String[] ALL_COLUMNS = { ID_COLUMN,
			OPERATE_TASKID_COLUMN, OPERATE_A_COLUMN, OPERATE_P_COLUMN,
			OPERATE_FILES_COLUMN, OPERATE_STATUS_COLUMN, SUBMIT_TIME_COLUMN,
			SUBMIT_TIME_COLUMN, CREATE_TIME_COLUMN, OPERATE_RESULT_COLUMN };

	public OfflineOperateDaoImpl(SQLiteDatabase readDb, SQLiteDatabase writeDb,
			Context context) {
		super(readDb, 2, writeDb, context);
	}

	@Override
	public long save(Operation operate) {
		if (operate == null) {
			throw new IllegalArgumentException("operate must be not null");
		}
		ContentValues values = getContentValues(operate);
		if (operate.getOperate_id() != null && operate.getOperate_id() > 0
				&& isExist(operate.getOperate_id())) {
			update(values, operate.getOperate_id() + "");
			return operate.getOperate_id();
		}
		return getWriteDb(2).insert(OFFLINE_OPERATE_TABLE, null, values);
	}

	@Override
	public void saveOrUpdate(Operation operate) {
		if (operate == null) {
			throw new IllegalArgumentException("operate must be not null");
		}
		ContentValues values = getContentValues(operate);
		if (operate.getOperate_a().equals(CommenAction.ACTION_WO_BS)
				|| operate.getOperate_a().equals(CommenAction.ACTION_WO_ZXTJ)) {
			long ret = getWriteDb(2)
					.insert(OFFLINE_OPERATE_TABLE, null, values);
			String text = null;
			if (ret == -1) {
				if (operate.getOperate_a().equals(CommenAction.ACTION_WO_BS)) {
					text = "离线报事失败";
				} else if (operate.getOperate_a().equals(
						CommenAction.ACTION_WO_ZXTJ)) {
					text = "离线执行失败";
				}
				Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
			} else {
				if (operate.getOperate_a().equals(CommenAction.ACTION_WO_BS)) {
					text = "离线报事成功";
				} else if (operate.getOperate_a().equals(
						CommenAction.ACTION_WO_ZXTJ)) {
					text = "离线执行成功";
				}
			}
			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
		} else {
			if (operate.getOperate_taskId() != null
					&& isExistByTaskId(operate.getOperate_taskId())) {
				String sql = "select _id from offlineoperate where operate_a = '"
						+ operate.getOperate_a()
						+ "' and operate_task_id = '"
						+ operate.getOperate_taskId() + "'";
				JSONArray jsonArray = DB_business.db_select(0, sql, "");
				if (jsonArray.length() != 0) {
					String operate_id = null;
					try {
						JSONObject json = jsonArray.getJSONObject(0);
						operate_id = json.getString("_id");
					} catch (Exception e) {
						e.printStackTrace();
					}
					String text = null;
					try {
						update(values, operate_id);
						if (operate.getOperate_a().equals(
								CommenAction.ACTION_WO_JS)) {
							text = "离线接收成功";
						} else if (operate.getOperate_a().equals(
								CommenAction.ACTION_WO_WC)) {
							text = "离线完成工单成功";
						}
					} catch (Exception e) {
						e.printStackTrace();
						if (operate.getOperate_a().equals(
								CommenAction.ACTION_WO_JS)) {
							text = "离线接收失败";
						} else if (operate.getOperate_a().equals(
								CommenAction.ACTION_WO_WC)) {
							text = "离线完成工单失败";
						}
						Toast.makeText(context, text, Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					long ret = getWriteDb(2).insert(OFFLINE_OPERATE_TABLE,
							null, values);
					String text = null;
					if (ret == -1) {
						if (operate.getOperate_a().equals(
								CommenAction.ACTION_WO_JS)) {
							text = "离线接收失败";
						} else if (operate.getOperate_a().equals(
								CommenAction.ACTION_WO_WC)) {
							text = "离线完成工单失败";
						}
						Toast.makeText(context, text, Toast.LENGTH_SHORT)
								.show();
					} else {
						if (operate.getOperate_a().equals(
								CommenAction.ACTION_WO_JS)) {
							text = "离线接收成功";
						} else if (operate.getOperate_a().equals(
								CommenAction.ACTION_WO_WC)) {
							text = "离线完成工单成功";
						}
					}
					Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
				}
			}
		}

	}

	private void updateByTaskId(ContentValues values, String pKey) {
		if (values == null || values.size() <= 0 || StringUtil.isNull(pKey)) {
			return;
		}
		getWriteDb(2).update(OFFLINE_OPERATE_TABLE, values,
				GetWhereSql(OPERATE_TASKID_COLUMN), new String[] { pKey });

	}

	private void update(ContentValues values, String pKey) {
		if (values == null || values.size() <= 0 || StringUtil.isNull(pKey)) {
			return;
		}
		getWriteDb(2).update(OFFLINE_OPERATE_TABLE, values,
				GetWhereSql(ID_COLUMN), new String[] { pKey });
	}

	private boolean isExist(int id) {
		boolean isExist = false;
		try {
			Cursor c = getReadDb(2).query(OFFLINE_OPERATE_TABLE,
					new String[] { ALL_COUNT_COLUMN }, GetWhereSql(ID_COLUMN),
					new String[] { id + "" }, null, null, null);

			if (c != null) {
				if (c.getCount() > 0) {
					isExist = true;
				}
				c.close();
			}
		} catch (Exception e) {
		}
		return isExist;
	}

	private boolean isExistByTaskId(String id) {
		boolean isExist = false;
		try {
			Cursor c = getReadDb(2).query(OFFLINE_OPERATE_TABLE,
					new String[] { ALL_COUNT_COLUMN },
					GetWhereSql(OPERATE_TASKID_COLUMN), new String[] { id },
					null, null, null);

			if (c != null) {
				if (c.getCount() > 0) {
					isExist = true;
				}
				c.close();
			}
		} catch (Exception e) {
		}
		return isExist;
	}

	@SuppressLint("SimpleDateFormat")
	private ContentValues getContentValues(Operation offlineOperate) {
		ContentValues values = new ContentValues();
		values.put(OPERATE_A_COLUMN, offlineOperate.getOperate_a());
		values.put(OPERATE_P_COLUMN, offlineOperate.getOperate_p());
		values.put(OPERATE_TASKID_COLUMN, offlineOperate.getOperate_taskId());
		values.put(OPERATE_FILES_COLUMN, offlineOperate.getOperate_files());
		values.put(OPERATE_STATUS_COLUMN, offlineOperate.getOperate_status());
		values.put(
				SUBMIT_TIME_COLUMN,
                offlineOperate
                        .getSubmit_time());
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String date = sDateFormat.format(new java.util.Date());
		values.put(
				CREATE_TIME_COLUMN,
				offlineOperate.getCreate_time() == null ? date : offlineOperate
						.getCreate_time());
		values.put(OPERATE_RESULT_COLUMN, offlineOperate.getOperate_result());
		return values;

	}

	@Override
	public List<Operation> getOfflineOperates() {
		List<Operation> operates = new ArrayList<Operation>();
		Cursor c = getReadDb(2).query(OFFLINE_OPERATE_TABLE, ALL_COLUMNS,
				GetWhereSql(OPERATE_STATUS_COLUMN), new String[] { "0" },// 原先是0
																			// 数据库存3
				null, null, ID_COLUMN);
		while (c.moveToNext()) {
			operates.add(getOfflineOperate(c, false));
		}
		c.close();
		return operates;
	}

	@Override
	public Operation getOfflineOperateById(String id) {
		Operation operate = null;
		Cursor c = getReadDb(2).query(OFFLINE_OPERATE_TABLE, ALL_COLUMNS,
				GetWhereSql(ID_COLUMN), new String[] { id },// 原先是0
															// 数据库存3
				null, null, ID_COLUMN);
		while (c.moveToNext()) {
			operate = getOfflineOperate(c, false);
		}
		c.close();
		return operate;
	}

	@Override
	public void deleteById(int id) {
		getWriteDb(2).delete(OFFLINE_OPERATE_TABLE, GetWhereSql(ID_COLUMN),
				new String[] { id + "" });
	}

	/**
	 * 返回一个离线操作对象
	 * 
	 * @param c
	 * @return
	 */
	private Operation getOfflineOperate(Cursor c, boolean isNeedClose) {
		Operation offlineOperate = null;
		if (c != null) {
			offlineOperate = new Operation();
			try {
				offlineOperate.setOperate_id(c.getInt(c
						.getColumnIndex(ID_COLUMN)));
				offlineOperate.setOperate_taskId(c.getString(c
						.getColumnIndex(OPERATE_TASKID_COLUMN)));
				offlineOperate.setOperate_a(c.getString(c
						.getColumnIndex(OPERATE_A_COLUMN)));
				offlineOperate.setOperate_p(c.getString(c
						.getColumnIndex(OPERATE_P_COLUMN)));
				offlineOperate.setOperate_files(c.getString(c
						.getColumnIndex(OPERATE_FILES_COLUMN)));
				offlineOperate.setOperate_result(c.getString(c
						.getColumnIndex(OPERATE_RESULT_COLUMN)));
				offlineOperate.setOperate_status(c.getInt(c
						.getColumnIndex(OPERATE_STATUS_COLUMN)));
				String createTimeStr = c.getString(c
						.getColumnIndex(CREATE_TIME_COLUMN));
				String submitTimeStr = c.getString(c
						.getColumnIndex(SUBMIT_TIME_COLUMN));
				if (!StringUtil.isNull(submitTimeStr)) {
					try {
						offlineOperate.setSubmit_time(submitTimeStr);
					} catch (Exception e) {
					}
				}
				try {
					if (!StringUtil.isNull(createTimeStr)) {
						offlineOperate.setCreate_time(createTimeStr);
					}
				} catch (Exception e) {
				}
			} catch (Exception e) {
				L.printStackTrace(e);
			} finally {
				if (isNeedClose) {
					c.close();
				}
			}

		}
		return offlineOperate;
	}
}
