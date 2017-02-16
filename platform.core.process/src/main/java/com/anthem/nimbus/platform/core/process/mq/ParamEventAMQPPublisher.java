package com.anthem.nimbus.platform.core.process.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.anthem.oss.nimbus.core.domain.command.execution.CommandTransactionInterceptor;
import com.anthem.oss.nimbus.core.domain.command.execution.ExecuteOutput;
import com.anthem.oss.nimbus.core.domain.command.execution.MultiExecuteOutput;
import com.anthem.oss.nimbus.core.domain.model.state.EntityState.Model;
import com.anthem.oss.nimbus.core.domain.model.state.EntityState.Param;
import com.anthem.oss.nimbus.core.domain.model.state.ModelEvent;
import com.anthem.oss.nimbus.core.domain.model.state.internal.AbstractEvent.SuppressMode;

/**
 * @author Rakesh Patel
 * 
 */

@Component
public class ParamEventAMQPPublisher { //implements StateAndConfigEventPublisher {

//	@Autowired
//	SimpMessagingTemplate messageTemplate;
//	
//	@Autowired CommandTransactionInterceptor interceptor;
//	
//	@Override
//	public boolean shouldSuppress(SuppressMode mode) {
//		return SuppressMode.ECHO == mode;
//	}	
//	
//	@Override
//	public boolean shouldAllow(StateAndConfig<?,?> p) {
//		if(p instanceof Param<?>) {
//			Param<?> param = (Param<?>) p;
//			return param.getConfig().isView();
//		}
//		else {
//			Model<?,?> param = (Model<?,?>) p;
//			return param.getConfig().isView();
//		}
//	}
//	
//	@Override
//	public boolean publish(ModelEvent<StateAndConfig<?,?>> modelEvent) {
//		StateAndConfig<?,?>  p = modelEvent.getPayload();
//		
//		//TODO temp impl for GRID collection handling
////		if(p.getConfig() instanceof ViewParamConfig) {
////			AnnotationConfig uiStyle = ((ViewParamConfig)p.getConfig()).getUiStyles();
////	
////			if(uiStyle!=null && ClassUtils.getShortName(Grid.class).equals(uiStyle.getName())) {
////				Object state = p.getState();
////				ModelEvent e = new ModelEvent(Action._replace, p instanceof Param<?> ?((Param<?>)p).getPath():((Model<?,?>)p).getPath(), state);
////				publishInternal(e);
////				return true;
////			}
////	
////		}
//
//		return publishInternal(modelEvent);
//	}
//	
//	protected boolean publishInternal(ModelEvent<?> result) {
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		ExecuteOutput<ModelEvent<?>> executeOutput = new ExecuteOutput<>();
//		executeOutput.setResult(result);
//		
//		MultiExecuteOutput multiExecOutput = interceptor.handleResponse(executeOutput);
//		
//		messageTemplate.convertAndSend("/queue/updates", multiExecOutput); // TODO get the destination name from the config server
//		
//		//messageTemplate.convertAndSendToUser(auth.getName(),"/queue/updates", executeOutput); // TODO get the destination name from the config server
//		
//		return true;
//	}

}
