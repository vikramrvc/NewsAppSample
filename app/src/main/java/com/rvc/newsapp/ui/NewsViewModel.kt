package com.rvc.newsapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rvc.newsapp.model.Article
import com.rvc.newsapp.model.NewsResponse
import com.rvc.newsapp.repo.NewsRepo
import com.rvc.newsapp.util.Resource
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

class NewsViewModel(app:Application, val newsRepo: NewsRepo) : AndroidViewModel(app) {

    val headlines : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var headlinesPage =1
    var headlinesResponse :NewsResponse?=null


    val searchNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage =1
    var searchNewsResponse :NewsResponse?=null
    var newSearchQuery:String?=null
    var oldSearchQuery:String?=null

    init {
        getHeadlines("us")
    }

    fun getHeadlines(countryCode: String) = viewModelScope.launch {
            headLinesInternet(countryCode)
        }


    fun getSearchData(searchQuery: String) =viewModelScope.launch {
            searchNewsInternet(searchQuery)
        }


    private fun handleHeadlinesResponse(response: Response<NewsResponse>): Resource<NewsResponse>
    {
        if(response.isSuccessful)
        {
            response.body()?.let {
                resultResponse->
                headlinesPage++
                if(headlinesResponse==null)
                {
                    headlinesResponse = resultResponse
                }else
                {
                    val oldArticles = headlinesResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(headlinesResponse?:resultResponse )
            }
        }
        return Resource.Error(response.message())

    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>):Resource<NewsResponse>
    {
        if(response.isSuccessful)
        {
            response.body()?.let {
                resultResponse->

                if(searchNewsResponse==null || newSearchQuery!=oldSearchQuery)
                {
                    searchNewsResponse = resultResponse
                    searchNewsPage=1
                    oldSearchQuery = newSearchQuery
                }else
                {
                    searchNewsPage++

                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse?:resultResponse)

            }
        }

        return Resource.Error(response.message())
    }

    fun addToFavourites(article: Article)
    {
        viewModelScope.launch {
            newsRepo.upsert(article)
        }
    }

    fun getFavourites()= newsRepo.getFavArticle()

    fun deleteArticle(article: Article)
    {
        viewModelScope.launch {
             newsRepo.deleteArticle(article)
        }
    }

    fun internetConnection(context: Context):Boolean
    {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {

            return getNetworkCapabilities(activeNetwork)?.run {
                when{
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI)->true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)->true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)->true
                    else -> false
                }
            }?:false
        }
    }

    private suspend fun headLinesInternet(countryCode:String){
        headlines.postValue(Resource.Loading())
        try{
            if(internetConnection(this.getApplication()))
            {
                val response = newsRepo.getHeadlines(countryCode,headlinesPage)
                headlines.postValue(handleHeadlinesResponse(response))
            }else
            {
                headlines.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t: Throwable) {
            Log.v("viki123", t.message.toString())
            headlines.postValue(Resource.Error("Something went wrong"))
        }
    }

    private suspend  fun searchNewsInternet(searchQuery:String)
    {
        newSearchQuery = searchQuery
        searchNews.postValue(Resource.Loading())
        try {
            if(internetConnection(this.getApplication()))
            {
                val response = newsRepo.getSearchData(searchQuery,searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            }else
            {
                searchNews.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t:Throwable)
        {
            headlines.postValue(Resource.Error("Something went wrong"))
        }

    }
}