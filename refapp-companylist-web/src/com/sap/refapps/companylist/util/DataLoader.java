package com.sap.refapps.companylist.util;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityManager;

import com.sap.refapps.companylist.util.StaxParser;
import com.sap.refapps.companylist.model.Company;
import com.sap.refapps.companylist.model.Employee;

/**
 * Class for inserting data into the JPA Model
 */
public class DataLoader {

	private EntityManagerFactory emf;
	private StaxParser parser;


	/**
	 * to insert data into the jpa model
	 * 
	 */
	public void loadData(){

		this.emf = Utility.getEntityManagerFactory();
		parser = new StaxParser();
		List<Employee> employeeList = parser.readPersons("com/sap/refapps/companylist/resources/Employees.xml");
		List<Company> companyList = parser.readGroups("com/sap/refapps/companylist/resources/Companys.xml");

		EntityManager em = emf.createEntityManager();
		try {

			for(Employee p : employeeList){
				em.getTransaction().begin();
				em.persist(p);
				em.getTransaction().commit();

			}

			for(Company p : companyList){
				em.getTransaction().begin();
				em.persist(p);
				em.getTransaction().commit();

			}

		}
		finally {
			em.close();
		}
	}
}
