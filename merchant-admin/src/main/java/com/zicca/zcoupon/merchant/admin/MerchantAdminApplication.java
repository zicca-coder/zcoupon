package com.zicca.zcoupon.merchant.admin;

import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.zicca.zcoupon.merchant.admin.dao.mapper")
@SpringBootApplication
public class MerchantAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(MerchantAdminApplication.class, args);
    }

}
