package com.sap.refapps.companylist.model;


import javax.persistence.NamedQuery;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;


@Entity
@Table(name = "Company")
@NamedQuery(name = "AllCompanies", query = "select p from Company p")
public class Company {

	

	public Company() {
		super();
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private int id;
	private String companyName;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String param) {
		this.companyName = param;
	}

}