/*
 * Copyright (c) 2002-2012, Mairie de Paris
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    // Beans
    private static final String BEAN_STOCK_TICKETS_SEANCE_SERVICE = "stock-tickets.seanceService";
    private static final String BEAN_STOCK_TICKETS_SHOW_SERVICE = "stock-tickets.showService";

    // Templates
    private static final String TEMPLATE_DIR = "skin/plugins/stock/modules/billetterie/";
    private static final String TEMPLATE_BOOKING_BLOC = "booking_bloc.html";
    private static final String TEMPLATE_LIST_SHOW_PAGE = "list_show_page.html";
    private static final String TEMPLATE_SHOW_PAGE = "show_page.html";

    // Parameters
    private static final String PARAMETER_DATE_SEANCE = "date_seance";
    private static final String PARAMETER_PRODUCT_ID = "product_id";
    private static final String PARAMETER_ACTION = "action";

    // Marks
    private static final String MARK_TYPE_LIST = "type_list";
    private static final String MARK_SHOW_LIST = "show_list";
    private static final String MARK_SEANCE_DATE = "seance_date";
    private static final String MARK_SEANCE_LIST = "seanceList";
    private static final String MARK_S_SEANCE_DATE = "sSeanceDate";
    private static final String MARK_BLOC_RESERVATION = "bloc_reservation";
    private static final String MARK_USER = "user";
    private static final String MARK_SEANCE_DATE_LIST = "seance_date_list";
    private static final String MARK_BOOKING_OPENED = "booking_opened";
    private static final String MARK_URL_POSTER = "url_poster";
    private static final String MARK_PARTNER = "partner";
    private static final String MARK_SHOW = "show";

    // Actions
    private static final String ACTION_SHOW_PAGE = "fiche-spectacle";
    private static final String ACTION_CURRENT_SHOW_LIST = "a-laffiche";
    private static final String ACTION_COME_SHOW_LIST = "a-venir";

    // Messages
    private static final String MESSAGE_ERROR_PARSING_SHOW_DATE = "Erreur lors du parsing de la date de séance";

    // Properties
    private static final String PROPERTY_POSTER_PATH = "stock-billetterie.poster.path";
    private static final String PROPERTY_POSTER_TB_PATH = "stock-billetterie.poster.tb.path";
    private static final String PROPERTY_NB_PLACES_MAX_TARIF_REDUIT = "stock-billetterie.nb_places_max.tarif_reduit";
    private static final String PROPERTY_NB_PLACES_MAX_INVITATION_ENFANT = "stock-billetterie.nb_places_max.invitation_enfant";
    private static final String PROPERTY_NB_PLACES_MAX_INVITATION = "stock-billetterie.nb_places_max.invitation";

    // Order filters
    private static final String ORDER_FILTER_DATE = "date";
    private static final String ORDER_FILTER_DATE_END = "dateEnd";
    private static final String ORDER_FILTER_DATE_START = "dateStart";

    // Constants
    private static final String TITLE_CURRENT_SHOW_LIST = "module.stock.billetterie.show_list.aLaffiche.title";
    private static final String TITLE_COME_SHOW_LIST = "module.stock.billetterie.show_list.aVenir.title";
    private static final String MAX_RESERVATION_INVITATION = "nb_max_invitation";
    private static final String MAX_RESERVATION_INVITATION_ENFANT = "nb_max_invitation_enfant";
    private static final String MAX_RESERVATION_TARIF_REDUIT = "nb_max_tarif_reduit";

    private static final int BOOKING_OPENED = 0;
    private static final int BOOKING_TO_COME = 1;
    private static final int BOOKING_PASSED = -1;

    private static final String TYPE_A_VENIR = "aVenir";
    private static final String TYPE_A_LAFFICHE = "aLaffiche";

    private IShowService _showService = (IShowService) SpringContextService.getContext( ).getBean(
            BEAN_STOCK_TICKETS_SHOW_SERVICE );
    private IProviderService _providerService = (IProviderService) SpringContextService.getContext( ).getBean(
            IProviderService.class );
    private ISeanceService _offerService = (ISeanceService) SpringContextService.getContext( ).getBean(
            BEAN_STOCK_TICKETS_SEANCE_SERVICE );


    /**
     * Return page with action specified.
     * 
     * @param request the request
     * @param nMode the n mode
     * @param plugin the plugin
     * @return the page
     * @throws UserNotSignedException the user not signed exception
     * @throws SiteMessageException the site message exception
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

        model.put( MARK_SHOW, show );
        
        model.put( MARK_PARTNER, _providerService.findByIdWithProducts( show.getIdProvider( ) ) );
        model.put( MARK_URL_POSTER, AppPropertiesService.getProperty( PROPERTY_POSTER_PATH ) );

        // Calculate if show is open to book
        model.put( MARK_BOOKING_OPENED, caculateBookingOpened( show ) );

        // Get seance list
        SeanceFilter filter = new SeanceFilter( );
        filter.setOrderAsc( false );
        List<String> orderList = new ArrayList<String>( );
        orderList.add( ORDER_FILTER_DATE );
        filter.setOrders( orderList );
        model.put( MARK_SEANCE_DATE_LIST, _offerService.findSeanceByShow( show.getId( ), filter ) );

        // If user authenticated, display booking bloc
        model.put( MARK_USER, getUser( request ) );

        model.put( MARK_BLOC_RESERVATION, getBookingBloc( show, sSeanceDate, locale ) );
        model.put( MARK_S_SEANCE_DATE, sSeanceDate );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_DIR + TEMPLATE_SHOW_PAGE, locale, model );

        page.setContent( template.getHtml( ) );
        page.setPathLabel( show.getName( ) );
        page.setTitle( show.getName( ) );

        return page;
    }

    /**
     * Calculate if show is open to book.
     * 
     * @param show the show
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
     * Return html for booking bloc into show page.
     * 
     * @param show the show
     * @param sDateSeance the s date seance
     * @param locale locale
     * @return xpage
     */
    private String getBookingBloc( ShowDTO show, String sDateSeance, Locale locale )
    {

        DateFormat sdfComboSeance = new SimpleDateFormat( TicketsConstants.FORMAT_COMBO_DATE_SEANCE );
        if ( sDateSeance == null )
        {
            return StringUtils.EMPTY;
        }

        Date dateSeance;
        try
        {
            dateSeance = sdfComboSeance.parse( sDateSeance );
        }
        catch ( ParseException e )
        {
            throw new TechnicalException( MESSAGE_ERROR_PARSING_SHOW_DATE, e );
        }
        List<SeanceDTO> seanceList = _offerService.findSeanceByDate( show.getId( ), dateSeance );

        // Generates template
        Map<String, Object> model = new HashMap<String, Object>( );

        model.put( MARK_SEANCE_LIST, seanceList );
        // Add nb max purchase per offer type
        ReferenceList quantityList = new ReferenceList( );
        ReferenceItem refItem = new ReferenceItem( );
        String strInteger = String.valueOf( 0 );
        refItem.setCode( strInteger );
        refItem.setName( strInteger );
        quantityList.add( refItem );
        refItem = new ReferenceItem( );
        strInteger = AppPropertiesService.getProperty( PROPERTY_NB_PLACES_MAX_INVITATION, "2" );
        refItem.setCode( strInteger );
        refItem.setName( strInteger );
        quantityList.add( refItem );
        model.put( MAX_RESERVATION_INVITATION, quantityList );

        quantityList = getNumberList( AppPropertiesService.getPropertyInt(
                PROPERTY_NB_PLACES_MAX_INVITATION_ENFANT, 2 ) );
        model.put( MAX_RESERVATION_INVITATION_ENFANT, quantityList );

        quantityList = getNumberList( AppPropertiesService.getPropertyInt(
                PROPERTY_NB_PLACES_MAX_TARIF_REDUIT, 2 ) );
        model.put( MAX_RESERVATION_TARIF_REDUIT, quantityList );

        model.put( MARK_SEANCE_DATE, sDateSeance );
        model.put( MARK_SHOW, show );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_DIR + TEMPLATE_BOOKING_BLOC, locale, model );

        return template.getHtml( );
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

    /**
     * Gets the current list show page.
     * 
     * @param page the page
     * @param request the request
     * @param locale the locale
     * @return the current list show page
     */
    private XPage getCurrentListShowPage( XPage page, HttpServletRequest request, Locale locale )
    {
        List<String> orderList = new ArrayList<String>( );
        orderList.add( ORDER_FILTER_DATE_END );
        List<ShowDTO> currentListShow = _showService.getCurrentProduct( orderList, null );
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_SHOW_LIST, currentListShow );
        model.put( MARK_TYPE_LIST, TYPE_A_LAFFICHE );
        model.put( MARK_URL_POSTER, AppPropertiesService.getProperty( PROPERTY_POSTER_TB_PATH ) );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_DIR + TEMPLATE_LIST_SHOW_PAGE, locale, model );
        page.setContent( template.getHtml( ) );

        String title = getMessage( TITLE_CURRENT_SHOW_LIST, request );
        page.setPathLabel( title );
        page.setTitle( title );

        return page;
    }

    /**
     * Gets the come list show page.
     * 
     * @param page the page
     * @param request the request
     * @param locale the locale
     * @return the come list show page
     */
    private XPage getComeListShowPage( XPage page, HttpServletRequest request, Locale locale )
    {
        List<String> orderList = new ArrayList<String>( );
        orderList.add( ORDER_FILTER_DATE_START );
        List<ShowDTO> comeListShow = _showService.getComeProduct( orderList, null );
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_SHOW_LIST, comeListShow );
        model.put( MARK_TYPE_LIST, TYPE_A_VENIR );
        model.put( MARK_URL_POSTER, AppPropertiesService.getProperty( PROPERTY_POSTER_TB_PATH ) );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_DIR + TEMPLATE_LIST_SHOW_PAGE, locale, model );
        page.setContent( template.getHtml( ) );

        String title = getMessage( TITLE_COME_SHOW_LIST, request );
        page.setPathLabel( title );
        page.setTitle( title );

        return page;
    }
}