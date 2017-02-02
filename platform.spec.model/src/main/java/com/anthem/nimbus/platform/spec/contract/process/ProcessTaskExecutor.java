/**
 * 
 */
package com.anthem.nimbus.platform.spec.contract.process;

import com.anthem.nimbus.platform.spec.model.command.CommandMessage;

/**
 * @author Soham Chakravarti
 *
 */
public interface ProcessTaskExecutor {

	/**
	 * 
	 * @param cmdMsg
	 * @return
	 */
	public <R> R doExecute(CommandMessage cmdMsg);

}