package com.zicca.zcoupon.merchant.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户实体类
 *
 * @author zicca
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("t_user")
public class User extends Base {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户名
     */
    @TableField(value = "username")
    private String username;

    /**
     * 密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 手机号
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 邮箱
     */
    @TableField(value = "mail")
    private String mail;

    /**
     * 店铺编号
     */
    // todo: 此处用户指的是商家，商家有店铺编号，店铺编号是唯一的。其他用户无店铺编号，如何设计？？？
    @TableField(value = "shop_number")
    private String shopNumber;

}
