## 项目启动
单机直接启动即可

---

集群需要先配置 hosts

```
127.0.0.1 eureka.peer1.cn
127.0.0.1 eureka.peer2.cn
127.0.0.1 eureka.peer3.cn
```
启动时分别带上启动参数

```
-Dspring.profiles.active=cluster-peer1/2/3
```


## Eureka REST 接口
Eureka 提供了 RESTful HTTP 接口 [参考地址](https://github.com/Netflix/eureka/wiki/Eureka-REST-operations)。比如这个：

Operation | HTTP action | Description
---|---|---
Query for all instances | GET /eureka/v2/apps |	HTTP Code: 200 on success Output: JSON/XML

你可以在浏览器地址栏输入以下链接来访问（忽略了版本号v2，需要的话可在 .yml 配置文件中配置）
```
http://localhost:8761/eureka/apps
```
这些接口都定义在这个包中：**com.netflix.eureka.resources**，类名以 Resource 结尾。比如，上表中接口对应方法为：**com.netflix.eureka.resources.getContainers()**。

## 服务端配置优化

```
# 是否向注册中心注册
eureka.client.registerWithEureka = true
# 是否同步注册信息
eureka.client.fetchRegistry = true
```
单机模式这两个都设置成 false，因为只有一个注册中心，没必要。集群模式两个都设置成 true，官方例子也是如此，否则副本节点会显示在 unavailable-replicas 一栏。

**fetchRegistry** 要生效还得配合同步重试次数 **number-of-replication-retries**，这个值 > 0 同步方法才会生效，默认值是 5 所以不用管它。

同步方法：**com.netflix.eureka.registry.PeerAwareInstanceRegistryImpl.syncUp()**

---

```
# 是否开启自我保护
eureka.server.enable-self-preservation = false
# 剔除服务时间间隔
eureka.server.eviction-interval-timer-in-ms = 5000
```
eureka 自我保护实现了 CAP 定理中的分区容错性(P)，在网络不稳定的情况下保护服务不被剔除，避免短暂的网络抖动可能使大部分服务无法续约面临从 eureka 剔除的风险。

eureka 会检测最近 15 分钟(可配置)内所有注册到 eureka 的实例心跳正常的比例，如果低于 85%(可配置) 就会进入自我保护模式。保护这些服务不被剔除，每隔 60 秒进行检测，剔除未恢复的服务。

**在测试环境或者服务数量比较少的情况下(比如10个)可以关闭自我保护，缩短剔除服务间隔时间。
在正式服且服务数量比较多的情况下开启自我保护，不用配置，因为默认是开的。**

---

```
# 是否开启只读缓存
eureka.server.use-read-only-response-cache = false
# 只读缓存向读写缓存同步的时间间隔
eureka.server.response-cache-update-interval-ms = 1000
```
eureka 注册信息是三级内存存储，高到低分别为：registry、readWriteCacheMap，readOnlyCacheMap。

服务注册时，将信息写入 registry，同时使 readWriteCacheMap 中的注册信息失效

客户端拉取注册信息时，按等级低到高依次尝试从缓存中获取，如果信息是从 registry 得到，则会写到 readOnlyCacheMap 中。

readOnlyCacheMap 是通过定时任务从 readWriteCacheMap 中全量同步的。所以没办法保证客户端从 readOnlyCacheMap 拿到的信息是最新的，有可能拿到时，服务已经不可用了。这里体现 eureka **未实现** CAP 定理中的一致性(C)

**关闭只读缓存可以避免读写缓存注册信息失效的问题。**

**只读缓存可以降低 registry 和 readWriteCacheMap 并发，所以为了性能考虑也可以考虑开启只读缓存，同时缩短同步时间间隔来达到平衡。**

---

```
# eureka 服务列表
eureka.client.defaultZone = http://eureka.peer1.cn:8761/eureka/,http://eureka.peer2.cn:8762/eureka/,http://eureka.peer3.cn:8763/eureka/
```
Client 在发送注册、心跳等请求时，会按顺序向 eureka 服务列表逐个尝试，如果有一个请求成功就返回，最多重试3次，超过3次抛出异常。

但是 Client 还保存了一个黑名单，请求失败会将其 server 放入黑名单中，获取重试列表时会过滤掉黑名单中的 server。

重试方法：com.netflix.discovery.shared.transport.decorator.RetryableEurekaHttpClient.execute()
重试列表：com.netflix.discovery.shared.transport.decorator.RetryableEurekaHttpClient.getHostCandidates()

**一般情况下集群有3个 eureka 就够了，因为最多重试3次。但是为了避免 eureka 服务响应失败进入黑名单，可以多加一台，但是这台在集群正常情况下用不到。**

**eureka 集群正常的情况下，为了避免请求都落到同一个节点，应该打乱 defaultZone 配置顺序。因为请求是按这个顺序依次尝试的。**

```
# 心跳过期时间
lease-expiration-duration-in-seconds = 30
```
一次心跳后超过这个时间没有收到心跳则会将服务剔除，注册服务较少的时候可以缩短这个时间来尽早剔除服务。默认是90秒。

## 客户端配置优化

```
# 心跳时间间隔
eureka.instance.lease-renewal-interval-in-seconds = 5
# 拉取注册注册信息时间间隔
eureka.client.registry-fetch-interval-seconds = 5
```
当注册服务较少的时候可以缩短向 eureka 发送心跳和拉取注册信息的间隔时间，达到尽量避免调用无效服务的目的。

## 功能实现
### 灰度发布
```
eureka.instance.metadata-map
```
注册时配置一些元数据，key-value 格式，可以实现**灰度发布**，比如配置 ABVersion:1，按一定规则将部分流量转发到有此元数据的实例，ribbon 本身自带了这种功能。

网关灰度发布继承 ZuulFilter 实现。
服务间调用灰度发布继承 AbstractLoadBalancerRule 实现。