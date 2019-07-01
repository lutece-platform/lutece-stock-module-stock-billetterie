package fr.paris.lutece.plugins.stock.modules.billetterie.business;

import fr.paris.lutece.plugins.extend.modules.rating.business.Rating;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;

public class RatingProductDAO implements IRatingProductDAO {

    public static final String SQL_ALL_PRODUCT_HAVE_RATING =
            "SELECT  p.name," +
                    " r.id_rating, r.id_resource, r.resource_type, r.vote_count, r.score_value, r.score_positifs_votes, r.score_negatives_votes" +
                    " FROM stock_product p, extend_rating r" +
                    " WHERE p.id_product = r.id_resource and r.resource_type = \"stock-product\"";

    @Override
    public List<RatingProductDTO> getAllRatingProduct() {
        List<RatingProductDTO> lstRatingProductDTO = new ArrayList<>();
        Rating rating = null;
        RatingProductDTO ratingProductDTO = null;

        int nIndex = 1;
        DAOUtil daoUtil = new DAOUtil( SQL_ALL_PRODUCT_HAVE_RATING );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            nIndex = 1;

            ratingProductDTO = new RatingProductDTO();
            ratingProductDTO.setName(daoUtil.getString( nIndex++ ) );

            rating = new Rating(  );
            rating.setIdRating( daoUtil.getInt( nIndex++ ) );
            rating.setIdExtendableResource( daoUtil.getString( nIndex++ ) );
            rating.setExtendableResourceType( daoUtil.getString( nIndex++ ) );
            rating.setVoteCount( daoUtil.getInt( nIndex++ ) );
            rating.setScoreValue( daoUtil.getDouble( nIndex++ ) );
            rating.setScorePositifsVotes( daoUtil.getInt( nIndex++ ) );
            rating.setScoreNegativesVotes( daoUtil.getInt( nIndex ) );

            ratingProductDTO.setVoteCount(rating.getVoteCount());
            ratingProductDTO.setRating(rating.getAverageScoreRoundToHalf());

            lstRatingProductDTO.add(ratingProductDTO);
        }

        daoUtil.free(  );

        return lstRatingProductDTO;
    }
}
