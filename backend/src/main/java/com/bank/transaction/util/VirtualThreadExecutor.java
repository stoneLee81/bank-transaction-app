package com.bank.transaction.util;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 虚拟线程执行器工具类
 * 基于Java 21虚拟线程，提供高性能并发执行能力
 * 
 * @author bank-transaction-system
 * @version 1.3.0
 */
public class VirtualThreadExecutor {
    // 共享的虚拟线程池，避免频繁创建和销毁
    private static final ExecutorService virtualThreadPool = Executors.newVirtualThreadPerTaskExecutor();

    /**
     * 条件性虚拟线程执行
     * @param list 执行列表
     * @param runInMainThread 是否在主线程执行
     * @param logic 执行逻辑
     */
    public static <T> void runWithVirtualThreads(List<T> list, Boolean runInMainThread, java.util.function.Consumer<T> logic) {
        if (runInMainThread) {
            list.stream().forEach(logic);
        } else {
            runWithVirtualThreads(list, logic);
        }
    }

    /**
     * 虚拟线程并行执行（无返回值）
     * @param list 执行列表
     * @param logic 执行逻辑
     */
    public static <T> void runWithVirtualThreads(List<T> list, java.util.function.Consumer<T> logic) {
        List<CompletableFuture<Void>> futures = list.stream()
                .map(item -> CompletableFuture.runAsync(() -> logic.accept(item), virtualThreadPool))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    /**
     * 虚拟线程并行执行（有返回值）
     * @param itemList 执行列表
     * @param logic 执行逻辑
     * @return 执行结果列表
     */
    public static <T, R> List<R> runWithVirtualThreadsLogic(List<T> itemList, Function<T, R> logic) {
        // 将 IntStream 映射为 CompletableFuture 并在虚拟线程中执行
        List<CompletableFuture<R>> futures = IntStream.range(0, itemList.size())
                .mapToObj(idx -> CompletableFuture.supplyAsync(() -> {
                    T item = itemList.get(idx);
                    // 执行传入的逻辑
                    return logic.apply(item);
                }, virtualThreadPool))
                .collect(Collectors.toList());

        // 等待所有任务完成并收集结果
        return futures.stream()
                .map(CompletableFuture::join) // 等待每个任务完成
                .collect(Collectors.toList()); // 收集结果
    }

    /**
     * 虚拟线程并行执行（保持索引顺序）
     * @param itemList 执行列表
     * @param logic 执行逻辑
     * @return 包含索引和结果的Map.Entry列表
     */
    public static <T, R> List<Map.Entry<Integer, R>> runWithVirtualThreadsWithIndex(List<T> itemList, Function<T, R> logic) {
        // 将 IntStream 映射为 CompletableFuture 并在虚拟线程中执行
        List<CompletableFuture<Map.Entry<Integer, R>>> futures = IntStream.range(0, itemList.size())
                .mapToObj(idx -> CompletableFuture.supplyAsync(() -> {
                    T item = itemList.get(idx);
                    // 执行传入的逻辑，并返回索引和结果
                    R result = logic.apply(item);
                    return (Map.Entry<Integer, R>) new AbstractMap.SimpleEntry<>(idx, result);
                }, virtualThreadPool))
                .collect(Collectors.toList());

        // 等待所有任务完成并收集结果
        return futures.stream()
                .map(CompletableFuture::join) // 等待每个任务完成
                .collect(Collectors.toList()); // 收集结果
    }

    /**
     * 在应用关闭时，确保关闭线程池
     */
    public static void shutdown() {
        virtualThreadPool.shutdown();
    }
} 