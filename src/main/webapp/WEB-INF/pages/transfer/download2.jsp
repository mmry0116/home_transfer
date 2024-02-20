<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<base href="<%=basePath%>">
<script type="text/javascript" src="jquery/jquery-3.3.1-min.js"></script>

<style type="text/css">

</style>
<head>
    <title>Title</title>
</head>
<script type="text/javascript">
    $(function () {
        $("#download_button").click(function () {
            downloadFile("download.do")
        })
        $("#download_button2").click(function () {
            downloadFile("download2.do")
        })

        //有下载百分比 但是下载了两次 第二次下载才会提示保存 不合适大文件下载,也比不上<a>标签下载
        function downloadFile(path, name) {
            const xhr = new XMLHttpRequest();
            xhr.open('get', path);
            xhr.responseType = 'blob';
            xhr.send();
            xhr.onprogress = function (evt) {
                if (evt.lengthComputable) {
                    var percentComplete = evt.loaded / evt.total;
                    $("#progressing2").html((percentComplete * 100) + "%");
                }
            }
            xhr.onload = function () {
                if (this.status === 200 || this.status === 304) {
                    const content = xhr.getResponseHeader('content-disposition'); // 注意是全小写，自定义的header也是全小写
                    let name;
                    if (content) {
                        console.info("content: " + content)
                        name = content.match(/filename=(.*)/)[1]; // 获取filename的值
                        // let name2 = content.match(/filename\*=(.*)/)[1]; // 获取filename*的值
                        name = decodeURIComponent(name);
                        //   name2 = decodeURIComponent(name2.substring(6)); // 这个下标6就是UTF-8''
                        console.info("name1: " + name + "name2: ")
                    }

                    const fileReader = new FileReader();
                    fileReader.readAsDataURL(this.response);
                    fileReader.onprogress = function (evt) {
                        if (evt.lengthComputable) {
                            var percentComplete = evt.loaded / evt.total;
                            $("#progressing").html((percentComplete * 100) + "%");
                        }
                    }
                    fileReader.onload = function () {
                        var blob = xhr.response;
                        const url = window.URL.createObjectURL(blob);
                        const urlor = this.result
                        let a = $("<a href='" + url + "' download='" + name + "'>" + name + "</a>").appendTo("body")
                        /*let a = $("<a>aaa</a>").appendTo("body")
                        a.css("display", "table-row")
                        a.text(name)
                        a.attr("download", name)
                        a.attr("href", urlor)*/
                        a[0].click();
                        window.URL.revokeObjectURL(url);//释放url
                        // $(a[0]).detach();//删除<a>标签
                    };
                }
            };
        }


    })
</script>
<body>
<br><br><br><br>
<a href="download.do" download="filename.zip">Download</a>
<button id="download_button">test</button>
<button id="download_button2">test2</button>
<p id="progressing"></p>
<p id="progressing2"></p>
</body>
</html>


