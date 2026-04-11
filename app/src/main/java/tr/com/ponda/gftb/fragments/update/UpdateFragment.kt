package tr.com.ponda.gftb.fragments.update

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import tr.com.ponda.gftb.R
import tr.com.ponda.gftb.databinding.FragmentUpdateBinding
import tr.com.ponda.gftb.model.Term
import tr.com.ponda.gftb.viewmodel.TermViewModel
import kotlin.getValue


class UpdateFragment : Fragment() {

    private val args by navArgs<UpdateFragmentArgs>()
    private lateinit var mTermViewModel: TermViewModel
    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)

        mTermViewModel = ViewModelProvider(this)[TermViewModel::class.java]
        binding.updateTermNameTitle.setText(args.currentTerm.term)
        binding.updateTerm.setText(args.currentTerm.term)
        binding.updateDefinition.setText(args.currentTerm.definition)
        binding.updateCitation.setText(args.currentTerm.citation)
        binding.updateLinks.setText(args.currentTerm.links.joinToString(", "))
        binding.updateFigures.setText(args.currentTerm.figures.joinToString(", "))

        binding.updateButton.setOnClickListener {
            updateItem()
        }
        // Add menu
        setHasOptionsMenu(true)
        return binding.root
    }
    private fun updateItem() {
        val term = binding.updateTerm.text.toString()
        val definition = binding.updateDefinition.text.toString()
        val citation = binding.updateCitation.text.toString()
        val links = binding.updateLinks.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val figures = binding.updateFigures.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }


        val msgUpdatedSuccessfully = getString(R.string.updated_successfully)
        val msgFillAllFields = getString(R.string.fill_all_fields)

        if (inputCheck(term, definition)) {
            // Create Term Object
            val updatedTerm = Term(args.currentTerm.id, term, definition, citation, links, figures)
            //Update Current Term
            mTermViewModel.updateTerm(updatedTerm)
            Toast.makeText(requireContext(), msgUpdatedSuccessfully, Toast.LENGTH_LONG).show()
            // Navigate Back
            val action = UpdateFragmentDirections.actionUpdateFragmentToListFragment()
            findNavController().navigate(action)
        } else {
            Toast.makeText(requireContext(), msgFillAllFields, Toast.LENGTH_LONG).show()
        }
    }
    private fun inputCheck(
        term: String,
        definition: String
    ): Boolean {
        return !TextUtils.isEmpty(term) && !TextUtils.isEmpty(definition)
    }


    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_delete){
            deleteTerm()
        }
        return super.onOptionsItemSelected(item)
    }
    private fun deleteTerm() {
        val msgDeletedSuccessfully = getString(R.string.deleted_successfully)
        val msgDelete = getString(R.string.delete)
        val msgDeleteQuestion = getString(R.string.delete_question)

        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            mTermViewModel.deleteTerm(args.currentTerm)
            Toast.makeText(
                requireContext(),
                msgDeletedSuccessfully + ": ${args.currentTerm.term}",
                Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
        builder.setNegativeButton(getString(R.string.no)) { _, _ ->}

        builder.setTitle(msgDelete + " : ${args.currentTerm.term}?")
        builder.setMessage( msgDeleteQuestion+ "${args.currentTerm.term}?")
        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}