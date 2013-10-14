
<%@page import="fr.paris.lutece.plugins.stock.business.product.Product"%>

<%@ page errorPage="../../../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.OfferJspBean"%>


<jsp:useBean id="orderSession" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.OrderSessionJspBean" />

<% 
	orderSession.init( request, OfferJspBean.RIGHT_MANAGE_OFFRES );
	orderSession.doEditTicket( request, response );
%>



