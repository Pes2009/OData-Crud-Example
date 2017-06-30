package com.sap.refapps.companylist.main;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmEntityContainer;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderException;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntityUriInfo;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAContext;
import org.apache.olingo.odata2.jpa.processor.core.ODataJPAProcessorDefault;




public class CustomODataJPAProcessor extends ODataJPAProcessorDefault {
	public CustomODataJPAProcessor(ODataJPAContext oDataJPAContext){
		super(oDataJPAContext);
		// todo 
	}
	  public static final String HTTP_METHOD_PUT = "PUT";
	  public static final String HTTP_METHOD_POST = "POST";
	  public static final String HTTP_METHOD_GET = "GET";
	  private static final String HTTP_METHOD_DELETE = "DELETE";

	  public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
	  public static final String HTTP_HEADER_ACCEPT = "Accept";

	  public static final String APPLICATION_JSON = "application/json";
	  public static final String APPLICATION_XML = "application/xml";
	  public static final String APPLICATION_ATOM_XML = "application/atom+xml";
	  public static final String APPLICATION_FORM = "application/x-www-form-urlencoded";
	  public static final String METADATA = "$metadata";
	  public static final String INDEX = "/index.jsp";
	  public static final String SEPARATOR = "/";
	  
	  public static final boolean PRINT_RAW_CONTENT = true;
	@Override // get request from entity
	public ODataResponse readEntitySet(final GetEntitySetUriInfo uriParserResultView, final String contentType)
		throws ODataException {
			// pre process step
			System.out.println("Odata pre process step");

			List<Object> jpaEntities = jpaProcessor.process(uriParserResultView);

			// post process step
			System.out.println("Odata post process step");

			ODataResponse oDataResponse = 
			responseBuilder.build(uriParserResultView, jpaEntities, contentType);

			return oDataResponse;
		}
	
	@Override
	public ODataResponse readEntity(final GetEntityUriInfo uriParserResultView, final String contentType)
	throws ODataException {
		// pre process step
		System.out.println("odata pre process step");

		Object jpaEntity = jpaProcessor.process(uriParserResultView);
		System.out.println("odata post process step");

		ODataResponse oDataResponse = 
		responseBuilder.build(uriParserResultView, jpaEntity, contentType);

		return oDataResponse;
	}
	
	// Map<String, Object> data // ...is a method parameter

  
    
    public ODataEntry createEntry(Edm edm, String serviceUri, String contentType, 
    	      String entitySetName, Map<String, Object> data) throws Exception {
    	    String absolutUri = createUri(serviceUri, entitySetName, null);
    	    return writeEntity(edm, absolutUri, entitySetName, data, contentType, HTTP_METHOD_POST);
    	  }
    
	private String createUri(String serviceUri, String entitySetName, String id) {
        final StringBuilder absolutUri = new StringBuilder(serviceUri).append("/").append(entitySetName);
        if(id != null) {
          absolutUri.append("(").append(id).append(")");
        }
        return absolutUri.toString();
      }
	
	 private ODataEntry writeEntity(Edm edm, String absolutUri, String entitySetName, 
		      Map<String, Object> data, String contentType, String httpMethod) 
		      throws EdmException, MalformedURLException, IOException, EntityProviderException, URISyntaxException {

		    HttpURLConnection connection = initializeConnection(absolutUri, contentType, httpMethod);

		    EdmEntityContainer entityContainer = edm.getDefaultEntityContainer();
		    EdmEntitySet entitySet = entityContainer.getEntitySet(entitySetName);
		    URI rootUri = new URI(entitySetName);

		    EntityProviderWriteProperties properties = EntityProviderWriteProperties.serviceRoot(rootUri).build();
		    // serialize data into ODataResponse object
		    ODataResponse response = EntityProvider.writeEntry(contentType, entitySet, data, properties);
		    // get (http) entity which is for default Olingo implementation an InputStream
		    Object entity = response.getEntity();
		    if (entity instanceof InputStream) {
		      byte[] buffer = streamToArray((InputStream) entity);
		      // just for logging
		      String content = new String(buffer);
		      connection.getOutputStream().write(buffer);
		    }

		    // if a entity is created (via POST request) the response body contains the new created entity
		    ODataEntry entry = null;
		    HttpStatusCodes statusCode = HttpStatusCodes.fromStatusCode(connection.getResponseCode());
		    if(statusCode == HttpStatusCodes.CREATED) {
		      // get the content as InputStream and de-serialize it into an ODataEntry object
		      InputStream content = connection.getInputStream();
		      content = logRawContent(httpMethod + " request on uri '" + absolutUri + "' with content:\n  ", content, "\n");
		      entry = EntityProvider.readEntry(contentType,
		          entitySet, content, EntityProviderReadProperties.init().build());
		    }

		    //
		    connection.disconnect();

		    return entry;
		  }
	 
