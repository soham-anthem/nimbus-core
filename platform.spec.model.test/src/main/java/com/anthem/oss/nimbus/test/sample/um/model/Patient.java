/**
 * 
 */
package com.anthem.oss.nimbus.test.sample.um.model;

import java.time.LocalDate;

import com.anthem.oss.nimbus.core.domain.command.Action;
import com.anthem.oss.nimbus.core.domain.definition.Domain;
import com.anthem.oss.nimbus.core.domain.definition.Execution;
import com.anthem.oss.nimbus.core.domain.definition.Repo;
import com.anthem.oss.nimbus.core.domain.definition.ConfigNature.Ignore;
import com.anthem.oss.nimbus.core.domain.definition.Repo.Database;
import com.anthem.oss.nimbus.core.entity.AbstractEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Soham Chakravarti
 *
 */
@Getter @Setter
@Domain("patient") 
@Repo(Database.rep_mongodb)
@Execution.Input.Default @Execution.Output.Default  @Execution.Output(Action._delete)
@ToString
public class Patient extends AbstractEntity.IdString {
    
	@Ignore
	private static final long serialVersionUID = 1L;

	private String subscriberId;

	private String firstName;

	private String lastName;

	private LocalDate dob;
	
}