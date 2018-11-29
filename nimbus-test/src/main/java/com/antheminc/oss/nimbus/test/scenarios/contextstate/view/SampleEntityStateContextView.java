package com.antheminc.oss.nimbus.test.scenarios.contextstate.view;

import java.util.Arrays;
import java.util.List;

import com.antheminc.oss.nimbus.domain.defn.Domain;
import com.antheminc.oss.nimbus.domain.defn.MapsTo.Path;
import com.antheminc.oss.nimbus.domain.defn.MapsTo.Type;
import com.antheminc.oss.nimbus.domain.defn.Model.Param.Values;
import com.antheminc.oss.nimbus.domain.defn.Model.Param.Values.Source;
import com.antheminc.oss.nimbus.domain.defn.Repo;
import com.antheminc.oss.nimbus.domain.defn.Repo.Cache;
import com.antheminc.oss.nimbus.domain.defn.Repo.Database;
import com.antheminc.oss.nimbus.domain.defn.extension.MessageConditional;
import com.antheminc.oss.nimbus.domain.defn.extension.ParamContext;
import com.antheminc.oss.nimbus.domain.defn.extension.ValuesConditional;
import com.antheminc.oss.nimbus.domain.defn.extension.ValuesConditional.Condition;
import com.antheminc.oss.nimbus.domain.model.config.ParamValue;
import com.antheminc.oss.nimbus.test.scenarios.contextstate.core.SampleEntityStateContextCore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Rakesh Patel
 *
 */
@Domain("sampleentitystatecontextview") 
@Type(SampleEntityStateContextCore.class)
@Repo(value=Database.rep_none, cache=Cache.rep_device)
@Getter @Setter @ToString
public class SampleEntityStateContextView {

	@Path("param1")
	@ParamContext(enabled=false, visible=false)
	private String viewParam1;
	
	@Values(value=ViewParam2Values.class)
	@ValuesConditional(targetPath = "../viewParam3", condition = { 
			@Condition(when = "state=='p2c1'", then = @Values(ViewParam3Values.class)),
			@Condition(when = "state=='p2c2'", then = @Values(ViewParam2Values.class)),
		},
		exclusive = false	
	)
	private String viewParam2;
	
	
	@Values(value=ViewParam3DefaultValues.class)
	@MessageConditional(when = "state == 'vp3'", message = "'Test msg - viewparam 3'")
	private String viewParam3;
	
	
	public static class ViewParam2Values implements Source {

		public List<ParamValue> getValues(String paramCode) {
			return Arrays.asList(
	                new ParamValue("p2c1", "l1"),
	                new ParamValue("p2c2", "l2"),
	                new ParamValue("p2c3", "l3"),
	                new ParamValue("p2c4", "l4")
	        );
		}
		
	}

	public static class ViewParam3Values implements Source {

		public List<ParamValue> getValues(String paramCode) {
			return Arrays.asList(
	                new ParamValue("p3c1", "l1"),
	                new ParamValue("p3c2", "l2"),
	                new ParamValue("p3c3", "l3"),
	                new ParamValue("p3c4", "l4")
	        );
		}
		
	}
	
	public static class ViewParam3DefaultValues implements Source {

		public List<ParamValue> getValues(String paramCode) {
			return Arrays.asList(
	                new ParamValue("pc1", "l1"),
	                new ParamValue("pc2", "l2"),
	                new ParamValue("pc3", "l3"),
	                new ParamValue("pc4", "l4")
	        );
		}
		
	}
	
}

