
//评分
var fen_d;
var preA;
var $objEl;
var countScore;
var atti;
var task_score;
function scoreFun(object, opts) {
	var defaults = {
		fen_d : 32,
		ScoreGrade : 5,
		types : [ "很不满意", "差得太离谱，与卖家描述的严重不符，非常不满", "不满意", "部分有破损，与卖家描述的不符，不满意",
				"一般", "质量一般", "没有卖家描述的那么好", "满意", "质量不错，与卖家描述的基本一致，还是挺满意的",
				"非常满意", "质量非常好，与卖家描述的完全一致，非常满意" ],
		nameScore : "star_fenshu",
		parent : "star_score",
		attitude : "star_attitude"
	};
	$objEl = object;
	options = $.extend({}, defaults, opts);
	countScore = object.find("." + options.nameScore);
	var startParent = object.find("." + options.parent);
	atti = object.find("." + options.attitude);
	var now_cli;
	var fen_cli;
	var atu;
	fen_d = options.fen_d;
	var len = options.ScoreGrade;
	startParent.width(fen_d * len);
	preA = (10 / len);
	for (var i = 0; i < len; i++) {
		var newSpan = $("<a href='javascript:void(0)'></a>");
		newSpan.css({
			"left" : 0,
			"width" : fen_d * (i + 1),
			"z-index" : len - i
		});
		newSpan.appendTo(startParent)
	}
	startParent.find("a").each(function(index, element) {
		$(this).click(function() {
			now_cli = index;
			showStar(index, $(this));
		});
		/*$(this).mouseenter(function() {
			showStar(index, $(this));
		});
		$(this).mouseleave(function() {
			if (now_cli >= 0) {
				var scor = preA * (parseInt(now_cli) + 1);
				startParent.find("a").removeClass("clibg");
				startParent.find("a").eq(now_cli).addClass("clibg");
				var ww = fen_d * (parseInt(now_cli) + 1);
				startParent.find("a").eq(now_cli).css({
					"width" : ww,
					"left" : "0"
				});
				if (countScore) {
					countScore.text(scor);
				}
			} else {
				startParent.find("a").removeClass("clibg");
				if (countScore) {
					countScore.text("");
				}
			}
		});*/
	});
};
function showStar(num, obj) {
	//alert(num);
	var n = parseInt(num) + 1;
	task_score = n;
	var lefta = num * fen_d;
	var ww = fen_d * n;
	var scor = preA * n;
	//atu = options.types[parseInt(num)];
	$objEl.find("a").removeClass("clibg");
	obj.addClass("clibg");
	obj.css({
		"width" : ww,
		"left" : "0"
	});
	//countScore.text(scor);
	//atti.text(atu);
	$("#star_fenshu_id").text(task_score+"分");//分数显示 task_score来自startScore.JS
}

function initShowStar(score){
	showStar(score-1, $(".star_content").find("a").eq(score-1));
}

function getScore(){
	return task_score;
}