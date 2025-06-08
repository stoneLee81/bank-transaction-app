package com.bank.transaction.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

/**
 * 银行交易服务测试套件
 * 包含所有核心服务的单元测试
 * 
 * 运行方式：
 * 1. 使用 Maven: mvn test
 * 2. 使用 IDE: 运行此类或单独运行各个测试类
 * 3. 测试类包括:
 *    - TransactionServiceImplTest: 交易服务核心逻辑测试
 *    - TransactionToolServiceImplTest: 工具服务测试  
 *    - TransactionServiceIntegrationTest: 集成测试
 */
@DisplayName("银行交易服务单元测试套件")
public class TransactionServiceTestSuite {
    
    /**
     * 测试套件说明
     * 
     * 由于JUnit 5平台套件API可能不可用，建议使用以下方式运行测试：
     * 
     * 1. 运行所有测试：
     *    mvn test
     * 
     * 2. 运行特定测试类：
     *    mvn test -Dtest=TransactionServiceImplTest
     *    mvn test -Dtest=TransactionToolServiceImplTest
     *    mvn test -Dtest=TransactionServiceIntegrationTest
     * 
     * 3. 在IDE中：
     *    - 右键点击 src/test/java 目录 → Run All Tests
     *    - 或单独运行各个测试类
     */
    
    @Nested
    @DisplayName("说明文档")
    class TestSuiteDocumentation {
        // 这个嵌套类仅用于在测试报告中显示说明信息
        // 实际的测试类请直接运行：
        // - TransactionServiceImplTest
        // - TransactionToolServiceImplTest  
        // - TransactionServiceIntegrationTest
    }
} 