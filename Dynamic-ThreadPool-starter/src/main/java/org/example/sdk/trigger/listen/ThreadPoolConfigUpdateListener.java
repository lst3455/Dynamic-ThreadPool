package org.example.sdk.trigger.listen;

import com.alibaba.fastjson2.JSON;
import org.example.sdk.domain.IDynamicThreadPoolService;
import org.example.sdk.domain.model.entity.ThreadPoolConfigEntity;
import org.example.sdk.registry.IRegistry;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ThreadPoolConfigUpdateListener implements MessageListener<ThreadPoolConfigEntity> {

    private final Logger logger = LoggerFactory.getLogger(ThreadPoolConfigUpdateListener.class);

    private final IDynamicThreadPoolService iDynamicThreadPoolService;

    private final IRegistry iRegistry;

    public ThreadPoolConfigUpdateListener(IDynamicThreadPoolService iDynamicThreadPoolService, IRegistry iRegistry) {
        this.iDynamicThreadPoolService = iDynamicThreadPoolService;
        this.iRegistry = iRegistry;
    }

    @Override
    public void onMessage(CharSequence charSequence, ThreadPoolConfigEntity threadPoolConfigEntity) {
        logger.info("dynamic thread pool - listen to threadPoolConfig update, threadPoolName:{}, threadNumber:{}, maxThreadNumber:{}",threadPoolConfigEntity.getThreadPoolName(),threadPoolConfigEntity.getPoolSize(),threadPoolConfigEntity.getCorePoolSize());
        iDynamicThreadPoolService.updateThreadPoolConfig(threadPoolConfigEntity);

        List<ThreadPoolConfigEntity> threadPoolConfigEntityList = iDynamicThreadPoolService.queryThreadPoolList();
        iRegistry.uploadThreadPool(threadPoolConfigEntityList);

        ThreadPoolConfigEntity threadPoolConfigEntityCurrent = iDynamicThreadPoolService.queryThreadPoolConfigByName(threadPoolConfigEntity.getThreadPoolName());
        iRegistry.uploadThreadPoolConfigParameter(threadPoolConfigEntityCurrent);
        logger.info("dynamic thread pool - update threadPoolConfig, data:{}", JSON.toJSONString(threadPoolConfigEntityCurrent));
    }
}
