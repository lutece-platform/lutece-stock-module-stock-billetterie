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

import fr.paris.lutece.plugins.stock.utils.DateUtils;
import fr.paris.lutece.portal.service.util.AppPathService;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.util.UriUtils;


/**
 * Used for special solr queries
 * 
 * @author abataille
 */
public class BilletterieSolrSearch extends HttpServlet
{

    /**  
     *
     */
    private static final long serialVersionUID = -806886865678792151L;

    private static final String MARK_QUOI = "quoi";
    private static final String MARK_OU = "ou";
    private static final String MARK_QUAND = "quand";
    private static final String MARK_TARIF_REDUIT = "tarif_reduit";
    private static final String MARK_INVITATION = "invitation";
    private static final String MARK_INVITATION_ENFANT = "invitation_enfant";
    private static final String MARK_FULL_SHOW = "full_show";
    private static final String MARK_CATEGORY = "categorie";

    private static final String MARK_QUERY = "query";
    private static final String MARK_SORT = "sort";
    private static final String MARK_SORT_ALPHABETIQUE = "alpha";
    private static final String MARK_SORT_DATE = "date";

    private static final String MARK_TYPE_SEARCH = "type_search";

    private static final String CHECKBOX_ON = "on";
    private static final String SEARCH_SIMPLE = "simple";
    private static final String SEARCH_AVANCEE = "avancee";

    private static final Logger LOGGER = Logger.getLogger( BilletterieSolrSearch.class );

    /**
     * Get billetterie specific parameters and call Solr Module.
     * 
     * @param request the request
     * @param response the response
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException,
            IOException
    {
        StringBuilder sbReq = new StringBuilder( "" );
        sbReq.append( AppPathService.getBaseUrl( request ) ).append( "jsp/site/Portal.jsp?page=search-solr" );

        String sTypeSearch = request.getParameter( MARK_TYPE_SEARCH );
        String sTypeSort = request.getParameter( MARK_SORT );

        StringBuilder sbFilter = new StringBuilder( "" );
        StringBuilder sbSort = null;
        String sQuery = request.getParameter( MARK_QUERY );

        if ( StringUtils.isNotEmpty( sTypeSort ) && MARK_SORT_ALPHABETIQUE.equals( sTypeSort ) )
        {
            // sort by title
            sbSort = new StringBuilder( "&sort_name=title_string&sort_order=asc" );
        }
        else
        {
            // Sort by date
            sTypeSort = MARK_SORT_DATE;
            sbSort = new StringBuilder( "&sort_name=end_date&sort_order=desc" );
        }

        if ( StringUtils.isEmpty( sTypeSearch ) || SEARCH_SIMPLE.equals( sTypeSearch ) )
        {
            // simple search with facets 
            sTypeSearch = SEARCH_SIMPLE;
            String sQuoi = request.getParameter( MARK_QUOI );
            String sOu = request.getParameter( MARK_OU );
            String sQuand = request.getParameter( MARK_QUAND );

            if ( StringUtils.isNotEmpty( sQuery ) )
            {
                sbReq.append( "&query=" + sQuery );
            }

            if ( StringUtils.isNotEmpty( sQuoi ) )
            {
                sbFilter.append( sQuoi );
            }
            if ( StringUtils.isNotEmpty( sOu ) )
            {
                sbFilter.append( sOu );
            }
            if ( StringUtils.isNotEmpty( sQuand ) )
            {
                sbFilter.append( sQuand );
            }
        }
        else if ( SEARCH_AVANCEE.equals( sTypeSearch ) )
        {
            SimpleDateFormat sdfXml = new SimpleDateFormat( DateUtils.XML_DATE_FORMAT );
            String sTarifReduit = request.getParameter( MARK_TARIF_REDUIT );
            String sInvitation = request.getParameter( MARK_INVITATION );
            String sInvitationEnfant = request.getParameter( MARK_INVITATION_ENFANT );
            String sFullShow = request.getParameter( MARK_FULL_SHOW );
            String[] categories = request.getParameterValues( MARK_CATEGORY );

            String sShowDateStart = request.getParameter( "show_date_start" );
            String sShowDateEnd = request.getParameter( "show_date_end" );

            if ( CHECKBOX_ON.equals( sTarifReduit ) )
            {
                sbFilter.append( "&fq=tarif_reduit_string:true" );
            }

            if ( CHECKBOX_ON.equals( sInvitation ) )
            {
                sbFilter.append( "&fq=invitation_string:true" );
            }

            if ( CHECKBOX_ON.equals( sInvitationEnfant ) )
            {
                sbFilter.append( "&fq=invitation_enfant_string:true" );
            }

            if ( CHECKBOX_ON.equals( sFullShow ) )
            {
                sbFilter.append( "&fq=full_string:false" );
            }

            if ( StringUtils.isNotEmpty( sQuery ) && ArrayUtils.isEmpty( categories ) )
            {
                sbReq.append( "&query=" + sQuery );
            }
            else if ( StringUtils.isNotEmpty( sQuery ) && !ArrayUtils.isEmpty( categories ) )
            {
                sbReq.append( "&query=(" + sQuery );

                sbFilter.append( " AND categorie:(" );
                int i = 1;
                for ( String categorie : categories )
                {
                    if ( i < categories.length )
                    {
                        sbFilter.append( categorie.replace( " ", "*" ) ).append( " OR " );
                    }
                    else
                    {
                        sbFilter.append( categorie.replace( " ", "*" ) );
                    }
                    i++;
                }
                sbFilter.append( "))" );
            }
            else if ( !ArrayUtils.isEmpty( categories ) )
            {
                sbFilter.append( "&query=categorie:(" );
                int i = 1;
                for ( String categorie : categories )
                {
                    if ( i < categories.length )
                    {
                        sbFilter.append( categorie.replace( " ", "*" ) ).append( " OR " );
                    }
                    else
                    {
                        sbFilter.append( categorie.replace( " ", "*" ) );
                    }
                    i++;
                }
                sbFilter.append( ")" );
            }

            if ( StringUtils.isNotEmpty( sShowDateStart ) )
            {
                Timestamp showDateStart = DateUtils.getDate( sShowDateStart, true );
                String sXmlShowDateStart = sdfXml.format( showDateStart );
                sbFilter.append( "&fq=end_date:[" ).append( sXmlShowDateStart ).append( " TO *]" );
            }
            if ( StringUtils.isNotEmpty( sShowDateEnd ) )
            {

                Timestamp showDateEnd = DateUtils.getDate( sShowDateEnd, true );
                String sXmlShowDateEnd = sdfXml.format( showDateEnd );

                sbFilter.append( "&fq=start_date:[* TO " ).append( sXmlShowDateEnd ).append( "]" );
            }

        }

        StringBuilder sbType = new StringBuilder( "&type_search=" + sTypeSearch );

        if ( sbFilter.toString( ).isEmpty( ) && StringUtils.isEmpty( sQuery ) )
        {
            // Create default filter
            sbReq.append( "&query=*:*" );
        }
        else
        {
            sbReq.append( sbFilter.toString( ) );
        }

        sbReq.append( sbSort.toString( ) );
        sbReq.append( sbType.toString( ) );

        LOGGER.debug( "Requête SOLR de date, redirection vers " + sbReq.toString( ) );

        response.sendRedirect( UriUtils.encodeUri( sbReq.toString( ), "UTF-8" ) );
    }
}
