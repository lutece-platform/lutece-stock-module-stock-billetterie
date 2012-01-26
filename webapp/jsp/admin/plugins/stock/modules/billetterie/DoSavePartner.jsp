<%@ page errorPage="../../../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.PartnerJspBean"%>

<%@page import="fr.paris.lutece.portal.service.spring.SpringContextService"%>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.PartnerJspBean"%>
<% 
	PartnerJspBean partner = (PartnerJspBean)SpringContextService.getBean( "stock-billetterie.partnerJspBean" );
	partner.init( request, PartnerJspBean.RIGHT_MANAGE_PARTNERS);
    response.sendRedirect( partner.doSavePartner( request ) );
%>

