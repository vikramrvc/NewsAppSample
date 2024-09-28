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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rvc.newsapp.R
import com.rvc.newsapp.adapaters.NewsAdapter
import com.rvc.newsapp.databinding.FragmentHeadlinesBinding
import com.rvc.newsapp.ui.NewsActivity
import com.rvc.newsapp.ui.NewsViewModel
import com.rvc.newsapp.util.Constants
import com.rvc.newsapp.util.Resource


class HeadlinesFragment : Fragment(R.layout.fragment_headlines) {

     lateinit var newsViewModel: NewsViewModel
     lateinit var newsAdapter: NewsAdapter
     lateinit var retryButton : Button
     lateinit var errorTextView: TextView
     lateinit var itemHeadLinesError : CardView
     lateinit var binding : FragmentHeadlinesBinding
     var isError = false
     var isLoading  = false
     var isLastPage = false
     var isScrolling = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHeadlinesBinding.bind(view)
        itemHeadLinesError = view.findViewById(R.id.itemHeadlinesError)

        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val errorView:View = inflater.inflate(R.layout.item_error,null)
        retryButton = errorView.findViewById(R.id.retryButton)
        errorTextView = errorView.findViewById(R.id.errorText)

        newsViewModel = (activity as NewsActivity).newsViewModel
        setUpHeadLinesRecycler()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(R.id.action_headlinesFragment_to_articleFragment, bundle)
        }

        newsViewModel.headlines.observe(viewLifecycleOwner, Observer { response->
            when(response){
                is Resource.Success<*>->{
                    hideProgressBar()
                    hideErrorMsg()
                    response.data?.let { newsResponse->
                        newsAdapter.differ.submitList(newsResponse.articles.toList() )
                        val totalPages = newsResponse.totalResults /Constants.QUERY_PAGE_SIZE+2
                        isLastPage = newsViewModel.headlinesPage == totalPages
                        if(isLastPage)
                        {
                            binding.recyclerHeadlines.setPadding(0,0,0,0)
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
        itemHeadLinesError.visibility = View.INVISIBLE
        isError = false
    }

    private fun showErrorMsg(message:String)
    {
        itemHeadLinesError.visibility = View.VISIBLE
        errorTextView.text = message
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
            val isTotalMoreThanVisible = totalItemCount>=Constants.QUERY_PAGE_SIZE
            val shldPaginate= isNoErrors && isNotLoadingAndNotLastPage && isLastItem && isNotBeginning && isTotalMoreThanVisible
            if(shldPaginate)
            {
                newsViewModel.getHeadlines("us")
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

    private fun setUpHeadLinesRecycler()
    {
        newsAdapter = NewsAdapter()
        binding.recyclerHeadlines.apply {
            adapter= newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@HeadlinesFragment.scrollListener)
        }
    }
}