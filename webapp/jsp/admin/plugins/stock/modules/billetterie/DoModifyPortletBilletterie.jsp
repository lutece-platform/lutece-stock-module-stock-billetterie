<%@ page errorPage="../../ErrorPage.jsp"%>

<jsp:useBean id="billetteriePortlet" scope="session"class="fr.paris.lutece.plugins.stock.modules.billetterie.web.portlet.PortletBilletterieJspBean" />

<%
	billetteriePortlet.init(request,billetteriePortlet.RIGHT_MANAGE_ADMIN_SITE);
	response.sendRedirect(billetteriePortlet.doModify(request));
%>