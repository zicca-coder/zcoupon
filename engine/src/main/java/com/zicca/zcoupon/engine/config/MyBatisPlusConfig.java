package com.zicca.zcoupon.engine.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

/**
 * MyBatisPlus 配置
 *
 * @author zicca
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * 配置 MyBatisPlus 插件
     */
//    @Bean
//    public MybatisPlusInterceptor mybatisPlusInterceptor() {
//        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
//        // todo: 分页插件原理
//        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
//        return interceptor;
//    }

    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MyMetaObjectHandler();
    }


    /**
     * MyBatisPlus 元数据自动填充类
     */
    static class MyMetaObjectHandler implements MetaObjectHandler {

        @Override
        public void insertFill(MetaObject metaObject) {
            // fieldName: Java实体类中的属性名
            strictInsertFill(metaObject, "createTime", Date::new, Date.class);
            strictInsertFill(metaObject, "updateTime", Date::new, Date.class);
            // todo: 此处应该填充当前登录用户信息
            strictInsertFill(metaObject, "createBy", () -> "admin", String.class);
            strictInsertFill(metaObject, "updateBy", () -> "admin", String.class);
            strictInsertFill(metaObject, "delFlag", () -> 0, Integer.class);
        }

        @Override
        public void updateFill(MetaObject metaObject) {
            strictUpdateFill(metaObject, "updateTime", Date::new, Date.class);
            // todo: 此处应该填充当前登录用户信息
            strictUpdateFill(metaObject, "updateBy", () -> "admin", String.class);
        }
    }


}
