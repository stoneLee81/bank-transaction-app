package com.bank.transaction.stress;

import com.bank.transaction.model.Transaction;
import com.bank.transaction.service.TransactionService;
import com.bank.transaction.util.Constants.TransactionType;
import com.bank.transaction.util.Constants.Currency;
import com.bank.transaction.util.PageInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 虚拟线程性能测试
 * 验证Service层虚拟线程优化的效果
 */
@SpringBootTest
@ActiveProfiles("test")
class VirtualThreadPerformanceTest {

    @Autowired
    private TransactionService transactionService;

    @Test
    @DisplayName("虚拟线程优化的分页查询性能测试")
    void testVirtualThreadOptimizedPaging() throws InterruptedException {
        System.out.println("=== 虚拟线程优化分页查询性能测试 ===");

        // 1. 先创建测试数据
        int testDataCount = 100;
        long setupStart = System.currentTimeMillis();
        
        for (int i = 0; i < testDataCount; i++) {
            Transaction transaction = createTestTransaction(i);
            transactionService.createTransaction(transaction);
        }
        
        long setupTime = System.currentTimeMillis() - setupStart;
        System.out.println("✅ 测试数据准备完成：" + testDataCount + " 条交易，耗时：" + setupTime + "ms");

        // 2. 并发查询测试
        int threadCount = 20;
        int requestsPerThread = 10;
        int totalRequests = threadCount * requestsPerThread;
        
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger totalRecords = new AtomicInteger(0);
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < requestsPerThread; j++) {
                        // 不同的分页参数
                        int page = (threadId * requestsPerThread + j) % 10;
                        int size = 5 + (j % 5); // 5-9的页大小
                        
                        PageInfo<Transaction> result = transactionService.getAllTransactions(page, size);
                        
                        if (result != null && result.getItems() != null) {
                            successCount.incrementAndGet();
                            totalRecords.addAndGet(result.getItems().size());
                        }
                        
                        // 模拟一些处理时间
                        Thread.sleep(1);
                    }
                } catch (Exception e) {
                    System.err.println("Thread " + threadId + " error: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        // 计算性能指标
        double tps = (double) totalRequests / (totalTime / 1000.0);
        double avgResponseTime = (double) totalTime / totalRequests;
        double successRate = (double) successCount.get() / totalRequests * 100;
        
        // 输出测试结果
        System.out.println("\n📊 === 虚拟线程分页查询性能测试结果 ===");
        System.out.println("🔢 总请求数: " + totalRequests);
        System.out.println("✅ 成功请求数: " + successCount.get());
        System.out.println("📈 成功率: " + String.format("%.2f%%", successRate));
        System.out.println("⚡ TPS: " + String.format("%.2f", tps));
        System.out.println("⏱️ 平均响应时间: " + String.format("%.2fms", avgResponseTime));
        System.out.println("📋 查询到的记录总数: " + totalRecords.get());
        System.out.println("⏰ 总耗时: " + totalTime + "ms");
        
        // 验证结果
        assert successRate >= 95.0 : "成功率应该 >= 95%";
        assert tps > 50 : "TPS 应该 > 50";
        assert avgResponseTime < 100 : "平均响应时间应该 < 100ms";
        
        System.out.println("✅ 所有性能指标都符合预期!");
    }

    @Test
    @DisplayName("虚拟线程 vs 传统线程池性能对比")
    void testVirtualThreadVsTraditionalThreadPool() throws InterruptedException {
        System.out.println("=== 虚拟线程 vs 传统线程池性能对比 ===");

        // 创建测试数据
        int testDataCount = 50;
        for (int i = 0; i < testDataCount; i++) {
            Transaction transaction = createTestTransaction(i);
            transactionService.createTransaction(transaction);
        }

        // 测试参数
        int concurrency = 50;
        int requestsPerThread = 5;
        
        // 1. 传统线程池测试
        long traditionalTime = performanceTest("传统线程池", concurrency, requestsPerThread, false);
        
        // 2. 虚拟线程测试 (当前Service层实现就是虚拟线程优化的)
        long virtualTime = performanceTest("虚拟线程优化", concurrency, requestsPerThread, true);
        
        // 性能对比
        double improvement = ((double) traditionalTime - virtualTime) / traditionalTime * 100;
        
        System.out.println("\n📊 === 性能对比结果 ===");
        System.out.println("🔄 传统线程池耗时: " + traditionalTime + "ms");
        System.out.println("⚡ 虚拟线程耗时: " + virtualTime + "ms");
        if (improvement > 0) {
            System.out.println("🚀 性能提升: " + String.format("%.2f%%", improvement));
        } else {
            System.out.println("📈 性能差异: " + String.format("%.2f%%", Math.abs(improvement)));
        }
        
        System.out.println("✅ 性能对比测试完成!");
    }
    
    /**
     * 执行性能测试
     */
    private long performanceTest(String testName, int concurrency, int requestsPerThread, boolean isVirtualThread) throws InterruptedException {
        System.out.println("\n🧪 开始 " + testName + " 测试...");
        
        CountDownLatch latch = new CountDownLatch(concurrency);
        AtomicInteger successCount = new AtomicInteger(0);
        
        ExecutorService executor = isVirtualThread ? 
            Executors.newVirtualThreadPerTaskExecutor() : 
            Executors.newFixedThreadPool(concurrency);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < concurrency; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < requestsPerThread; j++) {
                        PageInfo<Transaction> result = transactionService.getAllTransactions(threadId % 5, 10);
                        if (result != null) {
                            successCount.incrementAndGet();
                        }
                    }
                } catch (Exception e) {
                    System.err.println(testName + " Thread " + threadId + " error: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        int totalRequests = concurrency * requestsPerThread;
        double tps = (double) totalRequests / (totalTime / 1000.0);
        double successRate = (double) successCount.get() / totalRequests * 100;
        
        System.out.println("📊 " + testName + " 结果:");
        System.out.println("  - 成功率: " + String.format("%.2f%%", successRate));
        System.out.println("  - TPS: " + String.format("%.2f", tps));
        System.out.println("  - 耗时: " + totalTime + "ms");
        
        return totalTime;
    }
    
    /**
     * 创建测试交易
     */
    private Transaction createTestTransaction(int index) {
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(100 + index));
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setRemark("性能测试交易 #" + index);
        transaction.setCurrency(Currency.CNY);
        transaction.setChannel("PERFORMANCE_TEST");
        transaction.setToAccountId("ACC001");
        transaction.setTimestamp(LocalDateTime.now().minusMinutes(index));
        return transaction;
    }
} 