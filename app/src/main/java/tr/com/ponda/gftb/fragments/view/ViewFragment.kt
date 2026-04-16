package tr.com.ponda.gftb.fragments.view

import android.app.AlertDialog
import android.os.Bundle
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
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
import tr.com.ponda.gftb.databinding.FragmentViewBinding
import tr.com.ponda.gftb.viewmodel.TermViewModel

class ViewFragment : Fragment() {

    private val args by navArgs<ViewFragmentArgs>()
    private lateinit var mTermViewModel: TermViewModel
    private var _binding: FragmentViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentViewBinding.inflate(inflater, container, false)

        mTermViewModel = ViewModelProvider(this)[TermViewModel::class.java]

        binding.viewTerm.text = args.currentTerm.term
        
        mTermViewModel.readAllData.observe(viewLifecycleOwner) { allTerms ->
            val definitionText = args.currentTerm.definition
            val spannableString = SpannableString(definitionText)

            for (term in allTerms) {
                // Do not link the term to itself
                if (term.term.equals(args.currentTerm.term, ignoreCase = true)) {
                    continue
                }

                val termName = term.term
                // Case-insensitive search for the term
                var start = definitionText.indexOf(termName, 0, true)
                while (start >= 0) {
                    val end = start + termName.length
                    val clickableSpan = object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            // The 'term' from the loop is the one we want to navigate to
                            val action = ViewFragmentDirections.actionViewFragmentSelf(term)
                            findNavController().navigate(action)
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.isUnderlineText = true
                        }
                    }
                    spannableString.setSpan(clickableSpan, start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)

                    // Find next occurrence
                    start = definitionText.indexOf(termName, start + 1, true)
                }
            }

            binding.viewDefinition.text = spannableString
            binding.viewDefinition.movementMethod = LinkMovementMethod.getInstance()
        }

        binding.viewCitation.text = args.currentTerm.citation
        binding.viewFigures.text = args.currentTerm.figures.joinToString(", ")

        if (binding.viewCitation.text.isEmpty()) {
            binding.citationTitleTextview.visibility = View.GONE
            binding.viewCitation.visibility = View.GONE
        } else {
            binding.citationTitleTextview.visibility = View.VISIBLE
            binding.viewCitation.visibility = View.VISIBLE
        }

        if (binding.viewFigures.text.isEmpty()) {
            binding.figuresTitleTextview.visibility = View.GONE
            binding.viewFigures.visibility = View.GONE
        } else {
            binding.figuresTitleTextview.visibility = View.VISIBLE
            binding.viewFigures.visibility = View.VISIBLE
            binding.viewFigures.text = args.currentTerm.figures.joinToString(", ")
        }

        val linksText = args.currentTerm.links.joinToString(", ")
        if (linksText.isNotEmpty()) {
            val spannableString = SpannableString(linksText)
            val words = linksText.split(", ")
            for (word in words) {
                val start = linksText.indexOf(word)
                val end = start + word.length
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        mTermViewModel.getTermByName(word).observe(viewLifecycleOwner) { term ->
                            if (term != null) {
                                val action = ViewFragmentDirections.actionViewFragmentSelf(term)
                                findNavController().navigate(action)
                            } else {
                                Toast.makeText(requireContext(), getString(R.string.term_not_found), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = true
                    }
                }
                spannableString.setSpan(clickableSpan, start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            binding.viewLink.text = spannableString
            binding.viewLink.movementMethod = LinkMovementMethod.getInstance()
            binding.linkTitleTextview.visibility = View.VISIBLE
            binding.viewLink.visibility = View.VISIBLE
        } else {
            binding.linkTitleTextview.visibility = View.GONE
            binding.viewLink.visibility = View.GONE
        }

        binding.editButton.setOnClickListener {
            val currentItem = args.currentTerm
            val action = ViewFragmentDirections.actionViewFragmentToUpdateFragment(currentItem)
            findNavController().navigate(action)
        }
        // Add menu
        setHasOptionsMenu(true)
        return binding.root
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
            findNavController().navigate(R.id.action_viewFragment_to_listFragment)
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
