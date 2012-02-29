<%@ include file="billetterie_header.inc.jsp" %>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.OfferJspBean"%>
<jsp:useBean id="offer" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.OfferJspBean" />
<% 
	offer.init( request, OfferJspBean.RIGHT_MANAGE_OFFERS);
%>

<%= offer.getSaveOffer( request ) %>