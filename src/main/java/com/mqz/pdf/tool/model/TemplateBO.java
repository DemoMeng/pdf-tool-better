package com.mqz.pdf.tool.model;

import lombok.Data;

import java.util.List;

/**
 * @author mqz
 * @since 2021年01月14日
 * @description
 * @abount https://github.com/DemoMeng
 */
@Data
public class TemplateBO {

    private String templateName;

    private String freeMarkerUrl;

    private String ITEXTUrl;

    private String JFreeChartUrl;

    private List<String> scores;

    private String imageUrl;

    private String picUrl;

    private String scatterUrl;

    private String userName;
    private String password;
    private String about;


}
