/**
 * 
 */
package com.anthem.nimbus.platform.core.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.anthem.nimbus.platform.core.process.api.domain.ExecutionInputConfigHandler;
import com.anthem.nimbus.platform.core.process.api.domain.ExecutionOutputConfigHandler;
import com.anthem.nimbus.platform.core.process.api.domain.ModelConfigBuilder;
import com.anthem.nimbus.platform.core.process.api.domain.ModelConfigFactory;
import com.anthem.nimbus.platform.spec.model.dsl.CoreDomain;
import com.anthem.nimbus.platform.spec.model.dsl.Model;
import com.anthem.nimbus.platform.spec.model.dsl.config.DomainConfig;
import com.anthem.nimbus.platform.spec.model.dsl.config.ModelConfig;
import com.anthem.nimbus.platform.spec.model.dsl.config.ParamConfig;
import com.anthem.nimbus.platform.spec.model.dsl.config.ParamType;
import com.anthem.nimbus.platform.spec.model.person.Address;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Soham Chakravarti 
 *
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = {ModelConfigHandler.class, ExecutionInputConfigHandler.class, ExecutionOutputConfigHandler.class})
public class ModelConfigHandlerTest {

	@Autowired ModelConfigBuilder handler;
	
	@Model @Getter @Setter
	public static class TestBaseModel {
		private String id;
		
		
	}
	
	@Getter @Setter
	public static class TestNestedModel<ID extends Serializable, A extends Address<ID>> extends TestBaseModel {
		private Set<A> addresses;
	}
	
	@CoreDomain("test") @Getter @Setter
	public static class TestModel extends TestBaseModel {
		
		private int primitive_int;
		private Integer class_int;
		
		private List<Long> collection_primitive_long;
		private LocalDate localDate;
		
		//==private TestNestedModel<String, Address.IdString> nested;
		
	}
	
	ModelConfig<TestModel> mConfig;
	
	@Before
	public void before() {
		handler = new ModelConfigBuilder();
		DomainConfig dc = new DomainConfig("test");
		handler.setExecInputHandler(new ExecutionInputConfigHandler());
		handler.setExecOutputHandler(new ExecutionOutputConfigHandler());
		
		handler.setTypeClassMappings(new HashMap<String, String>());
		handler.getTypeClassMappings().put(LocalDate.class.getName(), "date");
		
		handler.setFactory(new ModelConfigFactory());
		
		//mConfig = handler.load(TestModel.class, dc, DomainConfigAPITest.visitedModels);
		Assert.assertNotNull(mConfig);
	}
	
	@Test
	public void test_id() {
		ParamConfig p = mConfig.templateParams().find("id");
		Assert.assertNotNull(p);
		Assert.assertEquals("id", p.getCode());
		Assert.assertFalse(p.getType().isNested());
		Assert.assertEquals("string", p.getType().getName());
		Assert.assertNull(p.getType().getCollection());
	}
	
	@Test
	public void test_primitive_int() {
		ParamConfig p = mConfig.templateParams().find("primitive_int");
		Assert.assertNotNull(p);
		Assert.assertFalse(p.getType().isNested());
		Assert.assertEquals("integer", p.getType().getName());
		Assert.assertNull(p.getType().getCollection());
	}
	
	@Test
	public void test_class_int() {
		ParamConfig p = mConfig.templateParams().find("class_int");
		Assert.assertNotNull(p);
		Assert.assertFalse(p.getType().isNested());
		Assert.assertEquals("integer", p.getType().getName());
		Assert.assertNull(p.getType().getCollection());
	}
	
	@Test 
	public void test_collection_primitive_long() {
		ParamConfig p = mConfig.templateParams().find("collection_primitive_long");
		Assert.assertNotNull(p);
		Assert.assertFalse(p.getType().isNested());
		Assert.assertEquals("long", p.getType().getName());
		Assert.assertSame(ParamType.CollectionType.list, p.getType().getCollection());
	}
	
	@Test
	public void test_localDate() {
		ParamConfig p = mConfig.templateParams().find("localDate");
		Assert.assertNotNull(p);
		Assert.assertFalse(p.getType().isNested());
		Assert.assertEquals("date", p.getType().getName());
		Assert.assertNull(p.getType().getCollection());
	}
	
	//==@Test
	public void test1_nested() {
		ParamConfig p = mConfig.templateParams().find("nested");
		Assert.assertNotNull(p);
		Assert.assertTrue(p.getType().isNested());
		Assert.assertEquals(TestNestedModel.class.getSimpleName(), p.getType().getName());
		Assert.assertNull(p.getType().getCollection());
		
		ModelConfig nmNested = ((ParamType.Nested)p.getType()).getModel();
		Assert.assertNotNull(nmNested);
	}
	
}