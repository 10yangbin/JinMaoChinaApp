package com.guomao.propertyservice.biz.manger;

import java.util.HashMap;
import java.util.Map;

import com.guomao.propertyservice.communcation.OfflineOperateComImpl;
import com.guomao.propertyservice.communcation.UserComImpl;
import com.guomao.propertyservice.communcation.WebClient;

/**
 * 联网管理者
 * 
 * @author Administrator
 * 
 */

public class ComManger {
	private static ComManger COM_MANGER = new ComManger();

	// 枚举的是任务分解的类型
	public enum ComType {
		// 任务联网
		TASK_COM,
		// 任务进度联网
		TASK_PROCESS_COM,
		// 用户联网
		USER_COM,
		// 离线操作联网
		OFFLINE_OPERATE_COM
	}

	private WebClient webClient;
	private Map<String, Object> map = new HashMap<String, Object>();

	private ComManger() {
		this.webClient = WebClient.getInstance();
	}

	public static ComManger getInstance() {
		return COM_MANGER;
	}

	/**
	 * 获取每个业务联网实现类的对象
	 * 
	 * @param type
	 * @return
	 */
	public Object get(ComType type) {
		Object obj = this.map.get(type.name());
		if (obj == null) {
			obj = init(type);
			map.put(type.name(), obj);
		}
		return obj;
	}

	/**
	 * 初始化和联网相关联的业务对象，方便调用每个业务联网的实现类
	 */
	private Object init(ComType type) {

		Object obj = null;
	   if (type == ComType.OFFLINE_OPERATE_COM) {
			obj = new OfflineOperateComImpl(webClient);
		} else if (type == ComType.USER_COM) {
			obj = new UserComImpl(webClient);
		}

		return obj;
	}

}
