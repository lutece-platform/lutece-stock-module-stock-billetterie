<%@ include file="billetterie_header.inc.jsp" %>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.OfferJspBean"%>
<% 
	OfferJspBean offer = (OfferJspBean)SpringContextService.getBean( "stock-billetterie.offerJspBean" );
	offer.init( request, OfferJspBean.RIGHT_MANAGE_OFFERS);
%>

<%= offer.getSaveOffer( request ) %>