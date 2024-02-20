<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<base href="<%=basePath%>">
<script type="text/javascript" src="jquery/jquery-3.3.1-min.js"></script>
<script type="text/javascript" src="jquery/StreamSaver.js"></script>
<script type="text/javascript" src="jquery/zip-stream.js"></script>

<style type="text/css">

</style>
<head>
    <title>Title</title>
</head>
<script type="text/javascript">
    $(function () {
        $("#download_button").click(function () {
            packZip();
        })
        $("#fetch_blob").click(function () {
            fetchTest();
        })
        $("#StreamSaver").click(function () {
            StreamSaver();
        })
        $("#StreamSaver_packZip").click(function () {
            StreamSaver_packZip();
        })

        function down(url) {
            fetch(url)
                .then(response => {
                    return response.blob();
                })
                .then(blob => {
                    const url = window.URL.createObjectURL(blob);
                    let a = $("<a href='" + url + "' download='abc.pdf'>abc.pdf</a>").appendTo("body")
                    a[0].click();
                    // $("body a:last").click()
                    console.info($("body a:last"))
                    window.URL.revokeObjectURL(url);//释放url
                });
        }

        //两次下载不好 第二次才会提示保存
        function fetchTest(url) {
            fetch(url)
                .then(response => {
                    const content = response.headers.get('content-disposition'); // 注意是全小写，自定义的header也是全小写
                    let filename;
                    if (content) {
                        console.info("content: " + content)
                        filename = content.match(/filename=(.*)/)[1]; // 获取filename的值
                        filename = decodeURIComponent(filename);
                        console.info("filename: " + filename)
                    }
                    response.blob().then(blob => {
                        const url = window.URL.createObjectURL(blob);
                        const urlor = this.result
                        let a = $("<a href='" + url + "' download='" + filename + "'>" + filename + "</a>").appendTo("body")
                        a[0].click();
                        a[0].href = "";
                        window.URL.revokeObjectURL(url);//释放url
                    });
                })
                .catch(error => {
                    console.error('下载文件时出错：', error);
                });
        }

        //速度相对fetch(url).then()慢一点
        function StreamSaver() {
            // 【步骤1】创建一个文件，该文件支持写入操作
            const fileStream = streamSaver.createWriteStream('test.txt') // 这里传入的是下载后的文件名，这个名字可以自定义

            // 【步骤2】使用 fetch 方法访问文件的url，将内容一点点的放到 StreamSaver 创建的文件里
            fetch('download2.do')
                .then(res => {
                    console.info(res)
                    console.info(res.headers)
                    const readableStream = res.body
                    if (window.WritableStream && readableStream.pipeTo) {
                        return readableStream.pipeTo(fileStream)
                            .then(() => console.log('完成写入'))
                    }

                    // 【步骤3】监听文件内容是否读取完整，读取完就执行“保存并关闭文件”的操作。
                    window.writer = fileStream.getWriter()
                    const reader = res.body.getReader()
                    const pump = () => reader.read()
                        .then(res => res.done
                            ? writer.close()
                            : writer.write(res.value).then(pump)
                        )
                    pump()
                })
        }

        //将文件打包成zip下载
         function StreamSaver_packZip () {
            let urls = [
                {
                    fileName: 'test.txt',
                    url: 'download2.do',
                },
                {
                    fileName: 'test.csv',
                    url: 'download.do',
                }
            ]
            // 【步骤1】
            const fileStream = streamSaver.createWriteStream('test.zip')

            // 【步骤2】
            const readableZipStream = new ZIP({
                async pull(ctrl) {
                    for (let i = 0; i < urls.length; i++) {
                        const res = await fetch(urls[i].url)
                        const stream = () => res.body
                        const name = urls[i].fileName
                        ctrl.enqueue({name, stream}) // 不断接收要下载的文件
                    }
                    // 【步骤3】
                    ctrl.close()
                }
            })

            if (window.WritableStream && readableZipStream.pipeTo) {
                return readableZipStream.pipeTo(fileStream).then(
                    () => console.log('下载完了')
                )
            }
        }

    })
</script>
<body>
<br><br><br><br>
<a href="download2.do" download="filename.zip">Download</a>
<button id="download_button">test</button></br></br>
<button id="fetch_blob">fetch_blob</button></br></br>
<button id="StreamSaver">StreamSaver</button></br></br>
<button id="StreamSaver_packZip">StreamSaver_packZip</button></br></br>
</body>
</html>


