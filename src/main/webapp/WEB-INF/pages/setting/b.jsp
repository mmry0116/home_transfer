<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<base href="<%=basePath%>">
<head>
    <script type="text/javascript" src="jquery/jquery.fileupload.js" ></script>
    <%
        System.out.println("hihi");
        out.write("vccccc");
    %>
    <title>Title</title>
</head>
<body>
<h2> ssss ${sessionScope.aaa}</h2>
<h2> qqq <%=session.getAttribute("aaa")%>
</h2>
<a href="hello.do">hello</a>
</body>
</html>
