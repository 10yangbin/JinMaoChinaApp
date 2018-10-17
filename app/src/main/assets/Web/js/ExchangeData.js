


function  rm_id_Change(id){
	
	if(id){
		var sql = "select rm_name from rm where rm_id='" + id+ "' COLLATE NOCASE";
		var data = window.csq_db.db_select(0, sql, null, 1);
		var data = eval("(" + data + ")");
		
		if (data.length > 0) {
			return data[0].rm_name;
		} else {
			window.csq_hw.showToast("房间信息未找到");
		}
	}else {
		return "";
	}

}

function  bl_id_Change(id){
	
	if(id){
	   var sql = "select bl_name from bl where bl_id='" + id+ "' COLLATE NOCASE";
	   var data = window.csq_db.db_select(0, sql, null, 1);
	   var data = eval("(" + data + ")");
	
	if (data.length > 0) {
		return data[0].bl_name;
	  } else {
		window.csq_hw.showToast("楼栋/库房信息未找到");
	   }
	}else {
		return "";
	}
	

}
