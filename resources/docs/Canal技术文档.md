# Canal技术文档

## 1. 概述

Canal是阿里巴巴开源的基于MySQL数据库增量日志解析，提供增量数据订阅和消费的中间件。Canal主要用于数据库实时同步、数据异构、缓存更新等场景。

### 1.1 核心特性
- **实时性**：基于MySQL binlog的实时数据同步
- **高可用**：支持HA模式，保证服务可用性
- **多种协议**：支持Canal协议、Kafka、RocketMQ等多种消息协议
- **灵活配置**：支持表级别、库级别的数据过滤
- **多语言客户端**：提供Java、Go、Python等多种语言客户端

### 1.2 应用场景
- 数据库实时备份和同步
- 数据异构（MySQL → ES/Redis/HBase等）
- 实时数据仓库构建
- 缓存失效和更新
- 业务解耦和事件驱动架构

## 2. 架构设计

### 2.1 整体架构

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│                 │    │                 │    │                 │
│   MySQL Master │────│  Canal Server   │────│  Canal Client   │
│                 │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
        │                        │                        │
        │                        │                        │
   Binlog Events            Parse & Filter           Business Logic
```

### 2.2 核心组件

#### 2.2.1 Canal Server
- **EventParser**：数据源接入，模拟slave协议和master进行交互，协议解析
- **EventSink**：Parser和Store链接器，进行数据过滤、加工、分发
- **EventStore**：数据存储，提供游标式访问
- **MetaManager**：增量订阅&消费信息管理器

#### 2.2.2 Canal Client
- **CanalConnector**：客户端连接器，负责与Canal Server通信
- **MessageHandler**：消息处理器，处理接收到的数据变更事件

### 2.3 数据流转过程

```
MySQL Binlog → EventParser → EventSink → EventStore → Canal Client
     ↓              ↓            ↓           ↓            ↓
  原始日志        协议解析      数据过滤     数据存储     业务处理
```

## 3. 核心原理

### 3.1 MySQL Binlog机制

#### 3.1.1 Binlog格式
- **Statement**：记录SQL语句
- **Row**：记录行变更（推荐）
- **Mixed**：混合模式

#### 3.1.2 Binlog事件类型
```java
// 主要事件类型
QUERY_EVENT         // SQL语句事件
TABLE_MAP_EVENT     // 表映射事件
WRITE_ROWS_EVENT    // 插入事件
UPDATE_ROWS_EVENT   // 更新事件
DELETE_ROWS_EVENT   // 删除事件
XID_EVENT          // 事务提交事件
```

### 3.2 Canal工作原理

#### 3.2.1 模拟MySQL Slave
Canal Server伪装成MySQL的Slave节点：
1. 向MySQL Master注册为Slave
2. 请求binlog dump协议
3. 接收binlog数据流
4. 解析binlog事件

#### 3.2.2 协议解析流程
```java
// 解析流程伪代码
while (running) {
    // 1. 读取binlog事件头
    LogHeader header = readLogHeader();
    
    // 2. 根据事件类型解析事件体
    LogEvent event = parseLogEvent(header);
    
    // 3. 过滤和转换
    if (filter.accept(event)) {
        CanalEntry entry = transform(event);
        
        // 4. 存储到EventStore
        eventStore.put(entry);
    }
}
```

### 3.3 数据一致性保证

#### 3.3.1 位点管理
- **Binlog Position**：记录当前消费位置
- **事务边界**：保证事务完整性
- **断点续传**：支持从指定位点开始消费

#### 3.3.2 HA机制
```yaml
# HA配置示例
canal.zkServers = 127.0.0.1:2181
canal.zookeeper.flush.period = 1000
canal.instance.detecting.enable = true
canal.instance.detecting.sql = select 1
canal.instance.detecting.interval.time = 3
```

## 4. 部署实现

### 4.1 环境准备

#### 4.1.1 MySQL配置
```ini
# my.cnf配置
[mysqld]
# 开启binlog
log-bin=mysql-bin
# 选择row模式
binlog-format=ROW
# 设置server-id
server-id=1
# binlog过期时间
expire_logs_days = 7
```

#### 4.1.2 创建Canal用户
```sql
-- 创建用户
CREATE USER 'canal'@'%' IDENTIFIED BY 'canal_password';

-- 授权
GRANT SELECT, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'canal'@'%';

