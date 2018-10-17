package com.guomao.propertyservice.biz.manger;

import java.util.HashMap;
import java.util.Map;

import com.guomao.propertyservice.biz.UserBiz;
import com.guomao.propertyservice.biz.manger.ComManger.ComType;
import com.guomao.propertyservice.biz.manger.DaoManger.DaoType;
import com.guomao.propertyservice.main.MainApplication;
import com.guomao.propertyservice.model.user.UserCom;
import com.guomao.propertyservice.model.user.UserDao;

import android.content.Context;

/**
 * 业务管理者 通过ComManger和DaoManger实现业务的管理
 * 
 * @author Administrator
 * 
 */
public class BizManger {
	private static BizManger BIZ_MANGER;
	private ComManger comManger;
	private DaoManger daoManger;
	private Map<String, Object> map = new HashMap<String, Object>();

	// 枚举的内容是业务的类型
	public enum BizType {
		// 任务业务
		TASK_BIZ,
		// 用户业务
		USER_BIZ,
		// 离线缓存业务
		OFFLINE_BIZ
	}

	private BizManger(Context context) {
		this.comManger = ComManger.getInstance();
		this.daoManger = new DaoManger(context);
	}

	/**
	 * 实例化业务管理者
	 * 
	 * @return BIZ_MANGER;
	 */
	public static BizManger getInstance() {
		if (BIZ_MANGER == null) {

			synchronized (BizManger.class) {
				if (BIZ_MANGER == null) {
					BIZ_MANGER = new BizManger(MainApplication.getInstance());
				}
			}
		}
		return BIZ_MANGER;
	}

	/**
	 * 获取业务分类的管理对象，如TASK_BIZ，OFFLINE_BIZ等 最后将业务名称和相关对象放入map集合
	 * 
	 * @param type
	 * @return obj
	 */
	public Object get(BizType type) {
		Object obj = this.map.get(type.name());
		if (obj == null) {
			obj = init(type);
			map.put(type.name(), obj);
		}
		return obj;
	}

	/**
	 * 初始化业务分类的对象
	 * 
	 * @param type
	 * @return obj
	 */
	private Object init(BizType type) {

		Object obj = null;
		/*if (type == BizType.TASK_BIZ) {

			obj = new TaskBiz((TaskDao) daoManger.get(DaoType.TASK_DAO),
					(TaskCom) comManger.get(ComType.TASK_COM),
					(OfflineOperateDao) daoManger
							.get(DaoType.OFFLINE_OPERATE_DAO),
					(TaskProcessMessageDao) daoManger
							.get(DaoType.TASK_PROCESS_DAO),
					(TaskProcessMessageCom) comManger
							.get(ComType.TASK_PROCESS_COM));

		} else*//* if (type == BizType.OFFLINE_BIZ) {

			obj = new OfflineOperateBiz(
					(OfflineOperateDao) daoManger
							.get(DaoType.OFFLINE_OPERATE_DAO),
					(OfflineOperateCom) comManger
							.get(ComType.OFFLINE_OPERATE_COM));

		} else*/ if (type == BizType.USER_BIZ) {
			obj = new UserBiz((UserDao) daoManger.get(DaoType.USER_DAO),
					(UserCom) comManger.get(ComType.USER_COM));
		}

		return obj;
	}

}
