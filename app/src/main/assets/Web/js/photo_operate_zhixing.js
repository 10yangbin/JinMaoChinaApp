

//图片的点击，删除等操作

var timeOutEvent=0;//定时器

var files = ""; //保存本地拍照图片的路径，不同图片间问号分割

            //开始按 
			function gtouchstart(m_index){ 
			    timeOutEvent = setTimeout("longPress("+m_index+")",500);//这里设置定时器，定义长按500毫秒触发长按事件，时间可以自己改，个人感觉500毫秒非常合适
			    return false; 
			}; 
			//手释放，如果在500毫秒内就释放，则取消长按事件，此时可以执行onclick应该执行的事件 
			function gtouchend(m_index){ 
			    clearTimeout(timeOutEvent);//清除定时器 
			    if(timeOutEvent!=0){ 
			        //这里写要执行的内容（尤如onclick事件） 

			    } 
			    return false; 
			}; 
			//如果手指有移动，则取消所有事件，此时说明用户只是要移动而不是长按 
			function gtouchmove(m_index){ 
			    clearTimeout(timeOutEvent);//清除定时器 
			    timeOutEvent = 0; 
			}; 
            
			//真正长按后应该执行的内容 
			function longPress(m_index){ 
			    timeOutEvent = 0; 
			    //执行长按要执行的内容，如弹出菜单 
			    //if(!confirm("删除确认|是否要移除该相片？|移除相片|不移除")){
                if(!confirm("是否要移除该相片？")){
			    	return;
			    }else{
			    	removePhoto(m_index);
			    }
                
			} 
			//清除相片
			function removePhoto(m_index){
				var t_files = '';
				var _image = files.split(",");
				var _html = '<a href="javascript:;" onclick="toPhoto();"><img src="img/btn_add.png"></a></span>';
				$("#Photo").html(_html);
				if(_image.length>0){
					for(var k=0;k<_image.length;k++){
						if(_image[k] && _image[k] != '-1'){
							var _index = k+1;
							if(m_index != _index){
								var t_html = $("#Photo").html();
								var n_html = "<a id=\""+_index +"\"";
								n_html = n_html + 'ontouchstart="gtouchstart('+_index+')" ontouchmove="gtouchmove('+_index+')" ontouchend="gtouchend('+_index+')"';
								n_html = n_html + " href=\"" + _image[k] + "\"> <img src=\"" + _image[k] + "\"></a>";
								$("#Photo").html(n_html + t_html);
								t_files = t_files +','+_image[k]; 
							}else{
								t_files = t_files +',-1';
							}
						}
					}
				}
				
				t_files = t_files.substring(1);
				files=t_files;
				saveTemp();
			}
			//处理image将其中的-1去掉
			function processImage(m_image){
				var _image = m_image.split(',');
				var _files = '';
				for(var k=0;k<_image.length;k++){
					if(_image[k]){
						if (_image[k] != '-1'){
							_files = _files + ',' +_image[k];	
						}
					}
				}
				_files = _files.substring(1);
				return _files;
			}		
			
			
			//拍照操作
			function toPhoto() {
				//saveTemp();
				//window.csq_hw.camera();
				window.csq_hw.selectImage();
			}
			
			 //扫码操作
			function toQR() {
				
				window.csq_hw.scanQR();
			}
			function pictureCallbackAfterRestart(data) {
				pictureCallback(data);
			}
			
			//拍照回调
			function pictureCallback(data) {
				var _index = 0;
				if (files == "") {
					files = data;
					_index =1;
				} else {
					files += "," + data;
					var _image = files.split(",");
					_index= _image.length;
				}
				var t_html = $("#Photo").html();
				var n_html = "<a id=\""+_index +"\"";
				n_html = n_html + 'ontouchstart="gtouchstart('+_index+')" ontouchmove="gtouchmove('+_index+')" ontouchend="gtouchend('+_index+')"';
				//n_html = n_html + " href=\"" + data + "\"> <img src=\"" + data + "\"></a>"; onclick=showPic("' + path+ '");
				n_html = n_html  + '><img class="miaoshu" src="' + data + '" onclick=showPic("' + data+ '"); /></a>';
				if(data!=null){
					$("#Photo").html(n_html + t_html);
				}
				//saveTemp();
			}
			
			
			//保存临时文件
			function saveTemp(){
				
				var picPath = files;
				//重新处理一下image把其中的-1值去掉
				m_image = processImage(files);
				var gdmaioshu = $("#taskDescriptId").val();
				
				 var money1=parseFloat( $("#money_need input").val());
                 var money2=parseFloat( $("#money_need2 input").val());
                 var money3=parseFloat( $("#money_need3 input").val());

				
				if(signaturePhoto && signaturePhoto.length>0){
					if(m_image && m_image.length>0){
						m_image = m_image+","+signaturePhoto;
					}
					else{
						m_image = signaturePhoto;
						
					}
			}
					var param = {
						'isNeedScan':isCanWork,
						'isNeedSign':isNeedSign,
						'money1': money1, //完成内容描述
						'money2': money2,
						'money3': money3, //评价内容描述
						'finish_desc': gdmaioshu, //完成内容描述
						'task_id': gongdanId,
						'cus_eval_text': pingfen_comment, //评价内容描述
						'cus_eval_star': pingfen_num, //评星
						'borrow': wuliaoList, //   物料  多个 JSON.stringify(wuliaoList)
						'images':m_image,
						'price': money
					};
			
				
					//alert(m_image);	
			
				window.csq_io.file_write('zhixing'+gongdanId, JSON.stringify(param), false);
			}	
			
			//加载临时文件
			function loadTemp(){
				var _pmflag=false;
			
				var bs = window.csq_io.file_read('zhixing'+gongdanId);
				
				//alert(bs);
				//如果有临时文件，从临时文件中取内容
				if(bs){
					bs = eval("(" + bs + ");");
					_bsflag=true;
				}else{
					return;
				}
				
				var bs_desc = '';
				if(_bsflag){
					var bs_desc = bs.finish_desc;
					files = processImage(bs.images);
					
					if(bs.isNeedScan){
						$('#isCanWork').attr('class', "baoshi_kaiguan_on");
					}
					$("#money_need input").val(bs.money1);
	                $("#money_need2 input").val(bs.money2);
	                $("#money_need3 input").val(bs.money3);
	                
	                isNeedSign =  bs.isNeedSign;
	                
	            	 pingfen_num = bs.cus_eval_star;
	    			 pingfen_comment =bs.cus_eval_text;
	    			 wuliaoList = bs.borrow;
	                
	                
	                
					$("#taskDescriptId").val(bs_desc);
					var _image = files.split(",");
					if(_image.length>0){
						for(var k=0;k<_image.length;k++){
							//alert(_image[k]);
							if(_image[k] && _image[k] !='-1'&& _image[k].indexOf("sign")<0){
								var _index = k+1;
								var t_html = $("#Photo").html();                       /*  onclick=\"chazhao(\'" + csi_id + "\'); */
								var n_html = "<a id=\""+_index +"\"";
								n_html = n_html + ' ontouchstart="gtouchstart('+_index+')" ontouchmove="gtouchmove('+_index+')" ontouchend="gtouchend('+_index+')"';
								//n_html = n_html + " href=\"" + _image[k] + "\"> <img src=\"" + _image[k] + "\"></a>";
								n_html = n_html  + '><img class="miaoshu" src="' + _image[k] + '" onclick=showPic("' +_image[k]+ '");/></a>';
								//alert(n_html);
								if(_image[k]!='null'){
									$("#Photo").html(n_html + t_html);
								}
							}else {
								//alert(_image[k]);
								var tag = "<img src='" +  _image[k] + "'  style='height:30%; width:60% ;  display:block;' ";//margin:0 auto;
								tag = tag + ' ontouchstart="gtouchstart()" ontouchmove="gtouchmove()" ontouchend="gtouchend()" onclick=showPic("' +  _image[k]+ '"); />';
								$("#picture_hand").html(tag);
							}
						}
					}
				}	
			}
			
			
			
			
			
			
			
			
			