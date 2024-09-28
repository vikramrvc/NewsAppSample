package com.rvc.newsapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.rvc.newsapp.R
import com.rvc.newsapp.databinding.ActivityNewsBinding
import com.rvc.newsapp.db.ArticleDataBase
import com.rvc.newsapp.repo.NewsRepo

class NewsActivity : AppCompatActivity() {

    lateinit var newsViewModel: NewsViewModel
    lateinit var  binding: ActivityNewsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newsRepo = NewsRepo(ArticleDataBase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(application,newsRepo)
        newsViewModel = ViewModelProvider(this,viewModelProviderFactory).get(NewsViewModel::class.java)


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController= navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

    }
}