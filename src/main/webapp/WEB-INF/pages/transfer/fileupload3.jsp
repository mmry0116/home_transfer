<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<base href="<%=basePath%>">
<script type="text/javascript" src="jquery/jquery-3.3.1-min.js"></script>

<link rel="stylesheet" type="text/css" href="bootstrap_3.3.0/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="File-Upload/css/jquery.fileupload.css">

<script type="text/javascript" src="File-Upload/js/vendor/jquery.ui.widget.js"></script>
<%--<script type="text/javascript" src="bootstrap_3.3.0/js/bootstrap.min.js"></script>--%>
<script type="text/javascript" src="File-Upload/js/jquery.fileupload.js"></script>
<script type="text/javascript" src="File-Upload/js/jquery.iframe-transport.js"></script>
<style type="text/css">
    body {
        margin: 0px;
    }

    .file {
        position: relative;
        /*上传进度颜色*/
        background: linear-gradient(to right, lightgreen 50%, transparent 50%);
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

    #div1 {
        width: 50%;
        border: 1px solid red;
    }

    #div1_button {
        margin-left: 25%;
        width: 25%;
        border: 1px solid blueviolet;
    }

    #button3 {
        margin-left: 15%;
    }

    #div2 {
        height: 50%;
        width: 50%;
        border: 1px solid black;
    }

    #div3 {
        height: 30%;
        width: 50%;
        border: 1px solid red;
    }
    #download_div{
        float: left;
        margin-left: 10%;

    }
</style>

<script type="text/javascript">
    $(function () {
        $("#button2").click(function () {
            $("#div2 button").click();
        })
        $("#button3").click(function () {
            /*删除div2下的所有子元素*/
            $("#div2>*").detach();
            $("#div3>*").detach();
        })
        $("#download_button").click(function () {
            window.location.href = "transfer/download.do";
        })
        $("#download_button2").click(function () {
            window.location.href = "transfer/download2.do";
        })
        $("#fileupload").fileupload({
            dataType: "json",
            url: "uploadb.do",
            sequentialUploads: true,
            //每添加一个文件 就会回调该方法
            add: function (e, data) {
                console.info("" + data.files[0].name)
                data.context = $('<button></button>').text(data.files[0].name + ' Upload')
                    .appendTo("#div2")
                    .click(function () {
                        $(this).next("br").detach()//删除多余的</br>标签
                        data.context = $('<p class="file"></p>')
                            .append($('<a target="_blank"></a>').text(data.files[0].name))
                            .replaceAll($(this));
                        data.submit();
                    });
                $("#div2 button:last").after("</br>")//添加换行</br> 给按钮标签
            },
            progress: function (e, data) {
                var progress = parseInt((data.loaded / data.total) * 100, 10);
                data.context.css("background-position-x", 100 - progress + "%");
            },
            done: function (e, data) {
                // $("#div2 p:last").text('Upload finished')
            }
        });
        //上传文件开始时间
        var startTime = 0
        //在上传文件时 回调此方法
        $('#fileupload').on('fileuploadprogress', function (e, data) {
            //参数1: 已经传输了的字节 参数2:总字节 参数3:比特率 单位: bit/s
            //console.log(data.loaded, data.total, data.bitrate);
            var otherTime = ((data.total - data.loaded) * 8 / data.bitrate).toFixed(2);
            $("#info1").text("已经上传字节: " + data.loaded / 8)
            $("#info2").text("上传文件名: " + data.files[0].name + ": " + (data.total / (1024 * 1024)).toFixed(2) + "M")
            $("#info3").text("上传速率: " + (data.bitrate / (8 * 1024 * 1024)).toFixed(2) + " MBps")
            $("#info4").text("剩余时间: " + otherTime)

            if (startTime == 0 || otherTime == 0.00) {
                if (otherTime == 0.00) {
                    if (startTime == 0) startTime = new Date().valueOf()
                    $("#info5").text("用时: " + dateDiff(startTime, new Date().valueOf()))
                    startTime = 0;
                    return;
                }
                startTime = new Date().valueOf();
                console.info(startTime)
            }
        });

        //将两个时间戳的差值转换成时间 **天**小时/**小时**分钟
        function dateDiff(startTime, endTime) {
            let diff = (endTime - startTime) / 1000
            if (diff < 60) {
                return diff.toFixed(2) + "秒";
            } else if (diff < 60 * 60) {
                let m = Math.floor((diff / 60));
                let s = (diff - m * 60).toFixed(2);
                return m + "分钟" + s + "秒";
            } else if (diff < 60 * 60 * 24) {
                let h = Math.floor((diff / 60 * 60));
                let m = ((diff - h * 60 * 60) / 60).toFixed(2);
                return h + "小时" + m + "分钟";
            } else if (diff < 60 * 60 * 24 * 365) {
                let d = Math.floor((diff / (60 * 60 * 24)));
                let h = ((diff - d * 60 * 60 * 24) / (60 * 60)).toFixed(2);
                return d + "天" + h + "小时";
            }
        }

    });
</script>
<title>Title</title>
</head>
<body>
<div id="div1">
    <br>
    <input id="fileupload" type="file" name="files" multiple>

</div>
<div id="download_div">
    <button id="download_button" >下载文件</button>
 <%--   <button id="download_button2" >download2</button>--%>
</div>
<div id="div1_button">
    <button id="button2">全部上传</button>
    <button id="button3">清除信息</button>
</div>
<div id="div2">
    div2<br>
</div>

<div id="div3">div3
    <p id="info1"></p>
    <p id="info2"></p>
    <p id="info3"></p>
    <p id="info4"></p>
    <p id="info5"></p>
</div>

</body>
</html>

