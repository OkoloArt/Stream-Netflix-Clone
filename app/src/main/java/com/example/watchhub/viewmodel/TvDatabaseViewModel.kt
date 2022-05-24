package com.example.watchhub.tvShowDatabase

import androidx.lifecycle.*
import com.example.watchhub.database.Movie
import com.example.watchhub.database.MovieDao
import com.example.watchhub.database.MovieDatabaseViewModel
import kotlinx.coroutines.launch

class TvDatabaseViewModel(private val tvShowDao: TvShowDao) : ViewModel() {


    // Cache all items form the database using LiveData.
    val allItems: LiveData<List<TvShows>> = tvShowDao.getItems().asLiveData()

    /**
     * Launching a new coroutine to insert an item in a non-blocking way
     */
    private fun insertItem(movie: TvShows) {
        viewModelScope.launch {
            tvShowDao.insert(movie)
        }
    }

    /**
     * Launching a new coroutine to update an item in a non-blocking way
     */
    private fun updateItem(movie: TvShows) {
        viewModelScope.launch {
            tvShowDao.update(movie)
        }
    }

    /**
     * Launching a new coroutine to delete an item in a non-blocking way
     */
    fun deleteItem(name: String) {
        viewModelScope.launch {
            tvShowDao.deleteFavouriteMovie(name)
        }
    }


    /**
     * Retrieve an item from the repository.
     */
    fun retrieveItem(id: Int): LiveData<TvShows> {
        return tvShowDao.getItem(id).asLiveData()
    }

    /**
     * Inserts the new Item into database.
     */
    fun addNewItem(itemName: String, itemPrice: String, itemCount: Int) {
        val newItem = getNewItemEntry(itemName, itemPrice, itemCount)
        insertItem(newItem)
    }


    /**
     * Returns true if the EditTexts are not empty
     */
    fun isEntryValid(itemPrice: String, itemCount: Int): Boolean {
        if (itemPrice.isBlank() || itemCount == 0) {
            return false
        }
        return true
    }

    /**
     * Returns an instance of the [Movie] entity class with the item info entered by the user.
     * This will be used to add a new entry to the Inventory database.
     */
    private fun getNewItemEntry(itemName: String, itemPrice: String, itemCount: Int): TvShows {
        return TvShows(
            tvShowName = itemName,
            tvShowImage = itemPrice,
            tvShowId = itemCount
        )
    }

    suspend fun checkUser(name: String, id: Int): TvShows? {
        return tvShowDao.getUser(name, id)
    }


}

/**
 * Factory class to instantiate the [ViewModel] instance.
 */
class TvDatabaseViewModelFactory(private val tvShowDao: TvShowDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TvDatabaseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TvDatabaseViewModel(tvShowDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}