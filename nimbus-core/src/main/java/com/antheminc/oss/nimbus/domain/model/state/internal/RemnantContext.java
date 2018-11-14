/**
 * 
 */
package com.antheminc.oss.nimbus.domain.model.state.internal;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Soham Chakravarti
 *
 */
@Getter @Setter @ToString(of="currentState")
public class RemnantContext<S> {

	private S previousState;
	private S currentState;
	
	public RemnantContext() {
	}
	
	public RemnantContext(S initState) {
		setPreviousState(initState);
		setCurrentState(initState);
	}
	
	public boolean isEquals(S state) {
		return new EqualsBuilder().append(this.currentState, state).isEquals();
	}
	
	public boolean hasChanged() {
		boolean isEquals = new EqualsBuilder().append(this.currentState, this.previousState).isEquals();
		return !isEquals;
	}
	
	public static class RemnantCollection<S, C extends Collection<S>> extends RemnantContext<C> {
		
		public RemnantCollection(C initState) {
			super(initState);
		}
		
		@Override
		public boolean hasChanged() {
			Collection<S> prev = CollectionUtils.isEmpty(getPreviousState()) ? null : getPreviousState();
			Collection<S> curr = CollectionUtils.isEmpty(getCurrentState()) ? null : getCurrentState();
			
			boolean isEquals = new EqualsBuilder().append(curr, prev).isEquals();
			return !isEquals;
		}
	}
}
