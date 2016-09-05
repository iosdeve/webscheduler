package com.openthinks.webscheduler.model.security;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Reference role key in User definition
 * @author dailey.yet@outlook.com
 *
 */
@XmlRootElement(name = "role")
@XmlAccessorType(XmlAccessType.FIELD)
public class RoleKey implements Serializable {
	private static final long serialVersionUID = -2158189478671124523L;
	@XmlAttribute
	private String id;
	@XmlAttribute
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}