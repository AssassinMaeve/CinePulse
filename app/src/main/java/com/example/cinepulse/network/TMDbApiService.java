package com.example.cinepulse.network;

import com.example.cinepulse.models.CastResponse;
import com.example.cinepulse.models.MovieDetail;
import com.example.cinepulse.models.MovieResponse;
import com.example.cinepulse.models.MultiSearchResponse;
import com.example.cinepulse.models.ReviewResponse;
import com.example.cinepulse.models.TVDetail;
import com.example.cinepulse.models.TrailerResponse;
import com.example.cinepulse.models.TvShowResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TMDbApiService {

    /* Movie Endpoints */
    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(
            @Query("api_key") String apiKey);

    @GET("movie/upcoming")
    Call<MovieResponse> getUpcomingMovies(@Query("api_key") String apiKey);

    @GET("movie/{movie_id}")
    Call<MovieDetail> getMovieDetail(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("trending/movie/week")
    Call<MovieResponse> getTrendingMovies(@Query("api_key") String apiKey);

    @GET("search/movie")
    Call<MovieResponse> searchMovies(
            @Query("api_key") String apiKey,
            @Query("query") String query
    );

    @GET("movie/{movie_id}/videos")
    Call<TrailerResponse> getMovieTrailers(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("movie/{movie_id}/credits")
    Call<CastResponse> getMovieCredits(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("discover/movie")
    Call<MovieResponse> getMoviesByGenre(
            @Query("with_genres") int genreId,
            @Query("api_key") String apiKey
    );

    /* TV Show Endpoints */
    @GET("tv/popular")
    Call<TvShowResponse> getPopularTV(@Query("api_key") String apiKey);

    @GET("tv/on_the_air")
    Call<TvShowResponse> getUpcomingTV(@Query("api_key") String apiKey);

    @GET("tv/airing_today")
    Call<TvShowResponse> getAiringTodayTV(@Query("api_key") String apiKey);

    @GET("trending/tv/week")
    Call<TvShowResponse> getTrendingTVShows(@Query("api_key") String apiKey);

    @GET("tv/{tv_id}")
    Call<TVDetail> getTVDetail(
            @Path("tv_id") int tvId,
            @Query("api_key") String apiKey
    );

    @GET("tv/{tv_id}/videos")
    Call<TrailerResponse> getTVTrailers(
            @Path("tv_id") int tvId,
            @Query("api_key") String apiKey
    );

    @GET("tv/{tv_id}/credits")
    Call<CastResponse> getTVCredits(
            @Path("tv_id") int tvId,
            @Query("api_key") String apiKey
    );

    @GET("discover/tv")
    Call<TvShowResponse> getTVByGenre(
            @Query("with_genres") int genreId,
            @Query("api_key") String apiKey
    );

    /* Multi-purpose Endpoints */
    @GET("search/multi")
    Call<MultiSearchResponse> searchMulti(
            @Query("api_key") String apiKey,
            @Query("query") String query
    );

    @GET("movie/{movie_id}/reviews")
    Call<ReviewResponse> getMovieReviews(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("tv/{tv_id}/reviews")
    Call<ReviewResponse> getTVReviews(
            @Path("tv_id") int tvId,
            @Query("api_key") String apiKey
    );

    @GET("{type}/{id}/reviews")
    Call<ReviewResponse> getReviews(
            @Path("type") String type,  // must be either "movie" or "tv"
            @Path("id") int id,
            @Query("api_key") String apiKey
    );

}
