package com.example.sslstoreproviderdemo;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory;
import org.apache.coyote.http11.AbstractHttp11JsseProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.Ssl;
import org.springframework.boot.context.embedded.SslStoreProvider;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
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

        return container -> {
            container.setSslStoreProvider(sslStoreProvider);
            ((TomcatEmbeddedServletContainerFactory) container).addConnectorCustomizers(this::customizeTomcatConnector);
        };
    }

    private void customizeTomcatConnector(Connector connector) {
        Ssl ssl = serverProperties.getSsl();
        AbstractHttp11JsseProtocol<?> protocol = (AbstractHttp11JsseProtocol<?>) connector.getProtocolHandler();
        protocol.setTruststorePass(ssl.getTrustStorePassword());
        protocol.setTruststoreType(ssl.getTrustStoreType());
    }
}
