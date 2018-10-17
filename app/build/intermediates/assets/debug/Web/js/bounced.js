

/*弹出框提醒的统一js*/

function bouncedShow(tip){
    document.querySelector("#bounced").style.display="block";
    document.querySelector("#bounced .con").innerHTML=tip;
    setTimeout(function(){
        document.querySelector("#bounced").style.display="none";
    },2000)
}

function bocuncedHide(){
    document.querySelector("#bounced").style.display="none";
}