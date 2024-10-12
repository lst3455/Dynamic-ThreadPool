package org.example.sdk.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "dynamic.thread.pool.config", ignoreInvalidFields = true)
public class DynamicThreadPoolAutoProperties {
    // State: open = enabled, close = disabled
    private boolean enable;
    // Redis host
    private String host;
    // Redis port
    private int port;
    // Redis password
    private String password;
    // Set connection pool size, default is 64
    private int poolSize = 64;
    // Set minimum idle connections in the pool, default is 10
    private int minIdleSize = 10;
    // Set maximum idle time for connections (in ms), connections idle longer will be closed, default is 10000
    private int idleTimeout = 10000;
    // Set connection timeout (in ms), default is 10000
    private int connectTimeout = 10000;
    // Set the number of retry attempts for connections, default is 3
    private int retryAttempts = 3;
    // Set the interval between connection retries (in ms), default is 1000
    private int retryInterval = 1000;
    // Set the interval to regularly check if connections are alive (in ms), default is 0 (no periodic checks)
    private int pingInterval = 0;
    // Set whether to keep persistent connections, default is true
    private boolean keepAlive = true;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public int getMinIdleSize() {
        return minIdleSize;
    }

    public void setMinIdleSize(int minIdleSize) {
        this.minIdleSize = minIdleSize;
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getRetryAttempts() {
        return retryAttempts;
    }

    public void setRetryAttempts(int retryAttempts) {
        this.retryAttempts = retryAttempts;
    }

    public int getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(int retryInterval) {
        this.retryInterval = retryInterval;
    }

    public int getPingInterval() {
        return pingInterval;
    }

    public void setPingInterval(int pingInterval) {
        this.pingInterval = pingInterval;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

}
