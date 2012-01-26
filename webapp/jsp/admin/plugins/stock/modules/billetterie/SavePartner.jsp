<%@ include file="billetterie_header.inc.jsp" %>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.PartnerJspBean"%>
<% 
	PartnerJspBean partner = (PartnerJspBean)SpringContextService.getBean( "stock-billetterie.partnerJspBean" );
	partner.init( request, PartnerJspBean.RIGHT_MANAGE_PARTNERS);
%>

<%= partner.getSavePartner( request ) %>