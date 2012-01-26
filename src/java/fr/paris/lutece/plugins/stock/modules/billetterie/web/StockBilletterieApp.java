/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
package fr.paris.lutece.plugins.stock.modules.billetterie.web;

import fr.paris.lutece.plugins.stock.commons.exception.FunctionnalException;
import fr.paris.lutece.plugins.stock.commons.exception.TechnicalException;
import fr.paris.lutece.plugins.stock.modules.tickets.business.SeanceDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.business.SeanceFilter;
import fr.paris.lutece.plugins.stock.modules.tickets.business.ShowDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.service.IProviderService;
import fr.paris.lutece.plugins.stock.modules.tickets.service.ISeanceService;
import fr.paris.lutece.plugins.stock.modules.tickets.service.IShowService;
import fr.paris.lutece.plugins.stock.modules.tickets.utils.constants.TicketsConstants;
import fr.paris.lutece.plugins.stock.utils.DateUtils;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;


/**
 * Pages for billetterie front
 * 
 */
public class StockBilletterieApp extends AbstractXPageApp implements XPageApplication
{

    private IShowService _showService = (IShowService) SpringContextService.getContext( ).getBean( "stock-tickets.showService" );
    private IProviderService _providerService = (IProviderService) SpringContextService.getContext( ).getBean(
            IProviderService.class );
    private ISeanceService _offerService = (ISeanceService) SpringContextService.getContext( ).getBean(
            "stock-tickets.seanceService" );

    // Templates
    private static final String TEMPLATE_DIR = "skin/plugins/stock/modules/billetterie/";

    // Parameters
    private static final String PARAMETER_DATE_SEANCE = "date_seance";
    private static final String PARAMETER_PRODUCT_ID = "product_id";
    private static final String PARAMETER_ACTION = "action";

    private static final String ACTION_SHOW_PAGE = "fiche-spectacle";
    private static final String ACTION_CURRENT_SHOW_LIST = "a-laffiche";
    private static final String ACTION_COME_SHOW_LIST = "a-venir";
    // Constants
    private static final String TITLE_CURRENT_SHOW_LIST = "module.stock.billetterie.show_list.aLaffiche.title";
    private static final String TITLE_COME_SHOW_LIST = "module.stock.billetterie.show_list.aVenir.title";
    private static final String MAX_RESERVATION_INVITATION = "nb_max_invitation";
    private static final String MAX_RESERVATION_INVITATION_ENFANT = "nb_max_invitation_enfant";
    private static final String MAX_RESERVATION_TARIF_REDUIT = "nb_max_tarif_reduit";

    private static final int BOOKING_OPENED = 0;
    private static final int BOOKING_TO_COME = 1;
    private static final int BOOKING_PASSED = -1;

