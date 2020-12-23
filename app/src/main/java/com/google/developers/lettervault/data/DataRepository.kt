package com.google.developers.lettervault.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.developers.lettervault.notification.NotificationWorker
import com.google.developers.lettervault.util.LETTER_ID
import com.google.developers.lettervault.util.LetterLock
import com.google.developers.lettervault.util.executeThread
import java.util.concurrent.TimeUnit

/**
 * Handles data sources and execute on the correct threads.
 */
class DataRepository(private val letterDao: LetterDao) {

    companion object {
        @Volatile
        private var instance: DataRepository? = null

        fun getInstance(context: Context): DataRepository? {
            return instance ?: synchronized(DataRepository::class.java) {
                if (instance == null) {
                    val database = LetterDatabase.getInstance(context)
                    instance = DataRepository(database.letterDao())
                }
                return instance
            }
        }
    }

    /**
     * Get letters with a filtered state for paging.
     */
    fun getLetters(filter: LetterState): LiveData<PagedList<Letter>> {
        val query = getFilteredQuery(filter)
        val pagingConfig = PagedList.Config.Builder()
            // nilai @true maka jika ada data null akan diganti dengan placeholder
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(8) // data yang pertama kali diambil dari datasource untuk tampil
            .setPageSize(4)
            .setPrefetchDistance(2)
            .build()
        return LivePagedListBuilder(letterDao.getLetters(query), pagingConfig).build()
    }

    fun getLetter(id: Long): LiveData<Letter> = letterDao.getLetter(id)

    fun delete(letter: Letter) = executeThread {
        letterDao.delete(letter)
    }

    fun getRecentLetter(): LiveData<Letter> = letterDao.getRecentLetter()

    /**
     * Add a letter to database and schedule a notification on
     * when the letter vault can be opened.
     */
    fun save(letter: Letter) = executeThread {
        val result = letterDao.insert(letter)
        if (result != 0L) {
            val timeDifference = letter.expires - System.currentTimeMillis()
            if (timeDifference >= 0L) {
                val constraints = Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .setRequiresStorageNotLow(true)
                    .build()
                val data = Data.Builder().putLong(LETTER_ID, result).build()
                val oneTimeWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInputData(data)
                    .setConstraints(constraints)
                    .setInitialDelay(timeDifference, TimeUnit.MILLISECONDS)
                    .build()
                WorkManager.getInstance().enqueue(oneTimeWorkRequest)
            }
        }
    }

    /**
     * Update database with a decode letter content and update the opened timestamp.
     */
    fun openLetter(letter: Letter) = executeThread {
        val letterCopy = letter.copy(
            subject = LetterLock.retrieveMessage(letter.subject),
            content = LetterLock.retrieveMessage(letter.content),
            opened = System.currentTimeMillis()
        )
        letterDao.update(letterCopy)
    }

    /**
     * Create a raw query at runtime for filtering the letters.
     */
    private fun getFilteredQuery(filter: LetterState): SimpleSQLiteQuery {
        val now = System.currentTimeMillis()
        val simpleQuery = StringBuilder()
            .append("SELECT * FROM letter ")
        if (filter == LetterState.FUTURE) {
            simpleQuery.append("WHERE expires >= $now OR expires <= $now AND opened IS 0")
        }
        if (filter == LetterState.OPENED) {
            simpleQuery.append("WHERE opened IS NOT 0")
        }
        simpleQuery.append(" ORDER BY created DESC")
        return SimpleSQLiteQuery(simpleQuery.toString())
    }

}
