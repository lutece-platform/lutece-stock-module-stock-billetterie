<%@page import="fr.paris.lutece.plugins.stock.business.product.Product"%>

<%@ page errorPage="../../../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.business.session.Session"%>
<jsp:useBean id="offers" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.OfferJspBean" />

<%
	offers.init( request, offers.RIGHT_MANAGE_OFFRES);
    response.sendRedirect( offers.doProcessSessionForm( request, Session.class.getName(  ) ) );
%>

