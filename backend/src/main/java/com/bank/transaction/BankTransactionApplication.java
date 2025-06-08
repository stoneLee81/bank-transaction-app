package com.bank.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class BankTransactionApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankTransactionApplication.class, args);
    }
    
    /**
     * 应用启动完成后的提示信息
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("=".repeat(80));
        log.info("🏦 银行交易管理系统 (Demo版本) 启动完成!");
        log.info("=".repeat(80));
        log.info("");
        log.info("📋 系统信息:");
        log.info("   ✅ 存储模式: 纯内存缓存 (ConcurrentHashMap)");
        log.info("   ✅ 数据特性: 高性能访问，重启后数据丢失");
        log.info("   ✅ 线程安全: 支持并发操作");
        log.info("   ✅ 适用场景: Demo演示、功能验证、开发测试");
        log.info("");
        log.info("🌐 访问地址:");
        log.info("   📖 API文档: http://localhost:8080/swagger-ui.html");
        log.info("   ❤️ 健康检查: http://localhost:8080/actuator/health");
        log.info("   📊 应用信息: http://localhost:8080/actuator/info");
        log.info("");
        log.info("🧪 测试命令:");
        log.info("   mvn test                                    # 运行所有测试");
        log.info("   mvn test -Dtest=TestRunner                  # 查看测试指南");
        log.info("");
        log.info("⚠️  注意: 这是Demo版本，数据在应用重启后会丢失！");
        log.info("=".repeat(80));
    }
} 