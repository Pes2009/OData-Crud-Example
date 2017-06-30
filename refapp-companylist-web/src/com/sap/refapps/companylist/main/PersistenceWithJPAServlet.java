package com.sap.refapps.companylist.main;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sap.refapps.companylist.util.DataLoader;
import com.sap.refapps.companylist.util.Utility;


/**
 * Servlet implementation class PersistenceWithJPAServlet
 * Servlet for creating the Persistence Service and the related EnitityManagerFactory instance
 */

public class PersistenceWithJPAServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PersistenceWithJPAServlet.class);


	private DataSource ds;
	private static EntityManagerFactory emf = null;
	private DataLoader datLoader;

	/**
	 * (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 * creating the EntityManagerFactory
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void init() throws ServletException {

		Connection connection = null;
		try {
			InitialContext ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/DefaultDB");
			connection = ds.getConnection();
			Map properties = new HashMap();
			properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, ds);

			emf = Persistence.createEntityManagerFactory("refapp-companylist-web", properties);

			Utility.setEntityManagerFactory(emf);
			datLoader = new DataLoader();
			datLoader.loadData();

		} catch (NamingException e) {
			throw new ServletException(e);
		} catch (SQLException e) {
			LOGGER.error("Could not determine database product.");
			throw new ServletException(e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException x) {
					LOGGER.debug("Unable to close connection.", x);
				}
			}
		}
	}



	/** {@inheritDoc} */
	@Override
	public void destroy() {
		emf.close();
	}

}
