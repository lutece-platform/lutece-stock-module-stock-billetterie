package fr.paris.lutece.plugins.stock.modules.billetterie.web.portlet;

import fr.paris.lutece.plugins.stock.modules.billetterie.business.portlet.BilletteriePortletHome;
import fr.paris.lutece.plugins.stock.modules.billetterie.business.portlet.PortletBilletterie;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.portlet.PortletJspBean;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


public class PortletBilletterieJspBean extends PortletJspBean
{

    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_LOCALE = "locale";
    private static final String COMPLEMENT_URL_ADMIN_SITE = "../../";
    private static final String PARAMETRE_NUMBER_SHOW = "porlet_number_show";
    private static final String PARAMETRE_TYPE_CONTENT_PORTLET = "type_content";


    @Override
    public String getCreate( HttpServletRequest request )
    {

        String strPageId = request.getParameter( PARAMETER_PAGE_ID );
        String strPortletTypeId = request.getParameter( PARAMETER_PORTLET_TYPE_ID );
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage( ) );
        model.put( PARAMETRE_NUMBER_SHOW, "" );
        model.put( PARAMETRE_TYPE_CONTENT_PORTLET, "" );
        HtmlTemplate template = getCreateTemplate( strPageId, strPortletTypeId, model );

        return template.getHtml( );
    }

    @Override
    public String doCreate( HttpServletRequest request )
    {

        PortletBilletterie billetteriePortlet = new PortletBilletterie( );

        String strErrorUrl = setPortletCommonData( request, billetteriePortlet );

        if ( strErrorUrl != null )
        {
            return strErrorUrl;
        }

        String strPageId = request.getParameter( PARAMETER_PAGE_ID );
        int nPageId = Integer.parseInt( strPageId );
        billetteriePortlet.setPageId( nPageId );
        String strNumerShow = request.getParameter( PARAMETRE_NUMBER_SHOW );
        int nShow = Integer.parseInt( strNumerShow );
        String strTypeContentPortlet = request.getParameter( PARAMETRE_TYPE_CONTENT_PORTLET );
        billetteriePortlet.setnShow( nShow );
        billetteriePortlet.setTypeContentPortlet( strTypeContentPortlet );
        BilletteriePortletHome.getInstance( ).create( billetteriePortlet );

        return COMPLEMENT_URL_ADMIN_SITE + getPageUrl( billetteriePortlet.getPageId( ) );
    }

    @Override
    public String getModify( HttpServletRequest request )
    {

        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        int nPortletId = Integer.parseInt( strPortletId );
        PortletBilletterie portlet = (PortletBilletterie) PortletHome.findByPrimaryKey( nPortletId );
        int nbrShow = portlet.getnShow( );
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage( ) );
        model.put( PARAMETRE_TYPE_CONTENT_PORTLET, portlet.getTypeContentPortlet( ) );
        if ( nbrShow != -1 )
        {
            model.put( PARAMETRE_NUMBER_SHOW, portlet.getnShow( ) );
        }
        else
        {
            model.put( PARAMETRE_NUMBER_SHOW, "" );
        }
        HtmlTemplate template = getModifyTemplate( portlet, model );

        return template.getHtml( );
    }

    @Override
    public String doModify( HttpServletRequest request )
    {

        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        int nPortletId = Integer.parseInt( strPortletId );
        PortletBilletterie billetterielPortlet = (PortletBilletterie) PortletHome.findByPrimaryKey( nPortletId );

        String strErrorUrl = setPortletCommonData( request, billetterielPortlet );

        if ( strErrorUrl != null )
        {
            return strErrorUrl;
        }
        String strNumberShow = request.getParameter( PARAMETRE_NUMBER_SHOW );
        int nShow = getNumberShow( strNumberShow );
        String strTypeContentPortlet = request.getParameter( PARAMETRE_TYPE_CONTENT_PORTLET );
        billetterielPortlet.setnShow( nShow );
        billetterielPortlet.setTypeContentPortlet( strTypeContentPortlet );
        billetterielPortlet.update( );

        return COMPLEMENT_URL_ADMIN_SITE + getPageUrl( billetterielPortlet.getPageId( ) );

    }

    private Integer getNumberShow( String strNumberShow )
    {
        int nShow;
        if ( strNumberShow == "" )
        {
            nShow = -1;
        }
        else
        {
            nShow = Integer.parseInt( strNumberShow );
        }

        return nShow;
    }
}
