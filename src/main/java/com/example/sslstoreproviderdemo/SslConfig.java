package com.example.sslstoreproviderdemo;

import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.Ssl;
import org.springframework.boot.context.embedded.SslStoreProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SslConfig {
    @Autowired private ServerProperties serverProperties;

    @Bean
    public SslStoreProvider sslStoreProvider() {
        return new InMemorySslStoreProvider(serverProperties.getSsl());
    }

    @Bean
    public EmbeddedServletContainerCustomizer servletContainerCustomizer(SslStoreProvider sslStoreProvider) {
        Ssl ssl = serverProperties.getSsl();
        TomcatURLStreamHandlerFactory.getInstance()
            .addUserFactory(new FixedSslStoreProviderUrlStreamHandlerFactory(sslStoreProvider, ssl.getKeyStorePassword(), ssl.getTrustStorePassword()));

        return container -> container.setSslStoreProvider(sslStoreProvider);
    }
}
