/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import java.util.List;

import fr.paris.lutece.plugins.stock.business.product.Product;
import fr.paris.lutece.plugins.stock.service.ISubscriptionProductService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage form features ( manage,
 * create, modify, remove)
 */
public class SubscriptionProductJspBean extends AbstractJspBean
{
    public static final Logger LOGGER = Logger.getLogger( SubscriptionProductJspBean.class );

    // PARAMETERS
    public static final String PARAMETER_PRODUCT_ID = "product_id";
    private static final long serialVersionUID = -4663062407048172927L;

    /** The Constant JSP_SAVE_PRODUCT. */
    private static final String JSP_PORTAL = "Portal.jsp";

    // BEANS
    private static final String BEAN_STOCK_TICKETS_SHOW_SERVICE = "stock-tickets.showService";

    // SERVICE
    private ISubscriptionProductService _subscriptionProductService;

    /**
     * Instantiates a new offer jsp bean.
     */
    public SubscriptionProductJspBean(  )
    {
        super(  );

        _subscriptionProductService = SpringContextService.getContext(  ).getBean( ISubscriptionProductService.class );
    }

    /**
     * Create the subscription for the user
     * @param request the HTTP request
     * @param currentUser the user who subscribe
     * @return an error if occur, null otherwise
     */
    public String doSubscribeToProduct( HttpServletRequest request, LuteceUser currentUser )
    {
        String strIdProduct = request.getParameter( PARAMETER_PRODUCT_ID );

        if ( StringUtils.isNotEmpty( strIdProduct ) && StringUtils.isNumeric( strIdProduct ) )
        {
        	String strEmailHome = currentUser.getUserInfo( LuteceUser.HOME_INFO_ONLINE_EMAIL );
        	String strEmailBusiness = currentUser.getUserInfo( LuteceUser.BUSINESS_INFO_ONLINE_EMAIL );
        	String strEmail = !strEmailHome.equals("") ? strEmailHome : strEmailBusiness;
        	_subscriptionProductService.doSaveSubscriptionProduct( strEmail, strIdProduct );
        }

        return null;
    }

    /**
     * Create the subscription for the user
     * @param request the HTTP request
     * @param currentUser the user who subscribe
     * @return an error if occur, null otherwise
     */
    public String doUnsubscribeToProduct( HttpServletRequest request, LuteceUser currentUser )
    {
        String strIdProduct = request.getParameter( PARAMETER_PRODUCT_ID );

        if ( StringUtils.isNotEmpty( strIdProduct ) && StringUtils.isNumeric( strIdProduct ) )
        {
        	String strEmailHome = currentUser.getUserInfo( LuteceUser.HOME_INFO_ONLINE_EMAIL );
        	String strEmailBusiness = currentUser.getUserInfo( LuteceUser.BUSINESS_INFO_ONLINE_EMAIL );
        	String strEmail = !strEmailHome.equals("") ? strEmailHome : strEmailBusiness;
        	_subscriptionProductService.doDeleteSubscriptionProduct( strEmail, strIdProduct );
        }

        return null;
    }

    /**
     * Say if an user is subscribe to a product
     * @param request the HTTP request
     * @param currentUser the user
     * @return true if the user subscribe to the product
     */
    public boolean isSubscribeToProduct( HttpServletRequest request, LuteceUser currentUser )
    {
        String strIdProduct = request.getParameter( PARAMETER_PRODUCT_ID );
        
        String strEmailHome = currentUser.getUserInfo( LuteceUser.HOME_INFO_ONLINE_EMAIL );
    	String strEmailBusiness = currentUser.getUserInfo( LuteceUser.BUSINESS_INFO_ONLINE_EMAIL );
    	String strEmail = !strEmailHome.equals("") ? strEmailHome : strEmailBusiness;

        return _subscriptionProductService.hasUserSubscribedToProduct( strEmail, strIdProduct );
    }

    /**
     * Get all subscription to products for an user
     * @param request the HTTP request
     * @param currentUser the user
     * @return a page
     */
    public String getSubscriptionToProduct( HttpServletRequest request, LuteceUser currentUser )
    {
        StringBuilder page = new StringBuilder(  );
        String strEmailHome = currentUser.getUserInfo( LuteceUser.HOME_INFO_ONLINE_EMAIL );
    	String strEmailBusiness = currentUser.getUserInfo( LuteceUser.BUSINESS_INFO_ONLINE_EMAIL );
    	
    	String strEmail = !strEmailHome.equals("") ? strEmailHome : strEmailBusiness;
    	
       
        for ( Product product : _subscriptionProductService.getProductsByUserSubscription( strEmail ) )
        {
            page.append( strEmail );
            page.append( ":" );
            page.append( product );
            page.append( "\n" );
        }

        return page.toString(  );
    }
    
    /**
     * Get all subscription to products for an user
     * @param request the HTTP request
     * @param currentUser the user
     * @return a page
     */
    public List<Product> getProductsSubscribedByUser( LuteceUser currentUser )
    {
        //StringBuilder page = new StringBuilder(  );
        String strEmailHome = currentUser.getUserInfo( LuteceUser.HOME_INFO_ONLINE_EMAIL );
    	String strEmailBusiness = currentUser.getUserInfo( LuteceUser.BUSINESS_INFO_ONLINE_EMAIL );
    	
    	String strEmail = !strEmailHome.equals("") ? strEmailHome : strEmailBusiness;
       
        /*for ( Product product : _subscriptionProductService.getProductsByUserSubscription( strEmail ) )
        {
            page.append( strEmail );
            page.append( ":" );
            page.append( product );
            page.append( "\n" );
        }

        return page.toString(  );*/
    	
    	return _subscriptionProductService.getProductsByUserSubscription( strEmail );
    }
}
