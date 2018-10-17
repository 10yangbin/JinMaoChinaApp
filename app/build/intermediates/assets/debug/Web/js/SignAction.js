


    var SignStatus;
    
    var cf_id = window.csq_hw.getUserId();
    
    var signCount = false;
    
    //签到签退的动作
   	function signInOut() {
		
   		
   		if(signCount){
   			if (!confirm("签到签退时间的间隔过短!")) {
    			   return;
    		 }
   		}else {
   		//alert("cfsign"+SignStatus);
   		if(SignStatus==1) { 
   			if (!confirm("确定签到？")) {
   			   return;
   			}
		}else if(SignStatus==2) {
			if (!confirm("确定签退？")) {
				return;
   			}
		  } 
		}
		  
   		var A = "/cf/sign";
			var param = {
				"craftspersonCode": cf_id,
				"ifon":SignStatus
			};
			 var bool = window.csq_exe.doexec("/cf/sign", JSON.stringify(param));
			//alert(bool);
			var retObj = eval("(" + bool + ");");
			
			if (retObj.ret==true) {
				//window.csq_hw.showToast(retObj.msg);
				
				if(SignStatus==1) {   
			 		SignStatus==2;
			 		$("#signIn_text").text("已签到");
			 		$("#signIn_text02").text("签到时间 :"+formatDataWithoutMillisecond(retObj.processtime));
				}else if(SignStatus==2) {
					SignStatus==1;
					$("#signIn_text").text("已签退");
					$("#signIn_text02").text("签退时间 :"+formatDataWithoutMillisecond(retObj.processtime));
				}
				
				signCount =true;
				
			}else{
				window.csq_hw.showToast(retObj.msg);
				return;
			}
		} 
     	
   	   // 获取最新的用户签到状态
    	function getSignStatus() {
		    
    		var A = "/cf/selectsign";
			var param = {
				"craftspersonCode":cf_id
			};
			var bool = window.csq_exe.doexec("/cf/selectsign", JSON.stringify(param));
			//alert(bool);
			var retObj = eval("(" + bool + ");");
			
		 	if(retObj.msg==1) {
				//当前为签到， 按钮显示签退
		 		$("#signIn_text").text("已签到");
		 		$("#signIn_text02").text("签到时间 :"+formatDataWithoutMillisecond(retObj.processtime));
		 		SignStatus =2;
			}else if(retObj.msg==2) {
				SignStatus =1;
				$("#signIn_text").text("已签退");
				$("#signIn_text02").text("签退时间 :"+formatDataWithoutMillisecond(retObj.processtime));
			}
		} 
    	
    	//更新完数据的回调方法
 	  /* function LoadServerDataSuccess() {
 		  getSignStatus();
 		}*/
    	
    	
    	