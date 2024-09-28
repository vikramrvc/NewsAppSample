package com.rvc.newsapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rvc.newsapp.repo.NewsRepo

class NewsViewModelProviderFactory(val app: Application, val newsRepo: NewsRepo):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(app,newsRepo) as T
    }
}