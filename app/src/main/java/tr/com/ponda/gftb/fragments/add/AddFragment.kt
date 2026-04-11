package tr.com.ponda.gftb.fragments.add

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import tr.com.ponda.gftb.R
import tr.com.ponda.gftb.model.Term
import tr.com.ponda.gftb.viewmodel.TermViewModel
import tr.com.ponda.gftb.databinding.FragmentAddBinding

class AddFragment : Fragment() {

    private lateinit var mTermViewModel: TermViewModel
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        val view = binding.root

        mTermViewModel = ViewModelProvider(this)[TermViewModel::class.java]

        binding.button.setOnClickListener {
            insertDataToDatabase()
        }
        return view
    }

    private fun insertDataToDatabase() {
        val term = binding.editTerm.text.toString()
        val definition = binding.editDefinition.text.toString()
        val citation = binding.editCitation.text.toString()
        val links = binding.editLinks.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val figures = binding.editFigures.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }

        val msgAddedSuccessfully = getString(R.string.added_successfully)
        val msgFillAllFields = getString(R.string.fill_all_fields)

        if (inputCheck(term, definition, citation,links, figures)) {
            // Create Term Object
            val newTerm = Term(0, term, definition, citation, links, figures)
            // Add Data to Database
            mTermViewModel.addTerm(newTerm)
            Toast.makeText(requireContext(), msgAddedSuccessfully, Toast.LENGTH_LONG).show()
            // Navigate Back
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        } else {
            Toast.makeText(requireContext(), msgFillAllFields, Toast.LENGTH_LONG).show()
        }
    }

    private fun inputCheck(
        term: String,
        definition: String,
        citation: String,
        links: List<String>,
        figures: List<String>,

    ): Boolean {
        return !(TextUtils.isEmpty(term) || TextUtils.isEmpty(definition) && TextUtils.isEmpty(citation) && links.isEmpty() && figures.isEmpty())

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}