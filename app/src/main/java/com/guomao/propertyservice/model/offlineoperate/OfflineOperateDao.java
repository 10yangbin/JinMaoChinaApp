package com.guomao.propertyservice.model.offlineoperate;

import java.util.List;


/**
 * 离线报事dao的接口
 * @author Administrator
 *
 */
public interface OfflineOperateDao {
	/**
	 * 将要离线的数据保存至数据库
	 * 
	 * @param operate
	 *            离线的实体
	 * @param urserId
	 * @return id
	 */
	long save(Operation operate);

	/**
	 * 提交离线
	 * 
	 * @param operation
	 */
	void saveOrUpdate(Operation operation);

	/**
	 * 获取该用户的所有离线操作
	 * 
	 * @param userId
	 * @return
	 */
	List<Operation> getOfflineOperates();

	/**
	 * 通过id获取Operate
	 * 
	 * @param id
	 * @return
	 */
	Operation getOfflineOperateById(String id);

	/**
	 * 删除该id对应的数据
	 * 
	 * @param id
	 */
	void deleteById(int id);

}
