package com.example.sslstoreproviderdemo;

import org.springframework.boot.context.embedded.SslStoreProvider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.security.KeyStore;

public class FixedSslStoreProviderUrlStreamHandlerFactory implements URLStreamHandlerFactory {

    private static final String PROTOCOL = "springbootssl";

    private static final String KEY_STORE_PATH = "keyStore";

    static final String KEY_STORE_URL = PROTOCOL + ":" + KEY_STORE_PATH;

    private static final String TRUST_STORE_PATH = "trustStore";

    static final String TRUST_STORE_URL = PROTOCOL + ":" + TRUST_STORE_PATH;

    private final SslStoreProvider sslStoreProvider;
    private final String keyStorePassword;
    private final String trustStorePassword;

    public FixedSslStoreProviderUrlStreamHandlerFactory(SslStoreProvider sslStoreProvider, String keyStorePassword, String trustStorePassword) {
        this.sslStoreProvider = sslStoreProvider;
        this.keyStorePassword = keyStorePassword;
        this.trustStorePassword = trustStorePassword;
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (PROTOCOL.equals(protocol)) {
            return new URLStreamHandler() {
                @Override
                protected URLConnection openConnection(URL url) throws IOException {
                    try {
                        if (KEY_STORE_PATH.equals(url.getPath())) {
                            return new KeyStoreUrlConnection(url, FixedSslStoreProviderUrlStreamHandlerFactory.this.sslStoreProvider.getKeyStore(), keyStorePassword);
                        }
                        if (TRUST_STORE_PATH.equals(url.getPath())) {
                            return new KeyStoreUrlConnection(url, FixedSslStoreProviderUrlStreamHandlerFactory.this.sslStoreProvider.getTrustStore(), trustStorePassword);
                        }
                    }
                    catch (Exception ex) {
                        throw new IOException(ex);
                    }
                    throw new IOException("Invalid path: " + url.getPath());
                }
            };
        }

        return null;
    }

    private static final class KeyStoreUrlConnection extends URLConnection {
        private final KeyStore keyStore;
        private final String password;

        private KeyStoreUrlConnection(URL url, KeyStore keyStore, String password) {
            super(url);
            this.keyStore = keyStore;
            this.password = password;
        }

        @Override
        public void connect() throws IOException {
        }

        @Override
        public InputStream getInputStream() throws IOException {

            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                this.keyStore.store(stream, password.toCharArray());
                return new ByteArrayInputStream(stream.toByteArray());
            }
            catch (Exception ex) {
                throw new IOException(ex);
            }
        }
    }
}

