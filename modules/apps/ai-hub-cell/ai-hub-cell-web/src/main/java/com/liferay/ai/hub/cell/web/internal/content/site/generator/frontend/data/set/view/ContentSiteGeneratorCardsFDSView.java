package com.liferay.ai.hub.cell.web.internal.content.site.generator.frontend.data.set.view;

import com.liferay.ai.hub.cell.web.internal.constants.AIHubCellFDSNames;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.cards.BaseCardsFDSView;
import com.liferay.frontend.data.set.view.cards.FDSCardSchema;
import com.liferay.frontend.data.set.view.cards.FDSCardSchemaBuilder;
import com.liferay.frontend.data.set.view.cards.FDSCardSchemaBuilderFactory;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	property = "frontend.data.set.name=" + AIHubCellFDSNames.CONTENT_SITE_GENERATOR,
	service = FDSView.class
)
public class ContentSiteGeneratorCardsFDSView extends BaseCardsFDSView {

	@Override
	public String getDescription() {
		return "status";
	}

	@Override
	public FDSCardSchema getFDSCardSchema(Locale locale) {
		return _fdsCardSchemaBuilderFactory.create().build();
	}

	@Override
	public String getTitle() {
		return "name";
	}

	@Reference
	private FDSCardSchemaBuilderFactory _fdsCardSchemaBuilderFactory;

}
