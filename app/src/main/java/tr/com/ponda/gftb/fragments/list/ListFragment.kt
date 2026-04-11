package tr.com.ponda.gftb.fragments.list

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tr.com.ponda.gftb.R
import tr.com.ponda.gftb.databinding.FragmentListBinding
import tr.com.ponda.gftb.model.Term
import tr.com.ponda.gftb.viewmodel.TermViewModel
import java.io.OutputStreamWriter

class ListFragment : Fragment() {

    private lateinit var mTermViewModel: TermViewModel
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazy { ListAdapter() }

    private val exportLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        uri?.let { exportData(it) }
    }

    private val importLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { importData(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)

        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

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

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_export -> {
                exportLauncher.launch("terms_export.json")
                true
            }
            R.id.menu_import -> {
                importLauncher.launch(arrayOf("application/json"))
                true
            }
            R.id.menu_exit -> {
                requireActivity().finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun exportData(uri: Uri) {
        mTermViewModel.readAllData.observe(viewLifecycleOwner) { terms ->
            try {
                requireContext().contentResolver.openOutputStream(uri)?.use { outputStream ->
                    OutputStreamWriter(outputStream).use { writer ->
                        val json = Gson().toJson(terms)
                        writer.write(json)
                    }
                }
                Toast.makeText(requireContext(), "Exported successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("ListFragment", "Export failed", e)
                Toast.makeText(requireContext(), "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun importData(uri: Uri) {
        try {
            requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                val json = inputStream.bufferedReader().use { it.readText() }
                val listType = object : TypeToken<List<Term>>() {}.type
                val terms: List<Term> = Gson().fromJson(json, listType)
                // When importing, we might want to clear existing or handle IDs.
                // For simplicity, we add them as new (Room @Insert IGNORE will handle duplicates if defined)
                mTermViewModel.addTerms(terms)
                Toast.makeText(requireContext(), "Imported ${terms.size} terms", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("ListFragment", "Import failed", e)
            Toast.makeText(requireContext(), "Import failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
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
