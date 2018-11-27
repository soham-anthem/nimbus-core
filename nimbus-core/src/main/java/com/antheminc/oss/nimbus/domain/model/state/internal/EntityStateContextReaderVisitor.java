/**
 * 
 */
package com.antheminc.oss.nimbus.domain.model.state.internal;

import com.antheminc.oss.nimbus.domain.model.state.EntityState.Model;
import com.antheminc.oss.nimbus.domain.model.state.EntityState.Param;
import com.antheminc.oss.nimbus.domain.model.state.EntityStateVisitor;

/**
 * @author Soham Chakravarti
 *
 */
public class EntityStateContextReaderVisitor implements EntityStateVisitor {
	
	
	@Override
	public void visit(Model<?> m) {
		
	}
	
	@Override
	public void visit(Param<?> p) {
		
	}
}
