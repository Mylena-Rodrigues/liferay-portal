package com.liferay.ai.hub.cell.web.internal.content.site.generator.frontend.data.set.view;

import com.liferay.ai.hub.cell.web.internal.constants.AIHubCellFDSNames;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.list.BaseListFDSView;

import org.osgi.service.component.annotations.Component;

@Component(
	property = "frontend.data.set.name=" + AIHubCellFDSNames.CONTENT_SITE_GENERATOR,
	service = FDSView.class
)
public class ContentSiteGeneratorListFDSView extends BaseListFDSView {

	@Override
	public String getDescription() {
		return "status";
	}

	@Override
	public String getTitle() {
		return "name";
	}

}