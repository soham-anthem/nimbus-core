/**
 * 
 */
package com.anthem.oss.nimbus.core.api.domain.state;

import java.beans.PropertyDescriptor;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.anthem.nimbus.platform.spec.contract.repository.ParamStateRepository;
import com.anthem.nimbus.platform.spec.model.dsl.Action;
import com.anthem.nimbus.platform.spec.model.dsl.binder.DomainState;
import com.anthem.nimbus.platform.spec.model.dsl.binder.DomainState.ListElemParam;
import com.anthem.nimbus.platform.spec.model.exception.InvalidOperationAttemptedException;
import com.anthem.nimbus.platform.spec.model.exception.PlatformRuntimeException;

/**
 * @author Soham Chakravarti
 *
 */
@Component("default.param.state.rep_local")
public class DefaultParamStateRepositoryLocal implements ParamStateRepository {

	@Autowired JavaBeanHandler javaBeanHandler;
	
	@Override
	public <P> P _get(DomainState.Param<P> param) {
		if(param.isCollectionElem()) {
			ListElemParam<P> pElem = param.findIfCollectionElem();
			
			// instantiate collection if needed
			List<P> coreList = pElem.getParentModel().instantiateOrGet();
			
			int index = pElem.getElemIndex();
			return coreList.size()>index ? coreList.get(index) : null;
			
		} else {
			PropertyDescriptor pd = param.getPropertyDescriptor();
			Object target = param.getParentModel().getState();
			return javaBeanHandler.getValue(pd, target);
		}
	}
	
	@Override
	public <P> Action _set(DomainState.Param<P> param, P newState) {
		if(param.isCollectionElem()) {
			DomainState.ListElemParam<P> pElem = param.findIfCollectionElem();
			
			// instantiate collection if needed
			List<P> coreList = pElem.getParentModel().instantiateOrGet();
			
			int index = pElem.getElemIndex();
//			if(index>coreList.size()) {
//				for(int i=coreList.size(); i<index; i++) {
//					coreList.set(i, null);
//				}				
//			}
			if(index==coreList.size()) { //add immediate next
				coreList.add(newState);
				return Action._new;
			} else if(index < coreList.size()) { //replace
				coreList.set(index, newState);
				return Action._replace;
			} else {
				throw new InvalidOperationAttemptedException(
						"Attemted to set in collection at index:"+index+" whereas size is:"+coreList.size()
						+" fpr param.path: "+param.getPath());
			}

			
			
		} else {
			PropertyDescriptor pd = param.getPropertyDescriptor();
			
			//ensure that the target parent model is instantiated
			Object target = param.getParentModel().instantiateOrGet();
			if(target==null) throw new PlatformRuntimeException("Target must not be null for setting in property: "+pd+" with value: "+ newState);
			
			javaBeanHandler.setValue(pd, target, newState);
			
			//TODO change detection
			return Action._replace;
		}
	}

}
