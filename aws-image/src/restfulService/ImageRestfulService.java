package restfulService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import service.ImageService;

import Utils.FileUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

//POJO, no interface no extends

//The class registers its methods for the HTTP GET request using the @GET annotation. 
//Using the @Produces annotation, it defines that it can deliver several MIME types,
//text, XML and HTML. 

//The browser requests per default the HTML MIME type.

//Sets the path to base URL + /hello
//@Path("/hello")
@Path("/image")
public class ImageRestfulService {

	private ImageService imageservice;
	
	public ImageRestfulService(){
		try{
			imageservice = new ImageService();
		} catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it " +
                    "to Amazon SQS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered " +
                    "a serious internal problem while trying to communicate with SQS, such as not " +
                    "being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        } catch ( IOException ioex ){
        	ioex.printStackTrace();        	
        }
	}

	// This method is called if TEXT_PLAIN is request
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/hello")
	public String sayPlainTextHello() {
		return "Hello Jersey";
	}

	@POST 
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/hello")
	public Response sayPlainTextHello( @FormParam("name") String name ) throws Exception{
		//return "Hello " + name;
		String result = "Hello " + name + "!";
		return Response.status(201).entity(result).build();
	}
	
	@POST
	@Path("/hello")
	@Consumes(MediaType.TEXT_PLAIN)
	public Response sayPlainTextHelloForClient(String text) { 
		String result = "Hello " + text + "!";
		return Response.status(201).entity(result).build(); 
	}
	

	// This method is called if XML is request
	@GET
	@Produces(MediaType.TEXT_XML)
	public String sayXMLHello() {
		return "<?xml version=\"1.0\"?>" + "<hello> Hello Jersey" + "</hello>";
	}

	// This method is called if HTML is request
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String sayHtmlHello() {
		return "<html> " + "<title>" + "Hello Jersey" + "</title>"
				+ "<body><h1>" + "Hello Jersey" + "</body></h1>" + "</html> ";
	}

	@GET @Path("/getImageUrl/{key}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getImageUrl( @PathParam("key") String key) throws Exception{	
		return imageservice.getImageUrl("kg-images", key);
	}
	
	@GET @Path("/getImage/{bucket}/{key}")
	@Produces("image/png")
	public Response getImage( @PathParam("bucket") String bucket,
			@PathParam("key") String key ) 
					throws Exception{
		File file = new File(key);
		imageservice.getObjectToFile(bucket, key, file);
	
		ResponseBuilder response = Response.ok((Object) file);
		response.header("Content-Disposition","attachment; filename=" + key);
		return response.build();	 
	}
	
	@POST
	@Path("/upload")
	@Consumes({ MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_OCTET_STREAM })
	public Response uploadFile(
		@FormDataParam("file") InputStream uploadedInputStream) {
 
		String uploadedFileLocation = "d://uploaded/alma";
 
		// save it
		writeToFile(uploadedInputStream, uploadedFileLocation);
 
		String output = "File uploaded to : " + uploadedFileLocation;
 
		return Response.status(200).entity(output).build();
 
	}
 
	// save uploaded file to new location
	private void writeToFile(InputStream uploadedInputStream,
		String uploadedFileLocation) {
 
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
	
}