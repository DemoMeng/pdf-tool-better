package com.mqz.pdf.tool.web;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.security.CertificateInfo;
import com.itextpdf.text.pdf.security.PdfPKCS7;
import com.mqz.pdf.tool.freemarker.PDFUtil;
import com.mqz.pdf.tool.model.DigitalSignatureVO;
import com.mqz.pdf.tool.model.UserTestBean;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.security.PdfCertificate;
import com.spire.pdf.security.PdfSignature;
import com.spire.pdf.security.X509NameType;
import com.spire.pdf.widget.PdfFormFieldWidgetCollection;
import com.spire.pdf.widget.PdfFormWidget;
import com.spire.pdf.widget.PdfSignatureFieldWidget;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * @author mqz
 * @description
 * @abount https://github.com/DemoMeng
 * @since 2021/1/14
 */
@RestController
@RequestMapping("/web")
@Slf4j
public class WebController {


    @SneakyThrows
    public static void main(String[] args) throws IOException {
        String pdfUrl = "/Applications/mqz/nzl/fxq-api/files/authorization.pdf";

        //创建PdfDocument实例
        PdfDocument pdf = new PdfDocument();
        //加载含有签名的PDF文件
        pdf.loadFromFile(pdfUrl);
        //获取域集合
        PdfFormWidget pdfFormWidget = (PdfFormWidget) pdf.getForm();
        PdfFormFieldWidgetCollection pdfFormFieldWidgetCollection = pdfFormWidget.getFieldsWidget();
        //遍历域
        for (int i = 0; i < pdfFormFieldWidgetCollection.getCount(); i++) {
            //判定是否为签名域
            if (pdfFormFieldWidgetCollection.get(i) instanceof PdfSignatureFieldWidget) {
                //获取签名域
                PdfSignatureFieldWidget signatureFieldWidget = (PdfSignatureFieldWidget) pdfFormFieldWidgetCollection.get(i);
                //获取签名
                PdfSignature signature = signatureFieldWidget.getSignature();

                //判断签名是否有效
                boolean result = signature.verifySignature();
                if(result) {
                    System.out.println("有效签名");
                }else
                {
                    System.out.println("无效签名");
                }

                String location = signature.getLocationInfo();
                String reason = signature.getReason();
                String data = signature.getDate().toString();
                PdfCertificate pc = signature.getCertificate();
                System.out.println(pc.getIssuer());
                System.out.println(pc.get_IssuerName().getName());
                System.out.println(pc.getNameInfo(X509NameType.DnsFromAlternativeName,true));
                System.out.println(pc.getNameInfo(X509NameType.DnsName,true));
                System.out.println(pc.getNameInfo(X509NameType.EmailName,true));
                System.out.println(pc.getNameInfo(X509NameType.SimpleName,true));
                System.out.println(pc.getNameInfo(X509NameType.UpnName,true));
                System.out.println(pc.getNameInfo(X509NameType.UrlName,true));
                System.out.println(pc.getSubject());
                System.out.println(signature.getCertificate());
                System.out.println(signature.getDigitalSignerLable());
                String name = signature.getSignatureName();
                System.out.println("签名位置信息："+ location +"\n"+
                        "签名原因：" + reason +"\n"+
                        "签名日期："+ data +"\n"+
                        "签名人："+ name +"\n"+
                        "文档中的签名坐标：X = "+ signatureFieldWidget.getLocation().getX()+ "  Y = "+ signatureFieldWidget.getLocation().getY()
                );
            }
        }
//        // spire获取图像，可以通过规划选择自己要获取的图片
//       //加载PDF文件
//        int index = 0;
//        //遍历PDF每一页
//        for (int i= 0;i< pdf.getPages().getCount(); i ++){
//            //获取PDF页面
//            PdfPageBase page = pdf.getPages().get(i);
//            BufferedImage[] images=page.extractImages();
//            if(images !=null){
//                for (BufferedImage image : page.extractImages()) {
//
//                    Raster data = image.getData();
//                    System.out.println(data);
//                    System.out.println(image.getWidth());
//                    System.out.println(image.getHeight());
//                    System.out.println("识别成功="+i);
//
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();//io流
//                    ImageIO.write(image, "png", baos);//写入流中
//                    byte[] bytes = baos.toByteArray();//转换成字节
//                    BASE64Encoder encoder = new BASE64Encoder();
//                    String png_base64 = encoder.encodeBuffer(bytes).trim();//转换成base64串
//                    png_base64 = png_base64.replaceAll("\n", "").replaceAll("\r", "");//删除 \r\n
//                    //ImageIO.write(bufferedImage, "png", new File("D:/qrcode1.png"));
//                    System.out.println("值为："+"data:image/jpg;base64,"+png_base64);
//                }
//            }
//        }

        //添加BC库支持
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        PdfReader pdfReader = new PdfReader(pdfUrl);
        AcroFields acroFields = pdfReader.getAcroFields();
        List<String> names = acroFields.getSignatureNames();
        List<DigitalSignatureVO> signatureVOS = new ArrayList<>(3);
        //如果set集合大小
        Set<Boolean> set = new HashSet<>(2);
        for (String name : names) {
            DigitalSignatureVO digitalSignatureVO = new DigitalSignatureVO();
            PdfDictionary signatureDict = acroFields.getSignatureDictionary(name);
            //时间戳
            String timestrap = signatureDict.getAsString(PdfName.M).toString().replace("D:","").substring(0,12);
            PdfPKCS7 pdfPKCS7 = acroFields.verifySignature(name);
            System.out.println("====="+pdfPKCS7.getReason());
            System.out.println("====="+pdfPKCS7.getLocation());
            System.out.println("====="+pdfPKCS7.getSignName());
            System.out.println("====="+pdfPKCS7.getTimeStampDate().toString());
            System.out.println("====="+pdfPKCS7.getSignDate());
            System.out.println("====="+pdfPKCS7.getEncryptionAlgorithm());
            System.out.println("====="+pdfPKCS7.getHashAlgorithm());
            System.out.println("====="+pdfPKCS7.getDigestAlgorithm());
            System.out.println("====="+pdfPKCS7.getDigestAlgorithmOid());
            System.out.println("====="+pdfPKCS7.getSigningInfoVersion());
            System.out.println("====="+pdfPKCS7.getVersion());
            System.out.println("====="+pdfPKCS7.getOcsp());
            System.out.println("=====证书："+pdfPKCS7.getSigningCertificate().getSubjectDN().getName());
            System.out.println("=====证书："+pdfPKCS7.getSigningCertificate().getIssuerDN().getName());
            System.out.println("=====证书："+pdfPKCS7.getSigningCertificate().getSigAlgName());
            System.out.println("=====证书："+pdfPKCS7.getSigningCertificate().getType());
            //System.out.println("=====证书："+pdfPKCS7.getSigningCertificate()!=null?pdfPKCS7.getSigningCertificate().getExtendedKeyUsage().toString():pdfPKCS7.getSigningCertificate());
            System.out.println("=====证书："+pdfPKCS7.getSigningCertificate().getIssuerX500Principal().getName());

            System.out.println("====="+pdfPKCS7.getCRLs());
            System.out.println("====="+pdfPKCS7.getSignCertificateChain());
            System.out.println("====="+pdfPKCS7.getSigningCertificate());
            System.out.println("====="+pdfPKCS7.getFilterSubtype().toString());


            Map<String,ArrayList<String>> map = CertificateInfo.getSubjectFields(pdfPKCS7.getSigningCertificate()).getFields();
            for(String key:map.keySet()){
                System.out.println("----------:"+key+":"+map.get(key).toString());;
            }
            System.out.println("****************:"+pdfPKCS7.getSigningCertificate().getSubjectDN());
            X509Certificate x509Certificate = pdfPKCS7.getSigningCertificate();
            Principal principal = x509Certificate.getIssuerDN();
            //证书颁发机构
            String s = principal.toString().split("CN")[2].replace("=","");
            try {
                //时间错是否有效
                digitalSignatureVO.setTimeValidity(pdfPKCS7.verifyTimestampImprint());
                //文档是否修改
                boolean flag = pdfPKCS7.verify();
                digitalSignatureVO.setTimeValidity(flag);
                set.add(flag);
            } catch (GeneralSecurityException e) {
                log.error("验签异常:{}",e.getMessage());
                break;
            }
            //文档是否被修改
            //签署人姓名
            String signerName = CertificateInfo.getSubjectFields(pdfPKCS7.getSigningCertificate()).getField("CN");
            digitalSignatureVO.setDateTime(timestrap);
            digitalSignatureVO.setIssuingAuthority(s);
            digitalSignatureVO.setSignerName(signerName);
            signatureVOS.add(digitalSignatureVO);
        }
        if (names.size() == 0) {
            log.error("验签文档没有签名!");
        } else {
            JSONObject resultJson = new JSONObject();
            resultJson.put("list",signatureVOS);
            resultJson.put("num",signatureVOS.size());
            //表示文档被修改啦
            if (set.size() == 2) {
                log.error("文档被修改!");
                resultJson.put("validityResult",false);
            } else {
                resultJson.put("validityResult",true);
                log.info("验签成功");
            }
        }
        System.out.println(signatureVOS.toString());
    }



