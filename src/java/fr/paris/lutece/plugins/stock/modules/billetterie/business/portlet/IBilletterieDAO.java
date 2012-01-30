package fr.paris.lutece.plugins.stock.modules.billetterie.business.portlet;

import fr.paris.lutece.portal.business.portlet.IPortletInterfaceDAO;
import fr.paris.lutece.portal.business.portlet.Portlet;


public interface IBilletterieDAO extends IPortletInterfaceDAO
{

    public void insert( Portlet portlet );

    public void delete( int nPortletId );

    public Portlet load( int nPortletId );

    public void store( Portlet portlet );

}
