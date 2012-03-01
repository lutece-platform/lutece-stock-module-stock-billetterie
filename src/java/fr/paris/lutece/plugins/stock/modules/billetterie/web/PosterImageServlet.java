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

import fr.paris.lutece.plugins.stock.modules.tickets.service.IShowService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Used for special solr queries
 * 
 * @author abataille
 */
public class PosterImageServlet extends HttpServlet
{

    /**  
     *
     */
    private static final long serialVersionUID = -1165966480942742905L;
    private static final Logger LOGGER = Logger.getLogger( PosterImageServlet.class );

    /** The product service. */
    private IShowService _productService = (IShowService) SpringContextService
            .getBean( "stock-tickets.showService" );

    /**
     * Returns poster image
     * 
     * @param request the request
     * @param response the response
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException,
            IOException
    {
        String sIdProduct = request.getParameter( "product_id" );
        if ( StringUtils.isNotEmpty( sIdProduct ) )
        {
            Integer idProduct = Integer.parseInt( sIdProduct );
            boolean isThumbnail = request.getParameter( "tb" ) != null && request.getParameter( "tb" ).equals( "true" );
            byte[] bImage;
            if ( isThumbnail )
            {
                bImage = _productService.getTbImage( idProduct );
            }
            else
            {
                bImage = _productService.getImage( idProduct );
            }
            response.setContentLength( bImage.length );
            response.setContentType( "image/jpeg" );

            ServletOutputStream os = response.getOutputStream( );
            IOUtils.write( bImage, os );
            os.close( );
        }
        else
        {
            LOGGER.error( "Appel de PosterImageServlet avec un id de produit null" );
        }
    }

}
