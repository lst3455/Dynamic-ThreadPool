package org.example.sdk.trigger.job;

import com.alibaba.fastjson2.JSON;
import org.example.sdk.config.DynamicThreadPoolAutoConfig;
import org.example.sdk.domain.IDynamicThreadPoolService;
import org.example.sdk.domain.model.entity.ThreadPoolConfigEntity;
import org.example.sdk.registry.IRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.BreakIterator;
import java.util.List;

public class ThreadPoolDataUploadJob {

    private final Logger logger = LoggerFactory.getLogger(ThreadPoolDataUploadJob.class);

    private final IDynamicThreadPoolService iDynamicThreadPoolService;

    private final IRegistry iRegistry;

    public ThreadPoolDataUploadJob(IDynamicThreadPoolService iDynamicThreadPoolService, IRegistry iRegistry) {
        this.iDynamicThreadPoolService = iDynamicThreadPoolService;
        this.iRegistry = iRegistry;
    }


    @Scheduled(cron = "0/20 * * * * ?")
    public void doUploadThreadPoolList(){
        List<ThreadPoolConfigEntity> threadPoolConfigEntityList = iDynamicThreadPoolService.queryThreadPoolList();
        logger.info("dynamic thread pool - upload threadpool list start");
        iRegistry.uploadThreadPool(threadPoolConfigEntityList);
        logger.info("dynamic thread pool - upload threadpool list complete");

        for (ThreadPoolConfigEntity threadPoolConfigEntity : threadPoolConfigEntityList){
            iRegistry.uploadThreadPoolConfigParameter(threadPoolConfigEntity);
            logger.info("dynamic thread pool - upload threadpool data, data:{}", JSON.toJSONString(threadPoolConfigEntity));
        }
    }

}
