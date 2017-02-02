/**
 * 
 */
package com.anthem.nimbus.platform.core.process.api.domain.event;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.anthem.nimbus.platform.core.process.api.repository.ModelPersistenceHandler;
import com.anthem.nimbus.platform.core.process.api.repository.ModelRepositoryFactory;
import com.anthem.nimbus.platform.spec.contract.event.StateAndConfigEventPublisher;
import com.anthem.nimbus.platform.spec.model.AbstractEvent.PersistenceMode;
import com.anthem.nimbus.platform.spec.model.dsl.ModelEvent;
import com.anthem.nimbus.platform.spec.model.dsl.Repo;
import com.anthem.nimbus.platform.spec.model.dsl.binder.StateAndConfig;
import com.anthem.nimbus.platform.spec.model.dsl.binder.StateAndConfig.Model;
import com.anthem.nimbus.platform.spec.model.dsl.binder.StateAndConfig.Param;
import com.anthem.nimbus.platform.spec.model.dsl.config.Config;
import com.anthem.nimbus.platform.spec.model.exception.InvalidConfigException;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Soham Chakravarti
 *
 */
@Component
@EnableConfigurationProperties
@ConfigurationProperties(exceptionIfInvalid=true,prefix="model.persistence.strategy")
public class ParamStateAtomicPersistenceEventPublisher implements StateAndConfigEventPublisher {

	@Autowired
	ModelRepositoryFactory repoFactory;

	@Getter @Setter
	private PersistenceMode mode;
	
//	@Autowired
//	@Qualifier("rep_mongodb_handler")
//	ModelPersistenceHandler handler;

	@Override
	public boolean shouldAllow(StateAndConfig<?,?> p) {
		if(p instanceof Param<?>) {
			Param<?> param = (Param<?>) p;
			return !param.getConfig().isView() && PersistenceMode.ATOMIC == mode;
		}
		else {
			Model<?,?> param = (Model<?,?>) p;
			return !param.getConfig().isView() && PersistenceMode.ATOMIC == mode;
		}
	}
	
	@Override
	public boolean publish(ModelEvent<StateAndConfig<?,?>> event) {
		List<ModelEvent<StateAndConfig<?,?>>> events = new ArrayList<>();
		events.add(event);
		
		StateAndConfig<?,?> param = event.getPayload();
		
		if(param instanceof Param<?>) {
			Param<?> p = (Param<?>) param;
			Repo repo = p.getRootParent().getConfig().getRepo();
			if(repo==null) {
				throw new InvalidConfigException("Core Persistent entity must be configured with "+Repo.class.getSimpleName()+" annotation. Not found for root model: "+event.getPayload().getRootParent());
			} 
				
			ModelPersistenceHandler handler = repoFactory.getHandler(p.getRootParent().getConfig().getRepo());
			
			if(handler == null) {
				throw new InvalidConfigException("There is no repository handler provided for the configured repository :"+repo.value().name()+ " for root model: "+event.getPayload().getRootParent());
			}
			
			return handler.handle(events);
		}
		else{
			Model<?,?> p = (Model<?,?>) param;
			
			Repo repo = p.getRootParent().getConfig().getRepo();
			if(repo==null) {
				throw new InvalidConfigException("Core Persistent entity must be configured with "+Repo.class.getSimpleName()+" annotation. Not found for root model: "+event.getPayload().getRootParent());
			} 
				
			ModelPersistenceHandler handler = repoFactory.getHandler(p.getRootParent().getConfig().getRepo());
			
			if(handler == null) {
				throw new InvalidConfigException("There is no repository handler provided for the configured repository :"+repo.value().name()+ " for root model: "+event.getPayload().getRootParent());
			}
			
			return handler.handle(events);
		}
		
		
	}

}