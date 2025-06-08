package com.bank.transaction.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "storage")
public class StorageConfig {
    private String type = "cache"; // 默认缓存
    private Cache cache = new Cache();
    private Mysql mysql = new Mysql();

    public String getType() {return type;}
    public void setType(String type) {this.type = type;}
    public Cache getCache() {return cache;}
    public void setCache(Cache cache) {this.cache = cache;}
    public Mysql getMysql() {return mysql;}
    public void setMysql(Mysql mysql) {this.mysql = mysql;}

    public static class Cache {
        private int maxSize = 10000; // 最大缓存条数
        private long expireMinutes = 60; // 过期时间(分钟)

        public int getMaxSize() {return maxSize;}
        public void setMaxSize(int maxSize) {this.maxSize = maxSize;}
        public long getExpireMinutes() {return expireMinutes;}
        public void setExpireMinutes(long expireMinutes) {this.expireMinutes = expireMinutes;}
    }

    public static class Mysql {
        private int connectionPoolSize = 20; // 连接池大小
        private int queryTimeout = 30; // 查询超时时间(秒)

        public int getConnectionPoolSize() {return connectionPoolSize;}
        public void setConnectionPoolSize(int connectionPoolSize) {this.connectionPoolSize = connectionPoolSize;}
        public int getQueryTimeout() {return queryTimeout;}
        public void setQueryTimeout(int queryTimeout) {this.queryTimeout = queryTimeout;}
    }
} 