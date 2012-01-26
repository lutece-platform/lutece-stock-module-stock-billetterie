<%@ page errorPage="../../../../ErrorPage.jsp" %>
<jsp:include page="../../../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.portal.service.spring.SpringContextService"%>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.OfferJspBean"%>
<% 
OfferJspBean partner = (OfferJspBean)SpringContextService.getBean( "stock-billetterie.offerJspBean" );
	partner.init( request, OfferJspBean.RIGHT_MANAGE_OFFERS);
%>
<%= partner.getManageOffers( request ) %>

<%@ include file="../../../../AdminFooter.jsp" %>