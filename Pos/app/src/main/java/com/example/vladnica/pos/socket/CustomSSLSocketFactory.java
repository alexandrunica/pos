package com.example.vladnica.pos.socket;

import android.content.Context;
import android.support.annotation.RawRes;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by Vlad on 10/18/2018.
 */
public final class CustomSSLSocketFactory {

    private CustomSSLSocketFactory() {
        throw new AssertionError();
    }

    /**
     * Creates an SSLSocketFactory instance for use with the CA provided in the resource file.
     *
     * @param context 	Context used to open up the CA file
     * @param caRawFile	Raw resource file to the CA (in .crt or .cer format, for instance)
     * @return An SSLSocketFactory which trusts the provided CA when provided to network clients
     */
    public static SSLSocketFactory create(Context context, @RawRes int caRawFile) {
        InputStream caInput = null;
        try {
            // Generate the CA Certificate from the raw resource file
            caInput = context.getResources().openRawResource(caRawFile);
            Certificate ca = CertificateFactory.getInstance("X.509").generateCertificate(caInput);

            // Load the key store using the CA
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Initialize the TrustManager with this CA
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            // Create an SSL context that uses the created trust manager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());
            return sslContext.getSocketFactory();

        } catch (Exception ex) {
            throw new RuntimeException(ex);

        } finally {
            if (caInput != null) {
                try {
                    caInput.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
