<%@ page errorPage="../../../../ErrorPage.jsp" %>
<jsp:include page="../../../../AdminHeader.jsp" />


<%@page import="fr.paris.lutece.portal.service.spring.SpringContextService"%>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.PartnerJspBean"%>
<% 
	PartnerJspBean partner = (PartnerJspBean)SpringContextService.getBean( "stock-billetterie.partnerJspBean" );
	partner.init( request, PartnerJspBean.RIGHT_MANAGE_PARTNERS);
   
%>
<%= partner.getModifyPartner( request ) %>

<%@ include file="../../../../AdminFooter.jsp" %>