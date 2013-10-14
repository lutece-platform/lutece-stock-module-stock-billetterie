<%@ page errorPage="../../../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.OfferJspBean"%>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.business.session.Session"%>

<jsp:useBean id="offers" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.OfferJspBean" />

<%
	offers.init( request, OfferJspBean.RIGHT_MANAGE_OFFRES);
    response.sendRedirect( offers.doDeleteProduct( request, Session.class.getName(  ) ) );
%>

