
          	
            function backGz(){
          		uiFlag = 0;
				$("#gzType").hide();
				$("#bodyId")[0].style.position = "static";
			}


            //返回分期 
			function backStage(){
				uiFlag = 3;
				$("#gzxianxiang").hide();
				$("#gzType").show();
				$("#bodyId")[0].style.position = "fixed";
			}


			//返回楼栋
			function backBuilding(){
				uiFlag = 4;
				$("#gz_unit").hide();
				$("#gzxianxiang").show();
				$("#bodyId")[0].style.position = "fixed";
			}
			
			//返回单元
			function backUnitList(){
				uiFlag = 5;
				$("#gz_floor").hide();
				$("#gz_unit").show();
				$("#bodyId")[0].style.position = "fixed";
			}
			
			//返回楼层
			function backFloorList(){
				uiFlag = 6;
				$("#gz_floor").show();
				$("#gz_room").hide();
				$("#bodyId")[0].style.position = "fixed";
			}
			

			//真实房屋list
			function showRoomList(){
				if(inform_type==1||inform_type==2||inform_type==3){
					stageInforShow();
				}else{
					window.csq_hw.showToast("请选择报事类型");
					return;
				}
				$("#bodyId")[0].style.position = "fixed";
				$("#baoshi_yezhu").empty();				
			}

			//地块 
			function stageInforShow() {
				uiFlag = 3;
				
				$("#gzType_show").empty();
				$("#gzType").show();
				new IScroll("#gzType .scroll_container",{preventDefault:false});
				
				 if(inform_type==1||inform_type==2){
					 var sql_001 = " select distinct stage_name from rm where site_id = '"+getUserMsg('site_id')+"' and property_type ='PublicArea' order by rm_id asc";	
			   	   }else{
			   		var sql_001 = " select distinct stage_name from rm where site_id = '"+getUserMsg('site_id')+"' and property_type ='House' order by rm_id asc";
			   	   }
				//加上不区分大小写的
				//sql_001 = sql_001 + " COLLATE NOCASE ";	

				var data = window.csq_db.db_select(0,sql_001, '',1);
				//整合数据
				var _scope = eval("(" + data + ")");
				
				for (var i = 0; i<_scope.length; i++) {
					var _li = '<li onclick=buildingInforList("'+_scope[i].stage_name+'")><strong>'+_scope[i].stage_name+'</strong><i><img src="img/arrow_r.png" alt="" width="12" height="21" /></i></li>';
					$("#gzType_show").append(_li);
				}
			}

			//楼栋
			function buildingInforList(stageName){
				uiFlag = 4;
				
				$("#gzxianxiang").show();
				new IScroll("#gzxianxiang .scroll_container",{preventDefault:false});
				$("#phenomenon").empty();
				
				$("#phenomenon").append(stageName);
						
			
				 if(inform_type==1||inform_type==2){
						var sql_001 = " select distinct bl_name ,bl_id ,stage_name from rm where site_id='"+getUserMsg('site_id')+"' and stage_name='"+stageName+"' and property_type ='PublicArea' order by bl_id asc ";
			   	   }else{
			   		var sql_001 = " select distinct bl_name ,bl_id ,stage_name from rm where site_id='"+getUserMsg('site_id')+"' and stage_name='"+stageName+"' and property_type ='House' order by bl_id asc ";
			   	   }
				//加上不区分大小写的
				//sql_001 = sql_001 + " COLLATE NOCASE ";	
				
				var data = window.csq_db.db_select(0,sql_001, '',1);
				
				//整合数据
				var reason = eval("(" + data + ")");
				
				if(String(reason).length < 1){
					window.csq_hw.showToast("没有楼栋信息。");
				}
				
				$("#gzPhenomenon_show").empty();
				for (var i = 0; i<reason.length; i++) {
					$("#gzPhenomenon_show").append('<li onclick=unitInforlist("'+reason[i].bl_id+'","'+reason[i].stage_name+'","'+reason[i].bl_name+'")><strong>'+reason[i].bl_name+'</strong><i><img src="img/arrow_r.png" alt="" width="12" height="21" /></i></li>');
				}
				
			}
			
			//单元
			function unitInforlist(bl_id,name1,name2){
				uiFlag = 5;
				
				$("#gz_unit").show();
				new IScroll("#gz_unit .scroll_container",{preventDefault:false});
				$("#phenomenon_01").empty();
				$("#component").empty();
				if(!name1 || name1 == 'undefined'){
					name1 = $("#phenomenon").text();
				}
				//地块
				$("#phenomenon_01").append(name1);
				//楼栋
				$("#component").append(name2);
				//真实房屋
				
				 if(inform_type==1||inform_type==2){
					 var sql_001 = " select distinct unit_name,unit_id from RM where upper(bl_id)='"+bl_id.toUpperCase()+"' and property_type ='PublicArea' order by unit_id asc";
			   	   }else{
			   		var sql_001 = " select distinct unit_name,unit_id from RM where upper(bl_id)='"+bl_id.toUpperCase()+"' and property_type ='House' order by unit_id asc";
			   	   }
				var data = window.csq_db.db_select(0,sql_001, '',1);
				//整合数据
				var _room = eval("(" + data + ")");
				if(String(_room).length < 1){
					window.csq_hw.showToast("没有单元信息");
				}
				
				$("#gzComponent_show").empty();
				
				for (var i = 0; i<_room.length; i++) {
					
					$("#gzComponent_show").append('<li onclick=floorInforList("'+_room[i].unit_id +'","'+_room[i].unit_name+'","'+name1+'","'+name2+'")><strong>'+_room[i].unit_name+'</strong><i><img src="img/arrow_r.png" alt="" width="12" height="21" /></i></li>');
				}
				
			}
		
			//楼层 
			function floorInforList(unit_id,unit_name,scope_name,building_name){
				uiFlag = 6;
				
				  $("#gz_floor").show();
				  new IScroll("#gz_floor .scroll_container",{preventDefault:false});
				  
					$("#phenomenon_02").empty();
					$("#component_01").empty();
					$("#unit").empty();
					
					$("#phenomenon_02").append(scope_name);
					$("#component_01").append(building_name);
					$("#unit").append(unit_name+"单元");
					
					 if(inform_type==1||inform_type==2){
						 var sql_001 = " select distinct fl_name, fl_id from RM where unit_id='"+unit_id+"' and property_type ='PublicArea' order by fl_id asc";
				   	   }else{
				   		var sql_001 = " select distinct fl_name, fl_id from RM where unit_id='"+unit_id+"' and property_type ='House' order by fl_id asc";
				   	   }
					var data = window.csq_db.db_select(0,sql_001, '',1);
					var _room = eval("(" + data + ")");
					if(String(_room).length < 1){
						window.csq_hw.showToast("没有楼层信息");
					}
					$("#gzFloor_show").empty();
					
					for (var i = 0; i<_room.length; i++) {
						
						$("#gzFloor_show").append('<li onclick=RoomInforShow("'+_room[i].fl_id +'","'+scope_name+'","'+building_name+'","'+unit_name+'","'+_room[i].fl_name+'")><strong>'+_room[i].fl_name+' </strong><i><img src="img/arrow_r.png" alt="" width="12" height="21" /></i></li>');
					}
				}
			

			//房屋 和描述 
			function RoomInforShow(fl_id,scope_name,building_name,unit_name,fl_name){
				uiFlag = 7;
				
				  $("#gz_room").show();
			  new IScroll("#gz_room .scroll_container",{preventDefault:false});	  
				 $("#phenomenon_03").empty();
				$("#component_02").empty();
				$("#unit_01").empty();
				$("#floor_name").empty();
				
				
				$("#phenomenon_03").append(scope_name);
				$("#component_02").append(building_name);
				$("#unit_01").append(unit_name+"单元");
				$("#floor_name").append(fl_name+"层");

				//真实房屋
				 if(inform_type==1||inform_type==2){
					 var sql_001 = "select distinct * from RM where fl_id='"+fl_id+"' and property_type ='PublicArea' order by rm_id asc";
			   	   }else{
			   		var sql_001 = "select distinct * from RM where fl_id='"+fl_id+"' and property_type ='House' order by rm_id asc";
			   	   }
				//var sql_001 = "select distinct * from RM where fl_id='"+fl_id+"' order by rm_id asc";
				var data = window.csq_db.db_select(0,sql_001, '',1);
				//整合数据
				var _room = eval("(" + data + ")");
				if(String(_room).length < 1){
					window.csq_hw.showToast("没有房间信息");
				}
				$("#gzRoom_show").empty();
				for (var i = 0; i<_room.length; i++) {
					var rmname = (!_room[i].rm_name)?'无': _room[i].rm_name;
					$("#gzRoom_show").append('<li onclick=roomResultShow("'+_room[i].rm_id +'")><strong>'+ rmname +'</strong></li>');//+_room[i].UNIT+'单元'+_room[i].FL_ID+'层 '
				}
				
			}

			//房屋结果展示
			function roomResultShow(rm_id){
				
				var sql_001 = " select * from RM where upper(rm_id)='"+rm_id.toUpperCase()+"' order by rm_id desc";
				var data = window.csq_db.db_select(0,sql_001, '',1);
				var _room = eval("(" + data + ")");
				if(_room.length < 1){
					window.csq_hw.showToast("没有房间信息。");
				}
				
				$("#baoshi_yezhu").empty();
				for (var i = 0; i < _room.length; i++) {
					var item = _room[i];
					var rmname1 = (!item.stage_name)?'无': item.stage_name; 
					var rmname2= (!item.bl_name)?'无': item.bl_name; 
					var rmname3 = (!item.unit_name)?'无': item.unit_name; 
					var rmname4 = (!item.fl_name)?'无': item.fl_name; 
					var rmname5 = (!item.rm_name)?'无': item.rm_name;
	
					rm_code = rm_id;
					var czRS;
					czRS = "<li>" 
						+ "<div class='baoshi_yezhu_biaoti'><i>地块</i></div>" + "<div class='baoshi_yezhu_biaoti'><i>" + rmname1 + "</i></div>" + "</li>" + "<li>" 
						+ "<div class='baoshi_yezhu_biaoti'><i>楼栋</i></div>" + "<div class='baoshi_yezhu_biaoti'><i>" + rmname2 + "</i></div>" + "</li>" + "<li>" 
						+ "<div class='baoshi_yezhu_biaoti'><i>单元</i></div>" + "<div class='baoshi_yezhu_biaoti'><i>" + rmname3 + "</i></div>" + "</li>"+"<li>" 
						+ "<div class='baoshi_yezhu_biaoti'><i>楼层</i></div>" + "<div class='baoshi_yezhu_biaoti'><i>" + rmname4 + "</i></div>" + "</li>" + "<li>" 
						+ "<div class='baoshi_yezhu_biaoti'><i>房间</i></div>" + "<div class='baoshi_yezhu_biaoti'><i>" + rmname5 + "</i></div>"; 
					
					$("#baoshi_yezhu").append(czRS);
				}						
				$("#gzType,#gz_bujian,#gzxianxiang,#gz_unit,#gz_floor,#gz_room").css("display","none");
				$("#bodyId")[0].style.position = "static";
				eqFlag = 0;
				uiFlag = 0;
				fmcode='';  // 房间报事不传fmcode ，不论扫码还是手动选择
			
			}