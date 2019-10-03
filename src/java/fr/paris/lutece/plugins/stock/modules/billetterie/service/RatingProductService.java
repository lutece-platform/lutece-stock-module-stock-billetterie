package fr.paris.lutece.plugins.stock.modules.billetterie.service;

import fr.paris.lutece.plugins.stock.modules.billetterie.business.IRatingProductDAO;
import fr.paris.lutece.plugins.stock.modules.billetterie.business.RatingProductDTO;

import javax.inject.Inject;
import java.util.List;

public class RatingProductService implements IRatingProductService
{

    @Inject
    private IRatingProductDAO _ratingProductDAO;

    @Override
    public List<RatingProductDTO> getAllRatingProduct( )
    {
        return _ratingProductDAO.getAllRatingProduct( );
    }

}
