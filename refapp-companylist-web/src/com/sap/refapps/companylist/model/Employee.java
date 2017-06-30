package com.sap.refapps.companylist.model;


import javax.persistence.NamedQuery;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@NamedQuery(name = "AllEmployees", query = "select p from Employee p")
public class Employee  {

	

	public Employee() {
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private int id;
	private String firstName;
	private String lastName;
	private String companyId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String param) {
		this.firstName = param;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String param) {
		this.lastName = param;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String param) {
		this.companyId = param;
	}

}