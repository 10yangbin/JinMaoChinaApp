

//afm_user信息的展示


		function initials() {//单人排序
		    var SortList=$("#paidan_danren li");
		    var SortBox=$("#paidan_danren");
		    SortList.sort(asc_sort).appendTo('#paidan_danren');//按首字母排序
		    function asc_sort(a, b) {
		        return makePy($(b).find('.num_name').text().charAt(0))[0].toUpperCase() < makePy($(a).find('.num_name').text().charAt(0))[0].toUpperCase() ? 1 : -1;
		    }

		    var initials = [];
		    var num=0;
		    SortList.each(function(i) {
		        var initial = makePy($(this).find('.num_name').text().charAt(0))[0].toUpperCase();
		        if(initial>='A'&&initial<='Z'){
		            if (initials.indexOf(initial) === -1)
		                initials.push(initial);
		        }else{
		            num++;
		        }
		        
		    });

		    $.each(initials, function(index, value) {//添加首字母标签
		        SortBox.append('<li class="firstLetter" id="'+ value +'">' + value + '</li>');
		    });
		    if(num!=0){SortBox.append('<li class="firstLetter"></li>');}

		    for (var i =0;i<SortList.length;i++) {//插入到对应的首字母后面
		        var letter=makePy(SortList.eq(i).find('.num_name').text().charAt(0))[0].toUpperCase();
		        switch(letter){
		            case "A":
		                $('#A').after(SortList.eq(i));
		                break;
		            case "B":
		                $('#B').after(SortList.eq(i));
		                break;
		            case "C":
		                $('#C').after(SortList.eq(i));
		                break;
		            case "D":
		                $('#D').after(SortList.eq(i));
		                break;
		            case "E":
		                $('#E').after(SortList.eq(i));
		                break;
		            case "F":
		                $('#F').after(SortList.eq(i));
		                break;
		            case "G":
		                $('#G').after(SortList.eq(i));
		                break;
		            case "H":
		                $('#H').after(SortList.eq(i));
		                break;
		            case "I":
		                $('#I').after(SortList.eq(i));
		                break;
		            case "J":
		                $('#J').after(SortList.eq(i));
		                break;
		            case "K":
		                $('#K').after(SortList.eq(i));
		                break;
		            case "L":
		                $('#L').after(SortList.eq(i));
		                break;
		            case "M":
		                $('#M').after(SortList.eq(i));
		                break;
		            case "N":
		                $('#N').after(SortList.eq(i));
		                break;
		            case "O":
		                $('#O').after(SortList.eq(i));
		                break;
		            case "P":
		                $('#P').after(SortList.eq(i));
		                break;
		            case "Q":
		                $('#Q').after(SortList.eq(i));
		                break;
		            case "R":
		                $('#R').after(SortList.eq(i));
		                break;
		            case "S":
		                $('#S').after(SortList.eq(i));
		                break;
		            case "T":
		                $('#T').after(SortList.eq(i));
		                break;
		            case "U":
		                $('#U').after(SortList.eq(i));
		                break;
		            case "V":
		                $('#V').after(SortList.eq(i));
		                break;
		            case "W":
		                $('#W').after(SortList.eq(i));
		                break;
		            case "X":
		                $('#X').after(SortList.eq(i));
		                break;
		            case "Y":
		                $('#Y').after(SortList.eq(i));
		                break;
		            case "Z":
		                $('#Z').after(SortList.eq(i));
		                break;
		            default:
		                $('#default').after(SortList.eq(i));
		                break;
		        }
		    };
		}
		
		function initials01() {//多人公众号排序
		    var SortList=$("#paidan_duoren li");
		    var SortBox=$("#paidan_duoren");
		    SortList.sort(asc_sort).appendTo('#paidan_duoren');//按首字母排序
		    function asc_sort(a, b) {
		        return makePy($(b).find('.num_name').text().charAt(0))[0].toUpperCase() < makePy($(a).find('.num_name').text().charAt(0))[0].toUpperCase() ? 1 : -1;
		    }

		    var initials = [];
		    var num=0;
		    SortList.each(function(i) {
		        var initial = makePy($(this).find('.num_name').text().charAt(0))[0].toUpperCase();
		        if(initial>='A'&&initial<='Z'){
		            if (initials.indexOf(initial) === -1)
		                initials.push(initial);
		        }else{
		            num++;
		        }
		        
		    });

		    $.each(initials, function(index, value) {//添加首字母标签
		    	idvalue=value+value;
		        SortBox.append('<li class="firstLetter" id="'+ idvalue +'">' + value + '</li>');
		    });
		    if(num!=0){SortBox.append('<li class="firstLetter"></li>');}

		    for (var i =0;i<SortList.length;i++) {//插入到对应的首字母后面
		        var letter=makePy(SortList.eq(i).find('.num_name').text().charAt(0))[0].toUpperCase();
		        switch(letter){
		            case "A":
		                $('#AA').after(SortList.eq(i));
		                break;
		            case "B":
		                $('#BB').after(SortList.eq(i));
		                break;
		            case "C":
		                $('#CC').after(SortList.eq(i));
		                break;
		            case "D":
		                $('#DD').after(SortList.eq(i));
		                break;
		            case "E":
		                $('#EE').after(SortList.eq(i));
		                break;
		            case "F":
		                $('#FF').after(SortList.eq(i));
		                break;
		            case "G":
		                $('#GG').after(SortList.eq(i));
		                break;
		            case "H":
		                $('#HH').after(SortList.eq(i));
		                break;
		            case "I":
		                $('#II').after(SortList.eq(i));
		                break;
		            case "J":
		                $('#JJ').after(SortList.eq(i));
		                break;
		            case "K":
		                $('#KK').after(SortList.eq(i));
		                break;
		            case "L":
		                $('#LL').after(SortList.eq(i));
		                break;
		            case "M":
		                $('#MM').after(SortList.eq(i));
		                break;
		            case "N":
		                $('#NN').after(SortList.eq(i));
		                break;
		            case "O":
		                $('#OO').after(SortList.eq(i));
		                break;
		            case "P":
		                $('#PP').after(SortList.eq(i));
		                break;
		            case "Q":
		                $('#QQ').after(SortList.eq(i));
		                break;
		            case "R":
		                $('#RR').after(SortList.eq(i));
		                break;
		            case "S":
		                $('#SS').after(SortList.eq(i));
		                break;
		            case "T":
		                $('#TT').after(SortList.eq(i));
		                break;
		            case "U":
		                $('#UU').after(SortList.eq(i));
		                break;
		            case "V":
		                $('#VV').after(SortList.eq(i));
		                break;
		            case "W":
		                $('#WW').after(SortList.eq(i));
		                break;
		            case "X":
		                $('#XX').after(SortList.eq(i));
		                break;
		            case "Y":
		                $('#YY').after(SortList.eq(i));
		                break;
		            case "Z":
		                $('#ZZ').after(SortList.eq(i));
		                break;
		            default:
		                $('#default').after(SortList.eq(i));
		                break;
		        }
		    };
		}
		
		function initDutyman_ohterman(){
		
			$("input[type=checkbox]").click(function() {
			$("#listnavChecked").show();
			if ($(this).attr("checked")) {
				
				/*var spanName = "";
				spanName += '<span calss="myU serSpan">' + $(this).prev().text() + '</span>'
				$("#listnavChecked").append(spanName);*/
				$(this).next().text("设为责任人");
				$(this)
					.next()
					.click(
						function() {
							var checkboxs = $("input[type=checkbox]");
							for (var i = 0; i < checkboxs.length; i++) {
								var checkbox = $(checkboxs[i]);
								if (checkbox
									.attr("checked")) {
									var next = checkbox
										.next();
									if (next[0] != $(this)[0]) {
										next
											.css(
												"color",
												"#999999");
										checkbox
											.attr(
												'data-zeren',
												'false');
										next
											.text("设为责任人");
									} else {
										if (next
											.text() == "设为责任人") {
											$(this)
												.css(
													"color",
													"#006e92");
											$(this)
												.text(
													"我是责任人");
											checkbox
												.attr(
													'data-zeren',
													'true');

											var arrSpan = new Array(); //多人派单选中的人
											$(
													"#listnavChecked span")
												.each(
													function(
														index,
														el) {
														arrSpan
															.push($(
																	this)
																.html());
													});
											var arrStrong = $(
													this)
												.prev()
												.prev()
												.text();
											$
												.each(
													arrSpan,
													function(
														n,
														value) {
														if (value === arrStrong) {
															$(
																	"#listnavChecked span:contains('" + value + "')")
																.css(
																	"color",
																	"#006e92")
																.siblings(
																	'span')
																.css(
																	"color",
																	"#6d6e71");
														};
													});
										} else {
											next
												.css(
													"color",
													"#999999");
											next
												.text("设为责任人");
											checkbox
												.attr(
													'data-zeren',
													'false');
										}
									}
								} else {
									checkbox
										.next()
										.text(
											"");
									checkbox
										.next()
										.css(
											"color",
											"#999999");
									checkbox
										.attr(
											'data-zeren',
											'false');
								}
							}
						});
			} else {
				$(this).next().text("");
				$(this).next().css("color", "#999999");
				$(this).attr('data-zeren', 'false');
				var arrSpan = new Array();
				$("#listnavChecked span").each(
					function(index, el) {
						arrSpan.push($(this).html());
					});
				var arrStrong = $(this).prev().text();
				$.each(arrSpan, function(n, value) {
					if (value === arrStrong) {
						$(
								"#listnavChecked span:contains('" + value + "')")
							.remove();
					};
				});

				function contains(arrA, arrB) {
					for (var i = 0; i < arrA.length; i++) {
						var obj = arrA[i];
						var len = arrB.length;
						while (len--) {
							if (arrB[len] === obj) {

								return true;
							}
						}
						return false;
					}
				}
				contains(arrStrong, arrSpan);
			}
			$('input[name="xiezhuDuo"]:checked').each(
				function() {
					var sfruit = $(this).val(); // 多人时  选中的人数
				})
		});
}		
		