

//star  执行 和 流程使用 
$(document).ready(function(){
    var stepW = 24;
    var stars = $("#star > li");
    var descriptionTemp;
    $("#showb").css("width",0);
    stars.each(function(i){
        $(stars[i]).click(function(e){
            var n = i+1;
            var cWidth = $("#showb").width();
            if(cWidth == stepW*n){
             $("#showb").css({"width":stepW*(n-1)});	
            }else{
            $("#showb").css({"width":stepW*n});
            }
//          descriptionTemp = description[i];
            $(this).find('a').blur();
            return stopDefault(e);
            return descriptionTemp;
        });
    });
    stars.each(function(i){
        $(stars[i]).hover(
            function(){
            });
    });
});
function stopDefault(e){
    if(e && e.preventDefault)
           e.preventDefault();
    else
           window.event.returnValue = false;
    return false;
};
