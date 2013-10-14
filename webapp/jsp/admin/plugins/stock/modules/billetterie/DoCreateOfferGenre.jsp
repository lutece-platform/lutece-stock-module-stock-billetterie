<%@ page errorPage="../../../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.OfferJspBean"%>
<jsp:useBean id="offerGenre" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.OfferGenreJspBean" />

<%
	offerGenre.init( request, OfferJspBean.RIGHT_MANAGE_OFFRES);
    response.sendRedirect( offerGenre.doCreateOfferGenre( request ) );
%>

