/**
 * 
 */
package com.anthem.nimbus.platform.spec.model.dsl.binder;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.anthem.nimbus.platform.spec.contract.event.StateAndConfigEventPublisher;
import com.anthem.nimbus.platform.spec.model.AbstractEvent.SuppressMode;
import com.anthem.nimbus.platform.spec.model.dsl.ModelEvent;
import com.anthem.nimbus.platform.spec.model.dsl.binder.StateAndConfig.Param;
import com.anthem.nimbus.platform.spec.model.util.JustLogit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Soham Chakravarti
 *
 */
@Getter @RequiredArgsConstructor
public class QuadScopedEventPublisher implements StateAndConfigEventPublisher {

	private final List<StateAndConfigEventPublisher> paramEventPublishers;
	
	private SuppressMode suppressMode;
	
	private JustLogit logit = new JustLogit(this.getClass());
	
	
	/**
	 * 
	 */
	@Override
	public synchronized void apply(SuppressMode suppressMode) {
		this.suppressMode = suppressMode;
		logit.trace(()->"suppressMode applied: "+suppressMode);
	}
	
	/**
	 * 
	 */
	@Override
	public boolean publish(ModelEvent<StateAndConfig<?,?>> modelEvent) {
		if(CollectionUtils.isEmpty(getParamEventPublishers())) return false;
		
		getParamEventPublishers()
			.stream()
			.filter(e-> !e.shouldSuppress(getSuppressMode()))
			.filter(e-> e.shouldAllow(modelEvent.getPayload()))
			.forEach(e-> e.publish(modelEvent));
		
		return true;
	}
	
}