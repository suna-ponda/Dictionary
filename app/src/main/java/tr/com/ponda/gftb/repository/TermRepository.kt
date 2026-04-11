package tr.com.ponda.gftb.repository

import androidx.lifecycle.LiveData
import tr.com.ponda.gftb.data.TermDao
import tr.com.ponda.gftb.model.Term

class TermRepository(private val termDao: TermDao) {
    val readAllData: LiveData<List<Term>> = termDao.readAllData()
    suspend fun addTerm(vararg terms: Term){
        termDao.addTerm(*terms)
    }

    suspend fun updateTerm(term: Term){
        termDao.updateTerm(term)
    }

    suspend fun deleteTerm(term: Term){
        termDao.deleteTerm(term)
    }

    suspend fun deleteAllTerms(){
        termDao.deleteAllTerms()
    }

    fun searchDatabase(searchQuery: String): LiveData<List<Term>> {
        return termDao.searchDatabase(searchQuery)
    }

    fun getTermByName(name: String): LiveData<Term?> {
        return termDao.getTermByName(name)
    }
}