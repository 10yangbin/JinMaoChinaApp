/**
 * 用于权限管理的js
 * 包含权限的常量
 *
 */
var CONST_PERM_NOTICE = "1-1";
var CONST_PERM_BAOSHI = "1-2";
var CONST_PERM_HISTORY = "1-3"
var CONST_PERM_LIXIAN = "1-4";
var CONST_PERM_BANNER = "1-5";
var CONST_PERM_BASEDATA = "1-6";
var CONST_PERM_CUSINFOR = "1-7";
var CONST_PERM_WARINGS = "1-8";
var CONST_PERM_GARDENINFRO = "1-9";

//收发函
var CONST_PERM_ACCEPT="2-0";
var CONST_PERM_ACCEPT_NEW="2-1";
var CONST_PERM_ACCEPT_LIST="2-2";
var CONST_PERM_CUS_INDEX="2-3";

//资产盘点
var CONST_PERM_ZICHAN = "3-1";

//工单管理
var CONST_PERM_GONGDAN_INDEX = "4-0";
var CONST_PERM_GONGDAN_ALL = "4-1";
var CONST_PERM_GONGDAN_DAICHULI = "4-2";
var CONST_PERM_GONGDAN_CHULIZHONG = "4-3";
var CONST_PERM_GONGDAN_BENREN = "4-4";
var CONST_PERM_GONGDAN_YIWANCHENG ="4-5";
//派单，退回，评价 
var CONST_PERM_GONGDAN_PD = "4-6";
var CONST_PERM_GONGDAN_TH = "4-7";
var CONST_PERM_GONGDAN_PJ = "4-8";
var CONST_PERM_GONGDAN_FQ = "4-9";//abound 
//二装
var CONST_PERM_ERZHUANG="6-0";
var CONST_PERM_ER_LIST="6-1";
var CONST_PERM_ER_CHANGE="6-2";
var CONST_PERM_BIANGENG="6-3";

//报事类型 
var CONST_PERM_TYPE_GQ="7-1";
var CONST_PERM_TYPE_GQDK="7-2";
var CONST_PERM_TYPE_RSWX="7-3";

/*判断是否有某项权限*/
function hasPermission(code){
	return window.csq_hw.hasPermission(code+"");
}
var user_type;
function canApplyErZhuang(){
	if("0" == getUserType() || "1" == getUserType() ){
		return true;
	}
	return false;
}
function canApproveErZhuang(){
	if("2" == getUserType()){
		return true;
	}
	return false;
}

