package com.example.cinepulse.network;

import com.example.cinepulse.models.*;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit interface for communicating with The Movie Database (TMDb) API.
 */
public interface TMDbApiService {

    /* ---------------------- Movie Endpoints ---------------------- */

    /** Get popular movies */
    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(@Query("api_key") String apiKey);

    /** Get trending movies (weekly) */
    @GET("trending/movie/week")
    Call<MovieResponse> getTrendingMovies(@Query("api_key") String apiKey);

    /** Get detailed information about a specific movie */
    @GET("movie/{movie_id}")
    Call<MovieDetail> getMovieDetail(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    /** Get trailers for a movie */
    @GET("movie/{movie_id}/videos")
    Call<TrailerResponse> getMovieTrailers(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    /** Get cast for a movie */
    @GET("movie/{movie_id}/credits")
    Call<CastResponse> getMovieCredits(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    /** Discover movies by genre */
    @GET("discover/movie")
    Call<MovieResponse> getMoviesByGenre(
            @Query("with_genres") int genreId,
            @Query("api_key") String apiKey
    );

    /** Get reviews for a movie */
    @GET("movie/{movie_id}/reviews")
    Call<ReviewResponse> getMovieReviews(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    /** Get streaming providers for a movie */
    @GET("movie/{movie_id}/watch/providers")
    Call<WatchProviderResponse> getMovieWatchProviders(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    /* ---------------------- TV Show Endpoints ---------------------- */

    /** Get trending TV shows (weekly) */
    @GET("trending/tv/week")
    Call<TvShowResponse> getTrendingTVShows(@Query("api_key") String apiKey);

    /** Get detailed information about a specific TV show */
    @GET("tv/{tv_id}")
    Call<TVDetail> getTVDetail(
            @Path("tv_id") int tvId,
            @Query("api_key") String apiKey
    );

    /** Get trailers for a TV show */
    @GET("tv/{tv_id}/videos")
    Call<TrailerResponse> getTVTrailers(
            @Path("tv_id") int tvId,
            @Query("api_key") String apiKey
    );

    /** Get cast for a TV show */
    @GET("tv/{tv_id}/credits")
    Call<CastResponse> getTVCredits(
            @Path("tv_id") int tvId,
            @Query("api_key") String apiKey
    );

    /** Discover TV shows by genre */
    @GET("discover/tv")
    Call<TvShowResponse> getTVByGenre(
            @Query("with_genres") int genreId,
            @Query("api_key") String apiKey
    );

    /** Get reviews for a TV show */
    @GET("tv/{tv_id}/reviews")
    Call<ReviewResponse> getTVReviews(
            @Path("tv_id") int tvId,
            @Query("api_key") String apiKey
    );

    /** Get streaming providers for a TV show */
    @GET("tv/{tv_id}/watch/providers")
    Call<WatchProviderResponse> getTVWatchProviders(
            @Path("tv_id") int tvId,
            @Query("api_key") String apiKey
    );

    /* ---------------------- Multi-purpose Endpoints ---------------------- */

    /** Multi-search across movies, TV shows, and people */
    @GET("search/multi")
    Call<MultiSearchResponse> searchMulti(
            @Query("api_key") String apiKey,
            @Query("query") String query
    );

}
