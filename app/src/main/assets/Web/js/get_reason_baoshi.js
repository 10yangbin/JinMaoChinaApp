
            
	  
			
			//有弹出层，背景遮罩出现
			function pupWindowShow(){
				$(".graybg_pd").fadeIn();
			    //史振伟UI 遮罩
			    $("#bodyId")[0].style.position="fixed";
				$("#fade").fadeIn();
			}
			
			//有弹出层消除，北京遮罩消失
			function pupWindowOut(){
				
				$(".graybg_pd").fadeOut();
				$("#fade").fadeOut();
				$("#bodyId")[0].style.position="static";	
				uiFlag = 0;
			}
			
			
			//一级数据 ,执行页面的详细报事类型
			function bsTypeShow(){
				
				if(inform_type==1||inform_type==2||inform_type==3){
					uiFlag = 1;
					if(inform_type==1||inform_type==2){
						var sql = "select REASON_DETAIL as name, REASON_DETAIL_ID as id from REASON_CAUSE where REASON_DETAIL_ID like '202|______|'";
					}else if(inform_type==3) {
						var sql = "select REASON_DETAIL as name, REASON_DETAIL_ID as id from REASON_CAUSE where REASON_DETAIL_ID like '201|______|'";
					}
					var reasonData = window.csq_db.db_select(0,sql, '',1);
					reasonData = eval("("+reasonData+")");
					if(reasonData && reasonData.length>0){
						var htmlFlag = "";
						for(var i=0; i<reasonData.length; i++){
							var reason = reasonData[i];
							htmlFlag += "<li style='overflow-x:auto;' onclick='bsTypeShowDetail(\""+reason.id+"\");'><strong>"+reason.name+"</strong></li>"
						}
						$("#bs_type_list_id").html(htmlFlag);
					}else{
						//这里可以做没有工程类类型的处理，比如弹窗告诉用户，没有查到原因类型，或提示用户同步基础数据。
						alert("没有查到原因类型，或提示用户同步基础数据");
					}
					 
					$("#bstypeselect").slideDown(500);
					 pupWindowShow();
				}else{
					window.csq_hw.showToast("请选择报事类型");
					return;
				}
				
			}
			
			//二级数据
			function bsTypeShowDetail(id){
				uiFlag = 8;
				var sql = "select REASON_DETAIL as name, REASON_DETAIL_ID as id ,REASON_MAIN_ID as key_id from REASON_CAUSE where REASON_DETAIL_ID like '"+id+"_________|'";
				var reasonData = window.csq_db.db_select(0,sql, '',1);
				reasonData = eval("("+reasonData+")");
				if(reasonData && reasonData.length>0){
					var htmlFlag = "";
					for(var i=0; i<reasonData.length; i++){
						var reason = reasonData[i];
					    //如果有子类，展示，没有显示当前
						if(bsTypeShowDetailThrid(reason.id)){
							htmlFlag += "<li style='overflow-x:auto;' data-key='" + reason.key_id + "' data-id='" + reason.id + "' data-name='" + reason.name + "' onclick='bsTypeShowDetailThridShow(\""+reason.id+"\");'><strong>"+reason.name+"</strong></li>"
						}else{
							htmlFlag += "<li style='overflow-x:auto;' data-key='" + reason.key_id + "' data-id='" + reason.id + "' data-name='" + reason.name + "' onclick='onTypeItemClick(this);'><strong>"+reason.name+"</strong></li>"
						}
					}
					$("#bs_type_list_id").html(htmlFlag);
				}else{
					alert("没有子类的原因类型，请重新选择");
					
				}
				$("#bstypeselect").slideDown(500);
				
				pupWindowShow();
			}
			
			//三级数据 判断
			function bsTypeShowDetailThrid(id){
				uiFlag = 8;
				var sql = "select REASON_DETAIL as name, REASON_DETAIL_ID as id ,REASON_MAIN_ID as key_id from REASON_CAUSE where REASON_DETAIL_ID like '"+id+"_________|'";//'GZ|"+id+"|________|'";
				var reasonData = window.csq_db.db_select(0,sql, '',1);
				reasonData = eval("("+reasonData+")");
				if(reasonData && reasonData.length>0){
					return true;
				}else{
					return false;
				}
			}
			
			//三级数据 显示
			function bsTypeShowDetailThridShow(id){
				uiFlag = 8;
				var sql = "select REASON_DETAIL as name, REASON_DETAIL_ID as id ,REASON_MAIN_ID as key_id from REASON_CAUSE where REASON_DETAIL_ID like '"+id+"_________|'";
				var reasonData = window.csq_db.db_select(0,sql, '',1);
				reasonData = eval("("+reasonData+")");
				
				if(reasonData && reasonData.length>0){
					var htmlFlag = "";
					for(var i=0; i<reasonData.length; i++){
						var reason = reasonData[i];
						htmlFlag += "<li style='overflow-x:auto;' data-key='" + reason.key_id + "' data-id='" + reason.id + "' data-name='" + reason.name + "' onclick='onTypeItemClick(this);'><strong>"+reason.name+"</strong></li>"
					}
					$("#bs_type_list_id").html(htmlFlag);
				}else{
					alert("没有子类的原因类型，请重新选择");
				}
				$("#bstypeselect").slideDown(500);
				
				pupWindowShow();
			}
			
			//部门item的点击,以及数据的处理
			function onTypeItemClick(obj){
				var $this = $(obj);
				bstype = $this.data('key');
				$("#bstypedisplay").empty();
				$("#bstypedisplay").text($this.data('name'));
				
				$(".baoshi_gdlx").slideUp(500);
				pupWindowOut();
			}
			
			//关闭工单类型
			function closeTypeOrDetp(){
				
				$(".baoshi_gdlx").slideUp(500);
				$(".graybg_pd").fadeOut();
				//史振伟UI 遮罩
				$("#fade").fadeOut();
				$("#bodyId")[0].style.position="static";	
				uiFlag = 0;
			}				
			
			