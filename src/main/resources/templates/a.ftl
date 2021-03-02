<html>
<head>
    <title>${title}</title>
    <style>
        table {
            width:100%;
        }
        td{
            height:55px;
        }
        .first-td{
            width:15%;
            text-align:center
        }
        .second-td{
            padding-left:20px;
        }
        .first-tr{
            background-color:#f9f9f9;
        }
        .second-tr{
            background-color:#dde4e8;
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
            font-style: normal;
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

<div>
    <div align="center"><h1>${title}</h1></div>
    <table>
        <thead>签署人信息</thead>
        <tr class="first-tr">
            <td class="first-td"><b>注册时间:</b></td>
            <td class="second-td">${registerTime}</td>
        </tr>
        <tr class="second-tr">
            <td class="first-td"><b>注册手机号:</b></td>
            <td class="second-td">${registerPhone}</td>
        </tr>
        <tr class="first-tr">
            <td class="first-td"><b>登录时间:</b></td>
            <td class="second-td">${loginTime}</td>
        </tr>
        <tr class="second-tr">
            <td class="first-td"><b>登录手机号:</b></td>
            <td class="second-td">${loginPhone}</td>
        </tr>
        <tr class="first-tr">
            <td class="first-td"><b>实名认证方式:</b></td>
            <td class="second-td">${authType}</td>
        </tr>
        <tr class="second-tr">
            <td class="first-td"><b>姓名:</b></td>
            <td class="second-td">${realName}</td>
        </tr>
    </table>
    <br/>
    <table>
        <tr class="first-tr">
            <td class="first-td"><b>账号标识:</b></td>
            <td class="second-td">${accountMark}</td>
        </tr>
        <tr class="second-tr">
            <td class="first-td"><b>用户类型:</b></td>
            <td class="second-td">${userType}</td>
        </tr>
        <tr class="first-tr">
            <td class="first-td"><b>合同文件:</b></td>
            <td class="second-td">${contractUrl}</td>
        </tr>
    </table>


</div>

</body>
</html>