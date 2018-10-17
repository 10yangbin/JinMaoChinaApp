

	
	 // 用于存放普通和计划工单的id ,如果wotr表里的工单技能包含人员的技能,显示工单
	var all_ids ;
	
      //初始化技能相关的工单
	function initWotrRelation() {
		
	 all_ids	 = new Array()
		
	var cf_trs = new Array();
	
	  //根据人员id获取技能id 
	var sqlCF_TR = "SELECT tr_id FROM CF_TR where cf_id='"+getUserMsg('user_id')+"'";
	
	
	var datatr =window.csq_db.db_select(0,sqlCF_TR, '',1);		
	var datacf_tr = eval("(" + datatr + ");");
	if(datacf_tr&&datacf_tr.length>0){
		
		for (var i = 0; i < datacf_tr.length; i++) {
			cf_trs.push(datacf_tr[i].tr_id);
		}
		cf_trs=cf_trs+"";
		cf_trs = cf_trs.replace(/\,/g,'\',\'');
	} 
	

	//普通工单相关的技能工单
	//alert(cf_trs);
	
	var sql01= "SELECT DISTINCT wo_id FROM WOTR where tr_id in('"+cf_trs+"')";
	
	var id_1 =window.csq_db.db_select(0,sql01, '',2);
	
	 id_1 = eval("(" + id_1 + ");");
	 
	// alert(id_1.length);
	 
	if(id_1&&id_1.length>0){
		
		for (var i = 0; i < id_1.length; i++) {
			all_ids.push(id_1[i].wo_id);
		}
		
	} 
	
	//alert("1-------"+id_1);
	
	//计划工单相关的技能工单
	
	var sql02= "SELECT task_id from taskmessage where TASK_TYPE in('P','R') and task_id not in(SELECT DISTINCT wo_id FROM WOTR where tr_id in('"+cf_trs+"'))";
	
	var id_2 =window.csq_db.db_select(0,sql02, '',2);		
	
	id_2 = eval("(" + id_2 + ");");
	
	//alert(id_2.length);
	 
		if(id_2&&id_2.length>0){
			
			for (var i = 0; i < id_2.length; i++) {
				all_ids.push(id_2[i].TASK_ID);
			}
			
		} 

}		
	