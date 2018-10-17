

//javascript 验证手机号码的正确性 
function is_mobile(value){ 
    
	if(!value){
		//alert(1);
		return; //非空直接返回
	}else {
	var pattern=/^1[34578][0123456789]\d{8}$/; 
    if(!pattern.test(value)){ 
    	//alert(2);
    	//window.csq_hw.showToast("输入正确手机号");
        return false; 
    }else{
    	//alert(3);
    	return true; 
    } 
	} 
} 
//javascript 验证电子邮箱的正确性 
function is_email(value){ 
    
	if(!value){
		//alert(1);
		return;
	}else {
		var pattern=/^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/; 
	    if(!pattern.test(value)){ 
	    	//alert(2);
	    	//window.csq_hw.showToast("输入正确邮箱");
	        return false;  
	    }else{
	    	//alert(3);
	    	return true; 
	    }  
	}
	
} 

