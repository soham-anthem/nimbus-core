/**
 * 
 */
package com.antheminc.oss.nimbus.domain.model.state.internal;

import java.util.List;
import java.util.Set;

import com.antheminc.oss.nimbus.domain.defn.extension.ValidateConditional.ValidationGroup;
import com.antheminc.oss.nimbus.domain.model.config.ParamValue;
import com.antheminc.oss.nimbus.domain.model.state.EntityState.Param.LabelState;
import com.antheminc.oss.nimbus.domain.model.state.EntityState.Param.Message;
import com.antheminc.oss.nimbus.domain.model.state.EntityState.Param.StyleState;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Soham Chakravarti
 *
 */
@Getter @Setter @ToString
public class DefaultEntityStateContext {

	@Getter @Setter @ToString
	public static class ParamStateContext {
		private boolean active;
		
		private RemnantContext<Boolean> visibleState;
		
		private RemnantContext<Boolean> enabledState;
		
		private RemnantContext<Class<? extends ValidationGroup>[]> activeValidationGroupsState;
		
		private RemnantContext.RemnantCollection<Message, Set<Message>> messageState;
		
		private RemnantContext.RemnantCollection<LabelState, Set<LabelState>> labelState;
		
		private RemnantContext<StyleState> styleState;
		
		private List<ParamValue> values;
	}
	
	@Getter @Setter @ToString
	public static class ModelStateContext {
		private boolean isNew;
	}
	
	private ParamStateContext param;
	
	private ModelStateContext model;
	
	public ModelStateContext instantiateModelContextIfNull() {
		if(model==null)
			this.model = new ModelStateContext();
		
		return model;
	}
	
	public ParamStateContext instantiateParamContextIfNull() {
		if(param==null)
			this.param = new ParamStateContext();
		
		return param;
	}
}
