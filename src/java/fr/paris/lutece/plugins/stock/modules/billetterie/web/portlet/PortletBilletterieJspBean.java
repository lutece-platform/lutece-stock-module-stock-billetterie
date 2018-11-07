/*
 * Copyright (c) 2002-2018, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.stock.modules.billetterie.web.portlet;

import fr.paris.lutece.plugins.stock.modules.billetterie.business.portlet.BilletteriePortletHome;
import fr.paris.lutece.plugins.stock.modules.billetterie.business.portlet.PortletBilletterie;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.portlet.PortletJspBean;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Portlet for "A l'affiche" and "A venir"
 */
public class PortletBilletterieJspBean extends PortletJspBean
{
    private static final long serialVersionUID = -4180051741846176406L;

    /** The Constant MARK_WEBAPP_URL. */
    private static final String MARK_WEBAPP_URL = "webapp_url";

    /** The Constant MARK_LOCALE. */
    private static final String MARK_LOCALE = "locale";

    /** The Constant COMPLEMENT_URL_ADMIN_SITE. */
    private static final String COMPLEMENT_URL_ADMIN_SITE = "../../";

    /** The Constant PARAMETRE_NUMBER_SHOW. */
    private static final String PARAMETRE_NUMBER_SHOW = "porlet_number_show";

    /** The Constant PARAMETRE_TYPE_CONTENT_PORTLET. */
    private static final String PARAMETRE_TYPE_CONTENT_PORTLET = "type_content";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCreate( HttpServletRequest request )
    {
        String strPageId = request.getParameter( PARAMETER_PAGE_ID );
        String strPortletTypeId = request.getParameter( PARAMETER_PORTLET_TYPE_ID );
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage( ) );
        model.put( PARAMETRE_NUMBER_SHOW, StringUtils.EMPTY );
        model.put( PARAMETRE_TYPE_CONTENT_PORTLET, StringUtils.EMPTY );

        HtmlTemplate template = getCreateTemplate( strPageId, strPortletTypeId, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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
            model.put( PARAMETRE_NUMBER_SHOW, StringUtils.EMPTY );
        }

        HtmlTemplate template = getModifyTemplate( portlet, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * Gets the number show.
     *
     * @param strNumberShow
     *            the str number show
     * @return the number show
     */
    private Integer getNumberShow( String strNumberShow )
    {
        int nShow;

        if ( StringUtils.isEmpty( strNumberShow ) )
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
