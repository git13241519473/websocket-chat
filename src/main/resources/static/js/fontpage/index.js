$(function(){
    //创建房间
    $("#createRoom").click(function(){
        //获取选中的人
        var userIds = [];
        $("[name='user']:checked").each(function(){
            userIds.push($(this).val());
        });
        if(userIds.length <= 0){
            alert("请选择成员");
            return ;
        }
        $.ajax({
            url: "/user/createRoom",
            type: "get",
            data: {"userIds": userIds.join(",")},
            dataType: "json",
            success: function(result){
                var roomId = result.roomId;
                $("#roomList").append("<input type='button' value='"+ roomId +"' onclick=roomChat('"+ roomId +"') />");
            }
        });
    });

    roomChat = function (roomId) {
        console.info(roomId);
        window.location.href = "/page/to/roomChat";
    }
});