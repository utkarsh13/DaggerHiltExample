package com.utkarsh.daggerhiltexample.trendingRepositories

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.utkarsh.daggerhiltexample.database.RepositoriesDatabase
import com.utkarsh.daggerhiltexample.R
import com.utkarsh.daggerhiltexample.databinding.FragmentRepositoriesBinding
import com.utkarsh.daggerhiltexample.domain.Repository
import com.utkarsh.daggerhiltexample.repository.TrendingReposRepository
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.android.synthetic.main.appbar_layout.*
import kotlinx.android.synthetic.main.fragment_repositories.view.*
import kotlinx.android.synthetic.main.shimmer_layout.view.*


class RepositoriesFragment : Fragment() {

    private var viewModelAdapter: RepositoriesAdapter? = null

    private val viewModel: RepositoriesViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        val database = RepositoriesDatabase.getInstance(activity.applicationContext)
        val trendingReposRepository = TrendingReposRepository(database, activity.applicationContext)
        ViewModelProviders.of(this, RepositoriesViewModelFactory(trendingReposRepository))
            .get(RepositoriesViewModel::class.java)
    }

    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var shimmerLayout: ShimmerFrameLayout? = null
    private var retryView: ConstraintLayout? = null
    private var recyclerView: RecyclerView? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity?)!!.setSupportActionBar(trToolbar)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentRepositoriesBinding = FragmentRepositoriesBinding.inflate(inflater)
        binding.setLifecycleOwner(viewLifecycleOwner)
        binding.viewModel = viewModel

        bindLayouts(binding)
        bindObservers()
        setupRecyclerView(binding)

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.expandedPosition = viewModelAdapter?.expandedPosition ?: -1
    }

    private fun setupRecyclerView(binding: FragmentRepositoriesBinding) {
        viewModelAdapter = RepositoriesAdapter()
        viewModelAdapter?.let {
            it.expandedPosition = viewModel.expandedPosition
        }

        recyclerView?.let {
            it.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = viewModelAdapter
            }
        }
    }

    private fun bindObservers() {
        viewModel.repositories.observe(
            viewLifecycleOwner,
            Observer<List<Repository>> { repositories ->
                repositories?.apply {
                    viewModelAdapter?.repositories = repositories
                }
            })

        viewModel.repositoriesSorted.observe(
            viewLifecycleOwner,
            Observer<List<Repository>> { repositories ->
                repositories?.apply {
                    viewModelAdapter?.expandedPosition = -1
                    viewModelAdapter?.repositories = repositories
                    recyclerView?.layoutManager?.scrollToPosition(0)
                }
            })

        viewModel.repositoriesApiStatus.observe(
            viewLifecycleOwner,
            Observer<RepositoriesApiStatus> {
                when (it) {
                    RepositoriesApiStatus.LOADING -> {
                        shimmerLayout?.startShimmer()
                        shimmerLayout?.visibility = View.VISIBLE
                        //Added this to hide flicker
                        viewModelAdapter?.repositories = listOf()
                        recyclerView?.visibility = View.GONE
                        retryView?.visibility = View.GONE
                        swipeRefreshLayout?.isEnabled = false
                    }
                    RepositoriesApiStatus.SUCCESS -> {
                        shimmerLayout?.stopShimmer()
                        shimmerLayout?.visibility = View.GONE
                        recyclerView?.visibility = View.VISIBLE
                        retryView?.visibility = View.GONE
                        stopSwipeRefreshAnimation()
                        swipeRefreshLayout?.isEnabled = true
                    }
                    RepositoriesApiStatus.ERROR -> {
                        shimmerLayout?.stopShimmer()
                        shimmerLayout?.visibility = View.GONE
                        recyclerView?.visibility = View.GONE
                        retryView?.visibility = View.VISIBLE
                        stopSwipeRefreshAnimation()
                        swipeRefreshLayout?.isEnabled = false
                    }
                    RepositoriesApiStatus.OFFLINE -> {
                        viewModel.updateSort(RepositorySort.DEFAULT)
                        shimmerLayout?.stopShimmer()
                        shimmerLayout?.visibility = View.GONE
                        recyclerView?.visibility = View.VISIBLE
                        retryView?.visibility = View.GONE
                        stopSwipeRefreshAnimation()
                        swipeRefreshLayout?.isEnabled = true
                    }
                }
            })
    }

    private fun bindLayouts(binding: FragmentRepositoriesBinding) {
        setSwipeRefreshLayout(binding)
        setShimmerLayout(binding)
        setRetryView(binding)
        setRecyclerView(binding)
    }

    private fun setRetryView(binding: FragmentRepositoriesBinding) {
        retryView = binding.root.retry_view
    }

    private fun setRecyclerView(binding: FragmentRepositoriesBinding) {
        recyclerView = binding.root.repositories_recycler_view
    }

    private fun stopSwipeRefreshAnimation() {
        if (swipeRefreshLayout?.isRefreshing == true) {
            swipeRefreshLayout?.isRefreshing = false
        }
    }

    private fun setSwipeRefreshLayout(binding: FragmentRepositoriesBinding) {
        swipeRefreshLayout = binding.root.swipe_refresh
        swipeRefreshLayout?.let {
            it.setOnRefreshListener {
                viewModel.refreshRepositories()
            }
        }
    }

    private fun setShimmerLayout(binding: FragmentRepositoriesBinding) {
        shimmerLayout = binding.root.fb_shimmer
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.updateSort(
            when (item.itemId) {
                R.id.sort_by_names -> RepositorySort.SORT_NAME
                R.id.sort_by_stars -> RepositorySort.SORT_STAR
                else -> RepositorySort.DEFAULT
            }
        )
        return true
    }

}
