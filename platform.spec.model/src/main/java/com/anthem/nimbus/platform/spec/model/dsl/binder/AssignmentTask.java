/**
 * 
 */
package com.anthem.nimbus.platform.spec.model.dsl.binder;

import java.util.Date;

import com.anthem.nimbus.platform.spec.model.AbstractModel;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Jayant Chaudhuri
 *
 */
@Getter @Setter
abstract public class AssignmentTask extends AbstractModel.IdString{
	
	private static final long serialVersionUID = 1L;
	
	private String taskId; //TODO change the name to bpmn id so that id is used from abstractmodel.IdString
	
	private String taskName;
	
	private String status;
	
	// (e.g. patientEnrollmentTask... so this will help us avoid check for instance of, abstract method)
	private String taskType;
	
	private Date dueDate;
	
	private String priority;
	
	abstract public void setEntity(Object entity);
	
	private String queueCode;
	
	private TaskStatus internalStatus;
	
	public enum TaskStatus{
		IN_PROGRESS,	
		COMPLETED	
	}
	
	
	
	
}
