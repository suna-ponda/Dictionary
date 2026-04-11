package tr.com.ponda.gftb.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tr.com.ponda.gftb.data.TermDatabase
import tr.com.ponda.gftb.repository.TermRepository
import tr.com.ponda.gftb.model.Term

class TermViewModel(application: Application): AndroidViewModel(application) {
    val readAllData: LiveData<List<Term>>
    private val repository: TermRepository
    private val _searchQuery = MutableLiveData<String>("")

    val terms: LiveData<List<Term>>

    init {
        val termDao = TermDatabase.Companion.getDatabase(application).termDao()
        repository = TermRepository(termDao)
        readAllData = repository.readAllData
        terms = _searchQuery.switchMap { searchQuery ->
            if (searchQuery.isEmpty() || searchQuery == "%%") {
                readAllData
            } else {
                repository.searchDatabase(searchQuery)
            }
        }
    }

    fun addTerm(term: Term){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTerm(term)
        }
    }

    fun updateTerm(term: Term){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTerm(term)
        }
    }

    fun deleteTerm(term: Term) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTerm(term)
        }
    }

    fun deleteAllTerms() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllTerms()
        }
    }

    fun searchDatabase(searchQuery: String) {
        _searchQuery.value = searchQuery
    }

    fun getTermByName(name: String): LiveData<Term?> {
        return repository.getTermByName(name)
    }
}