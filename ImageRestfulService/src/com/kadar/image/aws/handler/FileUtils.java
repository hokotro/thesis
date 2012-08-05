package com.kadar.image.aws.handler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public abstract class FileUtils {

	public static void writeBytes(byte[] content, File destination) throws IOException {
		if (null == content)		throw new IllegalArgumentException("content can't be null.");
		if (null == destination)	throw new IllegalArgumentException("destination can't be null.");
		
		FileOutputStream file_output_stream = new FileOutputStream(destination);
		file_output_stream.write(content);
		file_output_stream.flush();
		file_output_stream.close();

	}
	public static File writeBytes(byte[] content, String filename, String fileExt) throws IOException {
		File destination = File.createTempFile(filename, fileExt);
		destination.deleteOnExit();
        
		if (null == content)		throw new IllegalArgumentException("content can't be null.");
		//if (null == destination)	throw new IllegalArgumentException("destination can't be null.");
		
		FileOutputStream file_output_stream = new FileOutputStream(destination);
		file_output_stream.write(content);
		file_output_stream.flush();
		file_output_stream.close();

		return destination;
	}

	public static byte[] readBytes(File source)
	throws IOException
	{
		if (null == source)	throw new IllegalArgumentException("source can't be null.");

			FileInputStream	file_input_stream = new FileInputStream(source);
			byte[]			content = readBytes(file_input_stream);
			file_input_stream.close();
			return content;
	}
	
	
	private static byte[] readBytes(InputStream inputStream)
	throws IOException
	{
		if (null == inputStream)	throw new IllegalArgumentException("inputStream can't be null.");

		return readStream(inputStream).toByteArray();
	}
	
	private static ByteArrayOutputStream readStream(InputStream inputStream)
	throws IOException
	{
		if (null == inputStream)	throw new IllegalArgumentException("inputStream can't be null.");

			byte[]					buffer = new byte[1024];
			int						return_value = -1;
			ByteArrayOutputStream	output_stream = new ByteArrayOutputStream(buffer.length);

			return_value = inputStream.read(buffer);

			while (-1 != return_value)
			{
				output_stream.write(buffer, 0, return_value);
				return_value = inputStream.read(buffer);
			}

			output_stream.close();

			inputStream.close();

			return output_stream;

	}
}
