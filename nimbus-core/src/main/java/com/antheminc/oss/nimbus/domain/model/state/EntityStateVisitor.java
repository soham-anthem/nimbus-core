/**
 * 
 */
package com.antheminc.oss.nimbus.domain.model.state;

import com.antheminc.oss.nimbus.domain.model.state.EntityState.Model;
import com.antheminc.oss.nimbus.domain.model.state.EntityState.Param;

/**
 * @author Soham Chakravarti
 *
 */
public interface EntityStateVisitor {

	void visit(Param<?> p);
	
	void visit(Model<?> m);
}
