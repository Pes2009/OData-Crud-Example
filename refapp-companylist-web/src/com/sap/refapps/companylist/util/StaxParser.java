package com.sap.refapps.companylist.util;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.sap.refapps.companylist.model.Company;
import com.sap.refapps.companylist.model.Employee;


/**
 * Class for reading the xml model files using Java STAX API
 */
public class StaxParser {
	static final String EMPLOYEE = "Employee";
	static final String FIRSTNAME = "FirstName";
	static final String LASTNAME = "LastName";
	static final String COMPANYID = "CompanyId";
	static final String COMPANY = "Company";
	static final String COMPANYNAME = "CompanyName";

	/**
	 * parse the companylist from the xml file and 
	 * add it to a list
	 * @param configFile the location to the xml file
	 */
	@SuppressWarnings({ "unchecked", "null" })
	public List<Company> readGroups(String configFile) {
		List<Company> groups = new ArrayList<Company>();

		try {
			// First create a new XMLInputFactory
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();

			// InputStream in = StaxParser.class.getResourceAsStream(configFile);
			InputStream in =StaxParser.class.getClassLoader().getResourceAsStream(configFile);
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			// Read the XML document
			Company group = null;

			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();

				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();
					// If we have a item element we create a new item

					if (startElement.getName().getLocalPart() == (COMPANY)) {
						group = new Company();
					}

					if (event.isStartElement()) {

						if (event.asStartElement().getName().getLocalPart()
								.equals(COMPANYNAME)) {
							event = eventReader.nextEvent();
							group.setCompanyName(event.asCharacters().getData());
							continue;
						}
					}
				}
				// If we reach the end of an item element we add it to the list
				if (event.isEndElement()) {
					EndElement endElement = event.asEndElement();

					if (endElement.getName().getLocalPart() == (COMPANY)) {
						groups.add(group);
					}
				}

			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		return groups;
	}

	/**
	 * parse the employeelist from the xml file and 
	 * add it to a list
	 * @param configFile the location to the xml file
	 */
	@SuppressWarnings({ "unchecked", "null" })
	public List<Employee> readPersons(String configFile) {
		List<Employee> persons = new ArrayList<Employee>();

		try {
			// First create a new XMLInputFactory
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			// Setup a new eventReader

			InputStream in =StaxParser.class.getClassLoader().getResourceAsStream(configFile);
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			// Read the XML document
			Employee person = null;

			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();

				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();
					// If we have a item element we create a new item

					if (startElement.getName().getLocalPart() == (EMPLOYEE)) {
						person = new Employee();
					}

					if (event.isStartElement()) {
						System.out.println(event.asStartElement().getName().getLocalPart());
						if (event.asStartElement().getName().getLocalPart()
								.equals(FIRSTNAME)) {
							event = eventReader.nextEvent();
							person.setFirstName(event.asCharacters().getData());
							continue;
						}

						if (event.asStartElement().getName().getLocalPart()
								.equals(LASTNAME)) {
							event = eventReader.nextEvent();
							person.setLastName(event.asCharacters().getData());
							continue;
						}
						if (event.asStartElement().getName().getLocalPart()
								.equals(COMPANYID)) {
							event = eventReader.nextEvent();
							person.setCompanyId(event.asCharacters().getData());
							continue;
						}
					}
				}
				// If we reach the end of an item element we add it to the list
				if (event.isEndElement()) {
					EndElement endElement = event.asEndElement();

					if (endElement.getName().getLocalPart() == (EMPLOYEE)) {
						persons.add(person);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return persons;
	}

} 