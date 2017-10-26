package org.superbiz.moviefun;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.superbiz.moviefun.albums.Album;
import org.superbiz.moviefun.albums.AlbumFixtures;
import org.superbiz.moviefun.albums.AlbumsBean;
import org.superbiz.moviefun.movies.Movie;
import org.superbiz.moviefun.movies.MovieFixtures;
import org.superbiz.moviefun.movies.MoviesBean;

import java.util.Map;

@Controller
public class HomeController {

    private final MoviesBean moviesBean;
    private final AlbumsBean albumsBean;
    private final MovieFixtures movieFixtures;
    private final AlbumFixtures albumFixtures;
    private PlatformTransactionManager movieTransactionManager;
    private PlatformTransactionManager albumTransactionManager;

    public HomeController(MoviesBean moviesBean, AlbumsBean albumsBean, MovieFixtures movieFixtures, AlbumFixtures albumFixtures, @Qualifier("movieTransactionManager") PlatformTransactionManager movieTransactionManager, @Qualifier("albumTransactionManager") PlatformTransactionManager albumTransactionManager) {
        this.moviesBean = moviesBean;
        this.albumsBean = albumsBean;
        this.movieFixtures = movieFixtures;
        this.albumFixtures = albumFixtures;
        this.movieTransactionManager = movieTransactionManager;
        this.albumTransactionManager = albumTransactionManager;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/setup")
    public String setup(Map<String, Object> model) {

        movieSetup();

        TransactionTemplate transactionTemplate = new TransactionTemplate(albumTransactionManager);
        for (Album album : albumFixtures.load()) {
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    albumsBean.addAlbum(album);
                }
            });
        }


        model.put("movies", moviesBean.getMovies());
        model.put("albums", albumsBean.getAlbums());

        return "setup";
    }

    private void movieSetup(){
        TransactionStatus status = movieTransactionManager.getTransaction(null);

        for (Movie movie : movieFixtures.load()) {
            moviesBean.addMovie(movie);
        }
        movieTransactionManager.commit(status);
    }
}
