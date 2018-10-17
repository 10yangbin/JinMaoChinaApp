

    //撤销弹出
	function CancleTaskShow() {
		$(".graybg_pd").fadeIn();
		$("#task_cancle_show").slideDown();
		$("#fade").fadeIn();
		$("#bodyId")[0].style.position = "fixed";
		uiFlag = 1;
	}

	//撤销action
	function CancleTask() {
	
		var backcontent = $("#canclecontent").val();

		if (backcontent.trim() == '') {
			window.csq_hw.showToast("请输入废弃原因！");
			uiFlag = 0;
			return;
		}
		goBack();

		var A = "/process/aboundOrder";
		var P = {
			"task_id" : taskId,
			"inform_man" : task[0].INFORM_MAN_NAME + "",
			"content" : backcontent
		};
		
		var ret = window.csq_exe.exec(A, JSON.stringify(P), null, true);
		var retObj = eval("(" + ret + ");");
		if (retObj) {
			window.csq_hw.showToast("工单废弃成功");
			window.csq_hw.showToast("工单状态同步中，请稍后");
			backToList();
		} else {
			window.csq_hw.showToast("工单废弃失败，请稍候再试");
			return;
		}

	}

	  //撤销隐藏
	function cancleBack() {
		uiFlag = 0;
		$("#fade").fadeOut();
		$("#task_cancle_show").hide();
		$("#bodyId")[0].style.position = "static";
	}