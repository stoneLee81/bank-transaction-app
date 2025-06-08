# 银行交易管理系统 (Demo版本)

[![GitHub](https://img.shields.io/badge/GitHub-stoneLee81/bank--transaction--app-blue?logo=github)](https://github.com/stoneLee81/bank-transaction-app)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue?logo=docker)](https://github.com/stoneLee81/bank-transaction-app)
[![Flutter](https://img.shields.io/badge/Flutter-Multi--Platform-blue?logo=flutter)](https://github.com/stoneLee81/bank-transaction-app)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-green?logo=springboot)](https://github.com/stoneLee81/bank-transaction-app)

## 📋 项目简介

这是一个基于 **纯内存缓存** 的银行交易管理系统Demo，专为演示和开发环境设计。经过最新架构优化，实现了高性能、高并发的交易处理能力。

**🔗 GitHub仓库**: https://github.com/stoneLee81/bank-transaction-app

### 🎯 Demo特性
- ✅ **纯内存存储** - 使用 ConcurrentLinkedDeque + ConcurrentHashMap 提供高性能缓存
- ✅ **无数据库依赖** - 简化部署，零配置启动
- ✅ **线程安全** - 支持高并发操作，优化的时间索引管理
- ✅ **完整业务逻辑** - 包含银行标准交易处理流程
- ✅ **消息总线架构** - 异步后续处理机制
- 🚀 **超高性能** - TPS 11764+，虚拟线程优化，平均响应时间 0.09ms
- 🔒 **API安全设计** - 敏感信息保护，@JsonIgnore隐藏关键字段
- 🏗️ **优雅架构设计** - 缓存逻辑统一管理，消除instanceof判断
- ⚠️ **数据易失性** - 应用重启后数据丢失（Demo特性）

## 🌈 前端多端支持与功能

本系统前端基于Flutter开发，支持以下主流平台一套代码多端运行：

- 📱 **Android**
- 🍎 **iOS**
- 💻 **Windows 桌面**
- 🖥️ **macOS 桌面**
- 🌐 **Web/H5**
- 🟩 **小程序**（如微信小程序，需适配mpflutter插件）

### 🚀 各平台打包/运行命令

```bash
# Android APK 打包
flutter build apk

# iOS 打包（需Mac环境）
flutter build ios

# Windows 桌面应用
flutter build windows

# macOS 桌面应用
flutter build macos

# Web/H5
flutter build web

# 微信小程序（需安装mpflutter插件等）
dart scripts/build_wechat.dart  # 生成后用微信开发者工具导入
```

### 🐳 前端Docker部署

#### 1. 构建Web应用
```bash
# 进入前端目录
cd frontend

# 构建Web版本
flutter build web --release --tree-shake-icons --pwa-strategy=none --csp --dart-define=tool_env=sit --dart-define=tool_debug=false
```

#### 2. Docker部署方式

**方式一：使用Docker Compose**
```bash
# 在frontend目录下执行
docker-compose up -d --build

# 查看运行状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 停止应用
docker-compose down
```

**方式二：使用Docker命令**
```bash
# 构建镜像
docker build -t bank-transaction-frontend .

# 运行容器
docker run -d -p 3000:80 --name bank-transaction-frontend bank-transaction-frontend

# 查看容器状态
docker ps

# 停止并删除容器
docker stop bank-transaction-frontend
docker rm bank-transaction-frontend
```

#### 3. 访问前端应用
- **Web界面**: http://localhost:3000
- **特性**: 响应式设计，支持桌面和移动端访问

### 🖼️ 前端主要功能界面

- **交易列表页**：分页展示所有交易，支持多条件筛选、搜索、排序，点击可查看详情。
- **新增交易页**：表单输入，支持金额、类型、账户、描述等字段校验，交互友好。
- **修改交易页**：支持对已存在交易的编辑，自动填充原有数据，实时校验。

#### 体验亮点
- Material Design 3 风格，支持深色模式
- 响应式布局，适配手机、平板、桌面大屏
- 动画流畅，交互自然
- 统一的多端体验，数据实时同步

## 🏗️ 技术架构

### 核心技术栈
- **Spring Boot 3.2.3** - 基础框架
- **Java 21** - 编程语言 (虚拟线程支持)
- **Spring Cache** - 缓存抽象层
- **ConcurrentLinkedDeque** - 高性能时间索引存储
- **ConcurrentHashMap** - 线程安全内存存储引擎
- **JUnit 5 + Mockito** - 测试框架

### 架构层次
```
┌─────────────────────────────────────┐
│           Controller Layer          │  ← REST API接口
├─────────────────────────────────────┤
│            Service Layer            │  ← 业务逻辑处理
│  ┌─────────────────┬───────────────┐ │
│  │ TransactionSvc  │ ToolService   │ │  ← 缓存逻辑统一管理
│  │                 │ (Cache Logic) │ │
│  └─────────────────┴───────────────┘ │
├─────────────────────────────────────┤
│              DAO Layer              │  ← 数据访问层
│         CacheTransactionDao         │  ← 纯缓存实现
├─────────────────────────────────────┤
│          Cache Storage              │  ← 内存存储
│  ConcurrentHashMap + Deque         │  ← 双层缓存结构
└─────────────────────────────────────┘
```

### 🔄 最新架构优化亮点

#### 1. 缓存逻辑重构

**优化后**：
```java
// 统一调用 TransactionToolService
public PageInfo<Transaction> getAllTransactions(int page, int size) {
    return transactionToolService.getTransactionsPaginated(page, size);
}
```

#### 2. 高性能时间索引
- **数据结构优化**：`CopyOnWriteArrayList` → `ConcurrentLinkedDeque`
- **性能提升**：插入操作从 O(n) 优化为 O(1)
- **并发安全**：天然支持高并发读写操作

#### 3. 关注点分离
- **TransactionService**：专注业务逻辑处理
- **TransactionToolService**：统一管理缓存策略
- **CacheTransactionDao**：简化为基础 CRUD 操作

## 📁 项目结构

```
bank-transaction-app/
├── README.md                    # 项目说明文档
├── .gitignore                   # Git忽略文件配置
├── .fvmrc                       # Flutter版本管理
├── backend/                     # 后端Spring Boot项目
│   ├── src/                     # Java源代码
│   │   ├── main/java/           # 主要业务代码
│   │   │   └── com/bank/transaction/
│   │   │       ├── controller/  # REST API控制器
│   │   │       ├── service/     # 业务逻辑层
│   │   │       ├── dao/         # 数据访问层
│   │   │       ├── model/       # 数据模型
│   │   │       ├── config/      # 配置类
│   │   │       └── util/        # 工具类
│   │   └── test/                # 测试代码
│   ├── pom.xml                  # Maven依赖配置
│   ├── Dockerfile               # 后端Docker配置
│   ├── docker-compose.yml       # 后端容器编排
│   └── target/                  # 编译输出目录
├── frontend/                    # 前端Flutter项目
│   ├── lib/                     # Dart源代码
│   │   ├── main.dart            # 应用入口
│   │   └── src/                 # 主要代码
│   │       ├── pages/           # 页面组件
│   │       ├── models/          # 数据模型
│   │       ├── provider/        # 状态管理
│   │       ├── utils/           # 工具类
│   │       └── widgets/         # UI组件
│   ├── build/web/               # Web编译输出（已包含）
│   ├── android/                 # Android平台配置
│   ├── ios/                     # iOS平台配置
│   ├── web/                     # Web平台配置
│   ├── wechat/                  # 微信小程序配置
│   ├── pubspec.yaml             # Flutter依赖配置
│   ├── Dockerfile               # 前端Docker配置
│   ├── nginx.conf               # Nginx配置
│   └── docker-compose.yml       # 前端容器编排
└── .vscode/                     # VS Code配置
```

## 🚀 快速启动

### 方式一：本地开发运行

#### 后端启动
```bash
# 克隆项目
git clone https://github.com/stoneLee81/bank-transaction-app.git
cd bank-transaction-app/backend

# 环境要求：Java 21+, Maven 3.8+
mvn spring-boot:run

# 或者打包后启动
mvn clean package
java -jar target/transaction-0.0.1-SNAPSHOT.jar
```

#### 前端启动
```bash
# 进入前端目录
cd frontend

# 环境要求：Flutter 3.0+
flutter pub get
flutter run -d web  # Web版本
flutter run         # 移动端版本
```

### 方式二：Docker容器化部署

#### 后端Docker部署
```bash
cd backend
docker-compose up -d --build

# 访问后端API: http://localhost:8080
```

#### 前端Docker部署
```bash
cd frontend
docker-compose up -d --build

# 访问前端Web: http://localhost:3000
```

#### 完整系统部署
```bash
# 同时启动前后端
cd backend && docker-compose up -d --build
cd ../frontend && docker-compose up -d --build

# 验证部署状态
docker ps  # 查看运行的容器
curl http://localhost:8080/actuator/health  # 检查后端
curl http://localhost:3000  # 检查前端
```

#### 🔍 部署验证
```bash
# 检查后端服务
curl -X GET http://localhost:8080/api/transactions

# 检查前端服务
open http://localhost:3000  # macOS
# 或在浏览器中访问 http://localhost:3000

# 查看容器日志
docker logs bank-transaction-app      # 后端日志
docker logs bank-transaction-frontend # 前端日志
```

# 查看应用日志
docker-compose logs -f bank-transaction-app

# 停止应用
docker-compose down
```

#### 3. 使用Dockerfile手动构建
```bash
# 构建镜像
docker build -t bank-transaction-backend .

# 运行容器
docker run -d \
  --name bank-transaction-app \
  -p 8080:8080 \
  -e JAVA_OPTS="-Xms256m -Xmx512m" \
  bank-transaction-backend

# 查看容器状态
docker ps

# 查看应用日志
docker logs -f bank-transaction-app

# 停止并删除容器
docker stop bank-transaction-app
docker rm bank-transaction-app
```

#### 4. Docker镜像特性
- 🚀 **多阶段构建** - 减小镜像大小（最终镜像约200MB）
- 🔒 **非root用户** - 安全运行，降低安全风险
- 🏥 **健康检查** - 自动监控应用状态
- ⚡ **JVM优化** - 容器环境优化的JVM参数
- 📊 **资源限制** - 内存和CPU使用限制

### 🌐 访问地址

#### 后端服务 (端口: 8080)
- **API文档**: http://localhost:8080/swagger-ui.html
- **健康检查**: http://localhost:8080/actuator/health
- **应用信息**: http://localhost:8080/actuator/info
- **交易API**: http://localhost:8080/api/transactions

#### 前端应用 (端口: 3000)
- **Web界面**: http://localhost:3000
- **移动端体验**: 响应式设计，支持手机访问
- **桌面端体验**: 支持大屏显示和操作

#### 完整系统架构
```
用户浏览器 → 前端(3000) → 后端API(8080) → 内存缓存
    ↓              ↓              ↓
  Web界面      Flutter Web    Spring Boot
```

## 📚 API接口

### 交易管理
> 为了兼容旧设备，统一使用POST方法（部分旧设备/网络代理不支持PUT/DELETE）

| 方法 | 路径 | 描述 | 请求体 |
|-----|------|------|--------|
| POST | `/api/transactions/create` | 创建交易 | Transaction对象 |
| GET | `/api/transactions/{id}` | 查询交易 | - |
| POST | `/api/transactions/update` | 更新交易 | {id, transaction} |
| POST | `/api/transactions/delete` | 删除交易 | {id} |
| GET | `/api/transactions` | 分页查询交易 | ?page=0&size=10 |

### 账户管理

| 方法 | 路径 | 描述 | 请求体 |
|-----|------|------|--------|
| POST | `/api/accounts/create` | 创建账户 | Account对象 |
| GET | `/api/accounts/{id}` | 查询账户 | - |
| POST | `/api/accounts/update` | 更新账户 | {id, account} |
| POST | `/api/accounts/delete` | 删除账户 | {id} |
| GET | `/api/accounts` | 分页查询账户 | ?page=0&size=10 |

### 请求示例

#### 创建转账交易
```bash
curl -X POST http://localhost:8080/api/transactions/create \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 1000.00,
    "type": "TRANSFER",
    "description": "转账给朋友",
    "currency": "CNY",
    "channel": "ONLINE",
    "remark": "生日礼金",
    "fromAccountId": "ACC001",
    "toAccountId": "ACC002"
  }'
```

#### 创建存款交易
```bash
curl -X POST http://localhost:8080/api/transactions/create \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 1000.00,
    "type": "DEPOSIT",
    "description": "现金存款",
    "currency": "CNY",
    "channel": "ATM",
    "toAccountId": "ACC001"
  }'
```

**响应示例：**
```json
{
  "id": "TXN2025060611695015",
  "amount": 1000.00,
  "type": "DEPOSIT",
  "description": "现金存款",
  "timestamp": "2025-06-06 18:23:22",
  "status": "PENDING",
  "currency": "CNY",
  "channel": "ATM",
  "direction": "IN",
  "remark": null,
  "fromAccountId": null,
  "toAccountId": "ACC001",
  "initiatedBy": null
}
```

#### 创建取款交易
```bash
curl -X POST http://localhost:8080/api/transactions/create \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 500.00,
    "type": "WITHDRAWAL",
    "description": "ATM取款",
    "currency": "CNY",
    "channel": "ATM",
    "fromAccountId": "ACC003"
  }'
```

#### 分页查询交易
```bash
curl -X GET "http://localhost:8080/api/transactions?page=0&size=10"
```

**响应示例：**
```json
{
  "content": [
    {
      "id": "TXN2025060611695015",
      "amount": 1000.00,
      "type": "DEPOSIT",
      "description": "现金存款",
      "timestamp": "2025-06-06 18:23:22",
      "status": "PENDING",
      "currency": "CNY",
      "channel": "ATM",
      "direction": "IN"
    }
  ],
  "page": 0,
  "size": 10,
  "total": 1,
  "totalPages": 1
}
```

#### 查询单个交易
```bash
curl -X GET http://localhost:8080/api/transactions/TXN2025060611695015
```

#### 更新交易
```bash
curl -X POST http://localhost:8080/api/transactions/update \
  -H "Content-Type: application/json" \
  -d '{
    "id": "TXN2025060611695015",
    "transaction": {
      "description": "更新后的描述",
      "status": "COMPLETED"
    }
  }'