    @RequestMapping(value="/pdf/down",method={RequestMethod.GET,RequestMethod.POST})
    public static void PDFchange(HttpServletRequest request, HttpServletResponse response) throws Exception{
        //从数据库中查询需要的条件
        Map<String,Object> variables = new HashMap<String,Object>();
        List<UserTestBean> userList = new ArrayList<UserTestBean>();
        UserTestBean tom = new UserTestBean("Tom",19,1);
        UserTestBean amy = new UserTestBean("Amy",28,0);
        UserTestBean leo = new UserTestBean("Leo",23,1);
        userList.add(tom);
        userList.add(amy);
        userList.add(leo);

        variables.put("title", "报告书");
        variables.put("registerTime", "2020-11-12 12:02:12");
        variables.put("registerPhone", "13128550556");
        variables.put("loginTime", "2021-01-14 15:22:12");
        variables.put("loginPhone", "17101012212");
        variables.put("authType", "个人");
        variables.put("realName", "蒙大拿");
        variables.put("accountMark", "基础版");
        variables.put("userType", "个人");
        variables.put("contractUrl", " ****");
        variables.put("userList", userList);
        String ftlName="a.ftl";
        //然后组装好之后调用该方法  FTLIMAGEPATH是用的配置模板中的内容实现的
        try {
            ByteArrayOutputStream bos= PDFUtil.createPDF(request, ftlName, variables,"/Applications/mqz/fxq-account-2/pdf-tool/src/main/resources/backgroud/back.png");/**字节*/
            PDFUtil.renderPdf(response, bos.toByteArray(), "pwd-down");
        } catch (Exception e) {
            log.error("pdf导出出错。。。",e);
        }
    }
}
