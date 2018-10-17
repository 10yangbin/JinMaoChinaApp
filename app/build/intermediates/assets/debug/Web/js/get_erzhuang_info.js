



/**  获取项目二装的信息,提供整改，动火，水票，作业申请等业务使用*/

/**
 * 1  首页提供一个点击方法,查询项目对应的二装所有信息。
 * 2  拼接数据
 * 3  点击item,传递数据到对应的界面。
 * 4  传递下级界面所需要的id,数据等。
 * 
 * */ 

    
    function getErzhuangInfoData(){
       	 
          	 /**判断是否有二装数据*/
    	    var data;
    	 
            var sql_count = "select count(td_code) as count from decoration_infor where site_id ='" + getUserMsg('site_id') + "'";
       	 	var data_count = window.csq_db.db_select(0, sql_count,'',2);

          	 if(!data_count || data_count.length <= 0)   {
          	 		window.csq_hw.showToast("该项目没有二装信息");
          	 		return false;
          	 }

      	     var sql = "select * from decoration_infor where site_id ='" + getUserMsg('site_id') + "'";
      	     data = window.csq_db.db_select(0, sql,'',2);
      	     
      	     data = eval("(" + data + ");");
      	     $('.house-list').empty();
      	     appendList(data);

          }
    
     //拼接数据
 	function appendList(data) {
 		
 		    var item;
			var td_code;
			var apply_time;
			var rm_id;
			var rm_name;
			var apply_unit;
			var db_name;
			var db_charge_man;
			
			
			for (var i = 0; i < data.length; i++) {
				item = data[i];  
				td_code = item.td_code;
				apply_time = !item.po_application_date?'':item.po_application_date;
				rm_id = !item.po_rm_id?'':item.po_rm_id;
				rm_name = !item.po_rm_name?'':item.po_rm_name;
				apply_unit = !item.po_application_unit?'':item.po_application_unit;
				db_name = !item.db_name?'':item.db_name;
				db_charge_man = !item.applicant?'':item.applicant;
				var htmlStr='';
				htmlStr = "<li data-id='" + td_code + "' data-applicant='" + db_charge_man + "' onclick='onItemClick(this)'>";
				htmlStr = htmlStr +	"<p><span class='tit'>二装申请时间:</span><span class='detail'>" + formatDataWithoutMillisecond(apply_time) + "</span></p>";
				htmlStr = htmlStr +	"<p><span class='tit'>房屋编号:</span><span class='detail'>" + rm_id + "</span></p>";
				htmlStr = htmlStr +	"<p><span class='tit'>房屋名称:</span><span class='detail'>" + rm_name + "</span></p>";
				htmlStr = htmlStr +	"<p><span class='tit'>申请单位:</span><span class='detail'>" + apply_unit + "</span></p>";
				htmlStr = htmlStr +	"<p><span class='tit'>装修公司名称:</span><span class='detail'>" + db_name + "</span></p></li>";
				/*htmlStr = htmlStr +	"<span class='date'>" + apply_time + "</span></li>";*/
				$('.house-list').append(htmlStr);
			
			}
 	   }
 	
	 function onItemClick(obj) {
		var $this = $(obj);
		Td_code = $this.data('id');
		db_charge_man = $this.data('applicant');
		//alert(db_charge_man);
		$("#change_man").html($this.data('applicant'));
	 }







