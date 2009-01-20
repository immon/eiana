package org.iana.rzm.web.common.ssl;

import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;

/**
 * @author Piotr Tkaczyk
 */
public class SSLSocketFactory implements ProtocolSocketFactory {


    private javax.net.ssl.SSLSocketFactory sslSocketFactory;

    public SSLSocketFactory(String keyStoreLocation, String keyStorePassword, String keyStoreType,
                          String trustedStoreLocation, String trustedStorePassword, String trustedStoreType,
                          String sslContext) throws Exception {

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");

        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        InputStream keyInput = new FileInputStream(keyStoreLocation);
        keyStore.load(keyInput, keyStorePassword.toCharArray());
        keyInput.close();

        keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());


        KeyStore trustedStore = KeyStore.getInstance(trustedStoreType);
        InputStream trustedInput = new FileInputStream(trustedStoreLocation);
        trustedStore.load(trustedInput, trustedStorePassword.toCharArray());
        trustedInput.close();

        String algorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(algorithm);
        trustManagerFactory.init(trustedStore);
        TrustManager[] trustMgr = trustManagerFactory.getTrustManagers();

        SSLContext sslc = SSLContext.getInstance(sslContext);
        sslc.init(keyManagerFactory.getKeyManagers(), trustMgr, new SecureRandom());

        sslSocketFactory = sslc.getSocketFactory();
    }

    public Socket createSocket(String string, int i, InetAddress inetAddress, int i1)
            throws IOException {

        return sslSocketFactory.createSocket(string, i,inetAddress, i1);
    }

    public Socket createSocket(String string, int i, InetAddress inetAddress, int i1, HttpConnectionParams httpConnectionParams)
            throws IOException {

        if (httpConnectionParams == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        int timeout = httpConnectionParams.getConnectionTimeout();
        if (timeout != 0) {
            throw new IllegalArgumentException("Cannot create SSL socket without 0 timeout value");
        }

        return sslSocketFactory.createSocket(string, i,inetAddress, i1);
    }

    public Socket createSocket(String string, int i) throws IOException {
        return sslSocketFactory.createSocket(string, i);
    }
}
