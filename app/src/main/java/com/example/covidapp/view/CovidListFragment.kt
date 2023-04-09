package com.example.covidapp.view

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.covidapp.adapter.CovidListAdapter
import com.example.covidapp.databinding.FragmentCovidListBinding
import com.example.covidapp.viewmodel.CovidListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CovidListFragment : Fragment() {

    private var _binding: FragmentCovidListBinding? = null
    private val binding get() = _binding!!

    private lateinit var covidAdapter: CovidListAdapter
    private val viewModel: CovidListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCovidListBinding.inflate(inflater, container, false)
        covidAdapter = CovidListAdapter()

        //Status Bar
        val window = requireActivity().window

        // Before Android 6.0 Marshmallow
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Status bar will be white
            window.statusBarColor = Color.WHITE
        } else {
            if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO) {
                // Light mode
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.statusBarColor = Color.WHITE
            } else {
                // Dark mode
                window.decorView.systemUiVisibility = 0
                window.statusBarColor = Color.BLACK
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.refreshData()

        binding.rvCovidList.layoutManager = LinearLayoutManager(context)
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.rvCovidList.visibility = View.GONE
            binding.tvError.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE

            viewModel.refreshData()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    viewModel.refreshData()
                } else {
                    viewModel.filterResults(newText)
                }
                return true
            }
        })

        observeLiveData()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.rvCovidList.apply {
            adapter = covidAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        covidAdapter.setOnItemClickListener { country ->
            val action = CovidListFragmentDirections.actionCovidListFragmentToCovidDetailsFragment(
                country.Country,
                country.TotalConfirmed,
                country.TotalDeaths,
                country.TotalRecovered
            )
            findNavController().navigate(action)
        }

    }

    private fun observeLiveData() {
        viewModel.response.observe(viewLifecycleOwner, Observer { covidList ->
            covidList?.let {
                covidAdapter.covidlist = it
                binding.rvCovidList.visibility = View.VISIBLE
            }
        })

        viewModel.moviesError.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                if (it) {
                    binding.tvError.visibility = View.VISIBLE
                    binding.rvCovidList.visibility = View.GONE
                } else {
                    binding.tvError.visibility = View.GONE
                    binding.rvCovidList.visibility = View.VISIBLE
                }
            }
        })

        viewModel.moviesLoading.observe(viewLifecycleOwner, Observer { loading ->
            loading?.let {
                if (it) {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvCovidList.visibility = View.GONE
                    binding.tvError.visibility = View.GONE
                } else {
                    binding.progressBar.visibility = View.GONE
                }
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}