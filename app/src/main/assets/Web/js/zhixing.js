//报事界面可提取的js


	var uiFlag = 0; //定义现在界面的状态，0--工单执行；1--原因类型;2--选择报事设备;3--备注
	var wuliao ;  //用于保存每条物料的数据
	var wuliaoList =new Array();  //用于保存物料的数组对象
	var useNums = new Array();
	var user =null;
	var userId ='';
	var userName ='';
	var kszxTime = "";
	var isStart = false;//扫码结果是否与工单匹配 注意FM_CODE
	var isYyxl = false;//是否选择原因细类
	var gongdanId = ""; //$.getUrlParam('TASK_ID');
	var causeDetailId = "";


	
	//关闭类型选择事件
	function closeChoseType() {
		uiFlag = 0;
		$("#liucheng_tanchu_paidan").slideUp(500);
		$("#fade").fadeOut();//遮罩消失
		$("#bodyId")[0].style.position="static";//滚动条恢复
	}
		
		
	//史振伟UI 返回    执行页面返回流程页面
	function backTrack(){
		redirectLiucheng();
	}
	
	//流程按钮 点击流程按钮跳转到流程页
	function redirectLiucheng(){
		saveTemp();
		//记录原选的Tab页并返回
		if(g_tab!=null && g_tab!=-1 && g_tab>=0 && g_tab<=4){
			window.location.href="liucheng.html?taskid=" + gongdanId + "&index="+g_tab;
		}
		else{
			window.location.href = "liucheng.html?taskid=" + gongdanId;
		}			
		
	}
	
	//提交 弹出提交确认框。
	function beizhuShow() {
			$("#tijiaoQRKID").fadeIn("slow"); //淡入淡出效果 显示div
			$("#fade").fadeIn();
			uiFlag = 3;
			
	}
	//取消
	function cancelMemo(){
		uiFlag = 0;
		$("#tijiaoQRKID").fadeOut("slow"); //淡入淡出效果 隐藏div
		$("#fade").fadeOut();
		$("#tijiaoMS")[0].style.border = "0px none";		
	}	
	
	

	//当页面加载时显示的数据
	function loadData() {
        
			user = eval("(" + window.csq_hw.getUserInfo() + ")");
            
			userId = window.csq_hw.getUserId();
            
            userName =window.csq_hw.getCurrentUserName();
            
			gongdanId = getUrlParamValByKey("TASK_ID"); 
            
			g_tab = getUrlParamValByKey("index");
			
			var gdDataSql = "select * from TASKMESSAGE where task_id = '" + gongdanId + "' ";
			
			var gdDataJson = window.csq_db.db_select(0,gdDataSql, '',2);
			var gdData = eval("(" + gdDataJson + ");");
			var status = gdData[0].TASK_STATUS;
			var _fmcode = gdData[0].FM_CODE == undefined ? '': gdData[0].FM_CODE;
			if(gdData[0].TASK_TYPE){
				if(gdData[0].TASK_TYPE == 'T' || _fmcode==''){
					$(".btn_chazhao").hide();
					isStart = true;
				}
			}
			if(status){
				if(status!="Processed" && status!="Assigned" && status!="Answered"){
					isStart = true;
				}
			}
			$("#foot_menu_zhixing").hide();
			if(status=="Processing" || status=="Completed"){
				$("#kaishiycId").hide();
				$(".pazhaoMsClass").show();
				$("#foot_menu_zhixing").show();
			}
			
			var _pmflag=false;
			var pm='';
			if (status=="Processing"){
				pm = window.csq_io.file_read('gdzxtmp'+gongdanId);
				
				//如果有临时文件，从临时文件中取内容
				if(pm !=null){
					pm = eval("(" + pm + ");");
					_pmflag=true;
				}
			}
			
			var finish_desc = '';
			if(_pmflag){
				isStart = false;
				
				causeMainId = pm.reason_main_detail_id;
				finish_desc = pm.finish_desc;
				files = processImage(pm.image);
				var _image = files.split(",");
				if(_image.length>0){
					for(var k=0;k<_image.length;k++){
						if(_image[k] && _image[k]!='-1'){
							//var t_html = $("#Photo").html();
							//$("#Photo").html("<a href=\"" + _image[k] + "\"><img src=\"" + _image[k] + "\"></a>" + t_html);
							var _index = k+1;
							var t_html = $("#Photo").html();
							var n_html = "<a id=\""+_index +"\"";
							n_html = n_html + 'ontouchstart="gtouchstart('+_index+')" ontouchmove="gtouchmove('+_index+')" ontouchend="gtouchend('+_index+')"';
							//n_html = n_html + " href=\"" + _image[k] + "\"> <img src=\"" + _image[k] + "\"></a>";
							n_html = n_html + '> <img class="miaoshu" src="' + _image[k] + '"></a>';
							$("#Photo").html(n_html + t_html);											
						}
					}
				}					
			}else{
			
				causeMainId = gdData[0].REASON_MAIN_ID; //原因大类Id
			}
			$("#taskDescriptId").val(finish_desc);
			
			causeDetailId = gdData[0].REASON_MAIN_DETAIL_ID == undefined ? "" : gdData[0].REASON_MAIN_DETAIL_ID;
			if (causeDetailId != "") {
				var yyDataSql = " select * from REASON_CAUSE where REASON_MAIN_ID = '" + causeDetailId + "'";
				//20150717，改造sql语句以适应多项目切换，并且前台不处理数据库打开操作，都交给后台
				//最后的参数代表数据类型，1代表基础数据，2代表业务数据						
				//var yyDataJson = window.csq_db.db_select(0, yyDataSql, '');
				var yyDataJson = window.csq_db.db_select(0,yyDataSql, '',1);
				var yyData = eval("(" + yyDataJson + ");");
				if (yyData.length > 0) {
					var yyxlName = yyData[0].REASON_DETAIL;
					if (yyxlName) {
						$("#caseTypeId").html(yyxlName);
						isYyxl = true;
					}
				}
			}
			
			if(isStart){
				prepareDisRmOrRom();
			}
		}
	
	//保存临时文件
	function saveTemp(){
		var picPath = files;
		//产品关联isProduct        	
		var gdmaioshu = $("#taskDescriptId").val();
		var param = {
			'finish_desc': gdmaioshu, //完成内容描述
			'task_id': gongdanId,
			'reason_main_detail_id': causeDetailId,
			'image': processImage(files)
		}
		window.csq_io.file_write('gdzxtmp'+gongdanId, JSON.stringify(param), false);
	}
	
	
	
	//物料按钮的弹窗事件
	 function selectWuliao() {
	
		 $(".wl_bounced").fadeIn();
	    // $("#fade").fadeIn();
		
	    $("#wuliao_list").empty();
	 
	 var user_name = window.csq_hw.getCurrentUserCode();
	
	 	
	 var borrowSql = " select * from borrow b where b.USER_NAME='"
	 			+ user_name + "' and b.DATA_STATUS = 1 ";
	 	
	 	var borrowJson = window.csq_db.db_select(0, borrowSql, '', 2);
	 	
	 //	alert(borrowJson);
	 //	alert(useNums);
	 	
	 	var borrowData = eval("(" + borrowJson + ")");
	 	
	  var borrowTag  ="";
	 	
	 	if (borrowData.length > 0) {
	 		for (var i = 0; i < borrowData.length; i++) {
	 			var partId = borrowData[i].PART_ID;
	 			var partName = borrowData[i].DESCRIPTION;
	 			var partNum =  borrowData[i].NUMBERS;
	 			var Id = borrowData[i].ID;
	 			
	 	        var  nums = (!useNums[i])?0:useNums[i]; //保存使用数量,用于数据恢复
	 	        //alert("xxx"+nums);
	 			 borrowTag = '<li><span class="wl_name">'+partName+'</span><span class="usable_num">'+partNum+'</span><span class="use_num"><section class="jiajian"><i class="jian">-</i><input data-id='+Id+' data-partid='+partId+' type="text" value="'+nums+'"/> <i class="jia">+</i></section></span></li>' ;
	 			$("#wuliao_list").append(borrowTag);
	 			
	 		}
	 		useNums =[];	 //使用完数据,清空
	 		//alert("清空"+useNums);
	 		
	 	} else {
	 		var borrowTag = '<li style="margin-left:20px;margin-right:20px;"><span class="num_name">您没有可选择的物料......</span></li>';
	 		$("#wuliao_list").append(borrowTag);
	 	} 
	 		
	 		//减号点击
		     $(".jiajian .jian").click(function(){
		         var numNow=$(this).parent(".jiajian").find("input").val();
		         numNow--;
		         if(numNow<=0){
		             numNow=0;
		         }
		         $(this).parent(".jiajian").find("input").val(numNow);
		     });
	 		
	 		
	 		 //加号点击
		     $(".jiajian .jia").click(function(){
		         var numNow=$(this).parent(".jiajian").find("input").val();
		         var numUse=parseInt($(this).parents("li").find(".usable_num").text());
		         numNow++;
		         if(numNow>numUse){
		             numNow=numUse;
		         }
		         $(this).parent(".jiajian").find("input").val(numNow);
		     });

			   $(".sub").click(function(){

					wuliaoList = [] ;//如果重新选择物料 ,清空数据
					
			    	$("#wuliao_list").find("input[type=text]").each(function (){ 
						var temp = $(this).attr("value");
						
						if(temp>0){
							//alert("num"+$(this).data("value"));
							
							wuliao ={"id":$(this).data('id'),"numbers":$(this).attr("value"),"part_id":$(this).data('partid')};
							
							wuliaoList.push(wuliao);
							
							//alert(JSON.stringify(wuliao));
						}
						// alert("获取"+temp);
						 useNums.push(temp); //保存使用和未使用的数量
					});
			    	
			    	//alert(JSON.stringify(wuliaoList));
			    	 $(".wl_bounced").fadeOut();
				        $("#fade").fadeOut();
				      $("#wuliao_list").html("");
			       
			  });

	     $(".jiajian input").blur(function(){
	         var numUse=parseInt($(this).parents("li").find(".usable_num").text());
	         if($(this).val()<=0){
	             $(this).val(0);
	         }else if($(this).val()>numUse){
	             $(this).val(numUse);
	         }

	     });
	    
	    //物料的确定提交, 完成之后清空物料数据

	    //物料的关闭
	    $(".wl_bounced .close").click(function(){
	        $(".wl_bounced").fadeOut();
	        $("#fade").fadeOut();
	        $("#wuliao_list").html("");

	    });
	}
	
	function  closeSearch(){
		uiFlag = 0;
		$("#search").fadeOut();
		$("#fade").fadeOut();//遮罩消失
		$("#bodyId")[0].style.position="static";//滚动条恢复
	}
	
            //回退按钮回调
			function backCallBack(url){
				//根据现在显示的界面确定要回退到哪一个界面上
				//用以标识现在的界面，0--工单执行；1--原因类型;2--选择报事设备;3--备注
				switch (uiFlag){
					case 0:
						backTrack();
						break;
					case 1:
						closeChoseType();
						break;
					case 2:
						closeSearch();
						break;
					case 3:
						cancelMemo();
						break;
				}			
			}	

			//获取工单信息

			function getTaskMessage() {
					var gdztSql = "select * from TASKMESSAGE where task_id = '"+gongdanId+"' ";
					var gddataJson = window.csq_db.db_select(0,gdztSql, '',2);
					var gdData = eval("(" + gddataJson + ")");
					return gdData;
				}
			
			
			
						