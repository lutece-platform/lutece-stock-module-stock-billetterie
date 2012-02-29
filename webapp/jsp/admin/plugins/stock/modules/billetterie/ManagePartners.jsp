<%@ page errorPage="../../../../ErrorPage.jsp" %>
<jsp:include page="../../../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.portal.service.spring.SpringContextService"%>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.PartnerJspBean"%>
<jsp:useBean id="partner" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.PartnerJspBean" />
<% 
	partner.init( request, PartnerJspBean.RIGHT_MANAGE_PARTNERS);
%>
<%= partner.getManageProviders( request ) %>

<%@ include file="../../../../AdminFooter.jsp" %>