```

#### 删除交易
```bash
curl -X POST http://localhost:8080/api/transactions/delete \
  -H "Content-Type: application/json" \
  -d '{
    "id": "TXN2025060611695015"
  }'
```

## 📊 性能测试报告

### 🚀 最新性能指标 (架构优化后)

#### 虚拟线程分页查询性能
- **TPS**: 11,764.71 transactions/second
- **平均响应时间**: 0.09ms
- **成功率**: 100%
- **并发线程数**: 200
- **总请求数**: 200

#### 混合操作压力测试
- **并发线程数**: 20
- **每线程操作数**: 20
- **总操作数**: 400
- **成功率**: 100%
- **操作类型**: 创建、查询、更新混合

#### 单项操作性能
- **创建交易**: 平均 2-5ms
- **查询交易**: 平均 0.09ms（虚拟线程优化）
- **更新交易**: 平均 3-8ms
- **删除交易**: 平均 2-6ms

### 📈 性能优化对比

| 指标 | 优化前 | 优化后 | 提升幅度 |
|------|-------|-------|---------|
| 查询 TPS | ~1,800 | 11,764 | **+553%** |
| 平均响应时间 | 2-26ms | 0.09ms | **-99.6%** |
| 并发查询成功率 | 32-48% | 100% | **+108%** |
| 时间索引操作 | O(n) | O(1) | **算法优化** |

### 🔧 性能优化技术

1. **数据结构优化**
   - `CopyOnWriteArrayList` → `ConcurrentLinkedDeque`
   - 插入操作复杂度从 O(n) 降至 O(1)

2. **虚拟线程应用**
   - 利用 Java 21 虚拟线程特性
   - 并行查询优化，减少阻塞时间

3. **架构解耦**
   - 缓存逻辑统一管理
   - 消除运行时类型判断开销

## 🧪 测试覆盖

### 自动化测试
```bash
# 运行所有测试
mvn test

