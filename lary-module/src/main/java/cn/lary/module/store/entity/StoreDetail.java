package cn.lary.module.store.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author paul
 * @since 2024-10-28
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("store_detail")
public class StoreDetail implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long storeId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 库存预警数量
     */
    private Integer stockWarning;

    /**
     * 结算周期
     */
    private String settlementCycle;

    /**
     * 公司地址
     */
    private String companyAddress;

    /**
     * 公司地址地区ID
     */
    private String companyAddressIdPath;

    /**
     * 公司地址地区
     */
    private String companyAddressPath;

    /**
     * 电子邮箱
     */
    private String companyEmail;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 公司电话
     */
    private String companyPhone;

    /**
     * 同城配送达达店铺编码
     */
    private String ddCode;

    /**
     * 员工总数
     */
    private Integer employeeNum;

    /**
     * 店铺经营类目
     */
    private String goodsManagementCategory;

    /**
     * 法人身份证
     */
    private String legalId;

    /**
     * 法人姓名
     */
    private String legalName;

    /**
     * 法人身份证照片
     */
    private String legalPhoto;

    /**
     * 营业执照电子版
     */
    private String licencePhoto;

    /**
     * 营业执照号
     */
    private String licenseNum;

    /**
     * 联系人姓名
     */
    private String linkName;

    /**
     * 联系人电话
     */
    private String linkPhone;

    /**
     * 注册资金
     */
    private BigDecimal registeredCapital;

    /**
     * 法定经营范围
     */
    private String scope;

    /**
     * 结算银行开户行名称
     */
    private String settlementBankAccountName;

    /**
     * 结算银行开户账号
     */
    private String settlementBankAccountNum;

    /**
     * 结算银行开户支行名称
     */
    private String settlementBankBranchName;

    /**
     * 结算银行支行联行号
     */
    private String settlementBankJointName;

    /**
     * 退货地址Id
     */
    private String salesConsigneeAddressId;

    /**
     * 退货地址名称
     */
    private String salesConsigneeAddressPath;

    /**
     * 退货详细地址
     */
    private String salesConsigneeDetail;

    /**
     * 退货收件人手机
     */
    private String salesConsigneeMobile;

    /**
     * 退货收货人姓名
     */
    private String salesConsigneeName;

    /**
     * 店铺上次结算日
     */
    private LocalDateTime settlementDay;

    /**
     * 发货地址名称
     */
    private String consignorAddressPath;

    /**
     * 发货详细地址
     */
    private String consignorDetail;

    /**
     * 发货人手机
     */
    private String consignorMobile;

    /**
     * 发货人姓名
     */
    private String consignorName;

    private String appSecretKey;

    private String merchantNumber;

    private String appMerchantKey;

    

 

      
}
