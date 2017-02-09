/**
 * 
 */
package com.anthem.oss.nimbus.test.sample.um.model.view;

import com.anthem.nimbus.platform.spec.model.dsl.Action;
import com.anthem.nimbus.platform.spec.model.dsl.Domain;
import com.anthem.nimbus.platform.spec.model.dsl.Execution;
import com.anthem.nimbus.platform.spec.model.dsl.MapsTo;
import com.anthem.nimbus.platform.spec.model.view.ViewConfig.Page;
import com.anthem.oss.nimbus.test.sample.um.model.UMCase;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Soham Chakravarti
 *
 */
@Domain("view_umcase") @MapsTo.Type(UMCase.class)
@Execution.Input.Default @Execution.Output.Default @Execution.Output({Action._new, Action._nav, Action._process})
@Getter @Setter
public class UMCaseFlow {

//	@Page @Mode(Options.ReadOnly) //@ViewType(Page/Section/Form/Button/TextBox/Radio/Select/)
//	private Page_FindPatient pg1;
//
//	@Page
//	private Page_PatientDetails pg2;

	private String umcaseId;
	
	@Page
	private Page_Pg3 pg3;
	
//	// 1: mapped to refId param within view, will look for value within viewModel.refId, else will instantiate new core model
//	@Page @Path(linked=false, value="/umcase:{refId}/c/_get", kv=@KeyValue(k="refId", v="/umcaseId"))
//	private Page_Pg3 pg3;
//
//	// 2: mapped to refId param within nested view model which is self mapped; will look for value within viewModel.refId, else will instantiate new core model
//	@Page @Path(linked=false, value="/umcase:{refId}/c/_get", kv=@KeyValue(k="refId", v="/pg3b/mappedCaseId"))
//	private Page_Pg3 pg3b;
//
//	// 3. gets value by making an executor call; will use refId value if found, otherwise will instantiate new core model
//	@Page @Path(linked=false, value="/umcase:{refId}/c/_get", kv=@KeyValue(k="refId", v="/umcase:100/_get/id", quad=false))
//	private Page_Pg3 pg3c;
//
//	// 4. no mapping given, will instantiate new core model initially
//	@Page @Path(linked=false)
//	private Page_Pg3 pg3d;
}
