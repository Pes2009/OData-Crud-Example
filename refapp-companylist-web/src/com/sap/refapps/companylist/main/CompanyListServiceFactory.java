package com.sap.refapps.companylist.main;
import javax.persistence.EntityManagerFactory;

import org.apache.olingo.odata2.jpa.processor.api.ODataJPAContext;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAServiceFactory;
import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPARuntimeException;
import com.sap.refapps.companylist.util.Utility;
/**
 * Odata JPA Processor implementation class
 * creating the oDataJPAContext for using SAP Odata library
 */
public class CompanyListServiceFactory extends CustomODataServiceFactory {
	private static final String PERSISTENCE_UNIT_NAME = "refapp-companylist-web";
	@Override
	
	public ODataJPAContext initializeODataJPAContext()
			throws ODataJPARuntimeException {
		ODataJPAContext oDataJPAContext = this.getODataJPAContext();
		try {
			EntityManagerFactory emf = Utility.getEntityManagerFactory();
			oDataJPAContext.setEntityManagerFactory(emf);
			oDataJPAContext.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
			return oDataJPAContext;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
