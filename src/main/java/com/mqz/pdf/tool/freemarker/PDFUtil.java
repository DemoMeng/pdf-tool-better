package com.mqz.pdf.tool.freemarker;

import com.lowagie.text.pdf.BaseFont;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.Locale;
import java.util.Map;

/**
 * @author mqz
 * @description
 * @abount https://github.com/DemoMeng
 * @since 2021/1/15
 */
public class PDFUtil {

    private final static Logger logger = LoggerFactory.getLogger(PDFUtil.class);

    private static String[] ftlNames = {"dsc.ftl"};
    private static String[] fontNames = {"simsun.ttc"};
    static {
        cpFile("templates/",ftlNames);
        cpFile("fonts/",fontNames);
    }
    private static void cpFile(String filePath,String[] fileNames){
        String path = null;
        for (int i = 0; i < fileNames.length; i++) {
            path = createFtlFile(filePath, fileNames[i]);
            if (null == path) {
                logger.info("ftl not copy success:" + fileNames[i]);
            }
        }
    }

    private static String createFtlFile(String ftlPath, String ftlName) {
        try {
            //获取当前项目所在的绝对路径
            String proFilePath = System.getProperty("user.dir");
            logger.info("project run path：" + proFilePath);
            //获取模板下的路径　
            String newFilePath = proFilePath + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + ftlPath;
            newFilePath = newFilePath.replace("/", File.separator);
            logger.info("newFilePath:" + newFilePath);
            //检查项目运行时的src下的对应路径
            File newFile = new File(newFilePath + ftlName);
            if (newFile.isFile() && newFile.exists()) {
                return newFilePath;
            }
            //当项目打成jar包会运行下面的代码，并且复制一份到src路径下（具体结构看下面图片）
            InputStream certStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(ftlPath + ftlName);
            byte[] certData = IOUtils.toByteArray(certStream);
            FileUtils.writeByteArrayToFile(newFile, certData);
            return newFilePath;
        } catch (Exception e) {
            logger.error("复制ftl文件失败--> 异常信息：" + e);
        }
        return null;
    }

    public static ByteArrayOutputStream createPDF(HttpServletRequest request, String ftlName, Map root, String imageName) throws Exception {
        String basePath =request.getSession().getServletContext().getRealPath("/");//绝对路径
        String basePath2=basePath.replaceAll("\\\\", "/");
        Configuration cfg = new Configuration();
        try {
            cfg.setLocale(Locale.CHINA);
            cfg.setEncoding(Locale.CHINA, "UTF-8");
            //设置编码
            cfg.setDefaultEncoding("UTF-8");
            //设置模板路径
            //cfg.setDirectoryForTemplateLoading(new File(basePath + "/WEB-INF/views/ftl/"));

            cfg.setDirectoryForTemplateLoading(new File("/Applications/mqz/fxq-account-2/pdf-tool/src/main/resources/templates"));
            //注意这里打jar包之后，ftl文件打包访问不
            //可能需要采用复制文件的方式，把ftl文件复制出来
            //这是用的是绝对路径，在程序打成jar后可能会找不到resource文件
            //需要采用上面的方式，把文件复制到指定目录具体可以使用jar试试即可
//            String proFilePath = System.getProperty("user.dir");
//            String lastPath = proFilePath + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "templates";
//            String fontPath = proFilePath + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "fonts/simsun.ttc";
//            cfg.setDirectoryForTemplateLoading(new File(lastPath));
            //解决图片路径问题   设置好图片所选择的路径
            if(imageName!=null && !"".equals(imageName)){
                root.put("imagePath",imageName);
            }
            //获取模板
            Template template = cfg.getTemplate(ftlName);
            template.setEncoding("UTF-8");
            Writer writer = new StringWriter();
            //数据填充模板
            template.process(root, writer);
            String str = writer.toString();
            //pdf生成
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();


            ITextRenderer iTextRenderer = new ITextRenderer();
            iTextRenderer.setDocumentFromString(str);


            //设置字体  其他字体需要添加字体库  TODO
            ITextFontResolver fontResolver = iTextRenderer.getFontResolver();
            fontResolver.addFont("/Applications/mqz/fxq-account-2/pdf-tool/src/main/resources/fonts/simsun.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            fontResolver.addFont("/Applications/mqz/fxq-account-2/pdf-tool/src/main/resources/fonts/SIMLI.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            iTextRenderer.setDocument(builder.parse(new ByteArrayInputStream(str.getBytes())),null);
            iTextRenderer.layout();

            //生成PDF
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            iTextRenderer.createPDF(baos);
            baos.close();
            return baos;
        } catch(Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * 客户端下载pdf
     * @param response
     * @param bytes
     * @param filename
     */
    public static void renderPdf(HttpServletResponse response, final byte[] bytes, final String filename) {
        /*  initResponseHeader(response, PDF_TYPE);  */
        setFileDownloadHeader(response, filename, ".pdf");
        if (null != bytes) {
            try {
                response.getOutputStream().write(bytes);
                response.getOutputStream().flush();
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    /**
     * 分析并设置contentType与headers.
     */
        /*
        private HttpServletResponse initResponseHeader(HttpServletResponse response, final String contentType, final String... headers) {
            // 分析headers参数
            String encoding = DEFAULT_ENCODING;
            boolean noCache = DEFAULT_NOCACHE;
            for (String header : headers) {
                String headerName = StringUtils.substringBefore(header, ":");
                String headerValue = StringUtils.substringAfter(header, ":");
                if (StringUtils.equalsIgnoreCase(headerName, HEADER_ENCODING)) {
                    encoding = headerValue;
                } else if (StringUtils.equalsIgnoreCase(headerName, HEADER_NOCACHE)) {
                    noCache = Boolean.parseBoolean(headerValue);
                } else {
                    throw new IllegalArgumentException(headerName + "不是一个合法的header类型");
                }
            }
            // 设置headers参数
            String fullContentType = contentType + ";charset=" + encoding;
            response.setContentType(fullContentType);
            if (noCache) {
                // Http 1.0 header
                response.setDateHeader("Expires", 0);
                response.addHeader("Pragma", "no-cache");
                // Http 1.1 header
                response.setHeader("Cache-Control", "no-cache");
            }
            return response;
        }  */

    /**
     *  设置让浏览器弹出下载对话框的Header
     * @param response
     * @param fileName
     * @param fileType
     */
    public static void setFileDownloadHeader(HttpServletResponse response, String fileName, String fileType) {
        try {
            // 中文文件名支持
            String encodedfileName = new String(fileName.getBytes("GBK"), "ISO8859-1");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedfileName + fileType + "\"");
        } catch (UnsupportedEncodingException e) {
        }
    }

}
