<%@ page errorPage="../../../../ErrorPage.jsp" %>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.PartnerJspBean"%>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.OfferJspBean"%>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.business.offer.Offer"%>
<jsp:include page="../../../../AdminHeader.jsp" />



<jsp:useBean id="offers" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.OfferJspBean" />

<% 
	offers.init( request, OfferJspBean.RIGHT_MANAGE_OFFRES);
%>
<%= offers.getDeleteUnusedOffers( request ) %>


<%@ include file="../../../../AdminFooter.jsp" %>