-- 刷新权限
FLUSH PRIVILEGES;
```

### 4.2 Canal Server部署

#### 4.2.1 下载和安装
```bash
# 下载Canal
wget https://github.com/alibaba/canal/releases/download/canal-1.1.7/canal.deployer-1.1.7.tar.gz

# 解压
tar -zxvf canal.deployer-1.1.7.tar.gz -C /opt/canal

# 目录结构
/opt/canal/
├── bin/           # 启动脚本
├── conf/          # 配置文件
├── lib/           # 依赖jar包
└── logs/          # 日志文件
```

#### 4.2.2 配置文件
```properties
# conf/canal.properties
# canal server端口
canal.port = 11111

# canal实例配置
canal.destinations = example

# zookeeper配置（HA模式）
canal.zkServers = 127.0.0.1:2181
```

```properties
# conf/example/instance.properties
# MySQL连接配置
canal.instance.master.address=127.0.0.1:3306
canal.instance.dbUsername=canal
canal.instance.dbPassword=canal_password

# binlog配置
canal.instance.connectionCharset=UTF-8
canal.instance.defaultDatabaseName=test

# 过滤配置
canal.instance.filter.regex=test\\..*
canal.instance.filter.black.regex=test\\.sys_.*
```

#### 4.2.3 启动Canal Server
```bash
# 启动
sh bin/startup.sh

# 查看日志
tail -f logs/canal/canal.log
tail -f logs/example/example.log

# 停止
sh bin/stop.sh
```

### 4.3 Canal Client开发

#### 4.3.1 Maven依赖
```xml
<dependency>
    <groupId>com.alibaba.otter</groupId>
    <artifactId>canal.client</artifactId>
    <version>1.1.7</version>
</dependency>
```

#### 4.3.2 Java客户端示例
```java
@Component
@Slf4j
public class CanalClient {
    
    private CanalConnector connector;
    
    @PostConstruct
    public void init() {
        // 创建连接器
        connector = CanalConnectors.newSingleConnector(
            new InetSocketAddress("127.0.0.1", 11111),
            "example", "", ""
        );
        
        // 启动消费线程
        startConsume();
    }
    
    private void startConsume() {
        Thread thread = new Thread(() -> {
            try {
                connector.connect();
                connector.subscribe(".*\\..*");
                connector.rollback();
                
                while (true) {
                    // 获取指定数量的数据
                    Message message = connector.getWithoutAck(1000);
                    long batchId = message.getBatchId();
                    int size = message.getEntries().size();
                    
                    if (batchId == -1 || size == 0) {
                        Thread.sleep(1000);
                    } else {
                        processEntries(message.getEntries());
                        connector.ack(batchId);
                    }
                }
            } catch (Exception e) {
                log.error("Canal消费异常", e);
            }
        });
        
        thread.setDaemon(true);
        thread.start();
    }
    
    private void processEntries(List<CanalEntry.Entry> entries) {
        for (CanalEntry.Entry entry : entries) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN ||
                entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }
            
            CanalEntry.RowChange rowChange;
            try {
                rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                log.error("解析RowChange失败", e);
                continue;
            }
            
            CanalEntry.EventType eventType = rowChange.getEventType();
            String tableName = entry.getHeader().getTableName();
            String schemaName = entry.getHeader().getSchemaName();
            
            log.info("数据变更 - Schema: {}, Table: {}, EventType: {}", 
                    schemaName, tableName, eventType);
            
            for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
                switch (eventType) {
                    case INSERT:
                        handleInsert(tableName, rowData.getAfterColumnsList());
                        break;
                    case UPDATE:
                        handleUpdate(tableName, rowData.getBeforeColumnsList(), 
                                   rowData.getAfterColumnsList());
                        break;
                    case DELETE:
                        handleDelete(tableName, rowData.getBeforeColumnsList());
                        break;
                    default:
                        break;
                }
            }
        }
    }
    
    private void handleInsert(String tableName, List<CanalEntry.Column> columns) {
        log.info("INSERT - Table: {}", tableName);
        columns.forEach(column -> 
            log.info("Column: {} = {}", column.getName(), column.getValue()));
    }
    
    private void handleUpdate(String tableName, 
                            List<CanalEntry.Column> beforeColumns,
                            List<CanalEntry.Column> afterColumns) {
        log.info("UPDATE - Table: {}", tableName);
        // 处理更新逻辑
    }
    
    private void handleDelete(String tableName, List<CanalEntry.Column> columns) {
        log.info("DELETE - Table: {}", tableName);
        // 处理删除逻辑
    }
}
```

### 4.4 Spring Boot集成

#### 4.4.1 配置类
```java
@Configuration
@EnableConfigurationProperties(CanalProperties.class)
public class CanalConfig {
    
