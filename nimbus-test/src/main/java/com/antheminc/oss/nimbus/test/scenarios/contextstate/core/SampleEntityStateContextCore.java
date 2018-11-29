package com.antheminc.oss.nimbus.test.scenarios.contextstate.core;

import com.antheminc.oss.nimbus.domain.defn.Domain;
import com.antheminc.oss.nimbus.domain.defn.Model;
import com.antheminc.oss.nimbus.domain.defn.Domain.ListenerType;
import com.antheminc.oss.nimbus.domain.defn.Repo;
import com.antheminc.oss.nimbus.domain.defn.Repo.Cache;
import com.antheminc.oss.nimbus.domain.defn.Repo.Database;
import com.antheminc.oss.nimbus.domain.defn.extension.ParamContext;
import com.antheminc.oss.nimbus.domain.defn.extension.VisibleConditional;
import com.antheminc.oss.nimbus.entity.AbstractEntity.IdLong;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Rakesh Patel
 *
 */
@Domain(value="sampleentitystatecontextcore",includeListeners = { ListenerType.persistence }) 
@Repo(value = Database.rep_mongodb, cache = Cache.rep_device)
@Getter @Setter @ToString
public class SampleEntityStateContextCore extends IdLong {

	private static final long serialVersionUID = 1L;

	@ParamContext(enabled=true, visible=false)
	private String param1;
	
	private String param2;
	
	private SampleEntityStateContextCore_Nested nested;
	
	@Model
	@Getter @Setter
	public static class SampleEntityStateContextCore_Nested {
		
		@ParamContext(enabled=true, visible=false)
		private String nested_param1;
		
		@VisibleConditional(when="state == true", targetPath="/../nested_param1")
		private boolean nested_param2;
		
	}
	
}
