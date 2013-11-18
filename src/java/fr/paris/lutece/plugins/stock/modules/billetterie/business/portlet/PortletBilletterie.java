/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
package fr.paris.lutece.plugins.stock.modules.billetterie.business.portlet;

import fr.paris.lutece.plugins.stock.commons.dao.PaginationProperties;
import fr.paris.lutece.plugins.stock.commons.dao.PaginationPropertiesImpl;
import fr.paris.lutece.plugins.stock.modules.tickets.business.ShowDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.business.ShowFilter;
import fr.paris.lutece.plugins.stock.modules.tickets.service.IShowService;
import fr.paris.lutece.plugins.stock.modules.tickets.service.ShowService;
import fr.paris.lutece.plugins.stock.utils.DateUtils;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.util.xml.XmlUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


/**
 * class PortletBilletterie
 * 
 * @author kHamidi
 */
public class PortletBilletterie extends Portlet
{

    // ///////////////////////////////////////////////////////////////////////////////
    // Constants
    private static final String TAG_BILLETTERIE_PORTLET = "billetterie";
    private static final String TAG_SHOW_ID = "id";
    private static final String TAG_SHOW_NAME = "name";
    private static final String TAG_SHOW_CATEGORY_NAME = "categoryName";
    private static final String TAG_SHOW_CATEGORY_COLOR = "categoryColor";
    private static final String TAG_SHOW_START_DATE = "date_debut";
    private static final String TAG_SHOW_END_DATE = "date_fin";
    private static final String TAG_SHOW_POSTER_URL = "posterUrl";
    private static final String TAG_SHOW_DESCRIPTION = "description";
    private static final String TAG_SHOW_TYPE_PORTLET = "typePortlet";
    private static final String TAG_SHOW = "show";
    private static final String TAG_SHOW_URL = "url";
    private static final String PARAMETER_STOCK_ID = "product_id";
    private static final String URL_SHOW = "jsp/site/Portal.jsp?page=billetterie&action=fiche-spectacle";
    private static final String PROPERTY_POSTER_TB_PATH = "stock-billetterie.poster.tb.path";
    // ////////////////////
    private Integer _nShow;
    private String _typeContentPortlet;

    /** The _show service. */
    private IShowService _showService = (IShowService) SpringContextService.getContext( ).getBean(
            ShowService.ID_SPRING_DEFAULT );

    /**
     * Instantiates a new portlet billetterie.
     */
    public PortletBilletterie( )
    {

        setPortletTypeId( BilletteriePortletHome.getInstance( ).getPortletTypeId( ) );
    }

    /**
     * Gets the type content portlet.
     * 
     * @return the type content portlet
     */
    public String getTypeContentPortlet( )
    {
        return _typeContentPortlet;
    }

    /**
     * Sets the type content portlet.
     * 
     * @param typeContentPortlet the new type content portlet
     */
    public void setTypeContentPortlet( String typeContentPortlet )
    {
        this._typeContentPortlet = typeContentPortlet;
    }

    /**
     * Gets the n show.
     * 
     * @return the n show
     */
    public Integer getnShow( )
    {
        return _nShow;
    }

    /**
     * Sets the n show.
     * 
     * @param nShow the new n show
     */
    public void setnShow( Integer nShow )
    {
        this._nShow = nShow;
    }

    /**
     * get xml document.
     * 
     * @param request the request
     * @return string
     */
    public String getXml( HttpServletRequest request )
    {
        StringBuffer strXml = new StringBuffer( );
        PaginationProperties paginator;
        List<ShowDTO> listShow;
        Integer intNbre = getnShow( );
        String strContentPortlet = getTypeContentPortlet( );
        if ( intNbre != -1 )
        {
            paginator = new PaginationPropertiesImpl( 0, getnShow( ) );
        }
        else
        {
            paginator = null;
        }
        List<String> orders = new ArrayList<String>( );
        if ( strContentPortlet.equals( "a-laffiche" ) )
        {
            Calendar calendar = new GregorianCalendar( );
            String today = DateUtils.getDate( calendar.getTime( ), DateUtils.DATE_FR );
            
            ShowFilter filter = new ShowFilter( );
            filter.setAlaffiche( true );
            orders.add( "dateEnd" );
            filter.setOrders( orders );
            filter.setOrderAsc( true );
            //filter.setDateTo( today );
            listShow = _showService.findByFilter( filter, paginator );
        }
        else
        {
            orders.add( "dateStart" );
            listShow = _showService.getComeProduct( orders, paginator );

        }
        XmlUtil.beginElement( strXml, TAG_BILLETTERIE_PORTLET );
        XmlUtil.addElement( strXml, TAG_SHOW_TYPE_PORTLET, ( strContentPortlet.equals( "a-laffiche" ) ? "aLaffiche"
                : "aVenir" ) );

        for ( ShowDTO showDTO : listShow )
        {
            if ( strContentPortlet.equals( "a-venir" ) || ( !strContentPortlet.equals( "a-venir" ) && showDTO.getAlaffiche( ) ) )
            {
                XmlUtil.beginElement( strXml, TAG_SHOW );
                XmlUtil.addElement( strXml, TAG_SHOW_ID, showDTO.getId( ) );
                XmlUtil.addElement( strXml, TAG_SHOW_NAME, showDTO.getName( ) );
                XmlUtil.addElement( strXml, TAG_SHOW_POSTER_URL,
                        "<![CDATA[" + AppPropertiesService.getProperty( PROPERTY_POSTER_TB_PATH ) + showDTO.getId( )
                                + "]]>" );
                XmlUtil.addElement( strXml, TAG_SHOW_CATEGORY_NAME, showDTO.getCategoryName( ) );
                XmlUtil.addElement( strXml, TAG_SHOW_CATEGORY_COLOR, showDTO.getCategoryColor( ) );
                XmlUtil.addElement( strXml, TAG_SHOW_START_DATE, showDTO.getStartDate( ) );
                XmlUtil.addElement( strXml, TAG_SHOW_END_DATE, showDTO.getEndDate( ) );
                // XmlUtil.addElement( strXml, TAG_SHOW_DESCRIPTION,
                // showDTO.getDescription( ) );
                UrlItem url = new UrlItem( URL_SHOW );
                url.addParameter( PARAMETER_STOCK_ID, showDTO.getId( ) );
                XmlUtil.addElement( strXml, TAG_SHOW_URL, "<![CDATA[" + url.getUrl( ) + "]]>" );
                XmlUtil.endElement( strXml, TAG_SHOW );
            }

        }
        XmlUtil.endElement( strXml, TAG_BILLETTERIE_PORTLET );

        return addPortletTags( strXml );
    }

    /** {@inheritDoc} */
    public String getXmlDocument( HttpServletRequest request ) throws SiteMessageException
    {
        return XmlUtil.getXmlHeader( ) + getXml( request );
    }

    /**
     * Updates the current instance of the PortletBilletterie object
     */
    public void update( )
    {
        BilletteriePortletHome.getInstance( ).update( this );
    }

    /**
     * Removes the current instance of the PortletBilletterie object
     */
    public void remove( )
    {
        BilletteriePortletHome.getInstance( ).remove( this );
    }
}
