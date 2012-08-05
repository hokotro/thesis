package com.kadar.image.restful.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import sun.misc.IOUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.kadar.image.aws.handler.FileUtils;
import com.kadar.image.aws.handler.S3Handler;
import com.kadar.image.message.handler.MessageHandler;
import com.kadar.image.message.handler.TaskMessage;
import com.kadar.image.message.handler.TaskMessageType;
import com.sun.jersey.core.header.FormDataContentDisposition;

//import com.amazonaws.AmazonClientException;
//import com.amazonaws.AmazonServiceException;


@Path("/imageservice")
public class ImageService {
	private static final String bucketName = "kg-images";
	private static final String privateBucketName = "private-images";
	private MessageHandler ims;
	private S3Handler s3;
	
	public ImageService() throws IOException {
		ims = new MessageHandler("default-convert-queue");
		s3 = new S3Handler();
	}

	@GET
	@Path("/hello/")
	@Produces(MediaType.TEXT_PLAIN)
	public String Hello() {				
		return "Hello Jersey";
	}
	
	@GET
	@Path("/url/{url}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getUrl(@PathParam("url") String key) throws AmazonServiceException, AmazonClientException, IOException {
		return s3.getObjectURL(bucketName, key);		
	}
	
	private void sendMessageToConvert(String key) throws IOException{
    	ims.sendMessage(key);
	}
	
	@PUT
	@Path("/putFile/{a_filename}")
	@Consumes("application/octet-stream")
	public Response putFile(@Context HttpServletRequest a_request,
	                         @PathParam("a_filename") String a_filename,
	                         InputStream a_fileInputStream) throws Throwable
	{
		//byte[] contentBytes = FileUtils.readBytes(a_fileInputStream);Long contentLength = Long.valueOf(contentBytes.length);
		//s3.putInputStreamToBucket(bucketName, a_filename, a_fileInputStream, contentLength);
		
		File file = writeToTempFile(a_fileInputStream,a_filename);
		int [] size = detectImageSize(file);
		s3.putObjectWithPublicRead(bucketName, a_filename, file );
        
		//System.out.println(size[0] + "x" + size[1]);
		
		MessageHandler mh = new MessageHandler("default-s3-upload-queue");
		TaskMessage tm = new TaskMessage.Builder()
			.setValue(String.valueOf(size[0]) + "x" + String.valueOf(size[1]) ) 			
			.setStartTime(System.currentTimeMillis())
			.setKeyOfImage(a_filename)
			.setMessageType(TaskMessageType.ImageToConvert)
			.build();
		mh.sendMessage(tm);
		
		//writeToFile(a_fileInputStream, "/home/hokotro/workspace/" + a_filename);
		return Response.ok(200).entity("Ok\n").build();
	}
	
	@PUT
	@Path("/putPrivateFile/{a_filename}")
	@Consumes("application/octet-stream")
	public Response putPrivateFile(@Context HttpServletRequest a_request,
	                         @PathParam("a_filename") String a_filename,
	                         InputStream a_fileInputStream) throws Throwable
	{
		File file = writeToTempFile(a_fileInputStream,a_filename);
		s3.putObjectWithPrivateAccess(privateBucketName, a_filename, file );    
		return Response.ok(200).entity("Ok\n").build();
	}
	
	/*
	@Path("/upload")
	@Consumes("multipart/form-data")
	@POST
	public void handleUpload(@FormParam("file") InputStream file) throws Exception {
	// do your thing

		writeToFile(file, "/home/hokotro/workspace/apple.jpg");
	}
	*/

	/*
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
		@FormDataParam("file") InputStream uploadedInputStream,
		@FormDataParam("file") FormDataContentDisposition fileDetail) {
 
		String uploadedFileLocation = "d://uploaded/" + fileDetail.getFileName(); 
		// save it
		writeToFile(uploadedInputStream, uploadedFileLocation); 
		String output = "File uploaded to : " + uploadedFileLocation;
 
		return Response.status(200).entity(output).build();
	}
	*/
	
	// save uploaded file to new location
	private void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) {
	 
			try {
				OutputStream out = new FileOutputStream(new File(
						uploadedFileLocation));
				int read = 0;
				byte[] bytes = new byte[1024];
	 
				out = new FileOutputStream(new File(uploadedFileLocation));
				while ((read = uploadedInputStream.read(bytes)) != -1) {
					out.write(bytes, 0, read);
				}
				out.flush();
				out.close();
			} catch (IOException e) {
	 
				e.printStackTrace();
			}
	 
		}
	
	private File writeToTempFile(InputStream uploadedInputStream,
			String a_filename) throws IOException{

		File file = File.createTempFile(a_filename, "");
        file.deleteOnExit();
		try {
			OutputStream out = new FileOutputStream(file);
			int read = 0;
			byte[] bytes = new byte[1024];
	 
			out = new FileOutputStream(file);
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
			
		} catch (IOException e) {	 
			e.printStackTrace();
		}	 
		return file;
	}
	
	private int[] detectImageSize(File file) throws IOException{
		BufferedImage bimg = ImageIO.read(file);
		int[] size = new int[2];
		size[0] = bimg.getWidth();
		size[1] = bimg.getHeight();
	
		return size;
	}
	
}