    @Bean
    public CanalConnector canalConnector(CanalProperties properties) {
        return CanalConnectors.newSingleConnector(
            new InetSocketAddress(properties.getHost(), properties.getPort()),
            properties.getDestination(),
            properties.getUsername(),
            properties.getPassword()
        );
    }
}

@ConfigurationProperties(prefix = "canal")
@Data
public class CanalProperties {
    private String host = "127.0.0.1";
    private int port = 11111;
    private String destination = "example";
    private String username = "";
    private String password = "";
    private String filter = ".*\\..*";
}
```

#### 4.4.2 应用配置
```yaml
# application.yml
canal:
  host: 127.0.0.1
  port: 11111
  destination: example
  username: ""
  password: ""
  filter: "test\\..*"
```

## 5. 高级特性

### 5.1 多种消息协议支持

#### 5.1.1 Kafka模式
```properties
# conf/example/instance.properties
# 启用Kafka模式
canal.mq.topic=canal-topic
canal.mq.partition=0
canal.serverMode=kafka
canal.mq.servers=127.0.0.1:9092
```

#### 5.1.2 RocketMQ模式
```properties
# 启用RocketMQ模式
canal.serverMode=rocketmq
canal.mq.servers=127.0.0.1:9876
canal.mq.topic=canal-topic
```

### 5.2 数据过滤和路由

#### 5.2.1 表级过滤
```properties
# 只同步特定表
canal.instance.filter.regex=test\\.user,test\\.order

# 排除特定表
canal.instance.filter.black.regex=test\\.temp_.*
```

#### 5.2.2 自定义过滤器
```java
public class CustomEventFilter implements CanalEventFilter {
    
    @Override
    public boolean filter(CanalEntry.Entry entry) {
        String tableName = entry.getHeader().getTableName();
        
        // 自定义过滤逻辑
        if ("sensitive_table".equals(tableName)) {
            return false; // 过滤掉敏感表
        }
        
        return true;
    }
}
```

### 5.3 监控和管理

#### 5.3.1 JMX监控
```java
// 启用JMX
-Dcom.sun.management.jmxremote
-Dcom.sun.management.jmxremote.port=9999
-Dcom.sun.management.jmxremote.authenticate=false
-Dcom.sun.management.jmxremote.ssl=false
```

#### 5.3.2 Canal Admin
Canal Admin提供Web界面管理：
- 实例管理
- 配置管理
- 监控告警
- 日志查看

## 6. 生产注意事项

### 6.1 性能优化

#### 6.1.1 网络优化
```properties
# 增大网络缓冲区
canal.instance.receiveBufferSize = 16384
canal.instance.sendBufferSize = 16384

# 启用批量获取
canal.instance.memory.buffer.size = 32768
canal.instance.memory.buffer.memunit = 1024
```

#### 6.1.2 内存优化
```bash
# JVM参数优化
-Xms2048m -Xmx3072m
-XX:+UseG1GC
-XX:MaxGCPauseMillis=50
-XX:+HeapDumpOnOutOfMemoryError
```

### 6.2 高可用部署

#### 6.2.1 Canal Server HA
```yaml
# 使用ZooKeeper实现HA
canal.zkServers: zk1:2181,zk2:2181,zk3:2181
canal.zookeeper.flush.period: 1000

# 多实例部署
canal.destinations: instance1,instance2,instance3
```

#### 6.2.2 MySQL主从切换
```properties
# 支持MySQL主从自动切换
canal.instance.master.address = 192.168.1.100:3306
canal.instance.standby.address = 192.168.1.101:3306
canal.instance.detecting.enable = true
canal.instance.detecting.sql = select 1
```

### 6.3 数据一致性保障

#### 6.3.1 事务完整性
```java
// 确保事务完整性
public class TransactionHandler {
    
    private Map<Long, List<CanalEntry.Entry>> transactionBuffer = new ConcurrentHashMap<>();
    
