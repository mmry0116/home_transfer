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
    .file {
        position: relative;
        background: linear-gradient(to right, lightblue 50%, transparent 50%);
        background-size: 200% 100%;
        background-position: right bottom;
        transition: all 1s ease;
    }

    .file.done {
        background: lightgreen;
    }

    .file a {
        display: block;
        position: relative;
        padding: 5px;
        color: black;
    }
</style>

<script type="text/javascript">
    $(function () {
        $("#fileupload").fileupload({
            dataType: "json",
            url: "uploadb.do",
            sequentialUploads: true,
            add: function (e, data) {
                console.info(data)
                data.context = $('<p class="file"></p>')
                    .append($('<a target="_blank"></a>').text(data.files[0].name))
                    // .appendTo(document.body);
                    .appendTo("#div1");
                //  console.info(data.context)
                console.info(data)
                data.submit();
            },
            progress: function (e, data) {
                //console.info(data.context)
                var progress = parseInt((data.loaded / data.total) * 100, 10);
                data.context.css("background-position-x", 100 - progress + "%");
            },
            /*done: function (e, data) {
                data.context
                    .addClass("done")
                    .find("a")
                // .prop("href", data.result.files[0].url);
            }*/
        });
    });
</script>
<head>
    <title>Title</title>
</head>
<body>
<br><br>
<input id="fileupload" type="file" name="files" <%--data-url="upload2.do"--%> multiple>
<div id="div1" style="width: 20%;border: 1px solid red;"><p>div1</p></div>
<div id="div2" style="width: 20%;border: 1px solid black;"><p>div2</p></div>

</body>
</html>
<script>


</script>