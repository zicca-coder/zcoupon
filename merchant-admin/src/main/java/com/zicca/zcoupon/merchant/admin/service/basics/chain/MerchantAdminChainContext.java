package com.zicca.zcoupon.merchant.admin.service.basics.chain;

import com.zicca.zcoupon.framework.exeception.ServiceException;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 商家后管模块责任链上下文容器
 *
 * @param <T>
 * @author zicca
 */
@Component
public class MerchantAdminChainContext<T> implements ApplicationContextAware, CommandLineRunner {

    private ApplicationContext applicationContext;

    private final Map<String, List<MerchantAdminAbstractChainHandler>> abstractChainHandlerContainer = new HashMap<>();

    /**
     * 责任链组件执行
     *
     * @param mark         责任链标识
     * @param requestParam 请求参数
     */
    public void handle(String mark, T requestParam) {
        // 根据 mark 标识从责任链容器中获取一组责任链实现 Bean 集合
        List<MerchantAdminAbstractChainHandler> abstractChainHandlers = abstractChainHandlerContainer.get(mark);
        if (CollectionUtils.isEmpty(abstractChainHandlers)) {
            throw new ServiceException("String.format(\"[%s] Chain of Reponsibility ID is undefined.\",  mark)");
        }
        // 遍历责任链集合，依次执行 handler 方法
        abstractChainHandlers.forEach(handler -> handler.handle(requestParam));
    }

    @Override
    public void run(String... args) throws Exception {
        Map<String, MerchantAdminAbstractChainHandler> chainFilterMap = applicationContext.getBeansOfType(MerchantAdminAbstractChainHandler.class);
        chainFilterMap.forEach((beanName, bean) -> {
            List<MerchantAdminAbstractChainHandler> abstractChainHandlers = abstractChainHandlerContainer.getOrDefault(bean.mark(), new ArrayList<>());
            abstractChainHandlers.add(bean);
            abstractChainHandlerContainer.put(bean.mark(), abstractChainHandlers);
        });
        abstractChainHandlerContainer.forEach((mark, unsortedChainHandlers) -> {
            // 排序
            unsortedChainHandlers.sort(Comparator.comparing(Ordered::getOrder));
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


}
