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
package com.anthem.nimbus.platform.web.flow.login;

import com.anthem.oss.nimbus.core.domain.definition.ViewConfig.Hints;
import com.anthem.oss.nimbus.core.domain.definition.ViewConfig.Hints.AlignOptions;
import com.anthem.oss.nimbus.core.domain.definition.ViewConfig.Link;
import com.anthem.oss.nimbus.core.domain.definition.ViewConfig.PageHeader;
import com.anthem.oss.nimbus.core.domain.definition.ViewConfig.PageHeader.Property;
import com.anthem.oss.nimbus.core.domain.definition.ViewConfig.Paragraph;
import com.anthem.oss.nimbus.core.domain.definition.extension.Content.Label;
import com.antheminc.oss.nimbus.domain.defn.Model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Dinakar Meda
 *
 */
@Model @Getter @Setter
public class VSLoginHeader {
	
	@Link(url="/ui/", imgSrc="anthem-rev.svg", cssClass="bruce") @Hints(AlignOptions.Left) @PageHeader(Property.LOGO)
	private String linkHomeLogo;
	
	@Paragraph @Label(value = "") @PageHeader(Property.APPTITLE)
	private String linkHomeTitle;
	
}
