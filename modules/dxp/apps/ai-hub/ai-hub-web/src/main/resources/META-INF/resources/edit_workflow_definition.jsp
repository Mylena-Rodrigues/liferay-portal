<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.URLCodec" %><%@
page import="com.liferay.portal.workflow.constants.WorkflowPortletKeys" %>

<%
    String workflowName = ParamUtil.getString(request, "name");
    String kaleoScope = ParamUtil.getString(request, "kaleoScope");
    String kaleoRedirect = ParamUtil.getString(request, "kaleoRedirect");

    StringBuilder kaleoQS = new StringBuilder();
    kaleoQS.append("mvcPath=/edit_workflow_definition.jsp");
    kaleoQS.append("&name=").append(URLCodec.encodeURL(workflowName));
    kaleoQS.append("&scope=").append(kaleoScope);
    kaleoQS.append("&redirect=").append(URLCodec.encodeURL(kaleoRedirect));
    kaleoQS.append("&clearSessionMessage=true");
%>

<div class="embedded-kaleo-designer">
    <liferay-portlet:runtime
        portletName="<%= WorkflowPortletKeys.KALEO_DESIGNER %>"
        queryString="<%= kaleoQS.toString() %>"
    />
</div>