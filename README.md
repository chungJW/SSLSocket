# SSLSocket
Block SSL Socket Demo

###SSL Socket Demo

src 下面的 cmkey 是SSL证书

Server可以直接运行。

Client需要加上 JVM 参数
    
    -Djavax.net.ssl.trustStore=cmkey
    
Client 输入exit断开与服务的连接



###生成 SSL Key

//Termianl 输入下面的命令
	
	$keytool -genkey -keystore file_name -keyalg rsa -alias SSL

按提示输入相关信息，生成file_name的证书
相关信息需要在代码里面设置。文件名，密码。
