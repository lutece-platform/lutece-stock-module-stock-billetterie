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
package fr.paris.lutece.plugins.stock.modules.billetterie.business.portlet;

import fr.paris.lutece.portal.business.portlet.IPortletInterfaceDAO;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.business.portlet.PortletTypeHome;
import fr.paris.lutece.portal.service.spring.SpringContextService;


/**
 * The Class BilletteriePortletHome.
 */
public class BilletteriePortletHome extends PortletHome
{

    // Static variable pointed at the DAO instance
    private static IBilletterieDAO _dao = (IBilletterieDAO) SpringContextService.getPluginBean( "billetterie",
            "BilletteriePortletDAO" );

    /* This class implements the Singleton design pattern. */
    private static BilletteriePortletHome _singleton = null;

    /**
     * Constructor
     */
    public BilletteriePortletHome( )
    {
        // if ( _singleton == null )
        // {
        // _singleton = this;
        // }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.paris.lutece.portal.business.portlet.PortletHomeInterface#getPortletTypeId
     * ()
     */
    public String getPortletTypeId( )
    {

        String strCurrentClassName = this.getClass( ).getName( );
        String strPortletTypeId = PortletTypeHome.getPortletTypeId( strCurrentClassName );

        return strPortletTypeId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.paris.lutece.portal.business.portlet.PortletHomeInterface#getDAO()
     */
    public IPortletInterfaceDAO getDAO( )
    {
        return _dao;

    }

    /**
     * Gets the single instance of BilletteriePortletHome.
     * 
     * @return single instance of BilletteriePortletHome
     */
    public static PortletHome getInstance( )
    {
        if ( _singleton == null )
        {
            _singleton = new BilletteriePortletHome( );
        }

        return _singleton;
    }
}
