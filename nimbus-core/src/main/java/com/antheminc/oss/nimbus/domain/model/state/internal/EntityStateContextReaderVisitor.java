/**
 * 
 */
package com.antheminc.oss.nimbus.domain.model.state.internal;

import java.util.HashMap;
import java.util.Map;

import com.antheminc.oss.nimbus.domain.model.state.EntityStateVisitor;
import com.antheminc.oss.nimbus.domain.model.state.internal.DefaultEntityStateContext.ModelStateContext;
import com.antheminc.oss.nimbus.domain.model.state.internal.DefaultEntityStateContext.ParamStateContext;

import lombok.Getter;

/**
 * @author Soham Chakravarti
 *
 */
@Getter
public class EntityStateContextReaderVisitor implements EntityStateVisitor<DefaultModelState<?>, DefaultParamState<?>> {

	private Map<String, DefaultEntityStateContext> contextMap = new HashMap<>();
	
	@Override
	public void visit(DefaultModelState<?> m) {
		ModelStateContext mCtx = m.getStateContext();
		
		DefaultEntityStateContext ctx = contextMap.computeIfAbsent(m.getPath(), c -> new DefaultEntityStateContext());
		ctx.setModel(mCtx);
	}
	
	@Override
	public void visit(DefaultParamState<?> p) {
		ParamStateContext pCtx = p.getStateContext();
		
		DefaultEntityStateContext ctx = contextMap.computeIfAbsent(p.getPath(), c -> new DefaultEntityStateContext());
		ctx.setParam(pCtx);
	}
}
