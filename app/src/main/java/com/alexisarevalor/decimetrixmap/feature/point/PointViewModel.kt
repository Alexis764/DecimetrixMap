package com.alexisarevalor.decimetrixmap.feature.point

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexisarevalor.decimetrixmap.core.storage.MapDatabase
import com.alexisarevalor.decimetrixmap.core.storage.PointModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PointViewModel @Inject constructor(
    private val mapDatabase: MapDatabase
) : ViewModel() {

    private val _pointList = mutableStateListOf<PointModel>()
    val pointList: List<PointModel> = _pointList

    init {
        viewModelScope.launch {
            _pointList.addAll(mapDatabase.getAllPoints())
        }
    }

}