package io.github.jitinsharma.reduxmovieexample.middlewares

import io.github.jitinsharma.reduxmovieexample.actions.InitializeMovieList
import io.github.jitinsharma.reduxmovieexample.actions.LoadTopRatedMovies
import io.github.jitinsharma.reduxmovieexample.helpers.API_KEY
import io.github.jitinsharma.reduxmovieexample.models.MovieResponse
import io.github.jitinsharma.reduxmovieexample.network.ApiClient
import io.github.jitinsharma.reduxmovieexample.network.ApiInterface
import io.github.jitinsharma.reduxmovieexample.states.AppState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import tw.geothings.rekotlin.DispatchFunction
import tw.geothings.rekotlin.Middleware

/**
 * Created by jsharma on 18/01/18.
 */

internal val networkMiddleWare: Middleware<AppState> = { dispatch, _ ->
    { next ->
        { action ->
            when (action) {
                is LoadTopRatedMovies -> {
                    callTopRatedMovies(dispatch)
                }
            }
            next(action)
        }
    }
}

private fun callTopRatedMovies(dispatch: DispatchFunction) {
    val apiService = ApiClient.client?.create(ApiInterface::class.java)
    val call = apiService?.discoverMovies(API_KEY)

    call?.enqueue(object : Callback<MovieResponse> {
        override fun onFailure(call: Call<MovieResponse>?, t: Throwable?) {
            Timber.e(t)
        }

        override fun onResponse(call: Call<MovieResponse>?, response: Response<MovieResponse>?) {
            val movieObjects = response?.body()?.results
            movieObjects?.let {
                dispatch(InitializeMovieList(it))
            }
        }
    })
}