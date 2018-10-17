

//工单list界面可提取的js


    			function covertStatus(status) {
				switch (status) {
                    case 'Notdistribute':
                        return '未开始';
					case 'Processed':
						return '待分派';
					case 'Assigned':
						return '已分派';
					case 'Answered':
						return '已响应';
					case 'Backed':
						return '已退回';
					case 'Processing':
						return '处理中';
					case 'Completed':
						return '已完成';
					case 'Finished':
						return '工单完结';
                    case 'Closed':
                        return '已废弃';
                    case 'TimeOut':
                        return '已超时';
				}
			}

			//工单汉子类型的颜色样式
			function covertStatusClass(status) {
				switch (status) {
					case 'Processed':
						return 'gd_daiqiangdan';
					case 'TimeOut':
						return 'gd_daiqiangdan';	
					case 'Assigned':
						return 'gd_chulizhong';
					case 'Answered':
						return 'gd_chulizhong';
					case 'Backed':
						return 'gd_daipaidan';
					case 'Processing':
						return 'gd_chulizhong';
					case 'Completed':
						return 'gd_yiwancheng'; 
					case 'Finished':
						return 'gd_yijieshu';
						
				}
			}
			
			//工单类型的转换
			function covertTaskType(ttype) {

				switch (ttype) {
					case 'A':
						return '报事工单';
					case 'C':
						return '盘点工单';	
					case 'T':
						return '客服报事';
					case 'E':
						return '抄表工单';
					case 'X':
						return '抄表工单';	
					case 'P':
						return '计划工单';
					case 'R':
						return '二装计划工单';	
					
				}
			}
			
			//转换紧急程度
			function covertTaskPriority(num) {

				switch (num) {
					case '1':
						return '公区报事';
					case '2':
						return '公区代客';
					case '3':
						return '入室维修';
					case '4':
						return '计划工单';
				}
			}


	  function statusLineCss(status){

		if(status=="Processed"){
			return new Array("Processing","Completed");
		}else if(status=="Assigned"){
			return new Array("Processing","Completed");
		}else if(status=="Answered"){
			return new Array("Processing","Completed");;
		}else if(status=="Backed"){
			return new Array( "Assigned","Processing","Completed");
		}else if(status=="Processing"){
			return new Array("Completed");
		}else if(status=="Completed"){
			return new Array("Finished");
		}else if(status=="Finished"){
			return new Array();
		}
	}