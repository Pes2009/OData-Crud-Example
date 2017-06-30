package com.sap.refapps.companylist.main;


import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.api.edm.provider.EdmProvider;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataSingleProcessor;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAContext;
import org.apache.olingo.odata2.jpa.processor.api.OnJPAWriteContent;
import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPARuntimeException;
import org.apache.olingo.odata2.jpa.processor.api.factory.ODataJPAAccessFactory;
import org.apache.olingo.odata2.jpa.processor.api.factory.ODataJPAFactory;

public abstract class CustomODataServiceFactory extends ODataServiceFactory {
	
	private ODataJPAContext oDataJPAContext;
	private ODataContext oDataContext;
	private boolean setDetailErrors = false;
	private OnJPAWriteContent onJPAWriteContent = null;
	// creates an odata service based on the values set in ...
	@Override
	public final ODataService createService(final ODataContext ctx) throws ODataException {

		 oDataContext = ctx;

		    // Initialize OData JPA Context
		    oDataJPAContext = initializeODataJPAContext();

		    validatePreConditions();

		    ODataJPAFactory factory = ODataJPAFactory.createFactory();
		    ODataJPAAccessFactory accessFactory = factory
		        .getODataJPAAccessFactory();

		    // OData JPA Processor
		    if (oDataJPAContext.getODataContext() == null)
		      oDataJPAContext.setODataContext(ctx);

		    ODataSingleProcessor odataJPAProcessor = accessFactory
		        .createODataProcessor(oDataJPAContext);

		    // OData Entity Data Model Provider based on JPA
		    EdmProvider edmProvider = accessFactory
		        .createJPAEdmProvider(oDataJPAContext);

		    return createODataSingleProcessorService(edmProvider, odataJPAProcessor);
		  }

		  private  void validatePreConditions() throws ODataJPARuntimeException {

		    if (oDataJPAContext.getEntityManagerFactory() == null) {
		      throw ODataJPARuntimeException.throwException(
		          ODataJPARuntimeException.ENTITY_MANAGER_NOT_INITIALIZED,
		          null);
		    }

		  }

		  /**
		   * Implement this method and initialize OData JPA Context. It is mandatory
		   * to set an instance of type {@link javax.persistence.EntityManagerFactory}
		   * into the context. An exception of type
		   * {@link com.sap.core.odata.processor.api.jpa.exception.ODataJPARuntimeException}
		   * is thrown if EntityManagerFactory is not initialized. <br>
		   * <br>
		   * <b>Sample Code:</b> <code>
		   * 	<p>public class JPAReferenceServiceFactory extends ODataJPAServiceFactory{</p>
		   * 	
		   * 	<blockquote>private static final String PUNIT_NAME = "punit";
		   * <br>
		   * public  ODataJPAContext initializeODataJPAContext() { 
		   * <blockquote>ODataJPAContext oDataJPAContext = this.getODataJPAContext();
		   * <br>
		   * EntityManagerFactory emf = Persistence.createEntityManagerFactory(PUNIT_NAME);
		   * <br>
		   * oDataJPAContext.setEntityManagerFactory(emf);
		   * oDataJPAContext.setPersistenceUnitName(PUNIT_NAME);
		   * <br> return oDataJPAContext;</blockquote>
		   * }</blockquote>
		   * } </code>
		   * <p>
		   * 
		   * @return an instance of type
		   *         {@link com.sap.core.odata.processor.api.jpa.ODataJPAContext}
		   * @throws ODataJPARuntimeException
		   */
		  public abstract ODataJPAContext initializeODataJPAContext()
		      throws ODataJPARuntimeException;

		  /**
		   * @return an instance of type {@link ODataJPAContext}
		   * @throws ODataJPARuntimeException
		   */
		  public final  ODataJPAContext getODataJPAContext()
		      throws ODataJPARuntimeException {
		    if (oDataJPAContext == null) {
		      oDataJPAContext = ODataJPAFactory.createFactory()
		          .getODataJPAAccessFactory().createODataJPAContext();
		    }
		    if (oDataContext != null)
		      oDataJPAContext.setODataContext(oDataContext);
		    return oDataJPAContext;

		  }
		}