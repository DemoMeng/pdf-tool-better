package com.mqz.pdf.tool.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author mqz
 * @since 
 * @description
 * @abount https://github.com/DemoMeng
 */
@Data
public class DigitalSignatureVO implements Serializable  {

    /**
     * 签署人姓名
     */
    private String signerName;

    /**
     * 证书颁发机构
     */
    private String issuingAuthority;

    /**
     * 签署时间
     */
    private String dateTime;

    /**
     *  时间戳是否有效
     */
    private Boolean timeValidity;
}
