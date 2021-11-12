package com.example.shoppinglist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinglist.data.ShopListRepositoryImpl
import com.example.shoppinglist.domain.ShopItem
import com.example.shoppinglist.domain.usecases.AddShopItemUseCase
import com.example.shoppinglist.domain.usecases.EditShopItemUseCase
import com.example.shoppinglist.domain.usecases.GetShopItemUseCase
import java.lang.Exception

class ShopItemViewModel: ViewModel() {

    private val repository = ShopListRepositoryImpl

    private val editShopItemUseCase = EditShopItemUseCase(repository)
    private val addShopItemUseCase = AddShopItemUseCase(repository)
    private val getShopItemUseCase = GetShopItemUseCase(repository)

    private val _errorInputName = MutableLiveData<Boolean>()
    val errorInputName: LiveData<Boolean>
        get():LiveData<Boolean> =_errorInputName

    private val _errorInputCount = MutableLiveData<Boolean>()
    val errorInputCount: LiveData<Boolean>
        get():LiveData<Boolean> =_errorInputCount

    private val _shopItem = MutableLiveData<ShopItem>()
    val shopItem: LiveData<ShopItem>
    get() = _shopItem

    private val _shouldCloseScreen = MutableLiveData<Unit>()
    val shouldCloseScreen: LiveData<Unit>
    get() = _shouldCloseScreen

    fun editShopItem(inputName: String?, inputCount: String?){
        val name = parseName(inputName)
        val count = parseInt(inputCount)
        val fieldsValid = validateInput(name, count)
        if (fieldsValid){
            val shopItem = _shopItem.value
            shopItem?.let {
                val item = it.copy(name = name, count = count)
                editShopItemUseCase.editShopItem(item)
                finishWork()
            }

        }
    }

    fun addShopItem(inputName: String?, inputCount: String?){
        val name = parseName(inputName)
        val count = parseInt(inputCount)

        val fieldsValid = validateInput(name, count)
        if (fieldsValid){
            val shopItem = ShopItem(name, count, true)
            addShopItemUseCase.addShopItem(shopItem)
            finishWork()
        }
    }

    fun getShopItemById(id: Int) {
        val item =  getShopItemUseCase.getShopItemById(id)
        _shopItem.value = item
    }

    private fun parseName(inputName: String?):String{
        return  inputName?.trim() ?: ""
    }
    private fun parseInt(inputCount: String?): Int{
        return try {
            inputCount?.trim()?.toInt() ?: 0
        } catch (e: Exception){
            0
        }
    }

    private fun validateInput(name:String, count: Int): Boolean{
        var result = true
        if (name.isBlank()){
            _errorInputName.value = true
            result = false
        }
        if (count<=0){
            _errorInputCount.value = true
            result = false
        }
        return result
    }

    fun resetErrorInputName(){
        _errorInputName.value = false
    }
    fun resetErrorInputCount(){
        _errorInputCount.value = false
    }

    private fun finishWork(){
        _shouldCloseScreen.value = Unit
    }
}