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
package fr.paris.lutece.plugins.stock.modules.billetterie.web;

import fr.paris.lutece.plugins.stock.business.product.Product;
import fr.paris.lutece.plugins.stock.business.subscription.SubscriptionProduct;
import fr.paris.lutece.plugins.stock.business.subscription.SubscriptionProductFilter;
import fr.paris.lutece.plugins.stock.commons.exception.FunctionnalException;
import fr.paris.lutece.plugins.stock.modules.tickets.service.IShowService;
import fr.paris.lutece.plugins.stock.service.ISubscriptionProductService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;


/**
 * This class provides the user interface to manage form features ( manage,
 * create, modify, remove)
 * @author jchaline
 */
public class SubscriptionProductJspBean  extends AbstractJspBean
{
    public static final Logger LOGGER = Logger.getLogger( SubscriptionProductJspBean.class );
    
    // PARAMETERS
    public static final String PARAMETER_PRODUCT_ID = "product_id";

    /** The Constant JSP_SAVE_PRODUCT. */
    private static final String JSP_PORTAL = "Portal.jsp";

    // BEANS
    private static final String BEAN_STOCK_TICKETS_SHOW_SERVICE = "stock-tickets.showService";

    // SERVICE
    private IShowService _serviceProduct;
    private ISubscriptionProductService _subscriptionProductService;

    /**
     * Instantiates a new offer jsp bean.
     */
    public SubscriptionProductJspBean(  )
    {
        super(  );

        _serviceProduct = (IShowService) SpringContextService.getBean( BEAN_STOCK_TICKETS_SHOW_SERVICE );
        _subscriptionProductService = (ISubscriptionProductService) SpringContextService.getContext( ).getBean(
                ISubscriptionProductService.class );
    }

    /**
     * Create the subscription for the user
     * @param request the http request
     * @param currentUser the user who subscribe
     * @return an error if occur, null otherwise
     */
    public String doSubscribeToProduct( HttpServletRequest request, LuteceUser currentUser )
    {
        String strIdProduct = request.getParameter( PARAMETER_PRODUCT_ID );
        int idProduct = Integer.valueOf( strIdProduct );

        Product productToSubscribe = _serviceProduct.findById( idProduct ).convert( );
        SubscriptionProduct subscriptionProduct = new SubscriptionProduct( );
        subscriptionProduct.setProduct( productToSubscribe );
        subscriptionProduct.setUserName( currentUser.getUserInfo( LuteceUser.HOME_INFO_ONLINE_EMAIL ) );
        try
        {
            _subscriptionProductService.doSaveSubscriptionProduct( subscriptionProduct );
        }
        catch ( FunctionnalException e )
        {
            return manageFunctionnalException( request, e, JSP_PORTAL );
        }

        return null;
    }

    /**
     * Create the subscription for the user
     * @param request the http request
     * @param currentUser the user who subscribe
     * @return an error if occur, null otherwise
     */
    public String doUnsubscribeToProduct( HttpServletRequest request, LuteceUser currentUser )
    {
        String strIdProduct = request.getParameter( PARAMETER_PRODUCT_ID );
        int idProduct = Integer.valueOf( strIdProduct );

        Product productSubscribe = _serviceProduct.findById( idProduct ).convert( );
        String nameUser = currentUser.getUserInfo( LuteceUser.HOME_INFO_ONLINE_EMAIL );
        SubscriptionProductFilter filter = new SubscriptionProductFilter( );
        filter.setNameUser( nameUser );
        filter.setProduct( productSubscribe );
        List<SubscriptionProduct> subscriptionfindByFilter = _subscriptionProductService.findByFilter( filter );
        if ( subscriptionfindByFilter.size( ) == 1 )
        {
            Integer idSubscription = subscriptionfindByFilter.get( 0 ).getId( );
            try
            {
                _subscriptionProductService.doDeleteSubscriptionProduct( idSubscription );
            }
            catch ( FunctionnalException e )
            {
                return manageFunctionnalException( request, e, JSP_PORTAL );
            }
        }

        return null;
    }

    /**
     * Say if an user is subscribe to a product
     * @param request the http request
     * @param currentUser the user
     * @return true if the user subscribe to the product
     */
    public boolean isSubscribeToProduct( HttpServletRequest request, LuteceUser currentUser )
    {
        String strIdProduct = request.getParameter( PARAMETER_PRODUCT_ID );
        int idProduct = Integer.valueOf( strIdProduct );
        String nameUser = currentUser.getUserInfo( LuteceUser.HOME_INFO_ONLINE_EMAIL );
        Product productSubscribe = _serviceProduct.findById( idProduct ).convert( );

        SubscriptionProductFilter filter = new SubscriptionProductFilter( );
        filter.setNameUser( nameUser );
        filter.setProduct( productSubscribe );

        return _subscriptionProductService.findByFilter( filter ).size( ) > 0;
    }

    /**
     * Get all subscription to products for an user
     * @param request the http request
     * @param currentUser the user
     * @return a page
     */
    public String getSubscriptionToProduct( HttpServletRequest request, LuteceUser currentUser )
    {
        String page = "";
        String userName = currentUser.getName( );
        SubscriptionProductFilter filterSubscription = new SubscriptionProductFilter( );
        filterSubscription.setNameUser( userName );
        for ( SubscriptionProduct subscription : _subscriptionProductService.findByFilter( filterSubscription ) )
        {
            page += subscription.getUserName( ) + ":" + subscription.getProduct( ) + "\n";
        }

        return page;
    }
}
