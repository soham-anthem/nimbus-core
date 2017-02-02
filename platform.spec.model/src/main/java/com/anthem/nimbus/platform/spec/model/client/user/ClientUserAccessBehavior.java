/**
 * 
 */
package com.anthem.nimbus.platform.spec.model.client.user;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.anthem.nimbus.platform.spec.model.AbstractModelBehavior;
import com.anthem.nimbus.platform.spec.model.access.Permission;
import com.anthem.nimbus.platform.spec.model.access.Role;
import com.anthem.nimbus.platform.spec.model.client.access.ClientAccessEntity;
import com.anthem.nimbus.platform.spec.model.client.access.ClientUserRole;
import com.anthem.nimbus.platform.spec.model.client.access.ClientUserRole.Entry;
import com.anthem.nimbus.platform.spec.model.command.Command;
import com.anthem.nimbus.platform.spec.model.dsl.Constants;
import com.anthem.nimbus.platform.spec.model.dsl.binder.ModelStateAndConfig;
import com.anthem.nimbus.platform.spec.model.util.JustLogit;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Soham Chakravarti
 *
 */

@Getter @Setter
public class ClientUserAccessBehavior extends AbstractModelBehavior<ClientUser, Long> {
	
	//private static final String DOMAIN_URI_EXP = "/([0-9]*)([a-zA-Z]*)([0-9]+)([a-zA-Z]*)/";
	//private static final String DOMAIN_URI_SEPERATOR = "/";
	
	private static final Logger log = LoggerFactory.getLogger(ModelStateAndConfig.class);
	
	private static Map<String, List<String>> permissionToActions = new HashMap<>();
	
	static{
		permissionToActions.put("ACCESS", Arrays.asList("_get","_info","_search","_new","_replace","_update","_delete","_nav","_process"));
		permissionToActions.put("READ", Arrays.asList("_get","_info","_search","_getAll","_nav","_process"));
		permissionToActions.put("CREATE", Arrays.asList("_new","_nav","_process"));
		permissionToActions.put("UPDATE", Arrays.asList("_replace","_update","_nav","_process"));
		permissionToActions.put("DELETE", Arrays.asList("_delete","_nav","_process"));	
	}

	public ClientUserAccessBehavior(ClientUser user) {
		super(user);
	}
	
	public boolean canUserPerform(Command cmd){
		log.debug("begin canUserPerform for : ["+cmd+"] ");
		String path = cmd.getAbsoluteDomainAlias() + Constants.SEPARATOR_URI.code + cmd.getAction().name();
		return canUserPerformAction(path,cmd.getAction().name());
	}

	/**
	 * Determine if user can perform an action based on the restricted permissions for a role.
	 * @param path
	 * @param action
	 * @return
	 */
	public boolean canUserPerformAction(String path , String action){
		log.debug("begin canUserPerformAction for : ["+path+"] : action : ["+action+"]");
		return hasRestrictedPermissions(path,action);		
	}
	/**
	 * 
	 * @param command
	 * @return
	 */
	private boolean hasRestrictedPermissions(String path,String action) {
        Set<ClientUserRole> grantedRoles = getModel().getGrantedRoles();
        boolean canRolePerform = true;
		if (!CollectionUtils.isEmpty(grantedRoles)) {
			
			Iterator<ClientUserRole> roleIt = grantedRoles.iterator();
			while (roleIt.hasNext()) {
				Role<Entry, ClientAccessEntity> r = roleIt.next();
				Set<ClientUserRole.Entry> entries = r.getEntries();
				log.debug("begin getGrantedPermissionsFromRoleEntry for role: ["+r.getName()+"]");
				Set<Permission> grantedPermissions = getGrantedPermissionsFromRoleEntries(entries, path);
				log.debug("aggregated permissions for all entries for role : ["+r.getName()+"] :: "+grantedPermissions);				
				
				if (CollectionUtils.isEmpty(grantedPermissions)){
					canRolePerform = true;
				}
				log.debug("Permission to actions : "+permissionToActions);
				boolean doesExist = false;
				if(!CollectionUtils.isEmpty(permissionToActions) && !CollectionUtils.isEmpty(grantedPermissions)){
					
					for(Permission permission : grantedPermissions){ // list of all permissions in all roles for the domainuri 
						List<String> restrictedActions = permissionToActions.get(permission.getCode());
						if(!CollectionUtils.isEmpty(restrictedActions)){
							restrictedActions.forEach((restrictedAction)->restrictedAction.trim());
							log.trace("check if permission ["+permission+"] has action ["+action+"]" );
							if(restrictedActions.contains(action)){
								log.debug("permission ["+permission+"] contains action ["+action+"]");
								doesExist = true;
							}
						}
						
					}
				}
				
				if(doesExist){
					log.debug("["+r.getName()+"] has restrictive permission for performing ["+path+"] and action ["+action+"]");
						canRolePerform = false;
					}else{
					log.debug("["+r.getName()+"] does not have a restrictive permission for performing ["+path+"] and action ["+action+"]");
					return canRolePerform;
				}
			}
			return canRolePerform;		
		}
		return false; // return false if user is not assigned any role. Look for Role without restricted permission.
	}

	/**
	 * 
	 * @param entries
	 * @param domainUri
	 * @return
	 */
	private Set<Permission> getGrantedPermissionsFromRoleEntries(Set<ClientUserRole.Entry> entries, String domainUri) {
		if(!CollectionUtils.isEmpty(entries)) {
			Set<Permission> entryPermissions = new HashSet<>();
			for(Entry e : entries) {
				ClientAccessEntity cae = e.getReferredAccess();
				if(StringUtils.equalsIgnoreCase(domainUri, StringUtils.prependIfMissing(cae.getDomainUri(), "/")) ){
					log.debug("permissions for domainUri : ["+domainUri+"] : "+e.getGrantedPermissions()+" for entry "+e.getId());
					entryPermissions.addAll(e.getGrantedPermissions());
				}
			}
			return entryPermissions;
		}
		return null;
	}
	
	
}
