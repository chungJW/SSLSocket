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


    private OutputStream output = null;
    private InputStream input = null;
    private SSLSocket socket = null;
    private String server_ip = "localhost";
    private int server_port = 10010;
    private int MAX_BUF_SIZE = 1024;

    public void sslSocket2() throws Exception {
        SSLContext context = SSLContext.getInstance("SSL");

        // 初始化
        context.init(null,
                new TrustManager[]{new MyX509TrustManager()},
                new SecureRandom());
        SSLSocketFactory factory = context.getSocketFactory();
        socket = (SSLSocket) factory.createSocket(server_ip, server_port);
        System.out.println("connect server success!");

        output = socket.getOutputStream();
        input = socket.getInputStream();

        output.write("test".getBytes());
        System.out.println("sent: test");
        output.flush();

        byte[] buf = new byte[MAX_BUF_SIZE];
        int len = input.read(buf);
        System.out.println("received:" + new String(buf, 0, len));
    }

    public void sender() {
        new Thread(() -> {
            while (true) {
                Scanner input_text = new Scanner(System.in);
                String message = new String(input_text.next());

                try {
                    if (output != null) {
                        output.write(message.getBytes());
                        output.flush();
                        byte[] buf = new byte[MAX_BUF_SIZE];
                        int len = input.read(buf);
                        String rev_message = new String(buf, 0, len);
                        System.out.println("received:" + rev_message);
                        if (rev_message.equals("exit")) {
                            output.close();
                            output = null;
                            socket.close();
                        }
                    } else {
                        System.out.println("connection lost");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    private class MyX509TrustManager implements X509TrustManager {
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

    public static void main(String[] args) throws Exception {
        SSLSocketClient client = new SSLSocketClient();
        client.sslSocket2();
        client.sender();
    }
}
