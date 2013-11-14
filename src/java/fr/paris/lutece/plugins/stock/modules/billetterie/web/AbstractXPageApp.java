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

import fr.paris.lutece.plugins.mylutece.authentication.MultiLuteceAuthentication;
import fr.paris.lutece.plugins.stock.commons.dao.PaginationProperties;
import fr.paris.lutece.plugins.stock.commons.dao.PaginationPropertiesImpl;
import fr.paris.lutece.plugins.stock.commons.exception.BusinessException;
import fr.paris.lutece.plugins.stock.commons.exception.FunctionnalException;
import fr.paris.lutece.plugins.stock.commons.exception.TechnicalException;
import fr.paris.lutece.plugins.stock.commons.exception.ValidationException;
import fr.paris.lutece.plugins.stock.modules.tickets.utils.constants.TicketsConstants;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.security.BasicLuteceUser;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.beanvalidation.BeanValidationUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Abstract class for jsp bean
 * @author abataille
 */
public abstract class AbstractXPageApp
{
    public static final String PARAMETER_NB_ITEMS_PER_PAGE = "items_per_page";
    public static final String DEFAULT_RESULTS_PER_PAGE = "10";
    public static final String DEFAULT_PAGE_INDEX = "1";
    public static final String PROPERTY_RESULTS_PER_PAGE = "search.nb.docs.per.page";

    private static final String AUTHENTIFICATION_ERROR_MESSAGE = "Impossible de détecter une authentification.";
    private static final String POPULATE_ERROR_MESSAGE = "Erreur lors de la recuperation des donnees du formulaire.";
    private static final String ERROR_MESSAGE_KEY = "module.stock.billetterie.validation.error";
    private static final String ERROR_TEMPLATE = "admin/plugins/stock/modules/billetterie/error.html";
    private static final String FIELD_MESSAGE_PREFIX = "module.stock.billetterie.field.";
    private static final String MARK_MESSAGE_LIST = "messageList";
    private static final Logger LOGGER = Logger.getLogger( AbstractXPageApp.class );

    /**
     * Populate a bean using parameters in http request
     * @param bean bean to populate
     * @param request http request
     */
    protected static void populate( Object bean, HttpServletRequest request )
    {
        try
        {
            BeanUtils.populate( bean, request.getParameterMap( ) );
        }
        catch ( IllegalAccessException e )
        {
            LOGGER.error( POPULATE_ERROR_MESSAGE, e );
        }
        catch ( InvocationTargetException e )
        {
            LOGGER.error( POPULATE_ERROR_MESSAGE, e );
        }
    }

    /**
     * Validate a bean using jsr 303 specs.
     * 
     * @param <T> the generic type
     * @param bean to validate
     * @throws ValidationException exception containing informations about
     *             errors and the bean
     */
    protected <T> void validate( T bean ) throws ValidationException
    {
        Set<ConstraintViolation<T>> constraintViolations = BeanValidationUtil.validate( bean );
        if ( constraintViolations.size( ) > 0 )
        {
            ValidationException ve = new ValidationException( bean );
            for ( ConstraintViolation<T> constraintViolation : constraintViolations )
            {
                ve.addConstraintViolation( constraintViolation );
            }
            throw ve;
        }
    }

    /**
     * Return authified user and throw technical exception if no user auth
     * found.
     * 
     * @param request http request
     * @return user lutece
     * @throws TechnicalException the technical exception
     */
    protected LuteceUser getUser( HttpServletRequest request ) throws TechnicalException
    {
        // FIXME à décommenter lorsque myLutece fonctionnera
        try
        {
            return SecurityService.getInstance( ).getRemoteUser( request );
        }
        catch ( UserNotSignedException e )
        {
            throw new TechnicalException( AUTHENTIFICATION_ERROR_MESSAGE, e );
        }
            // DEBUT BOUCHON
        // LuteceUser user = new BasicLuteceUser( "abataille@sopragroup.com",
        // new MultiLuteceAuthentication( ) );
        // user.setUserInfo( LuteceUser.NAME_GIVEN, "Alexis" );
        // user.setUserInfo( LuteceUser.NAME_FAMILY, "Bataille" );
        // user.setUserInfo( LuteceUser.HOME_INFO_ONLINE_EMAIL,
        // "abataille@sopragroup.com" );
        // return user;
            // FIN BOUCHON


    }

