package com.example.dogsapplication.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dogsapplication.R
import com.example.dogsapplication.viewmodel.ListViewModel
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment() {

    private lateinit var viewModel: ListViewModel
    private val dogsListAdapter= DogsListAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        viewModel.refresh()

        dogsList.apply {

            layoutManager= LinearLayoutManager(context)
            adapter = dogsListAdapter

        }

        refreshLayout.setOnRefreshListener {

            dogsList.visibility = View.GONE
            listError.visibility = View.GONE
            loadingView.visibility = View.VISIBLE
            viewModel.refreshByPassCache()
            refreshLayout.isRefreshing = false
        }

        observeViewModel()
    }

    private fun observeViewModel(){

        viewModel.dogs.observe(viewLifecycleOwner, Observer {

            it.let {

                dogsList.visibility = View.VISIBLE
                if (it != null) {
                    dogsListAdapter.updateDogList(it)
                }

            }

        })

        viewModel.dogsLoadError.observe( viewLifecycleOwner, Observer {

            it.let {

                listError.visibility =if (it) View.VISIBLE else View.GONE
            }
        })

        viewModel.loading.observe(viewLifecycleOwner, Observer {

            it.let {

                loadingView.visibility = if (it)View.VISIBLE else View.GONE
                if (it){

                    listError.visibility = View.GONE
                    dogsList.visibility = View.GONE
                }
                }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.list_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            R.id.actionSettings ->{

                view?.let {
                    Navigation.findNavController(it).navigate(ListFragmentDirections.actionSettings())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}