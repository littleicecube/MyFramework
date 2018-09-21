package com.palace.seeds.base.io;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class MainIO {

	@Test
	public void ioBuffTest() throws Exception{
		ByteBuffer bf = ByteBuffer.allocate(2014*1024);
		byte[] arr = FileUtils.readFileToByteArray(new File(MainIO.class.getClassLoader().getResource("log4j.properties").getPath()));
		bf.put(arr, 0, arr.length);
		System.out.println("res");
	}
	@Test
	public void bufferTest() throws Exception{
		byte[] arr = FileUtils.readFileToByteArray(new File(MainIO.class.getClassLoader().getResource("log4j.properties").getPath()));
		File wf = new File(MainIO.class.getClassLoader().getResource(".").getPath()+File.separator+"data.txt");
		if(!wf.exists()){
			wf.createNewFile();
		}
		new FileOutputStream(wf).write(arr);
		System.out.println("write end");
	}
}
