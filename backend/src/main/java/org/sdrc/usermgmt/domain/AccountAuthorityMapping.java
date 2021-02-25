package org.sdrc.usermgmt.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

/**
 * @author Subham Ashish(subham@sdrc.co.in)
 *
 */
@Entity
@Data
public class AccountAuthorityMapping implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -971612580923266621L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "acc_id_fk")
	private Account account;

	@ManyToOne
	@JoinColumn(name = "authority_id_fk")
	private Authority authority;

	public AccountAuthorityMapping() {

	}

	public AccountAuthorityMapping(Integer id) {
		this.id = id;
	}
}
