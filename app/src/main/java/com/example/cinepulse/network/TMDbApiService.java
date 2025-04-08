package com.example.cinepulse;

import com.example.cinepulse.models.MovieDetails;
import com.example.cinepulse.models.MovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TMDbApiService {

    @GET("movie/{movie_id}")
    Call<MovieDetails> getMovieDetails(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("trending/movie/week")
    Call<MovieResponse> getTrendingMovies(@Query("api_key") String apiKey);

    @GET("trending/tv/week")
    Call<MovieResponse> getTrendingTVShows(@Query("api_key") String apiKey);
}
