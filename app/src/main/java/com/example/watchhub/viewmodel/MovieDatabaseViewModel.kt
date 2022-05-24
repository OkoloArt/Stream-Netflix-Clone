package com.example.watchhub.database

import androidx.lifecycle.*
import kotlinx.coroutines.launch

/**
 * View Model to keep a reference to the Inventory repository and an up-to-date list of all items.
 *
 */
class MovieDatabaseViewModel(private val movieDao: MovieDao) : ViewModel() {


    // Cache all items form the database using LiveData.
    val allItems: LiveData<List<Movie>> = movieDao.getItems().asLiveData()

    /**
     * Launching a new coroutine to insert an item in a non-blocking way
     */
    private fun insertItem(movie: Movie) {
        viewModelScope.launch {
            movieDao.insert(movie)
        }
    }

    /**
     * Launching a new coroutine to update an item in a non-blocking way
     */
    private fun updateItem(movie: Movie) {
        viewModelScope.launch {
            movieDao.update(movie)
        }
    }

    /**
     * Launching a new coroutine to delete an item in a non-blocking way
     */
    fun deleteItem(name: String) {
        viewModelScope.launch {
            movieDao.deleteFavouriteMovie(name)
        }
    }


    /**
     * Retrieve an item from the repository.
     */
    fun retrieveItem(id: Int): LiveData<Movie> {
        return movieDao.getItem(id).asLiveData()
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
    private fun getNewItemEntry(itemName: String, itemPrice: String, itemCount: Int): Movie {
        return Movie(
            movieName = itemName,
            movieImage = itemPrice,
            movieId = itemCount
        )
    }

    suspend fun checkUser(name: String, id: Int): Movie? {
        return movieDao.getUser(name, id)
    }




}

/**
 * Factory class to instantiate the [ViewModel] instance.
 */
class MovieDatabaseViewModelFactory(private val movieDao: MovieDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieDatabaseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieDatabaseViewModel(movieDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
