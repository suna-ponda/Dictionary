package tr.com.ponda.gftb.fragments.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import tr.com.ponda.gftb.R
import tr.com.ponda.gftb.databinding.FragmentListBinding
import tr.com.ponda.gftb.viewmodel.TermViewModel

class ListFragment : Fragment() {

    private lateinit var mTermViewModel: TermViewModel
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazy { ListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentListBinding.inflate(inflater, container, false)

        //Recyclerview
        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        //TermViewModel
        mTermViewModel = ViewModelProvider(this)[TermViewModel::class.java]
        mTermViewModel.terms.observe(viewLifecycleOwner) { terms ->
            adapter.submitList(terms)
            if (terms.isEmpty()) {
                binding.rvLayout.visibility = View.GONE
                binding.noData.visibility = View.VISIBLE
            } else {
                binding.rvLayout.visibility = View.VISIBLE
                binding.noData.visibility = View.GONE
            }
        }

        binding.searchInput.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchDatabase(query)
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                searchDatabase(query)
                return true
            }
        })

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }

        return binding.root
    }

    private fun searchDatabase(query: String?) {
        val searchQuery = "%${query.orEmpty()}%"
        mTermViewModel.searchDatabase(searchQuery)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
