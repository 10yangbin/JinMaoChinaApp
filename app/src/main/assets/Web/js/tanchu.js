function popWin(a) {
    function n() {
        var a = k ? k: document.body,
        b = a.scrollHeight > a.clientHeight ? a.scrollHeight: a.clientHeight,
        c = a.scrollWidth > a.clientWidth ? a.scrollWidth: a.clientWidth;
        $("#maskLayer").css({
            height: b,
            width: c
        })
    }
    var d, e, b = 9e3,
    c = !1,
    f = $("#" + a),
    g = f.width(),
    h = f.height(),
    i = f.find(".tit"),
    j = f.find(".close"),
    k = document.documentElement,
    l = ($(document).width() - f.width()) / 2,
    m = (k.clientHeight - f.height()) / 2;
    f.css({
        display: "block",
        "z-index": b - -1
    }),
    i.mousedown(function(a) {
        c = !0,
        d = a.pageX - parseInt(f.css("left")),
        e = a.pageY - parseInt(f.css("top")),
        f.css({
            "z-index": b - -1
        }).fadeTo(50, .5)
    }),
    i.mouseup(function() {
        c = !1,
        f.fadeTo("fast", 1)
    }),
    $(document).mousemove(function(a) {
        if (c) {
            var b = a.pageX - d;
            0 >= b && (b = 0),
            b = Math.min(k.clientWidth - g, b) - 5;
            var i = a.pageY - e;
            0 >= i && (i = 0),
            i = Math.min(k.clientHeight - h, i) - 5,
            f.css({
                top: i,
                left: b
            })
        }
    }),
    j.live("click",
    function() {
        $(this).parent().parent().hide().siblings("#maskLayer").remove()
    }),
    $('<div id="maskLayer"></div>').appendTo("body").css({
        background: "#000",
        opacity: ".4",
        top: 0,
        left: 0,
        position: "absolute",
        zIndex: "8000"
    }),
    n(),
    $(window).bind("resize",
    function() {
        n()
    }),
    $(document).keydown(function(a) {
        27 == a.keyCode && ($("#maskLayer").remove(), f.hide())
    })
}