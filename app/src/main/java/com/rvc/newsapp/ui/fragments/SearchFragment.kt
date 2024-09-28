package com.rvc.newsapp.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rvc.newsapp.R
import com.rvc.newsapp.adapaters.NewsAdapter
import com.rvc.newsapp.databinding.FragmentFavouriteBinding
import com.rvc.newsapp.databinding.FragmentSearchBinding
import com.rvc.newsapp.ui.NewsActivity
import com.rvc.newsapp.ui.NewsViewModel
import com.rvc.newsapp.util.Constants
import com.rvc.newsapp.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment(R.layout.fragment_search) {

    lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var retryButton: Button
    lateinit var errorText : TextView
    lateinit var itemSearchError: CardView
    lateinit var binding: FragmentSearchBinding

    var isError = false
    var isLoading  = false
    var isLastPage = false
    var isScrolling = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSearchBinding.bind(view)
        itemSearchError = view.findViewById(R.id.itemSearchError)
        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val errorView:View = inflater.inflate(R.layout.item_error,null)
        retryButton = errorView.findViewById(R.id.retryButton)
        errorText = errorView.findViewById(R.id.errorText)

        newsViewModel = (activity as NewsActivity).newsViewModel
        setUpSearchRecycler()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(R.id.action_searchFragment_to_articleFragment, bundle)
        }

        var job:Job?=null
        binding.searchEdit.addTextChangedListener { editable->
            job?.cancel()
            job = MainScope().launch {
                delay(Constants.TIME_DELAY)
                editable?.let{
                    if(editable.toString().isNotEmpty())
                    {
                        newsViewModel.getSearchData(editable.toString())
                    }
                }
            }
        }

        newsViewModel.searchNews.observe(viewLifecycleOwner, Observer { response->

            when(response){
                is Resource.Success<*>->{
                    hideProgressBar()
                    hideErrorMsg()
                    response.data?.let { newsResponse->
                        newsAdapter.differ.submitList(newsResponse.articles.toList() )
                        val totalPages = newsResponse.totalResults /Constants.QUERY_PAGE_SIZE+2
                        isLastPage = newsViewModel.searchNewsPage == totalPages
                        if(isLastPage)
                        {
                            binding.recyclerSearch.setPadding(0,0,0,0)
                        }
                    }
                }

                is Resource.Error<*> ->{
                    hideProgressBar()
                    response.message?.let {
                        showErrorMsg(it)
                    }
                }

                is Resource.Loading<*>->{
                    showProgressBar()
                }
            }
        })

        retryButton.setOnClickListener {
            if(binding.searchEdit.text.toString().isNotEmpty())
            {
                newsViewModel.getSearchData(binding.searchEdit.toString())
            }else
            {
                hideErrorMsg()
            }
        }
    }

    private fun hideProgressBar()
    {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar(){
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideErrorMsg()
    {
        itemSearchError.visibility = View.INVISIBLE
        isError = false
    }

    private fun showErrorMsg(message:String)
    {
        itemSearchError.visibility = View.VISIBLE
        errorText.text = message
        isError = false
    }

    val scrollListener = object : RecyclerView.OnScrollListener()
    {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNoErrors = !isError
            val isNotLoadingAndNotLastPage = !isLastPage && !isLoading
            val isLastItem = firstVisibleItemPosition+visibleItemCount>=totalItemCount
            val isNotBeginning=firstVisibleItemPosition>=0
            val isTotalMoreThanVisible = totalItemCount>= Constants.QUERY_PAGE_SIZE
            val shldPaginate= isNoErrors && isNotLoadingAndNotLastPage && isLastItem && isNotBeginning && isTotalMoreThanVisible
            if(shldPaginate)
            {
                newsViewModel.getSearchData(binding.searchEdit.text.toString())
                isScrolling = false
            }

        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
            {
                isScrolling = true
            }
        }

    }

    private fun setUpSearchRecycler()
    {
        newsAdapter = NewsAdapter()
        binding.recyclerSearch.apply {
            adapter= newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchFragment.scrollListener)
        }
    }
}