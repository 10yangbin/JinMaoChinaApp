


    //判断工单技能是否包含人员的技能

    // 在技能维度， 默认流程的操作按钮全部隐藏，如果有当前用户有技能，展示

    var isShowOperationAboutLiuCheng = true ; //隐藏
    
    var isHasPermissionCancleTask=false;
    
    
    function isCangiveUpTask() {
    	
    	if(task[0].INFORM_MAN_NAME==getUserMsg('user_id')){
    		return true;
    	}
    	
    }
    
    
    
	function isTaskHasUsertrRelation() {	 
	 
		var wo_trs = new Array();
		
		var sql01= "SELECT DISTINCT tr_id FROM WOTR where wo_id ='"+taskId+"'";
		
		var wotrs =window.csq_db.db_select(0,sql01, '',2);
		
		wotrs = eval("(" + wotrs + ");");
		 
		// alert(wotrs.length);
		 
		if(wotrs&&wotrs.length>0){
			
			for (var i = 0; i < wotrs.length; i++) {
				wo_trs.push(wotrs[i].tr_id);
			}
			
		} 
		
	//	alert(wo_trs);	
    	
     /** 
	  * 用于存放人员的技能	
	  */
	var cf_trs = new Array();
	
	 //根据人员id获取技能id 
	var sqlCF_TR = "SELECT tr_id FROM CF_TR where cf_id='"+getUserMsg('user_id')+"'";
	
	var datatr =window.csq_db.db_select(0,sqlCF_TR, '',1);		
	var datacf_tr = eval("(" + datatr + ");");
	if(datacf_tr&&datacf_tr.length>0){
		
		for (var i = 0; i < datacf_tr.length; i++) {
			//alert(array_in(datacf_tr[i].tr_id,wo_trs));
			if(array_in(datacf_tr[i].tr_id,wo_trs)){
				
				//只要工单的技能包含任意一条当前登录人的技能，流程操作按钮不隐藏。fasle
				
				isShowOperationAboutLiuCheng =false;
				
				return;
			}
			
		}
		
	} 
	

}		
	