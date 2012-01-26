<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="PortletBilletterie" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.portlet.PortletBilletterieJspBean" />


<% PortletBilletterie.init( request, PortletBilletterie.RIGHT_MANAGE_ADMIN_SITE ); %>
<%= PortletBilletterie.getCreate ( request ) %>

