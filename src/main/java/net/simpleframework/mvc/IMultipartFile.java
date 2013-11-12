package net.simpleframework.mvc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public interface IMultipartFile {
	/**
	 * 获取上传文件的原始文件名
	 * 
	 * @return
	 */
	String getOriginalFilename();

	/**
	 * 获取文件对象
	 * 
	 * @return
	 */
	File getFile();

	/**
	 * 获取文件流
	 * 
	 * @return
	 * @throws IOException
	 */
	InputStream getInputStream() throws IOException;

	/**
	 * 获取文件字节数组
	 * 
	 * @return
	 * @throws IOException
	 */
	byte[] getBytes() throws IOException;

	/**
	 * 获取文件的大小
	 * 
	 * @return
	 */
	long getSize();

	/**
	 * 拷贝到指定文件
	 * 
	 * @param file
	 * @throws IOException
	 */
	void transferTo(File file) throws IOException;
}
