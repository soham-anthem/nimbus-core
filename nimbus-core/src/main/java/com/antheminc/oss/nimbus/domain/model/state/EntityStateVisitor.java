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
public interface EntityStateVisitor<M extends Model<?>, P extends Param<?>> {

	void visit(M m);
	
	void visit(P p);
}
