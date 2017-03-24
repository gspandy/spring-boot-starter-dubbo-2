package me.longyuan.boot.starter.dubbo.configuration;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.*;
import com.alibaba.dubbo.config.spring.AnnotationBean;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * dubbo 自动配置
 */
@Configuration
@ConditionalOnProperty(prefix="dubbo",name = "enabled" , havingValue = "true")
@ConditionalOnClass({AnnotationBean.class,ApplicationConfig.class,ProtocolConfig.class,RegistryConfig.class})
public class DubboAutoConfiguration {


    @Bean
    @ConditionalOnProperty(prefix="dubbo",name = "packageNames")
    @ConditionalOnMissingBean(AnnotationBean.class)
    public static AnnotationBean dubboAnnotationBean(@Value("${dubbo.packageNames}")String packageNames) {
        AnnotationBean annotationBean = new AnnotationBean();
        annotationBean.setPackage(packageNames);
        return annotationBean;
    }

    @Bean(name = "dubboApplication")
    @ConditionalOnProperty(prefix="dubbo.application", name = "enabled", havingValue = "true")
    @ConfigurationProperties(prefix = "dubbo.application")
    public DubboApplication dubboApplication(){
        return new DubboApplication();
    }

    @Bean
    @ConditionalOnProperty(prefix="dubbo.registry", name = "enabled", havingValue = "true")
    @ConfigurationProperties(prefix = "dubbo.registry")
    public DubboRegistry dubboRegistry(){
        return new DubboRegistry();
    }

    @Bean(name = "dubboProtocol")
    @ConditionalOnProperty(prefix="dubbo.protocol.dubbo", name = "enabled", havingValue = "true")
    @ConfigurationProperties(prefix = "dubbo.protocol.dubbo")
    public DubboProtocol dubboProtocol(){
        return new DubboProtocol();
    }

    @Bean
    @ConditionalOnProperty(prefix="dubbo.provider", name = "enabled", havingValue = "true")
    @ConfigurationProperties(prefix = "dubbo.provider")
    public DubboProvider dubboProvider(){
        return new DubboProvider();
    }

    @Bean
    @ConditionalOnProperty(prefix="dubbo.consumer", name = "enabled", havingValue = "true")
    @ConfigurationProperties(prefix = "dubbo.consumer")
    public DubboConsumer dubboConsumer(){
        return new DubboConsumer();
    }

    @Bean
    @ConditionalOnMissingBean(ApplicationConfig.class)
    @ConditionalOnBean(DubboApplication.class)
    public ApplicationConfig applicationConfig(DubboApplication dubboApplication) {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(dubboApplication.name);
        applicationConfig.setOwner(dubboApplication.owner);
        applicationConfig.setLogger(dubboApplication.logger);
        return applicationConfig;
    }

    @Bean
    @ConditionalOnMissingBean(RegistryConfig.class)
    @ConditionalOnBean(DubboRegistry.class)
    public RegistryConfig registryConfig(DubboRegistry dubboRegistry) {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(dubboRegistry.address);
        registryConfig.setTimeout(dubboRegistry.timeout);
        if (StringUtils.isNotEmpty(dubboRegistry.username))
            registryConfig.setUsername(dubboRegistry.username);
        if (StringUtils.isNotEmpty(dubboRegistry.password))
            registryConfig.setPassword(dubboRegistry.password);
        return registryConfig;
    }


    @Bean
    @ConditionalOnBean( name = "dubboProtocol")
    @ConditionalOnMissingBean(ProtocolConfig.class)
    public ProtocolConfig dubboProtocolConfig(@Qualifier("dubboProtocol") DubboProtocol dubboDubboProtocol) {
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName(dubboDubboProtocol.name);
        protocolConfig.setHost(dubboDubboProtocol.host);
        protocolConfig.setPort(dubboDubboProtocol.port);
        protocolConfig.setThreads(dubboDubboProtocol.threads);
        return protocolConfig;
    }

    @Bean
    @ConditionalOnMissingBean(ProviderConfig.class)
    @ConditionalOnBean(DubboProvider.class)
    public ProviderConfig providerConfig(DubboProvider dubboProvider) {
        ProviderConfig providerConfig = new ProviderConfig();
        providerConfig.setTimeout(dubboProvider.timeout);
        providerConfig.setDelay(dubboProvider.delay);

        if (StringUtils.isNotEmpty(dubboProvider.accesslog)) {
            if (dubboProvider.accesslog.equalsIgnoreCase("true") || dubboProvider.accesslog.equalsIgnoreCase("false")) {
                providerConfig.setAccesslog(Boolean.parseBoolean(dubboProvider.accesslog.toLowerCase()));
            } else {
                providerConfig.setAccesslog(dubboProvider.accesslog);
            }
        }

        return providerConfig;
    }

    @Bean
    @ConditionalOnMissingBean(ConsumerConfig.class)
    @ConditionalOnBean(DubboConsumer.class)
    public ConsumerConfig consumerConfig(DubboConsumer dubboConsumer) {
        ConsumerConfig consumerConfig = new ConsumerConfig();
        return consumerConfig;
    }

    @Setter
    static abstract class DubboConfigEnable {
        boolean enabled;
    }

    // http://dubbo.io/User+Guide-zh.htm#UserGuide-zh-%253Cdubbo%253Aapplication%252F%253E
    @Setter
    //@ConfigurationProperties(prefix = "dubbo.application")
    public static class DubboApplication extends DubboConfigEnable {
        String name;
        String owner;
        String logger;
    }

    // http://dubbo.io/User+Guide-zh.htm#UserGuide-zh-%253Cdubbo%253Aregistry%252F%253E
    @Setter
    public static class DubboRegistry  extends DubboConfigEnable  {
        String address;
        String username;
        String password;
        int timeout;
    }

    // http://dubbo.io/User+Guide-zh.htm#UserGuide-zh-%253Cdubbo%253Aprotocol%252F%253E
    @Setter
    public static class DubboProtocol  extends DubboConfigEnable {
        String name;
        String host;
        int port = 20880;
        int threads = 100;
    }

    // http://dubbo.io/User+Guide-zh.htm#UserGuide-zh-%253Cdubbo%253Aprovider%252F%253E
    @Setter
    public static class DubboProvider  extends DubboConfigEnable {
        int timeout;
        int delay;
        String accesslog;
    }

    // http://dubbo.io/User+Guide-zh.htm#UserGuide-zh-%253Cdubbo%253Aconsumer%252F%253E
    @Setter
    public static class DubboConsumer  extends DubboConfigEnable {
        int timeout;
        String accesslog;
    }

}