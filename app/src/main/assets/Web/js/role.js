
//光谷暂时没有用

//客服专员
var typeServiceCoordinator = '535fd704-9b2c-9110-ca0a-b6b67f441e98';
// 客服中心负责人
var typeServiceCoordinatorManager = '4b44e06b-370e-183c-d1dc-2be095754cfa';
// 工程班长
var typeProjectMonitor = 'a352dfa7-7bb2-74bb-5454-dba02da89eb8';
// 中心工程负责人
var typeProjectManager = 'c5ed1162-358d-1625-c69e-d6b608b0b480';
//工程维修员
var typeProjectMan = '0c0cb81a-70d1-5f51-c436-00b40197609d';
/*
 * 根据用户id 判断是否是客服人员
 */
function judgeIsServiceCoordinator(user_id) {
	var judgeSQL = "SELECT count(USER_ID) as NUM FROM user_site WHERE USER_ID = '"
			+ user_id + "' AND standard_role_id in('" + typeServiceCoordinator
			+ "','" + typeServiceCoordinatorManager + "')";
	var total_data = window.csq_db.db_select(judgeSQL, '', 1);
	if(!total_data){
	 return false;
	}
	total_data = eval("("+total_data+");");
	if (total_data != 'undefined' && total_data.length>0 && total_data[0].NUM >0) {
		return true;
	}
	return false;
}


function canDealWith(user_id,reasonId){
	var isServiceCoordinator = judgeIsServiceCoordinator(user_id);
	if(isServiceCoordinator)return reasonId == '23';
	return reasonId == '20'
	
}

