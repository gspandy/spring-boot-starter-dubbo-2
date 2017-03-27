package me.longyuan.boot.starter.dubbo.configuration;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.*;
import com.alibaba.dubbo.config.spring.AnnotationBean;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * dubbo 自动配置
 */
@Configuration
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
@ConditionalOnProperty(prefix="dubbo",name = "enabled" , havingValue = "true")
@ConditionalOnClass({AnnotationBean.class,ApplicationConfig.class,ProtocolConfig.class,RegistryConfig.class})
public class DubboAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DubboAutoConfiguration.class);

    @Bean
    @ConditionalOnProperty(prefix="dubbo",name = "packageNames")
    //@ConditionalOnMissingBean(AnnotationBean.class)
    public static AnnotationBean dubboAnnotationBean(@Value("${dubbo.packageNames}")String packageNames) {
        LOGGER.info("dubboAnnotationBean init: "+packageNames);
        AnnotationBean annotationBean = new AnnotationBean();
        annotationBean.setPackage(packageNames);
        return annotationBean;
    }

    @Bean(name = "dubboApplication")
    @ConditionalOnProperty(prefix="dubbo.application", name = "enabled", havingValue = "true")
    @ConfigurationProperties(prefix = "dubbo.application")
   // @Order(1)
    public DubboApplication dubboApplication(){
        return new DubboApplication();
    }

    @Bean
    @ConditionalOnProperty(prefix="dubbo.registry", name = "enabled", havingValue = "true")
    @ConfigurationProperties(prefix = "dubbo.registry")
   // @Order(1)
    public DubboRegistry dubboRegistry(){
        return new DubboRegistry();
    }

    @Bean(name = "dubboDubboProtocol")
    //@ConditionalOnProperty(prefix="dubbo.protocol.dubbo", name = "enabled", havingValue = "true")
    @ConfigurationProperties(prefix = "dubbo.protocol.dubbo")
   // @Order(1)
    public DubboProtocol dubboDubboProtocol(){
        return new DubboProtocol();
    }

    @Bean
    @ConditionalOnProperty(prefix="dubbo.provider", name = "enabled", havingValue = "true" ,matchIfMissing = true)
    @ConfigurationProperties(prefix = "dubbo.provider")
   // @Order(1)
    public DubboProvider dubboProvider(){
        return new DubboProvider();
    }

    @Bean
    @ConditionalOnProperty(prefix="dubbo.consumer", name = "enabled", havingValue = "true" ,matchIfMissing = true)
    @ConfigurationProperties(prefix = "dubbo.consumer")
   // @Order(1)
    public DubboConsumer dubboConsumer(){
        return new DubboConsumer();
    }

    @Bean
    @ConditionalOnBean(DubboApplication.class)
    //@ConditionalOnMissingBean(ApplicationConfig.class)
   // @Order(2)
    public ApplicationConfig applicationConfig(DubboApplication dubboApplication) {
        LOGGER.info("applicationConfig init. "+dubboApplication.toString());
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(dubboApplication.name);
        applicationConfig.setOwner(dubboApplication.owner);
        applicationConfig.setLogger(dubboApplication.logger);
        return applicationConfig;
    }

    @Bean
    @ConditionalOnBean({DubboRegistry.class,ApplicationConfig.class})
    //@ConditionalOnMissingBean(RegistryConfig.class)
   // @Order(2)
    public RegistryConfig registryConfig(DubboRegistry dubboRegistry) {
        LOGGER.info("registryConfig init. "+dubboRegistry.toString());
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(dubboRegistry.address);
        registryConfig.setTimeout(dubboRegistry.timeout);
        if (StringUtils.isNotEmpty(dubboRegistry.username))
            registryConfig.setUsername(dubboRegistry.username);
        if (StringUtils.isNotEmpty(dubboRegistry.password))
            registryConfig.setPassword(dubboRegistry.password);
        return registryConfig;
    }


    @Bean(name = "dubboProtocolConfig")
    //@ConditionalOnBean(name = {"dubboDubboProtocol"},value = {RegistryConfig.class})
    @ConditionalOnBean(value = {RegistryConfig.class,DubboProtocol.class})
    //@ConditionalOnMissingBean(ProtocolConfig.class)
   // @Order(3)
    public ProtocolConfig dubboProtocolConfig(@Qualifier("dubboDubboProtocol") DubboProtocol dubboDubboProtocol) {
        LOGGER.info("dubboProtocolConfig init. "+dubboDubboProtocol.toString());
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName(dubboDubboProtocol.name);
        protocolConfig.setHost(dubboDubboProtocol.host);
        protocolConfig.setPort(dubboDubboProtocol.port);
        protocolConfig.setThreads(dubboDubboProtocol.threads);
        return protocolConfig;
    }

    @Bean
    @ConditionalOnBean({DubboProvider.class,ProtocolConfig.class})
    //@ConditionalOnMissingBean(ProviderConfig.class)
   // @Order(4)
    public ProviderConfig providerConfig(DubboProvider dubboProvider) {
        LOGGER.info("providerConfig init. "+dubboProvider.toString());

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
    @ConditionalOnBean({DubboConsumer.class,ProtocolConfig.class})
    //@ConditionalOnMissingBean(ConsumerConfig.class)
   // @Order(4)
    public ConsumerConfig consumerConfig(DubboConsumer dubboConsumer) {
        LOGGER.info("consumerConfig init. "+dubboConsumer.toString());
        ConsumerConfig consumerConfig = new ConsumerConfig();
        consumerConfig.setTimeout(dubboConsumer.timeout);
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

        @Override
        public String toString() {
            return "DubboApplication{" +
                    "enabled=" + enabled +
                    ", name='" + name + '\'' +
                    ", owner='" + owner + '\'' +
                    ", logger='" + logger + '\'' +
                    '}';
        }
    }

    // http://dubbo.io/User+Guide-zh.htm#UserGuide-zh-%253Cdubbo%253Aregistry%252F%253E
    @Setter
    public static class DubboRegistry  extends DubboConfigEnable  {
        String address;
        String username;
        String password;
        int timeout;

        @Override
        public String toString() {
            return "DubboRegistry{" +
                    "enabled=" + enabled +
                    ", address='" + address + '\'' +
                    ", username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    ", timeout=" + timeout +
                    '}';
        }
    }

    // http://dubbo.io/User+Guide-zh.htm#UserGuide-zh-%253Cdubbo%253Aprotocol%252F%253E
    @Setter
    public static class DubboProtocol  extends DubboConfigEnable {
        String name;
        String host;
        int port = 20880;
        int threads = 100;

        @Override
        public String toString() {
            return "DubboProtocol{" +
                    "enabled=" + enabled +
                    ", name='" + name + '\'' +
                    ", host='" + host + '\'' +
                    ", port=" + port +
                    ", threads=" + threads +
                    '}';
        }
    }

    // http://dubbo.io/User+Guide-zh.htm#UserGuide-zh-%253Cdubbo%253Aprovider%252F%253E
    @Setter
    public static class DubboProvider  extends DubboConfigEnable {
        int timeout;
        int delay;
        String accesslog;

        @Override
        public String toString() {
            return "DubboProvider{" +
                    "enabled=" + enabled +
                    ", timeout=" + timeout +
                    ", delay=" + delay +
                    ", accesslog='" + accesslog + '\'' +
                    '}';
        }
    }

    // http://dubbo.io/User+Guide-zh.htm#UserGuide-zh-%253Cdubbo%253Aconsumer%252F%253E
    @Setter
    public static class DubboConsumer  extends DubboConfigEnable {
        int timeout;
        String accesslog;

        @Override
        public String toString() {
            return "DubboConsumer{" +
                    "enabled=" + enabled +
                    ", timeout=" + timeout +
                    ", accesslog='" + accesslog + '\'' +
                    '}';
        }
    }


}