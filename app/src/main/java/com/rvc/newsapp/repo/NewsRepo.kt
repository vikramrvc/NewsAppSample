package com.rvc.newsapp.repo

import androidx.lifecycle.LiveData
import com.rvc.newsapp.api.RetrofitInstance
import com.rvc.newsapp.db.ArticleDataBase
import com.rvc.newsapp.model.Article

class NewsRepo(val db:ArticleDataBase) {

    suspend fun getHeadlines(countryCode:String, pageNum: Int) =    RetrofitInstance.api.getHeadlines(countryCode,pageNum)


    suspend fun getSearchData(query:String, pageNum: Int) =  RetrofitInstance.api.search(query,pageNum)


    suspend fun upsert(article: Article)
    {
        db.getArticleDao().upsert(article)
    }

    fun getFavArticle():LiveData<List<Article>>
    {
        return  db.getArticleDao().getArticles()
    }

    suspend fun deleteArticle(article: Article){
        db.getArticleDao().delete(article)
    }
}