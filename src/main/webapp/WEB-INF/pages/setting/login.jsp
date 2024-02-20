<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<base href="<%=basePath%>">
<script type="text/javascript" src="jquery/jquery-3.3.1-min.js"></script>

<meta charset="UTF-8">
<title>login</title>
<style type="text/css">
    #info {
        color: red;
    }
</style>
<script type="text/javascript">
    $(function () {

        $("#login_button").click(function () {
            var pwd = $("#password").val().trim();
            var user = $("#userName").val().trim();
            var kaptcha = $("#kaptcha").val().trim();
            var rememberMe = $("#rememberMe:checked").val();
            console.info(pwd + " " + user + " " + kaptcha + " " + rememberMe)
            $.ajax({
                url: "dologin.do",
                type: "POST",
                /*data: {
                    pwd: pwd,
                    user: user,
                    kaptcha: kaptcha
                },*/
                data: {
                    data: JSON.stringify({
                        pwd: pwd,
                        user: user,
                        kaptcha: kaptcha,
                        "remember-me": rememberMe
                    })
                },
                contentType: "application/x-www-form-urlencoded",
                success: function (data) {
                    console.info(data)
                    var retInfo = JSON.parse(data);
                    if (!retInfo.msg == "登录成功") {
                        alert(retInfo.mes)
                    }
                    window.location.href = "transfer/fileupload3.do";
                },
                error: function (data) {
                    document.getElementById("info").innerText = data.responseJSON.msg
                }
            })
        })
    });
    //点击图片刷新验证码
    function kaptchaFlash() {
        var imgElement = $("#img")[0];
        imgElement.src = imgElement.src + "?" + new Date().getTime()
    }
</script>
</head>
<body>
<h1>登录</h1>
<form method="POST" action="/dologin">
    用户名:<input id="userName" type="text" name="user"/><br><br>
    密 码:<input id="password" type="text" name="pwd"/><br><br>
    验证码<input id="kaptcha" name="kaptcha" type="text">
    <img src="kaptcha.do" id="img" onclick="kaptchaFlash()"><br><br>
    <input id="login_button" type="button" value="登录"> <input type="checkbox" id="rememberMe" name="remember-me">记住我
    &nbsp&nbsp&nbsp <span id="info"></span>
</form>
<!--从request中获取登录失败信息-->
<p>${SPRING_SECURITY_LAST_EXCEPTION}</p>
<!--从session中获取登录失败信息-->
<p>${sessionScope.SPRING_SECURITY_LAST_EXCEPTION}</p>
</body>
</html>