	  private byte[] streamToArray(InputStream stream) throws IOException {
		    byte[] result = new byte[0];
		    byte[] tmp = new byte[8192];
		    int readCount = stream.read(tmp);
		    while(readCount >= 0) {
		      byte[] innerTmp = new byte[result.length + readCount];
		      System.arraycopy(result, 0, innerTmp, 0, result.length);
		      System.arraycopy(tmp, 0, innerTmp, result.length, readCount);
		      result = innerTmp;
		      readCount = stream.read(tmp);
		    }
		    stream.close();
		    return result;
		  }

	  private HttpStatusCodes checkStatus(HttpURLConnection connection) throws IOException {
	    HttpStatusCodes httpStatusCode = HttpStatusCodes.fromStatusCode(connection.getResponseCode());
	    if (400 <= httpStatusCode.getStatusCode() && httpStatusCode.getStatusCode() <= 599) {
	      throw new RuntimeException("Http Connection failed with status " + httpStatusCode.getStatusCode() + " " + httpStatusCode.toString());
	    }
	    return httpStatusCode;
	  }
	  
	  private InputStream logRawContent(String prefix, InputStream content, String postfix) throws IOException {
		    if(PRINT_RAW_CONTENT) {
		      byte[] buffer = streamToArray(content);
		      return new ByteArrayInputStream(buffer);
		    }
		    return content;
		  }

	

	  private String createUri(String serviceUri, String entitySetName, String id, String expand) {
	    final StringBuilder absolutUri = new StringBuilder(serviceUri).append(SEPARATOR).append(entitySetName);
	    if(id != null) {
	      absolutUri.append("(").append(id).append(")");
	    }
	    if(expand != null) {
	      absolutUri.append("/?$expand=").append(expand);
	    }
	    return absolutUri.toString();
	  }

	  private InputStream execute(String relativeUri, String contentType, String httpMethod) throws IOException {
	    HttpURLConnection connection = initializeConnection(relativeUri, contentType, httpMethod);

	    connection.connect();
	    checkStatus(connection);

	    InputStream content = connection.getInputStream();
	    content = logRawContent(httpMethod + " request on uri '" + relativeUri + "' with content:\n  ", content, "\n");
	    return content;
	  }

	  private HttpURLConnection connect(String relativeUri, String contentType, String httpMethod) throws IOException {
	    HttpURLConnection connection = initializeConnection(relativeUri, contentType, httpMethod);

	    connection.connect();
	    checkStatus(connection);

	    return connection;
	  }

	  private HttpURLConnection initializeConnection(String absolutUri, String contentType, String httpMethod)
	      throws MalformedURLException, IOException {
	    URL url = new URL(absolutUri);
	    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

	    connection.setRequestMethod(httpMethod);
	    connection.setRequestProperty(HTTP_HEADER_ACCEPT, contentType);
	    if(HTTP_METHOD_POST.equals(httpMethod) || HTTP_METHOD_PUT.equals(httpMethod)) {
	      connection.setDoOutput(true);
	      connection.setRequestProperty(HTTP_HEADER_CONTENT_TYPE, contentType);
	    }

	    return connection;
	  }
    
    
}