    /**
     * Return page with action specified
     */
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin ) throws UserNotSignedException,
            SiteMessageException
    {

        XPage page = new XPage( );
        Locale locale = request.getLocale( );

        String strAction = request.getParameter( PARAMETER_ACTION );

        if ( ACTION_SHOW_PAGE.equals( strAction ) )
        {
            page = getShowPage( page, request, locale );
        }
        if ( ACTION_CURRENT_SHOW_LIST.equals( strAction ) )
        {
            page = getCurrentListShowPage( page, request, locale );

        }
        if ( ACTION_COME_SHOW_LIST.equals( strAction ) )
        {
            page = getComeListShowPage( page, request, locale );
        }
        return page;
    }

    /**
     * Page for the show
     * 
     * @param page
     *            xpage
     * @param request
     *            http request
     * @param locale
     *            locale
     * @return xpage
     */
    public XPage getShowPage( XPage page, HttpServletRequest request, Locale locale )
    {
        String sIdShow = request.getParameter( PARAMETER_PRODUCT_ID );
        String sSeanceDate = request.getParameter( PARAMETER_DATE_SEANCE );
        Integer idShow = -1;

        if ( StringUtils.isNotEmpty( sIdShow ) && StringUtils.isNumeric( sIdShow ) )
        {
            idShow = Integer.valueOf( sIdShow );
        }

        // Get the show
        ShowDTO show = _showService.getProduct( idShow );

        // Generates template
        Map<String, Object> model = new HashMap<String, Object>( );

        FunctionnalException fe = getErrorOnce( request );
        if ( fe != null )
        {
            model.put( TicketsConstants.PARAMETER_ERROR, getHtmlError( fe, request ) );
        }

        model.put( "show", show );
        
        model.put( "partner", _providerService.findByIdWithProducts( show.getIdProvider( ) ) );

        // Calculate if show is open to book
        model.put( "booking_opened", caculateBookingOpened( show ) );

        // Get seance list
        SeanceFilter filter = new SeanceFilter( );
        filter.setOrderAsc( false );
        List<String> orderList = new ArrayList<String>( );
        orderList.add( "date" );
        filter.setOrders( orderList );
        model.put( "seance_date_list", _offerService.findSeanceByShow( show.getId( ), filter ) );

        // If user authenticated, display booking bloc
        model.put( "user", getUser( request ) );

        model.put( "bloc_reservation", getBookingBloc( show, sSeanceDate, locale ) );
        model.put( "sSeanceDate", sSeanceDate );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_DIR + "show_page.html", locale, model );

        page.setContent( template.getHtml( ) );
        page.setPathLabel( show.getName( ) );
        page.setTitle( show.getName( ) );

        return page;
    }

    /**
     * Calculate if show is open to book
     * @param show
     * @return 0 open, 1 to come, -1 passed
     */
    private int caculateBookingOpened( ShowDTO show )
    {
        Date startDate = DateUtils.getDate( show.getStartDate( ), true );
        Date endDate = DateUtils.getDate( show.getEndDate( ), false );
        Date today = new Date();
        int seanceOpened;
        if ( today.before( startDate ) )
        {
            seanceOpened = BOOKING_TO_COME;
        }
        else if ( today.after( endDate ) )
        {
            seanceOpened = BOOKING_PASSED;
        }
        else
        {
            seanceOpened = BOOKING_OPENED;
        }
        return seanceOpened;
    }

    /**
     * Return html for booking bloc into show page
     * 
     * @param page
     *            xpage
     * @param request
     *            http request
     * @param locale
     *            locale
     * @return xpage
     */
    private String getBookingBloc( ShowDTO show, String sDateSeance, Locale locale )
    {

        if ( sDateSeance == null )
        {
            return "";
        }

        Date dateSeance;
        try
        {
            dateSeance = TicketsConstants.FORMAT_COMBO_DATE_SEANCE.parse( sDateSeance );
        }
        catch ( ParseException e )
        {
            throw new TechnicalException( "Erreur lors du parsing de la date de séance", e );
        }
        List<SeanceDTO> seanceList = _offerService.findSeanceByDate( show.getId( ), dateSeance );

        // Generates template
        Map<String, Object> model = new HashMap<String, Object>( );

        model.put( "seanceList", seanceList );
        // Add nb max purchase per offer type
        ReferenceList quantityList = getNumberList( AppPropertiesService.getPropertyInt(
                "stock-billetterie.nb_places_max.invitation", 2 ) );
        model.put( MAX_RESERVATION_INVITATION, quantityList );

        quantityList = getNumberList( AppPropertiesService.getPropertyInt(
                "stock-billetterie.nb_places_max.invitation_enfant", 2 ) );
        model.put( MAX_RESERVATION_INVITATION_ENFANT, quantityList );

        quantityList = getNumberList( AppPropertiesService.getPropertyInt(
                "stock-billetterie.nb_places_max.tarif_reduit", 2 ) );
        model.put( MAX_RESERVATION_TARIF_REDUIT, quantityList );

        model.put( "seance_date", sDateSeance );
        model.put( "show", show );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_DIR + "booking_bloc.html", locale, model );

        return template.getHtml( );
        // return "";
    }

    /**
     * Return a list of number from 0 to quantity
     * 
     * @param quantity the quantity
     * @return list of number
     */
    private ReferenceList getNumberList( Integer quantity )
    {
        ReferenceList quantityList = new ReferenceList( );
        for ( Integer i = 0; i <= quantity; i++ )
        {
            ReferenceItem refItem = new ReferenceItem( );
            refItem.setCode( i.toString( ) );
            refItem.setName( i.toString( ) );
            quantityList.add( refItem );
        }
        return quantityList;
    }

    private XPage getCurrentListShowPage( XPage page, HttpServletRequest request, Locale locale )
    {
        List<String> orderList = new ArrayList<String>( );
        orderList.add( "dateEnd" );
        List<ShowDTO> currentListShow = _showService.getCurrentProduct( orderList, null );
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( "show_list", currentListShow );
        model.put( "type_list", "aLaffiche" );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_DIR + "list_show_page.html", locale, model );
        page.setContent( template.getHtml( ) );

        String title = getMessage( TITLE_CURRENT_SHOW_LIST, request );
        page.setPathLabel( title );
        page.setTitle( title );

        return page;
    }

    private XPage getComeListShowPage( XPage page, HttpServletRequest request, Locale locale )
    {
        List<String> orderList = new ArrayList<String>( );
        orderList.add( "dateStart" );
        List<ShowDTO> comeListShow = _showService.getComeProduct( orderList, null );
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( "show_list", comeListShow );
        model.put( "type_list", "aVenir" );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_DIR + "list_show_page.html", locale, model );
        page.setContent( template.getHtml( ) );

        String title = getMessage( TITLE_COME_SHOW_LIST, request );
        page.setPathLabel( title );
        page.setTitle( title );

        return page;
    }
}