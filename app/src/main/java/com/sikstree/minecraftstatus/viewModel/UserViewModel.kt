package com.sikstree.minecraftstatus.viewModel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sikstree.minecraftstatus.Model.Event
import com.sikstree.minecraftstatus.Model.MinecraftAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class UserViewModel(application: Application) : AndroidViewModel(application){

// ViewModel()을 상속받을 경우
// class MainViewModel():ViewModel(){}

    //LiveData
//값이 변경되는 경우 MutableLiveData로 선언한다.

    private val title = "HomeViewModel"

    var serverUID = MutableLiveData<String>()
    var serverUID_after = MutableLiveData<String>()
    var serverUUID = MutableLiveData<String>()
    var serverHostTxt = MutableLiveData<String>()
    var showDialog = MutableLiveData<Boolean>() // 다이얼로그를 띄우기 위한 LiveData 변수

    private val _event = MutableLiveData<Event<String>>()


    init {
        serverUID.value = ""
        serverUID_after.value = ""
        serverUUID.value = ""
        serverHostTxt.value = ""
        showDialog.value = false
    }

    val event: LiveData<Event<String>>
        get() = _event

    fun onEvent(userSkin : String) {
        _event.value = Event(userSkin)
    }



    fun getMineacraftUUID() {
        showDialog.value = true // 다이얼로그 띄우기

        var userID : String = serverUID.value.toString()
        val retrofit = Retrofit.Builder().baseUrl("https://api.mojang.com/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create()).build();

        val service = retrofit.create(MinecraftAPI::class.java);

        CoroutineScope(Dispatchers.Main).launch { // 코루틴 사용하여 retorfit2 GET 호출
            try {
                Log.d(title, "inputuserID :" +  userID)
                val result = service.getSuspendUUID(userID)

                Log.d(title, "onResponse 성공: " + result);

                val jsonObject = JSONObject(result)

                Log.d(title, "id : " + jsonObject.get("id").toString())
                serverUUID.value = jsonObject.get("id").toString()
                serverUID_after.value = jsonObject.get("name").toString()

//                getMineacraftSkin(serverUUID.value.toString())
                onEvent(serverUUID.value.toString())

                showDialog.value = false // 다이얼로그 종료
            } catch (e : Exception) {
                Log.d(title, "통신 실패 : " + e.printStackTrace())
                Toast.makeText(getApplication(), "ID 혹은 인터넷 연결상태를 확인해주세요.",Toast.LENGTH_SHORT).show()
                showDialog.value = false // 다이얼로그 종료
            }
        }

    }

    fun getMineacraftSkin(uuid : String) {
        val retrofit = Retrofit.Builder().baseUrl("https://crafatar.com/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create()).build();

        val service = retrofit.create(MinecraftAPI::class.java);

        CoroutineScope(Dispatchers.Main).launch { // 코루틴 사용하여 retorfit2 GET 호출
            try {
                Log.d(title, "uuid :" +  uuid)
                val result = service.getSuspendSkin(uuid)

                Log.d(title, "onResponse 성공: " + result);

                val jsonObject = JSONObject(result)

                Log.d(title, "id : " + jsonObject.get("id").toString())
                serverUUID.value = jsonObject.get("id").toString()

            } catch (e : Exception) {
                Log.d(title, "통신 실패 : " + e.printStackTrace())
                Toast.makeText(getApplication(), "ID 혹은 인터넷 연결상태를 확인해주세요.",Toast.LENGTH_SHORT).show()
            }
        }

    }

}