package io.kinescope.demo.playlist

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import io.kinescope.sdk.api.KinescopeApiHelper
import io.kinescope.sdk.models.videos.KinescopeVideo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch


class PlaylistViewModel(private val apiHelper: KinescopeApiHelper) : ViewModel() {
    private val _allVideos:MutableLiveData<ArrayList<KinescopeVideo>> = MutableLiveData(arrayListOf())
    val allVideos:LiveData<ArrayList<KinescopeVideo>>
    get() = _allVideos

    init {
        getAllVideos()
    }

    private fun getAllVideos() {
        viewModelScope.launch {
            apiHelper.getAllVideos().flowOn(Dispatchers.IO)
                .catch {  e ->
                    e
                }
                .collect() {
                    _allVideos.value = ArrayList(it.data)
                }
        }
    }

    class Factory (private val apiHelper: KinescopeApiHelper) : ViewModelProvider.Factory  {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras
        ): T {
            return PlaylistViewModel(apiHelper) as T
        }
    }
}

