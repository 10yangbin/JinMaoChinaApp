

//执行人名单
    	function showNextPeopleInfolist() {
    		
    		$(".graybg_pd").fadeIn();
    		$("#bstypeselect").slideDown(500);
    		$("#fade").fadeIn();
    		$("body").css("position","fixed");
    		var myScroll2 = new IScroll(".liucheng_paidan",{preventDefault:false});
    		var paiHtml01 = "";
    		var sql = " select * from afm_users afm , user_site ute where afm.status!=0 and afm.user_id = ute.user_id and ute.SITE_ID='" + window.csq_hw.getcurrUserSiteID() + "' and ute.DMS_UPDATE_TIME = (SELECT MAX(DMS_UPDATE_TIME) FROM user_site x WHERE x.USER_ID = afm.USER_ID) order by UPPER(afm.user_name) ";
    		
    		var userData = window.csq_db.db_select(0,sql, null,1);
    		
    		//整合数据
    		var userList = eval("(" + userData + ");");
    		var paiHtml01 = "";
    		for (var i = 0; i < userList.length; i++) {
    			paiHtml01 += '<li><strong class="num_name">' + userList[i].USER_ALIAS + '</strong><strong class="num_name">(' + userList[i].ROLE_NAME + ')</strong><i><input value="1" name="xiezhu" type="radio"  data-id="' + userList[i].USER_ALIAS + '"  data-u_id="' + userList[i].USER_ID + '"/></i></li>';
    		}
    		
    		$("#paidan_danren").html(paiHtml01);
    		initials();
    	  
    	} 
 
    

    	// 获取选择的执行人 
    	function getSelectedPerson() {
    			
    		    next_person = $("input[name='xiezhu']:checked").data('id');
    		    next_person_id  =  $("input[name='xiezhu']:checked").data('u_id');
    			
    			$("#next_person").html(next_person);
    			$("#bstypeselect").slideUp();
    			$("#fade").slideUp();
    			$("body").css("position","static");
    		
    	}


    	function closeType() {
    		$(".graybg_pd").fadeOut();
    		$(".baoshi_gdlx").slideUp(500);
    		$("#fade").fadeOut();
    	}
    	
    	
    	