    /**
     * Return localized message.
     * 
     * @param key i18n key
     * @param request the request
     * @return localized message
     */
    protected String getMessage( String key, HttpServletRequest request )
    {
        return I18nService.getLocalizedString( key, request.getLocale( ) );
    }

    /**
     * Return localized message with args.
     * 
     * @param key i18n key
     * @param request the request
     * @param args args
     * @return localized message
     */
    protected String getMessage( String key, HttpServletRequest request, String... args )
    {
        return I18nService.getLocalizedString( key, args, request.getLocale( ) );
    }

    /**
     * Return html code for error message.
     * 
     * @param e the e
     * @param request the request
     * @return html
     */
    protected String getHtmlError( FunctionnalException e, HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<String, Object>( );
        List<String> messageList = new ArrayList<String>( );
        try
        {
            throw e;
        }
        // Validation error
        catch ( ValidationException ve )
        {
            String typeName = ve.getBean( ).getClass( ).getSimpleName( );

            // Add a validation error message using value, field name and
            // provided
            // message
            for ( ConstraintViolation<?> constraintViolation : ve.getConstraintViolationList( ) )
            {
                String fieldName = getMessage(
                        FIELD_MESSAGE_PREFIX + typeName + "." + constraintViolation.getPropertyPath( ), request );
                messageList.add( getMessage( ERROR_MESSAGE_KEY, request,
                        String.valueOf( constraintViolation.getInvalidValue( ) ), fieldName,
                        constraintViolation.getMessage( ) ) );
            }
        }
        // Business error
        catch ( BusinessException be )
        {
            messageList.add( getMessage( be.getCode( ), request, be.getArguments( ) ) );
        }

        model.put( MARK_MESSAGE_LIST, messageList );

        HtmlTemplate template = AppTemplateService.getTemplate( ERROR_TEMPLATE, request.getLocale( ), model );
        return template.getHtml( );
    }

    /**
     * Manage functionnal exception.
     * 
     * @param request the request
     * @param e the e
     * @param targetUrl the target url
     * @return the string
     */
    protected String manageFunctionnalException( HttpServletRequest request, FunctionnalException e, String targetUrl )
    {
        request.getSession( ).setAttribute( TicketsConstants.PARAMETER_ERROR, e );
        return targetUrl;
    }

    /**
     * Get validation error from session and remove from it
     * @param request http request
     * @return validation exception
     */
    protected FunctionnalException getErrorOnce( HttpServletRequest request )
    {
        FunctionnalException fe = (FunctionnalException) request.getSession( ).getAttribute(
                TicketsConstants.PARAMETER_ERROR );
        if ( fe != null )
        {
            request.getSession( ).removeAttribute( TicketsConstants.PARAMETER_ERROR );
        }
        return fe;
    }

    /**
     * Return a bean for pagination in service/dao using parameter in http
     * request
     * @param request http request
     * @return paginator the populate paginator
     */
    protected PaginationProperties getPaginationProperties( HttpServletRequest request )
    {
        String strPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, "1" );
        int nCurrentPageIndex = 1;
        if ( StringUtils.isNotEmpty( strPageIndex ) )
        {
            nCurrentPageIndex = Integer.valueOf( strPageIndex );
        }

        String strNbItemPerPage = request.getParameter( PARAMETER_NB_ITEMS_PER_PAGE );
        String strDefaultNbItemPerPage = AppPropertiesService.getProperty( PROPERTY_RESULTS_PER_PAGE,
                DEFAULT_RESULTS_PER_PAGE );
        strNbItemPerPage = ( strNbItemPerPage != null ) ? strNbItemPerPage : strDefaultNbItemPerPage;

        int nItemsPerPage = Integer.valueOf( strNbItemPerPage );

        return new PaginationPropertiesImpl( ( nCurrentPageIndex - 1 ) * nItemsPerPage, nItemsPerPage );
    }
}
