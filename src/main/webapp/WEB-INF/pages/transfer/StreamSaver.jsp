<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<base href="<%=basePath%>">
<script type="text/javascript" src="jquery/jquery-3.3.1-min.js"></script>
<script type="text/javascript" src="jquery/StreamSaver.js"></script>

<style type="text/css">

</style>
<script type="text/javascript">
    $("#download").click(function () {

    })
</script>
<head>
    <title>Title</title>
</head>
<body>
</br></br>
<button id="download">下载</button>
</body>
</html>
