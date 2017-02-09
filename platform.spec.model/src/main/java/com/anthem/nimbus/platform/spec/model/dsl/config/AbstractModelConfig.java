/**
 * 
 */
package com.anthem.nimbus.platform.spec.model.dsl.config;

import com.anthem.nimbus.platform.spec.model.dsl.MapsTo;
import com.anthem.nimbus.platform.spec.model.dsl.Repo;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Soham Chakravarti
 *
 */
@Getter @Setter @RequiredArgsConstructor @ToString
public abstract class AbstractModelConfig<T> implements ModelConfig<T> {

	final private Class<T> referredClass;
	
	final private MapsTo.Type mapsTo;
	
	@JsonIgnore transient private Repo repo;
	

	@Override
	public boolean isMapped() {
		return getMapsTo() != null;
	}
	

}
