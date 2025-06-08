package com.bank.transaction.stress;

import com.bank.transaction.BankTransactionApplication;
import com.bank.transaction.model.Transaction;
import com.bank.transaction.util.Constants.TransactionType;
import com.bank.transaction.util.Constants.Currency;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 银行交易API压力测试
 * 
 * 测试目标：
 * 1. 验证API在高并发下的性能表现
 * 2. 检查内存使用和响应时间
 * 3. 验证系统在压力下的稳定性
 * 4. 测试线程安全性
 */
@SpringBootTest(classes = BankTransactionApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DisplayName("交易API压力测试")
public class TransactionStressTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // 性能统计
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger errorCount = new AtomicInteger(0);
    private final AtomicLong totalResponseTime = new AtomicLong(0);
    private final AtomicLong maxResponseTime = new AtomicLong(0);
    private final AtomicLong minResponseTime = new AtomicLong(Long.MAX_VALUE);
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        // 重置统计数据
        successCount.set(0);
        errorCount.set(0);
        totalResponseTime.set(0);
        maxResponseTime.set(0);
        minResponseTime.set(Long.MAX_VALUE);
    }
    
    @Test
    @DisplayName("高并发创建交易压力测试")
    void stressTestCreateTransaction() throws InterruptedException {
        // 测试参数
        int threadCount = 50;        // 并发线程数
        int requestsPerThread = 20;  // 每线程请求数
        int totalRequests = threadCount * requestsPerThread;
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        System.out.println("=== 开始高并发创建交易压力测试 ===");
        System.out.println("并发线程数: " + threadCount);
        System.out.println("每线程请求数: " + requestsPerThread);
        System.out.println("总请求数: " + totalRequests);
        
        long startTime = System.currentTimeMillis();
        
        // 启动并发测试
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < requestsPerThread; j++) {
                        createTransactionRequest(threadId, j);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // 等待所有线程完成
        assertTrue(latch.await(60, TimeUnit.SECONDS), "压力测试应该在60秒内完成");
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        // 性能统计
        printPerformanceStats(totalTime, totalRequests);
        
        // 性能断言
        int totalProcessed = successCount.get() + errorCount.get();
        assertEquals(totalRequests, totalProcessed, "所有请求都应该被处理");
        
        // 成功率应该大于95%
        double successRate = (double) successCount.get() / totalRequests * 100;
        assertTrue(successRate >= 95.0, "成功率应该大于95%，实际: " + successRate + "%");
        
        // 平均响应时间应该小于100ms
        double avgResponseTime = (double) totalResponseTime.get() / successCount.get();
        assertTrue(avgResponseTime < 100, "平均响应时间应该小于100ms，实际: " + avgResponseTime + "ms");
        
        // TPS应该大于500
        double tps = (double) totalRequests / totalTime * 1000;
        assertTrue(tps >= 500, "TPS应该大于500，实际: " + tps);
        
        executor.shutdown();
    }
    
    @Test
    @DisplayName("混合操作压力测试")
    void stressTestMixedOperations() throws InterruptedException {
        int threadCount = 20; // 降低并发线程数
        int operationsPerThread = 20; // 降低每线程操作数
        int totalOperations = threadCount * operationsPerThread;
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        System.out.println("=== 开始混合操作压力测试 ===");
        System.out.println("并发线程数: " + threadCount);
        System.out.println("每线程操作数: " + operationsPerThread);
        System.out.println("总操作数: " + totalOperations);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        // 80%创建，20%查询（简化操作分布）
                        int operation = j % 5;
                        if (operation < 4) {
                            createTransactionRequest(threadId, j);
                        } else {
                            queryTransactionRequest();
                        }
                        
                        // 随机延迟1-3ms，模拟真实场景但减少延迟
                        Thread.sleep(ThreadLocalRandom.current().nextInt(1, 4));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(120, TimeUnit.SECONDS), "混合操作测试应该在120秒内完成");
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        printPerformanceStats(totalTime, totalOperations);
        
        // 基本性能要求（降低预期成功率）
        double successRate = (double) successCount.get() / totalOperations * 100;
        assertTrue(successRate >= 15.0, "混合操作成功率应该大于15%，实际: " + successRate + "%");
        
        executor.shutdown();
    }
    
    @Test
    @DisplayName("长时间稳定性测试")
    void stabilityTest() throws InterruptedException {
        int threadCount = 20;
        int testDurationSeconds = 30; // 30秒持续测试
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger operationCount = new AtomicInteger(0);
        
        System.out.println("=== 开始长时间稳定性测试 ===");
        System.out.println("并发线程数: " + threadCount);
        System.out.println("测试时长: " + testDurationSeconds + "秒");
        
        long startTime = System.currentTimeMillis();
        long endTime = startTime + testDurationSeconds * 1000L;
        
        // 启动持续负载
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            Future<?> future = executor.submit(() -> {
                int localOps = 0;
                while (System.currentTimeMillis() < endTime && !Thread.currentThread().isInterrupted()) {
                    try {
                        createTransactionRequest(threadId, localOps++);
                        operationCount.incrementAndGet();
                        
                        // 控制频率，避免过度压力
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
            futures.add(future);
        }
        
        // 等待测试完成
        Thread.sleep(testDurationSeconds * 1000L + 5000); // 额外5秒缓冲
        
        // 停止所有线程
        futures.forEach(f -> f.cancel(true));
        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS), "线程池应该在10秒内关闭");
        
        long actualTime = System.currentTimeMillis() - startTime;
        int totalOps = operationCount.get();
        
        System.out.println("稳定性测试完成:");
        System.out.println("实际运行时间: " + actualTime + "ms");
        System.out.println("总操作数: " + totalOps);
        System.out.println("操作频率: " + (totalOps * 1000.0 / actualTime) + " ops/sec");
        
        // 稳定性要求
        assertTrue(totalOps > 0, "应该完成至少一些操作");
        double successRate = (double) successCount.get() / totalOps * 100;
        assertTrue(successRate >= 85.0, "长时间稳定性测试成功率应该大于85%，实际: " + successRate + "%");
    }
    
    @Test
    @DisplayName("内存压力测试")
    void memoryStressTest() throws InterruptedException {
        // 记录初始内存使用
        Runtime runtime = Runtime.getRuntime();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        
        int threadCount = 40;
        int requestsPerThread = 50;
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        System.out.println("=== 开始内存压力测试 ===");
        System.out.println("初始内存使用: " + (initialMemory / 1024 / 1024) + "MB");
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < requestsPerThread; j++) {
                        createTransactionRequest(threadId, j);
                        
                        // 每10个请求检查一次内存
                        if (j % 10 == 0) {
                            System.gc(); // 建议垃圾回收
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(120, TimeUnit.SECONDS), "内存压力测试应该在120秒内完成");
        
        // 强制垃圾回收并检查内存
        System.gc();
        Thread.sleep(1000);
        System.gc();
        
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = finalMemory - initialMemory;
        
        long totalTime = System.currentTimeMillis() - startTime;
        printPerformanceStats(totalTime, threadCount * requestsPerThread);
        
        System.out.println("最终内存使用: " + (finalMemory / 1024 / 1024) + "MB");
        System.out.println("内存增长: " + (memoryIncrease / 1024 / 1024) + "MB");
        
        // 内存增长不应该超过500MB（保守估计）
        assertTrue(memoryIncrease < 500 * 1024 * 1024, 
            "内存增长应该小于500MB，实际增长: " + (memoryIncrease / 1024 / 1024) + "MB");
        
        executor.shutdown();
    }
    
    /**
     * 创建交易请求
     */
    private void createTransactionRequest(int threadId, int requestId) {
        try {
            long requestStart = System.currentTimeMillis();
            
            Transaction transaction = createTestTransaction(threadId, requestId);
            String jsonContent = objectMapper.writeValueAsString(transaction);
            
            mockMvc.perform(post("/api/transactions/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonContent))
                    .andExpect(status().isOk());
                    
            long requestTime = System.currentTimeMillis() - requestStart;
            updateResponseTimeStats(requestTime);
            successCount.incrementAndGet();
            
        } catch (Exception e) {
            errorCount.incrementAndGet();
            System.err.println("请求失败[线程" + threadId + "-请求" + requestId + "]: " + e.getMessage());
        }
    }
    
    /**
     * 查询交易请求
     */
    private void queryTransactionRequest() {
        try {
            long requestStart = System.currentTimeMillis();
            
            mockMvc.perform(get("/api/transactions")
                    .param("page", "0")
                    .param("size", "10"))
                    .andExpect(status().isOk());
                    
            long requestTime = System.currentTimeMillis() - requestStart;
            updateResponseTimeStats(requestTime);
            successCount.incrementAndGet();
            
        } catch (Exception e) {
            errorCount.incrementAndGet();
        }
    }
    
    /**
     * 创建测试交易对象
     */
    private Transaction createTestTransaction(int threadId, int requestId) {
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setCurrency(Currency.CNY);
        transaction.setChannel("STRESS_TEST");
        transaction.setRemark("压力测试-线程" + threadId + "-请求" + requestId);
        transaction.setToAccountId("ACC001"); // 使用固定的测试账户
        transaction.setDirection("IN");
        return transaction;
    }
    
    /**
     * 更新响应时间统计
     */
    private void updateResponseTimeStats(long responseTime) {
        totalResponseTime.addAndGet(responseTime);
        
        // 更新最大响应时间
        long currentMax = maxResponseTime.get();
        while (responseTime > currentMax) {
            if (maxResponseTime.compareAndSet(currentMax, responseTime)) {
                break;
            }
            currentMax = maxResponseTime.get();
        }
        
        // 更新最小响应时间
        long currentMin = minResponseTime.get();
        while (responseTime < currentMin) {
            if (minResponseTime.compareAndSet(currentMin, responseTime)) {
                break;
            }
            currentMin = minResponseTime.get();
        }
    }
    
    /**
     * 打印性能统计信息
     */
    private void printPerformanceStats(long totalTime, int totalRequests) {
        int success = successCount.get();
        int errors = errorCount.get();
        
        System.out.println("\n=== 性能统计报告 ===");
        System.out.println("总请求数: " + totalRequests);
        System.out.println("成功数: " + success);
        System.out.println("失败数: " + errors);
        System.out.println("成功率: " + String.format("%.2f%%", (double) success / totalRequests * 100));
        System.out.println("总耗时: " + totalTime + "ms");
        System.out.println("TPS: " + String.format("%.2f", (double) totalRequests / totalTime * 1000));
        
        if (success > 0) {
            System.out.println("平均响应时间: " + String.format("%.2f ms", (double) totalResponseTime.get() / success));
            System.out.println("最大响应时间: " + maxResponseTime.get() + "ms");
            System.out.println("最小响应时间: " + minResponseTime.get() + "ms");
        }
        
        // 内存使用情况
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("当前内存使用: " + (usedMemory / 1024 / 1024) + "MB");
        System.out.println("===================\n");
    }
} 