# 运行单元测试
mvn test -Dtest=TransactionServiceImplTest

# 运行压力测试
mvn test -Dtest=TransactionStressTest

# 运行虚拟线程性能测试
mvn test -Dtest=VirtualThreadPerformanceTest
```

### 测试类型
- ✅ **单元测试** - 15个测试用例，覆盖核心业务逻辑
- ✅ **压力测试** - 高并发场景验证
- ✅ **性能测试** - 虚拟线程优化验证
- ✅ **集成测试** - API接口端到端测试

### 测试报告
- **总测试数**: 17个
- **成功率**: 100%
- **代码覆盖率**: 核心业务逻辑 90%+


## 🛡️ 安全特性

### API安全
- 🔒 **敏感字段保护**: 使用 @JsonIgnore 隐藏内部字段
- 🛡️ **输入验证**: Bean Validation + 自定义业务校验
- 🔐 **幂等性保护**: 防止重复交易提交
- 📋 **审计日志**: 完整的操作记录跟踪

### 业务安全
- 💰 **余额校验**: 防止透支操作
- 🚫 **状态检查**: 账户状态验证
- 💳 **限额控制**: 单笔和累计限额检查
- 🔄 **软删除**: 保护历史数据完整性


---

**⚠️ 重要提醒**: 这是一个Demo版本，使用纯内存存储，应用重启后数据会丢失。生产环境请使用持久化存储方案。