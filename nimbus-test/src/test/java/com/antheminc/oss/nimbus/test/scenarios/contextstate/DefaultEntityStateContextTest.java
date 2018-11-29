package com.antheminc.oss.nimbus.test.scenarios.contextstate;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.antheminc.oss.nimbus.domain.model.config.ParamValue;
import com.antheminc.oss.nimbus.domain.model.state.EntityState.Param;
import com.antheminc.oss.nimbus.domain.model.state.EntityState.Param.Message;
import com.antheminc.oss.nimbus.domain.model.state.EntityStateVisitor;
import com.antheminc.oss.nimbus.domain.model.state.internal.DefaultEntityStateContext;
import com.antheminc.oss.nimbus.domain.model.state.internal.EntityStateContextReaderVisitor;
import com.antheminc.oss.nimbus.test.domain.support.AbstractFrameworkIntegrationTests;
import com.antheminc.oss.nimbus.test.scenarios.contextstate.view.SampleEntityStateContextView;

/**
 * @author Rakesh Patel
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings({ "rawtypes", "unchecked" })
public class DefaultEntityStateContextTest extends AbstractFrameworkIntegrationTests {

	
	private static final String CORE_ENTITY_STATE_CONTEXT = "sampleentitystatecontextcore";
	private static final String CORE_ENTITY_STATE_CONTEXT_ROOT = PLATFORM_ROOT + "/" + CORE_ENTITY_STATE_CONTEXT;
	
	private static final String VIEW_ENTITY_STATE_CONTEXT = "sampleentitystatecontextview";
	private static final String VIEW_ENTITY_STATE_CONTEXT_ROOT = PLATFORM_ROOT + "/" + VIEW_ENTITY_STATE_CONTEXT;
	
	private Param<?> coreParam;
	private Param<?> viewParam;
	
	@Before
	public void newEntity() {
		coreParam = invokeNew(CORE_ENTITY_STATE_CONTEXT_ROOT, null);
		viewParam = invokeNew(VIEW_ENTITY_STATE_CONTEXT_ROOT, null);
	}
	
	@Test
	public void t00_createCoreEntity() throws Exception {
		EntityStateVisitor entityStateVisitor = new EntityStateContextReaderVisitor();
		Map<String, DefaultEntityStateContext> contextMap;
		
		Param<?> p = coreParam;
		
		assertThat(p.findParamByPath("/param1").isVisible()).isEqualTo(false);
		assertThat(p.findParamByPath("/param1").isEnabled()).isEqualTo(true);
		assertThat(p.findParamByPath("/nested/nested_param1").isVisible()).isEqualTo(false);
		assertThat(p.findParamByPath("/nested/nested_param1").isEnabled()).isEqualTo(true);
		
		p.getRootExecution().getAssociatedParam().accept(entityStateVisitor);
		contextMap = ((EntityStateContextReaderVisitor)entityStateVisitor).getContextMap();
		
		DefaultEntityStateContext nestedParam1Ctx = contextMap.get("/sampleentitystatecontextcore/nested/nested_param1");
		assertThat(nestedParam1Ctx.getParam().getVisibleState().getCurrentState()).isEqualTo(p.findParamByPath("/nested/nested_param1").isVisible());
		
		p.findParamByPath("/nested/nested_param2").setState(true);
		
		assertThat(p.findParamByPath("/nested/nested_param1").isVisible()).isEqualTo(true);
		assertThat(p.findParamByPath("/nested/nested_param1").isEnabled()).isEqualTo(true);
		
		p.getRootExecution().getAssociatedParam().accept(entityStateVisitor);
		contextMap = ((EntityStateContextReaderVisitor)entityStateVisitor).getContextMap();
		
		nestedParam1Ctx = contextMap.get("/sampleentitystatecontextcore/nested/nested_param1");
		
		assertThat(nestedParam1Ctx.getParam().getVisibleState().getCurrentState()).isEqualTo(p.findParamByPath("/nested/nested_param1").isVisible());
	}
	
	
	@Test
	public void t01_createViewEntity() throws Exception {
		EntityStateVisitor entityStateVisitor = new EntityStateContextReaderVisitor();
		Map<String, DefaultEntityStateContext> contextMap;
		
		Param<?> p = viewParam;
		
		assertThat(p.findParamByPath("/viewParam1").isVisible()).isEqualTo(false);
		assertThat(p.findParamByPath("/viewParam1").isEnabled()).isEqualTo(false);
		assertThat(p.findParamByPath("/.m/param1").isVisible()).isEqualTo(false);
		assertThat(p.findParamByPath("/.m/param1").isEnabled()).isEqualTo(true);
		assertThat(p.findParamByPath("/.m/nested/nested_param1").isVisible()).isEqualTo(false);
		assertThat(p.findParamByPath("/.m/nested/nested_param1").isEnabled()).isEqualTo(true);
		
		p.getRootExecution().getAssociatedParam().accept(entityStateVisitor);
		contextMap = ((EntityStateContextReaderVisitor)entityStateVisitor).getContextMap();
		
		DefaultEntityStateContext viewParamCtx = contextMap.get("/sampleentitystatecontextview/viewParam1");
		DefaultEntityStateContext coreNestedParam1Ctx = contextMap.get("/sampleentitystatecontextcore/nested/nested_param1");
		
		assertThat(viewParamCtx.getParam().getEnabledState().getCurrentState()).isEqualTo(p.findParamByPath("/viewParam1").isEnabled());
		assertThat(viewParamCtx.getParam().isActive()).isEqualTo(p.findParamByPath("/viewParam1").isActive());
		assertThat(coreNestedParam1Ctx.getParam().getVisibleState().getCurrentState()).isEqualTo(p.findParamByPath("/.m/nested/nested_param1").isVisible());
		
		p.findParamByPath("/.m/nested/nested_param2").setState(true);
		
		assertThat(p.findParamByPath("/.m/nested/nested_param1").isVisible()).isEqualTo(true);
		assertThat(p.findParamByPath("/.m/nested/nested_param1").isEnabled()).isEqualTo(true);
		
		p.getRootExecution().getAssociatedParam().accept(entityStateVisitor);
		contextMap = ((EntityStateContextReaderVisitor)entityStateVisitor).getContextMap();
		
		coreNestedParam1Ctx = contextMap.get("/sampleentitystatecontextcore/nested/nested_param1");
		
		assertThat(coreNestedParam1Ctx.getParam().getVisibleState().getCurrentState()).isEqualTo(p.findParamByPath("/.m/nested/nested_param1").isVisible());
	}
	
	@Test
	public void t02_testParamValues() {
		EntityStateVisitor entityStateVisitor = new EntityStateContextReaderVisitor();
		Map<String, DefaultEntityStateContext> contextMap;
		
		Param<?> p = viewParam;
		
		List<ParamValue> valuesParam2 = p.findParamByPath("/viewParam2").getValues();
		List<ParamValue> valuesParam3 = p.findParamByPath("/viewParam3").getValues();
		
		assertThat(valuesParam2).isEqualTo(new SampleEntityStateContextView.ViewParam2Values().getValues(""));
		assertThat(valuesParam3).isEqualTo(new SampleEntityStateContextView.ViewParam3DefaultValues().getValues(""));
		
		p.getRootExecution().getAssociatedParam().accept(entityStateVisitor);
		contextMap = ((EntityStateContextReaderVisitor)entityStateVisitor).getContextMap();
		DefaultEntityStateContext paramContext = contextMap.get("/sampleentitystatecontextview/viewParam2");
		
		assertThat(paramContext.getParam().getValues()).isEqualTo(valuesParam2);
		
		paramContext = contextMap.get("/sampleentitystatecontextview/viewParam3");
		
		assertThat(paramContext.getParam().getValues()).isEqualTo(valuesParam3);
		
		p.findParamByPath("/viewParam2").setState("p2c1");
		
		valuesParam3 = p.findParamByPath("/viewParam3").getValues();
		
		p.getRootExecution().getAssociatedParam().accept(entityStateVisitor);
		contextMap = ((EntityStateContextReaderVisitor)entityStateVisitor).getContextMap();
		paramContext = contextMap.get("/sampleentitystatecontextview/viewParam3");
		
		assertThat(paramContext.getParam().getValues()).isEqualTo(valuesParam3).isEqualTo(new SampleEntityStateContextView.ViewParam3Values().getValues(""));
		
	}
	
	@Test
	public void t02_testMessage() {
		EntityStateVisitor entityStateVisitor = new EntityStateContextReaderVisitor();
		Map<String, DefaultEntityStateContext> contextMap;
		
		Param<?> p = viewParam;
		
		Set<Message> messageParam3 = p.findParamByPath("/viewParam3").getMessages();
		
		assertThat(messageParam3).isNull();
		
		p.getRootExecution().getAssociatedParam().accept(entityStateVisitor);
		contextMap = ((EntityStateContextReaderVisitor)entityStateVisitor).getContextMap();
		DefaultEntityStateContext paramContext = contextMap.get("/sampleentitystatecontextview/viewParam3");
		
		assertThat(paramContext.getParam().getMessageState().getCurrentState()).isEqualTo(messageParam3);
		
		p.findParamByPath("/viewParam3").setState("vp3");
		
		messageParam3 = p.findParamByPath("/viewParam3").getMessages();
		
		assertThat(messageParam3).isNotEmpty();
		
		p.getRootExecution().getAssociatedParam().accept(entityStateVisitor);
		contextMap = ((EntityStateContextReaderVisitor)entityStateVisitor).getContextMap();
		paramContext = contextMap.get("/sampleentitystatecontextview/viewParam3");
		
		assertThat(paramContext.getParam().getMessageState().getCurrentState()).isEqualTo(messageParam3);
		
	}
	
	
}
