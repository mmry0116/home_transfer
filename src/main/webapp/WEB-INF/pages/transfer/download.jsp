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
    #display_room img {
        width: 20px;
        height: 20px;
    }

    #display_room {
        font-size: 14px;
        font-family: 微软雅黑;
    }


    #current_room img {
        width: 18px;
        height: 18px;
    }

    /*  #current_room  {
          margin-top: 5px;
      }*/

    /* #select_room{
         margin-top: 6px;
         margin-bottom: 6px;
     }*/
    #display_room img {
        /*margin-left: 75px;*/
        margin-top: 5px;
    }
</style>
<head>
    <script type="text/javascript">
        $(function () {

            $("#fetch_blob").click(function () {
                fetchTest();
            })
            $("#StreamSaver").click(function () {
                StreamSaver();
            })
            $("#StreamSaver_packZip").click(function () {
                StreamSaver_packZip();
            })
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
            function StreamSaver(filePath) {
                let fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length)
                if (fileName.indexOf(".") === -1) fileName = fileName + ".zip"
                let url = "download2.do" + "?filePath=" + filePath
                // 【步骤1】创建一个文件，该文件支持写入操作
                const fileStream = streamSaver.createWriteStream(fileName) // 这里传入的是下载后的文件名，这个名字可以自定义

                // 【步骤2】使用 fetch 方法访问文件的url，将内容一点点的放到 StreamSaver 创建的文件里
                fetch(url)
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
            function StreamSaver_packZip(multiPath) {
               // console.info(multiPath)
                console.info(multiPath.split("-"))
                let urls = [];
                let p;
                $(multiPath.split("-")).each(function (index, path) {
                    p = {}
                    p.fileName = path
                    p.url = "download2.do?filePath=" + currentPath + path
                    urls.push(p)
                })
                // let urls = JSON.stringify(arr)
                /* let urls = [
                     {
                         fileName: 'test.txt',
                         url: 'download2.do',
                     },
                     {
                         fileName: 'test.csv',
                         url: 'download.do',
                     }
                 ]*/
                // 【步骤1】
                const fileStream = streamSaver.createWriteStream('multiFiles.zip')

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

            //选择盘符 拼接
            let drives = JSON.parse('${drives}');
            if (!drives){

                $("#drive_room").remove()
            }
            for (let i in drives) {
                let dri = drives[i].replace(":/", "");
                $("#drive_room").append('&nbsp ' + dri + '盘<input type="radio" name="driveName" value="' + drives[i] + '">')
            }
            //页面开始加载时 自动选中盘符
            let countDriver = $("#drive_room input").length;
            if (countDriver > 1) {
                $("#drive_room input")[1].checked = true
            }
            if (countDriver === 1) {
                $("#drive_room input")[0].checked = true
            }

            $("#drive_room").on("change", "input[name=driveName]", function () {
                updatePath(this.value)
            })

            let currentPath = "";
            let currentMap;
            listPath(JSON.parse('${pathMap}'));


            function listPath(pathMap) {
                //删除所有<a> <img> 标签
                $("#display_room").empty();
                //删除 select_room 下的checkbox
                if($("#display_room")){
                    $("#current_room input[type=checkbox]")[0].checked=false;
                }
                for (let path in pathMap) {
                    let type = pathMap[path];
                    let img;
                    //图片拼接
                    if (type === "file") {
                        img = $("<img src='picture/file.png'>").appendTo("#display_room")
                    } else if (type === "directory") {
                        img = $("<img src='picture/directory.png'>").appendTo("#display_room")
                    } else if (type === "hide_file") {
                        img = $("<img src='picture/hide_file.png'>").appendTo("#display_room")
                    } else {
                        img = $("<img src='picture/hide_directory.png'>").appendTo("#display_room")
                    }
                    let pathShort = path.substring(path.lastIndexOf("/") + 1, path.length)

                    // img.before(" <input type='checkbox' value='" + pathShort + "' id='selectFile'>")

                    currentPath = path.substring(0, path.lastIndexOf("/") + 1)
                    //展示区域 拼接的文件拼接文件夹
                    var a = $("<a href='javascript:void(0);' ></a>").appendTo("#display_room");
                    a.after("<br>")
                    a.before("&nbsp;");
                    a.text(pathShort);
                    //存储字段:文件的类型 是文件类型还是文件夹类型
                    a.attr("type", type);
                }

                //当前路径 拼接<a>
                /*这个map存放着一段一段路径
                像这样存放
                G:/ G:/
                AliWor/ G:/AliWor/
                kbenchD/ G:/AliWor/kbenchD/   */
                currentMap = new Map();
                currentMap.set(0, currentPath)
                //拼接前面删除当前路径下所有的<a>标签
                $("#current_room a").remove()
                let index = 0;
                for (let i = 0; !(currentPath.indexOf("/", index) === -1); i++) {
                    if (index >= currentPath.length) {
                        break;
                    }
                    index = currentPath.indexOf("/", index) + 1
                    let realPath = currentPath.substring(0, index - 1);
                    let position = realPath.lastIndexOf("/");
                    let shortPath = realPath.substring(position + 1) + "/"
                    let aFlag = $("<a href='javascript:void(0);' >" + shortPath + "</a>").appendTo("#current_room");
                    //  aFlag.after("&nbsp;")
                    //  console.info(shortPath + " " + realPath + "/")
                    currentMap.set(shortPath, realPath + "/")
                }
            }

            //当前路径 所有<a>绑定单击事件
            $("#current_room").on("click", "a", function () {
                let clickPath = $(this).text()
                updatePath(currentMap.get(clickPath))
            })

            //展示列表 所有<a>标签添加单击事件
            $("#display_room").on("click", "a", function () {
                let text = $(this).text();
                let filePath = currentPath + text;
                if ($(this).attr("type").includes("directory")) {
                    updatePath(filePath)
                } else {
                    if (confirm("是否下载")) {
                        StreamSaver(filePath);
                    }
                }

            })

            //向后端发送请求 更新#display_room 区域
            function updatePath(path) {
                let url = "transfer/updatePath.do"
                $.ajax({
                    url: url,
                    type: "get",
                    data: {
                        path: path
                    },
                    success: function (data) {
                        let retData = JSON.parse(data);
                        listPath(retData);
                    }
                })
            }

            //选择文件按钮 控制所有复选框
            $("#selectButton").click(function () {
                let checkBoxAll = $("#display_room input[type=checkbox]");
                if (checkBoxAll.length > 0) {
                    console.info("checkBoxAll: " + checkBoxAll.length)
                    let boolean = this.checked;
                    $("#display_room :checkbox").each(function (index, iDom) {
                        iDom.checked = boolean
                    })
                } else {
                    //判断第一次点全选按钮则将复选框创建拼接进去
                    $("#display_room img").each(function (index, iDom) {
                        // console.info($(iDom).next().text())
                        let pathShort = $(iDom).next().text()
                        $(iDom).before(" <input type='checkbox' value='" + pathShort + "' id='selectFile'>")
                    })
                    //第一次取消全选
                    this.checked = false;

                }
            })
            //复选框监听
            $("#display_room").on("click", "input[type=checkbox]", function () {
                //全选款赋值
                $("#selectButton")[0].checked =
                    $("#display_room input:checked").length === $("#display_room input[type=checkbox]").length
            })

            //下载图片监听
            $("#download").click(function () {
                let contain = "";
                let shortPath="";
                $("#display_room :checkbox:checked").each(function (index, cDom) {
                    let type = $(cDom).next().next().attr("type");
                    var iDom = $(cDom).next().next();
                    if (type === "directory" || type === "hide_directory") {
                        console.info(iDom.text())
                        shortPath = shortPath + iDom.text() + "-"
                        contain += "d"
                    } else {
                        shortPath = shortPath + iDom.text() + "-"
                        contain += "f"
                    }
                })
                shortPath = shortPath.substring(0, shortPath.length - 1)
                if (!contain) {
                    alert("请选着文件或文件夹")
                } else if (contain.includes("d") && contain.includes("f")) {
                    alert("不能同时选着文件和文件夹类型")
                } else if (contain.length === 1) {
                    //运行到这里说明是下载单个文件或文件夹
                    StreamSaver(currentPath + shortPath.replace("-", ""))
                } else if (contain.includes("d")) {
                    //运行到这里说明是下载多个文件夹
                    alert("只可以选着一个文件夹下载")
                } else {
                    //运行到这里说明是下载多个文件
                    StreamSaver_packZip(shortPath)
                }
            })
        })
    </script>
    <title>Title</title>
</head>
<body>
<button style="display: none" id="download_button">test</button>
<button id="fetch_blob" style="display: none">fetch_blob</button>
<br>
<div id="drive_room">
    选择盘符:
</div>
<div id="current_room">
    <input type="checkbox" id="selectButton">
    <img src='picture/download.png' id="download"> &nbsp;
</div>

<div id="display_room">

</div>
<div></div>
</body>
</html>


