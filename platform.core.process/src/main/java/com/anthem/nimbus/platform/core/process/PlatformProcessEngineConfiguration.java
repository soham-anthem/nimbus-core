package com.anthem.nimbus.platform.core.process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.bpmn.parser.factory.DefaultActivityBehaviorFactory;
import org.activiti.engine.impl.history.HistoryLevel;
import org.activiti.engine.impl.persistence.deploy.Deployer;
import org.activiti.engine.impl.rules.RulesDeployer;
import org.activiti.spring.SpringAsyncExecutor;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.AbstractProcessEngineAutoConfiguration;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.anthem.nimbus.platform.core.process.api.PlatformExpressionManager;
import com.anthem.nimbus.platform.core.process.api.PlatformProcessDAO;
import com.anthem.nimbus.platform.core.process.api.PlatformUserTaskActivityBehavior;


@Configuration
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class PlatformProcessEngineConfiguration extends AbstractProcessEngineAutoConfiguration {
	
	@Value("${process.database.driver}") 
	private String dbDriver;
	
	@Value("${process.database.url}") 
	private String dbUrl;
	
	@Value("${process.database.username}") 
	private String dbUserName;
	
	@Value("${process.database.password}") 
	private String dbPassword;
	
	@Value("${process.history.level}") 
	private String processHistoryLevel;	
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private PlatformExpressionManager platformExpressionManager;
	
    @Bean
    public SpringProcessEngineConfiguration springProcessEngineConfiguration(
            DataSource dataSource,
            PlatformTransactionManager transactionManager,
            SpringAsyncExecutor springAsyncExecutor) throws IOException {
    	SpringProcessEngineConfiguration engineConfiguration = this.baseSpringProcessEngineConfiguration(dataSource, transactionManager, springAsyncExecutor);
    	engineConfiguration.setActivityBehaviorFactory(platformActivityBehaviorFactory());
    	engineConfiguration.setHistoryLevel(HistoryLevel.getHistoryLevelForKey(processHistoryLevel));
    	
    	List<Deployer> deployers = new ArrayList<>();
        deployers.add(new RulesDeployer());
        engineConfiguration.setCustomPostDeployers(deployers);
        
        List<Resource> resources = new ArrayList<>(Arrays.asList(engineConfiguration.getDeploymentResources()));
        Resource[] supportingResources = processResources();
        if(supportingResources != null && supportingResources.length > 0){
        	resources.addAll(Arrays.asList(processResources()));
        }
        Resource[] resss = new Resource[resources.size()];
        engineConfiguration.setDeploymentResources(resources.toArray(resss)); 
        engineConfiguration.setExpressionManager(platformExpressionManager);
        return engineConfiguration;
    }
    
    @Bean
   	public DefaultActivityBehaviorFactory platformActivityBehaviorFactory() {
   		return new DefaultActivityBehaviorFactory() {
   			@Override
   			public UserTaskActivityBehavior createUserTaskActivityBehavior(UserTask userTask) {
   				PlatformUserTaskActivityBehavior platformUserTaskBehavior = new PlatformUserTaskActivityBehavior(userTask);
   				return platformUserTaskBehavior;
   			}
   		};
   	}
    
    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }
    
    @Bean
    public PlatformProcessDAO platformProcessDAO(){
    	return new PlatformProcessDAO(dataSource);
    }
    
    
//    @Bean
//	public DriverManagerDataSource processDataSource() {
//		DriverManagerDataSource dataSource = new DriverManagerDataSource();
//		dataSource.setDriverClassName(dbDriver);
//		dataSource.setUrl(dbUrl);
//		dataSource.setUsername(dbUserName);
//		dataSource.setPassword(dbPassword);
//		return dataSource;
//	}  
    
    protected Resource[] processResources() { 
        try {
        	PathMatchingResourcePatternResolver pmrs = new PathMatchingResourcePatternResolver();
        	Resource[] rules = pmrs.getResources("rules-sample/**.drl");
            Resource[] processDefs = pmrs.getResources("process-defs/**.xml");
			return ArrayUtils.addAll(rules, processDefs);
		} catch (IOException e) {
			
		}
		return null;
    } 
	

}