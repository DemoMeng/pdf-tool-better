<html>
<head>
    <title>${title}</title>
    <style>
        table {
            width:100%;border:green dotted ;border-width:2 0 0 2
        }
        td {
            border:green dotted;border-width:0 2 2 0
        }
        @page {
            size: 8.5in 11in;
            @bottom-center {
                /*content: "page " counter(page) " of  " counter(pages);*/
                content: "页码：" counter(page) "/" counter(pages);
            }
        }
        <#--使用字体一定要定义好-->
        body {
            font-family: SimSun;
            font-size:14px;
            font-style:italic;
            font-weight:500;
        }
        .heiti
        {
            font-family: simsun-bold;
        }
    </style>
</head>
<body>
<#--
<h1>Just a blank page.</h1>
-->

<div style="page-break-before:always;">
    <div align="center">
        <h1>${title}</h1>
    </div>
    <div>
        <#-- freemarker的注释   imagePath是用来存储路径的 -->
        图片支持 <img src="${imagePath}add.png " />
    </div>
    <table>
        <tr>
            <td><b>Name</b></td>
            <td><b>Age</b></td>
            <td><b>Sex</b></td>
        </tr>
        <#list userList as user>
            <tr>
                <td>${user.name}</td>
                <td>${user.age}</td>
                <td>
                    <#if user.sex = 1>
                        male
                    <#else>
                        female
                    </#if>
                </td>
            </tr>
        </#list>
    </table>
</div>
</body>
</html>