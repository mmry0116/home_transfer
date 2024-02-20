<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<base href="<%=basePath%>">
<script type="text/javascript" src="../../../jquery/jquery-3.3.1-min.js"></script>

<link rel="stylesheet" type="text/css" href="../../../bootstrap_3.3.0/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="../../../File-Upload/css/jquery.fileupload.css">

<script type="text/javascript" src="../../../File-Upload/js/vendor/jquery.ui.widget.js"></script>
<%--<script type="text/javascript" src="bootstrap_3.3.0/js/bootstrap.min.js"></script>--%>
<script type="text/javascript" src="../../../File-Upload/js/jquery.fileupload.js"></script>
<script type="text/javascript" src="../../../File-Upload/js/jquery.iframe-transport.js"></script>
<style type="text/css">
    .bar {
        height: 18px;
        background: green;
    }
</style>
<script type="text/javascript">
    $(function () {
        $('#fileupload').fileupload({
            dataType: 'json',
            url: "upload.do",
            sequentialUploads: true,

            done: function (e, data) {
                /*$.each(data.result.files, function (index, file) {
                    $('<p></p>').text(file.name).appendTo(document.body);
                });*/
                $('<p></p>').text(data.result.content).appendTo(document.body);
            },
            //每添加一个文件就会触发该方法
            add: function (e, data) {
                //每上传一个文件就会触发依次data.submit
                data.submit();
            },
            progressall: function (e, data) {
                var progress = parseInt(data.loaded / data.total * 100, 10);
                $('#progress .bar').css(
                    'width',
                    progress + '%'
                );
            },

        });
    });
</script>
<head>

    <title>Title</title>
</head>
<body>
<br><br><br><br>
<input id="fileupload" type="file" name="files" <%--data-url="upload.do"--%> multiple>
<div id="progress" style="width: 100px">
    <div class="bar" style="width: 0%;"></div>
</div>
<p>hahah</p>
</body>
</html>


