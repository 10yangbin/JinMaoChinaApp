

//公用的js

	
	var detpCode;//当前用户的部门编号 

	//var BasePicUrl	='http://192.168.0.161:8000/images/';
	//var BasePicUrl	='http://219.141.190.18:8125/images/';
	//var BasePicUrl	='http://192.168.0.161:8000/images/';
	var BasePicUrl ='https://pdcbp01t.glprop.com:8443/images/';

    function showPic(path){ 
	window.csq_hw.showDrawingImage(path);
    };
    
    function onPicClick(url) {
		if(url.length>9){
		window.location.href=url;
		}
		
	}
    
	function dialPhone(phoneNumber) {
		if (phoneNumber.trim().length != 0) {
			window.csq_hw.dialNumber(phoneNumber);
		}
	}

	//通过参数key获取当前URL的参数value 
	function getUrlParamValByKey(name) {
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
		var r = decodeURIComponent(window.location.search).substr(1).match(reg);
		if(r){
			return unescape(r[2]);
		}
		return "";
	}
	

	function strCheckOut(str){ 
		//如果备注不为空,展示
		str = !str?'':str;
	}

	
	
	//获取用户姓名
	function getUserAlias(user_id) {
				
		var sql = "select USER_NAME from afm_users where upper(USER_ID)='"
				+ user_id.toUpperCase() + "'";

		var data = window.csq_db.db_select(0, sql, '', 1);
        
	
		data = eval("(" + data + ")");
		var useralias = '';
		if (data.length > 0) {
			useralias = data[0].user_name;
		}
		if(!useralias){
			useralias = user_id;
		}
		return useralias;
	}
	
	
	/*function getUserType(){
		var  sql_dept = "SELECT cf_type FROM user_site where USER_ID='"+getUserMsg('user_id')+"' and site_id='"+getUserMsg('site_id')+"'";

		var deptcode =  window.csq_db.db_select(0, sql_dept, '', 1);
	
		//alert("user"+deptcode);
		detpCode = eval("(" + deptcode + ");");
		 
		if(detpCode.length>0){
		return	detpCode[0].cf_type;
		
		}else{
		  window.csq_hw.showToast("人员类型未完善,请更新数据!");	
		}
	}*/
	
	function getUserType(){
		if(!user_type){
			user_type = window.csq_hw.getcurrUserType();
			//alert("用户类型"+user_type);
		}
		return user_type;
	}
	
	
	function getUserTypebyID(user_id){
		var  sql_dept = "SELECT cf_type FROM user_site where USER_ID='"+user_id+"' and site_id='"+getUserMsg('site_id')+"'";

		var deptcode =  window.csq_db.db_select(0, sql_dept, '', 1);
	
		//alert("CF"+deptcode);
		detpCode = eval("(" + deptcode + ");");
		 
		if(detpCode.length>0){
		return detpCode[0].cf_type;
		
		}else{
		  //window.csq_hw.showToast("人员部门信息未完善,请添加。");	
		}
	}
	
	//通过用户id在 人员表 获取所属部门信息
	function getUserDeptCode(){
		var  sql_dept = "SELECT dp_code FROM AFM_USERS where USER_ID='"+getUserMsg('user_id')+"'";

		var deptcode =  window.csq_db.db_select(0, sql_dept, '', 1);
		//alert(deptcode);
		
		detpCode = eval("(" + deptcode + ");");
		 
		if(detpCode.length>0){
			return detpCode= detpCode[0].dp_code;
		}else{
		  //window.csq_hw.showToast("人员部门信息未完善,请添加。");	
		}
	}
	
	//通过用户id在 获取客户的编号 
	function getUserCustomerID(){
		var  sql_dept = "SELECT customer_id FROM AFM_USERS where USER_ID='"+getUserMsg('user_id')+"'";

		var deptcode =  window.csq_db.db_select(0, sql_dept, '', 1);
		//alert(deptcode);
		
		detpCode = eval("(" + deptcode + ");");
		 
		if(detpCode.length>0){
			return detpCode= detpCode[0].customer_id;
		}else{
		  //window.csq_hw.showToast("人员部门信息未完善,请添加。");	
		}
	}
	
	function getUserCustomerName(){
		var  sql_dept = "SELECT customer_name FROM CUSTOMER where customer_id='"+getUserCustomerID()+"'";

		var deptcode =  window.csq_db.db_select(0, sql_dept, '', 1);
		//alert(deptcode);
		
		detpCode = eval("(" + deptcode + ");");
		 
		if(detpCode.length>0){
			return detpCode= detpCode[0].customer_name;
		}else{
		  //window.csq_hw.showToast("人员部门信息未完善,请添加。");	
		}
	}

	
	 //获取网页传参
	function getUserMsg(key) {
		if(key == "user_id"){
			return window.csq_hw.getUserId();
		}else if(key == "name"){
			return window.csq_hw.getCurrentUserName();
		}else if(key == "site_id"){
			return window.csq_hw.getcurrUserSiteID();
		}else if(key == "phone"){
			return window.csq_hw.getUserPhone();
		}else if(key == "ismonitor"){
			return window.csq_hw.isCurrUserMonitor();
		}
	}

	   //数组包含字符串
	function array_in(val, arr) {
			for (i in arr) {
				if (arr[i] == val) {
					return true;
				}
			}
			return false;
		}
	
	/**
	* 删除左右两端的空格
	*/
	function trim(str)
	{
	     return str.replace(/(^\s*)|(\s*$)/g, '');
	}
	
	//去左空格; 
	function ltrim(str){ 
		return str.replace(/^s+/g,''); 
	} 
	//去右空格; 
	function rtrim(str){ 
		return str.replace(/s+$/g,''); 
	} 
	//去左右空格; 
	function trim(s){ 
		return rtrim(ltrim(s)); 
	}	
	
	
	function formatDataWithoutMillisecond(date){
		if(!date || !(typeof(date) == 'date' || typeof(date) == 'string')) return date;
		//时间类型的
		
		if(typeof(date) == 'date'){
			
		}else{
			date = trim(date);
			if(date.length >= 19){
				return date.substring(0,16); 
			}else{
				return date;
			}
		}
	}
	
	//获取当前时间 
	function curentTime() {
		var now = new Date();
		var year = now.getFullYear(); //年
		var month = now.getMonth() + 1; //月
		var day = now.getDate(); //日
		var hh = now.getHours(); //时
		var mm = now.getMinutes(); //分
		var clock = year + "-";
		if (month < 10)
			clock += "0";
		clock += month + "-";
		if (day < 10)
			clock += "0";
		clock += day + " ";
		if (hh < 10)
			clock += "0";
		clock += hh + ":";
		if (mm < 10) clock += '0';
		clock += mm;
		clock += ":" + now.getSeconds();
		return (clock);
	}
	
	
	/*----------------------派单人员按字母搜索--------------------------*/
	$.expr[":"].Contains = function(a, i, m) {
		var str =  (a.textContent || a.innerText || "").toUpperCase();
		var filterchar = m[3].toUpperCase();
		if(/^[\u4e00-\u9fa5]+$/.test(str) && !(/^[\u4e00-\u9fa5]+$/.test(filterchar))) {
			//转换拼音
			str = codefans_net_CC2PY(str).toUpperCase();
			//str = str.substring(0,1);
		}
		return str.indexOf(filterchar) == 0;
	};

	//搜索
	function filterList(header, list) {
		//@header 头部元素
		//@list 无需列表
		//创建一个搜素表单
		var form = $("<form>").attr({
			"class":"filterform",
			action:"#"
		}), input = $("<input>").attr({
			"class":"filterinput",
			"id" : "filterinput",
			type:"text",
			placeholder:"搜索"
		});

		$(form).append(input).appendTo(header);
		$(".filterinput").change(function() {
			if($(this).val() == ""){
				$(list).find("li").show();
			}
			
			var filter = $(this).val();

			$(".soso span").click(function(event) {
			$("input#filterinput").attr("value","");
			$(this).hide();
			$(list).find("li").show();
		});
			if (filter != null) {
				$(".soso span").show();
				$matches = $(list).find("strong:contains(" + filter + ")").parent();
				//$("li", list).not($matches).hide();
				$("li", list).hide();
				$matches.show();
			} else {
				$(".soso span").hide();
				$(list).find("li").show();
			}
			return false;

		}).keyup(function() {
			$(this).change();
		});
	}
	



	
	
	
	
	
	
	