    public void handleEntry(CanalEntry.Entry entry) {
        if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN) {
            long transactionId = entry.getHeader().getExecuteTime();
            transactionBuffer.put(transactionId, new ArrayList<>());
        } else if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
            long transactionId = entry.getHeader().getExecuteTime();
            List<CanalEntry.Entry> entries = transactionBuffer.remove(transactionId);
            
            // 批量处理事务内的所有变更
            processTransaction(entries);
        } else {
            // 缓存事务内的变更
            long transactionId = entry.getHeader().getExecuteTime();
            transactionBuffer.get(transactionId).add(entry);
        }
    }
}
```

#### 6.3.2 位点管理
```java
// 自定义位点存储
public class CustomMetaManager implements CanalMetaManager {
    
    @Override
    public void start() {
        // 初始化位点存储
    }
    
    @Override
    public void stop() {
        // 清理资源
    }
    
    @Override
    public Position getCursor(ClientIdentity clientIdentity) {
        // 从存储中获取位点
        return loadPositionFromStorage(clientIdentity);
    }
    
    @Override
    public void updateCursor(ClientIdentity clientIdentity, Position position) {
        // 更新位点到存储
        savePositionToStorage(clientIdentity, position);
    }
}
```

### 6.4 监控告警

#### 6.4.1 关键指标监控
```java
// 监控指标
public class CanalMetrics {
    
    // 延迟监控
    private final Timer parseLatency = Timer.build()
        .name("canal_parse_latency_seconds")
        .help("Canal parse latency")
        .register();
    
    // 吞吐量监控
    private final Counter processedEvents = Counter.build()
        .name("canal_processed_events_total")
        .help("Total processed events")
        .register();
    
    // 错误监控
    private final Counter errorCount = Counter.build()
        .name("canal_errors_total")
        .help("Total errors")
        .register();
}
```

#### 6.4.2 告警配置
```yaml
# Prometheus告警规则
groups:
  - name: canal
    rules:
      - alert: CanalHighLatency
        expr: canal_parse_latency_seconds > 5
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "Canal解析延迟过高"
          
      - alert: CanalInstanceDown
        expr: up{job="canal"} == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Canal实例宕机"
```

### 6.5 故障处理

#### 6.5.1 常见问题排查
```bash
# 检查Canal Server状态
ps aux | grep canal
netstat -tlnp | grep 11111

# 查看日志
tail -f logs/canal/canal.log
tail -f logs/example/example.log

# 检查MySQL binlog
SHOW MASTER STATUS;
SHOW BINARY LOGS;
```

#### 6.5.2 数据修复
```java
// 数据一致性检查和修复
public class DataConsistencyChecker {
    
    public void checkAndRepair(String tableName, Long startTime, Long endTime) {
        // 1. 从Canal获取变更记录
        List<ChangeRecord> canalRecords = getCanalRecords(tableName, startTime, endTime);
        
        // 2. 从目标系统获取数据
        List<DataRecord> targetRecords = getTargetRecords(tableName, startTime, endTime);
        
        // 3. 比较和修复
        List<DataRecord> inconsistentRecords = findInconsistentRecords(canalRecords, targetRecords);
        
        // 4. 执行修复
        repairInconsistentData(inconsistentRecords);
    }
}
```

## 7. 最佳实践

### 7.1 架构设计建议
1. **读写分离**：Canal消费不影响主库性能
2. **异步处理**：使用消息队列解耦Canal和业务处理
3. **幂等设计**：处理逻辑要支持重复消费
4. **监控完善**：建立完整的监控和告警体系

### 7.2 开发规范
1. **异常处理**：完善的异常处理和重试机制
2. **日志记录**：详细的操作日志便于问题排查
3. **配置管理**：使用配置中心统一管理配置
4. **版本控制**：Canal Server和Client版本要匹配

### 7.3 运维建议
1. **定期备份**：定期备份Canal配置和位点信息
2. **容量规划**：根据数据量合理规划资源
3. **版本升级**：及时升级到稳定版本
4. **文档维护**：维护详细的运维文档

## 8. 总结

Canal作为MySQL数据同步的重要工具，在数据实时处理场景中发挥着重要作用。通过合理的架构设计、完善的监控体系和规范的开发运维流程，可以构建稳定可靠的数据同步系统。

在实际使用中，需要根据具体业务场景选择合适的部署模式和配置参数，同时要重点关注数据一致性、性能优化和故障处理等关键问题。