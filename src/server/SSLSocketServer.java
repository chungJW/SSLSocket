package server;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by Johnny on 4/11/16.
 */
public class SSLSocketServer {


    public static void main(String[] args) throws Exception {
        sslSocketServer();
    }

    // 启动一个ssl server socket
    // 配置了证书, 所以不会抛出异常
    public static void sslSocketServer() throws Exception {

        // key store相关信息
        String keyName = "cmkey";
        char[] keyStorePwd = "ar#sp8_".toCharArray();
        char[] keyPwd = "ar#sp8_".toCharArray();
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

        // 装载当前目录下的key store. 可用jdk中的keytool工具生成keystore
        InputStream in = SSLSocketServer.class.getClassLoader().getResourceAsStream(
                keyName);
        keyStore.load(in, keyPwd);
        in.close();

        // 初始化key manager factory
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory
                .getDefaultAlgorithm());
        kmf.init(keyStore, keyPwd);

        // 初始化ssl context
        SSLContext context = SSLContext.getInstance("SSL");
        context.init(kmf.getKeyManagers(),
                new TrustManager[]{new MyX509TrustManager()},
                new SecureRandom());

        // 监听和接收客户端连接
        SSLServerSocketFactory factory = context.getServerSocketFactory();
        SSLServerSocket server = (SSLServerSocket) factory
                .createServerSocket(10010);
        System.out.println("ok");

        while (true) {
            Socket client = server.accept();
            System.out.println(client.getRemoteSocketAddress());

            new Thread(() -> {
                while (true) {
                    try {
                        execute(client);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

//        server.close();
    }

    private static void execute(Socket client) throws Exception {
        if (client == null || client.isClosed()) {
            return;
        }
        // 向客户端发送接收到的字节序列
        OutputStream output = client.getOutputStream();

        // 当一个普通 socket 连接上来, 这里会抛出异常
        // Exception in thread "main" javax.net.ssl.SSLException: Unrecognized
        // SSL message, plaintext connection?
        InputStream input = client.getInputStream();
        byte[] buf = new byte[1024];
        int len = input.read(buf);
        String message = new String(buf, 0, len);
        System.out.println("received: " + message);
        output.write(buf, 0, len);
        output.flush();
        if (message.equals("exit")) {
            output.close();
            input.close();
            client.close();
        }
    }

    public static class MyX509TrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}


