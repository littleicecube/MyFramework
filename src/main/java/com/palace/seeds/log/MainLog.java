package com.palace.seeds.log;

import org.apache.log4j.Logger;

public class MainLog {
	private static  Logger log = Logger.getLogger(MainLog.class);
	static Logger userLog = Logger.getLogger("userInfoLog");
	static Logger gLog = Logger.getLogger("gatewayLogger");
	
	public static void main(String[] args) {
		gLog.info("gateway log");
		log.info("logMsg");
		userLog.info("user log");
	}
}
