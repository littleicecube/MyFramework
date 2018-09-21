package com.palace.seeds.ssl;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class MainSSL {

	
	public static void main(String[] args) throws Exception {
		
		String serverKeyStoreFile = "c:\\_tmp\\catserver.keystore";  
        String serverKeyStorePwd = "catserverks";  
        String catServerKeyPwd = "catserver";  
  
        KeyStore serverKeyStore = KeyStore.getInstance("JKS");  
        serverKeyStore.load(new FileInputStream(serverKeyStoreFile), serverKeyStorePwd.toCharArray());  
  
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());  
        kmf.init(serverKeyStore, catServerKeyPwd.toCharArray());  
  
        SSLContext sslContext = SSLContext.getInstance("TLSv1");  
        sslContext.init(kmf.getKeyManagers(), null, null);  
  
        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();  
        SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(123);  
        sslServerSocket.setNeedClientAuth(false);  
  
        while (true) {  
            SSLSocket s = (SSLSocket)sslServerSocket.accept();  
        }  
		
	}
}
