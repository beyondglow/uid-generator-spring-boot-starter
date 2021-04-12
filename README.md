# uid-generator-spring-boot-starter

#### 介绍
UidGenerator for SpringBoot 百度唯一UID生成器SpringBoot自动化配置

#### 使用说明

1. 导入POM依赖
``` xml
<dependency>
    <groupId>cn.amorou.uid</groupId>
    <artifactId>uid-generator-spring-boot-starter</artifactId>
    <version>最新版本</version>
</dependency>
```

2. 注入UidGenerator对象
默认情况下不需要配置任何参数，直接注入UidGenerator对象即可使用。

``` java
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestApplicationTests {

    @Resource
    private UidGenerator uidGenerator;

    @Test
    public void test() {
        System.out.println(uidGenerator.getUID());
    }

}
```


#### 版本说明

该项目版本与[uid-generator-1.0.0-SNAPSHOT](https://github.com/baidu/uid-generator)版本保持一致

#### 参数说明

|参数|名称|默认值|备注|
|---|---|---|---|
|**uid.worker-id-assigner-impl**|WorkerIdAssigner实现|cn.amorou.uid.worker.SimpleWorkerIdAssigner|可选[cn.amorou.uid.worker.DisposableWorkerIdAssigner(基于数据库),cn.amorou.uid.worker.SimpleWorkerIdAssigner(不基于数据库)]|
|**uid.uid-generator-impl**|UidGenerator实现方式|cn.amorou.uid.impl.DefaultUidGenerator|可选[cn.amorou.uid.impl.DefaultUidGenerator,cn.amorou.uid.impl.CachedUidGenerator]|
|**uid.time-bits**|时间长度|28|可使用时长为以时间基点epochStr为起点，timeBits为增量的时间长度|
|**uid.worker-bits**|机器id|22|最多可支持约2^n次机器启动|
|**uid.seq-bits**|每秒下的并发序列|13|该值越大，每秒支持的并发生成的序列越大|
|**uid.epoch-str**|时间基点|2016-05-20|格式：yyyy-MM-dd，这个值会转换成毫秒的时间戳，用于时间长度起始|
|**uid.boost-power**|RingBuffer size扩容参数|3|可提高UID生成的吞吐量，过高会造成栈溢出|
|**uid.schedule-interval**|RingBuffer填充周期||另外一种RingBuffer填充时机, 在Schedule线程中, 周期性检查填充。一般不使用这个属性，除非使用ID的频次固定。|

#### 官方文档

[UidGenerator](https://github.com/baidu/uid-generator)