/**
 *  Copyright 2016-2018 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.antheminc.oss.nimbus.domain.model.state.internal;

import org.apache.commons.lang3.StringUtils;

import com.antheminc.oss.nimbus.domain.defn.Converters.ParamConverter;



/**
 * @author Rakesh Patel
 *
 */
public class IdParamConverter implements ParamConverter<Long, String> {

	public static final String PREFIX = "ANT";
	
	@Override
	public String serialize(Long input) {
		if(input == null) return null;
		
		StringBuilder output = new StringBuilder().append(PREFIX).append(input);
		return output.toString();
	}

	@Override
	public Long deserialize(String input) {
		if(input == null) return null;
		
		String output = StringUtils.stripStart(input, PREFIX);
		if(StringUtils.isEmpty(output)) return null;
		
		return (long)Long.valueOf(output);
		
		
	}

}