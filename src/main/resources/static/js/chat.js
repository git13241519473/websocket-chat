$(function () {
    var socket;     //websocket pc端对象
    var param = {   //发送消息承载对象
        "toUserId": "",
        "contentText": ""
    };
    //点击发送按钮，发送消息
    $("#submit").click(function () {
        param.toUserId = $("#toUserId").val();
        param.contentText = $("#contentText").val();
        if (!param.toUserId) return;
        if (!param.contentText) return;
        sendMessage(param);
    });

    //连接到服务器，相当于打开并登陆QQ，证明自己可以收到消息
    $("#linkServer").click(function () {
        var userId = $("#userId").val();
        if (userId) {
            openSocket();
        }
    });


    function openSocket() {
        if (typeof(WebSocket) == "undefined") {
            console.log("您的浏览器不支持WebSocket");
            return ;
        }
        console.log("您的浏览器支持WebSocket");
        //实现化WebSocket对象，指定要连接的服务器地址与端口  建立连接
        //等同于socket = new WebSocket("ws://localhost:8888/xxxx/im/25");
        //var socketUrl="${request.contextPath}/im/"+$("#userId").val();
        var socketUrl = "ws://localhost:8081/imserver/" + $("#userId").val();
        console.log(socketUrl);
        if (socket != null) {
            socket.close(); //关闭socket对象
            socket = null;
        }
        socket = new WebSocket(socketUrl);
        //打开事件
        socket.onopen = function () {
            console.info(socket.readyState);
            //实例对象的readyState属性表示： 0:表示正在连接；1:表示连接成功，可以通信了；2:表示正在关闭；3:表示已经关闭或者连接失败。
            if(socket.readyState == 1){
                console.info("websocket已打开");
                $("#linkServer").hide();
            }else{
                console.info("websocket打开异常");
                this.alert("服务器忙，请稍后再试。。。")
            }
        };
        //获得消息事件
        socket.onmessage = function (msg) {
            //发现消息进入    开始处理前端触发逻辑
            var message = $.parseJSON(msg.data);
            if(message.type == "0"){ //系统消息
                $("#showMsgs").append("<p style='color: red;'>"+ message.contentText +"</p>");
            }else if(message.type == "1"){ //用户消息
                $("#showMsgs").append("<p style='color: fuchsia;'>"+ message.contentText +"</p>");
            }
        };
        //关闭事件
        socket.onclose = function () {
            console.log("websocket已关闭");
        };
        //发生了错误事件
        socket.onerror = function () {
            console.log("websocket发生了错误");
        }
    }

    function sendMessage(param) {
        if (typeof(WebSocket) == "undefined") {
            console.log("您的浏览器不支持WebSocket");
        } else {
            console.log("您的浏览器支持WebSocket, 即将发送消息： "+ param);
            $("#showMsgs").append("<p>"+ param.contentText +"</p>");
            $("#contentText").val("");
            socket.send('{"toUserId":"'+ param.toUserId +'","contentText":"'+ param.contentText +'"}');
        }
    }

    $("#closeMsgDiv").click(function(){
        console.info("关闭消息弹窗");
        $("#warningMsgDiv").hide();
    });
    setTimeout(function(){
        $("#warningMsgDiv").hide();
    }, 5000);


});