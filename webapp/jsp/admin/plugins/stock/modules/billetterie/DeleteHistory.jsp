<%@ page errorPage="../../../../ErrorPage.jsp" %>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.PartnerJspBean"%>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.OfferJspBean"%>


<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.business.offer.Offer"%>

<jsp:useBean id="history" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.HistoryJspBean" />

<%
	history.init( request, OfferJspBean.RIGHT_MANAGE_OFFRES);
    response.sendRedirect( history.getDeleteHistory( request ) );
%>

