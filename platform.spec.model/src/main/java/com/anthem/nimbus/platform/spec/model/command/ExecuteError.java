/**
 * 
 */
package com.anthem.nimbus.platform.spec.model.command;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * @author Soham Chakravarti
 *
 */
@Data
public class ExecuteError implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String code;
	
	private String message;
	
	private String label;
	
	private String uniqueId;
	
	public static final String CODE_SUFFIX = ".message";
	
	
	public ExecuteError() {
		
	}
	
	public ExecuteError(String uniqueId, Class<? extends Throwable> exClass, String message) {
		Assert.notNull(uniqueId);
		setUniqueId(uniqueId);
		
		Assert.notNull(exClass);
		setCode(exClass.getName() + CODE_SUFFIX);
		
		setMessage(message);
	}

	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") 
	private LocalDateTime dateTime;
	
}