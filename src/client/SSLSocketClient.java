package client;

import javax.net.ssl.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Scanner;

/**
 * Created by Johnny on 4/11/16.
 */
public class SSLSocketClient {


    public static void main(String[] args) throws Exception {
        sslSocket2();
        sender();
    }


    private static OutputStream output = null;
    private static InputStream input = null;

    public static void sslSocket2() throws Exception {
        SSLContext context = SSLContext.getInstance("SSL");

        // 初始化
        context.init(null,
                new TrustManager[]{new MyX509TrustManager()},
                new SecureRandom());
        SSLSocketFactory factory = context.getSocketFactory();
        SSLSocket s = (SSLSocket) factory.createSocket("localhost", 10010);
        System.out.println("连接服务器成功");

        output = s.getOutputStream();
        input = s.getInputStream();

        output.write("test".getBytes());
        System.out.println("sent: test");
        output.flush();

        byte[] buf = new byte[1024];
        int len = input.read(buf);
        System.out.println("received:" + new String(buf, 0, len));
    }

    public static void sender() {
        new Thread(() -> {
            while (true) {
                Scanner input_text = new Scanner(System.in);
                String message = new String(input_text.next());

                try {
                    if (output != null) {
                        output.write(message.getBytes());
                        output.flush();
                        byte[] buf = new byte[1024];
                        int len = input.read(buf);
                        System.out.println("received:" + new String(buf, 0, len));
                    } else {
                        System.out.println("connection failure");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    private static class MyX509TrustManager implements X509TrustManager {
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
