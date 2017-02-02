/**
 * 
 */
package com.anthem.nimbus.platform.spec.model.dsl;

/**
 * @author Soham Chakravarti
 *
 */
public enum Action {
	
	/* CRUD */
	_get,		//HTTP GET - defaults to _detail
	_info,		//HTTP GET
	_new,		//HTTP POST
	_replace,	//HTTP PUT	- full update
	_update,	//HTTP PATCH- partial update
	_delete, 	//HTTP DELETE
	
	/* search, lookup */
	_search,
	
	/* process */
	_process,	//Allows for custom process/work-flow definitions
	//_lifecycle,
	
	/* navigation */
	_nav
	;

	public static final Action DEFAULT = _get;
}

/**
 * Process:
 * 	- has endpoint url
 * 		- it can end with action: _search or inferred from HTTP methods
 * 		- GET & DELETE no input, but expected output
 * 		- POST & PUT will have input and expected output
 * 
 * Config:
 * 	- has endpoint url of a Process that ends with "config"
 * 		- returns 
 * 			- input
 * 			- output
 * */