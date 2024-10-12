package org.example.sdk.config;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.example.sdk.domain.DynamicThreadPoolService;
import org.example.sdk.domain.IDynamicThreadPoolService;
import org.example.sdk.domain.model.entity.ThreadPoolConfigEntity;
import org.example.sdk.domain.model.vo.RegistryVO;
import org.example.sdk.registry.IRegistry;
import org.example.sdk.registry.redis.RedisRegistry;
import org.example.sdk.trigger.job.ThreadPoolDataUploadJob;
import org.example.sdk.trigger.listen.ThreadPoolConfigUpdateListener;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableConfigurationProperties(DynamicThreadPoolAutoProperties.class)
@EnableScheduling
public class DynamicThreadPoolAutoConfig {

    private final Logger logger = LoggerFactory.getLogger(DynamicThreadPoolAutoConfig.class);

    private String applicationName;

    @Bean("dynamicThreadRedissonClient")
    public RedissonClient redissonClient(DynamicThreadPoolAutoProperties properties) {
        Config config = new Config();
        // 根据需要可以设定编解码器；https://github.com/redisson/redisson/wiki/4.-%E6%95%B0%E6%8D%AE%E5%BA%8F%E5%88%97%E5%8C%96
        config.setCodec(JsonJacksonCodec.INSTANCE);

        config.useSingleServer()
                .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
                .setPassword(properties.getPassword())
                .setConnectionPoolSize(properties.getPoolSize())
                .setConnectionMinimumIdleSize(properties.getMinIdleSize())
                .setIdleConnectionTimeout(properties.getIdleTimeout())
                .setConnectTimeout(properties.getConnectTimeout())
                .setRetryAttempts(properties.getRetryAttempts())
                .setRetryInterval(properties.getRetryInterval())
                .setPingConnectionInterval(properties.getPingInterval())
                .setKeepAlive(properties.isKeepAlive())
        ;

        RedissonClient redissonClient = Redisson.create(config);

        logger.info("dynamic thread pool - redis registry initialize, {} {} {}", properties.getHost(), properties.getPoolSize(), !redissonClient.isShutdown());

        return redissonClient;
    }

    @Bean
    public IRegistry redisRegistry(RedissonClient redissonClient){
        return new RedisRegistry(redissonClient);
    }

    @Bean("dynamicThreadPoolService")
    public DynamicThreadPoolService dynamicThreadPoolService(ApplicationContext applicationContext, Map<String, ThreadPoolExecutor> threadPoolExecutorMap, RedissonClient redissonClient){
        applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");
        if (StringUtils.isBlank(applicationName)) {
            applicationName = "name can not find";
            logger.warn("dynamic thread pool - start, SpringBoot loss spring.application.name configuration");
        }

        // get redis cache first
        Set<String> threadPoolKeys = threadPoolExecutorMap.keySet();
        for (String threadPoolKey : threadPoolKeys) {
            ThreadPoolConfigEntity threadPoolConfigEntity = redissonClient.<ThreadPoolConfigEntity>getBucket(RegistryVO.THREAD_POOL_CONFIG_PARAMETER_LIST_KEY.getKey() + "_" + applicationName + "_" + threadPoolKey).get();
            if (null == threadPoolConfigEntity) continue;
            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolKey);
            threadPoolExecutor.setCorePoolSize(threadPoolConfigEntity.getCorePoolSize());
            threadPoolExecutor.setMaximumPoolSize(threadPoolConfigEntity.getMaximumPoolSize());
        }

        logger.info("dynamic thread pool - start, info:{}", JSON.toJSONString(threadPoolExecutorMap));
        return new DynamicThreadPoolService(applicationName, threadPoolExecutorMap);
    }

    @Bean
    public ThreadPoolDataUploadJob threadPoolDataUploadJob(IDynamicThreadPoolService iDynamicThreadPoolService, IRegistry iRegistry){
        return new ThreadPoolDataUploadJob(iDynamicThreadPoolService,iRegistry);
    }

    @Bean
    public ThreadPoolConfigUpdateListener threadPoolConfigAdjustListener(IDynamicThreadPoolService iDynamicThreadPoolService, IRegistry iRegistry) {
        return new ThreadPoolConfigUpdateListener(iDynamicThreadPoolService, iRegistry);
    }

    @Bean
    public RTopic threadPoolConfigUpdateListener(RedissonClient redissonClient, ThreadPoolConfigUpdateListener threadPoolConfigUpdateListener){
        String cacheKey = RegistryVO.THREAD_POOL_CONFIG_PARAMETER_LIST_KEY.getKey() + "_" + applicationName;
        RTopic topic = redissonClient.getTopic(cacheKey);
        topic.addListener(ThreadPoolConfigEntity.class, threadPoolConfigUpdateListener);
        return topic;
    }
}
