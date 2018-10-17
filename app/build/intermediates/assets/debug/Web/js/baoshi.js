//报事界面可提取的js
/**通过bl_id及fl_id 联合查询区域*/
var currentBLID = "";
var currentSID = "";
var currentFLID = "";
var currentAreaID = "";
function getDataByBLAndFL(bl_id,site_id){
	var sql = "select * from rm where bl_id='"+bl_id+"' AND site_id = '"+site_id+"' ";
	var data = window.csq_db.db_select(0, sql,'',1);
	//alert("rm"+data)  
	data = eval("(" + data + ");");
	return data;
}

function getFLDataByBL(site_id){
	var sql = "select * from bl where site_id = '"+site_id+"' group by bl_id" ;
	var data = window.csq_db.db_select(0, sql,'',1);
	//("库号"+data);
	data = eval("(" + data + ");");
	return data;
}

/*一级界面*/
function initDrawingView(view,data){
	if(data && data.length>0){
		
		var arrow;
		$(view).empty();
		for(var i = 0;i<data.length;i++){
			arrow = data[i];
			appendOneArea($(view),arrow);
		}
	}
}

/*初始化楼栋界面*/
function initFlView(view,site_id){
	currentSID = site_id;
	var data = getFLDataByBL(site_id);
	if(data && data.length>0){
		var fl;
		view.empty();
		for(var i = 0;i<data.length;i++){
			arrow = data[i];
			appendOneFL(view,arrow);
		}
	}
}

function appendOneArea(view,arrow){
	if(arrow && view){
		$(view).append("<li onclick='gzResult(\""+arrow.bl_id+"\",\""+arrow.rm_id+"\",\""+arrow.rm_name+"\");'><p>"+arrow.rm_name+"</p><img src='img/arrow_r.png'></li>");
	}
}

//库号，楼栋列表
function appendOneFL(view,arrow){
	if(arrow && view){
		$(view).append("<li onclick='onFLClick(\""+arrow.bl_id+"\",this);' ><p>"+arrow.bl_name+"</p><img src='img/arrow_r.png'></li>");
	}
}

var canBackToFl = false;

function onAreaSelectBackPress(){
	if(canBackToFl){
		initFlView($("#zbg ul"),currentSID);
		canBackToFl = false;
	}else{
		$("#zbg").css({"display":"none"});
		$("#bodyId")[0].style.position="static";	
		uiFlag = 0;
	}
}

function onFLClick(bl_id,obj){
	$this = $(obj);
	var data = getDataByBLAndFL(bl_id,currentSID);
	if(data && data.length>0){
		initDrawingView($this.parent(), data);
		canBackToFl= true;
	}
}

//---------------------
//获取 地块 数据 
function getStageDataBySite(site_id){
	var sql = "select * from rm where site_id = '"+site_id+"' group by dms_update_time" ;
	var data = window.csq_db.db_select(0, sql,'',1);
	//("地块"+data);
	data = eval("(" + data + ");");
	return data;
}


/*初始化  地块 界面*/
function initStageNameView(view,site_id){
	currentSID = site_id;
	var data = getStageDataBySite(site_id);
	if(data && data.length>0){
		//var stage_data;
		view.empty();
		for(var i = 0;i<data.length;i++){
			arrow = data[i];
			appendStageData(view,arrow);
		}
	}
}

//拼接地块数据
function appendStageData(view,arrow){
	if(arrow && view){
		$(view).append("<li onclick='onStageDataClick(\""+arrow.stage_name+"\",this);' ><p>"+arrow.stage_name+"</p><img src='img/arrow_r.png'></li>");
	}
}

function onStageDataClick(stage,obj){
	$this = $(obj);
	var data = getBlByStage(stage,currentSID);
	if(data && data.length>0){
		arrow = data[i];
		appendBlData(view,arrow);
		canBackToFl= true;
	}
}

function getBlByStage(stage,site_id){
	var sql = "select * from rm where stage_name='"+stage+"' AND site_id = '"+site_id+"' ";
	var data = window.csq_db.db_select(0, sql,'',1);
	//alert("rm"+data)  
	data = eval("(" + data + ");");
	return data;
}

//拼接 楼栋 数据
function appendBlData(view,arrow){
	if(arrow && view){
		$(view).append("<li onclick='onStageDataClick(\""+arrow.stage_name+"\",this);' ><p>"+arrow.stage_name+"</p><img src='img/arrow_r.png'></li>");
	}
}





