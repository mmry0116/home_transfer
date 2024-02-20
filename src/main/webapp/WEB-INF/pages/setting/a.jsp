<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<base href="<%=basePath%>">

<head>
    <meta charset="UTF-8">
    <title>hello</title>
    <script type="text/javascript" src="../../../jquery/jquery-1.11.1-min.js"></script>
</head>
<body>

<h2> hello..</h2>

<a href="hello.do">hello</a>
</body>
</html>