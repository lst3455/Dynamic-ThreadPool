package org.example.sdk.registry;

import org.example.sdk.domain.model.entity.ThreadPoolConfigEntity;

import java.util.List;

public interface IRegistry {

    void uploadThreadPool(List<ThreadPoolConfigEntity> threadPoolConfigEntityList);

    void uploadThreadPoolConfigParameter(ThreadPoolConfigEntity threadPoolConfigEntity);
}
