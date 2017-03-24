
# dubbo 配置
# 全局 dubbo 开启
dubbo.enabled=true
# dubbo 扫描包配置
dubbo.packageNames=com.longyuan.dubbo.xxx
# 是否开启dubbo application 自动配置
dubbo.application.enabled=true
# 当前应用名称，用于注册中心计算应用间依赖关系
dubbo.application.name=dubbo-demo-app
# 应用负责人，用于服务治理，请填写负责人公司邮箱前缀
dubbo.application.owner=longyuan
dubbo.application.logger=slf4j

# 是否开启dubbo protocol 自动配置
dubbo.protocol.dubbo.enabled=true
dubbo.protocol.dubbo.name=dubbo
dubbo.protocol.dubbo.host=127.0.0.1
dubbo.protocol.dubbo.port=20883
# 服务线程池大小(固定大小)
dubbo.protocol.dubbo.threads=100

# 是否开启dubbo 注册中心 自动配置
dubbo.registry.enabled=true
dubbo.registry.client=curator
dubbo.registry.address=zookeeper://127.0.0.1:2181
#  ?client=${dubbo.registry.client}
# 登录注册中心用户名，如果注册中心不需要验证可不填
dubbo.registry.username=
# 登录注册中心密码，如果注册中心不需要验证可不填
dubbo.registry.password=
# 注册中心会话超时时间(毫秒)，用于检测提供者非正常断线后的脏数据，比如用心跳检测的实现，此时间就是心跳间隔，不同注册中心实现不一样。
dubbo.registry.timeout=1500

# 是否开启dubbo 服务提供者 自动配置
dubbo.provider.enabled=true
dubbo.provider.timeout=120000
# 延迟注册服务时间(毫秒) ，设为-1时，表示延迟到Spring容器初始化完成时暴露服务
dubbo.provider.delay=-1
# 记录每一次请求信息，可开启访问日志
dubbo.provider.accesslog=true

# 是否开启dubbo 服务消费者 自动配置
dubbo.consumer.enabled=true
dubbo.consumer.timeout=120000
dubbo.consumer.accesslog=true