package org.example.sdk.registry.redis;

import org.example.sdk.domain.model.entity.ThreadPoolConfigEntity;
import org.example.sdk.domain.model.vo.RegistryVO;
import org.example.sdk.registry.IRegistry;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import java.util.List;

public class RedisRegistry implements IRegistry {

    private final RedissonClient redissonClient;

    public RedisRegistry(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }


    @Override
    public void uploadThreadPool(List<ThreadPoolConfigEntity> threadPoolConfigEntityList) {
        RList<ThreadPoolConfigEntity> threadPoolConfigEntityRList = redissonClient.getList(RegistryVO.THREAD_POOL_CONFIG_PARAMETER_LIST_KEY.getKey());
        threadPoolConfigEntityRList.addAll(threadPoolConfigEntityList); // todo try to reduce repeating add
    }

    @Override
    public void uploadThreadPoolConfigParameter(ThreadPoolConfigEntity threadPoolConfigEntity) {

    }
}
