package com.example.minecraftstatus.viewModel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.minecraftstatus.Model.Event
import com.example.minecraftstatus.Model.MinecraftAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class HomeViewModel( application: Application) : AndroidViewModel(application){

// ViewModel()을 상속받을 경우
// class MainViewModel():ViewModel(){}

    //LiveData
//값이 변경되는 경우 MutableLiveData로 선언한다.

    private val title = "HomeViewModel"

    var testText = MutableLiveData<String>()
    var serverStatus = MutableLiveData<String>()
    var serverHostTxt = MutableLiveData<String>()
    var serverVersion = MutableLiveData<String>()
    var serverPeople = MutableLiveData<String>()
    var serverInputHost = MutableLiveData<String>()
    var isServerAdd = MutableLiveData<Boolean>()

    private val _event = MutableLiveData<Event<Boolean>>()


    init {
        testText.value = ""
        serverStatus.value = ""
        serverHostTxt.value = ""
        serverVersion.value = ""
        serverPeople.value = ""
        serverInputHost.value = ""
        isServerAdd.value = false
    }

    val event: LiveData<Event<Boolean>>
        get() = _event

    fun onEvent(isAdd : Boolean) {
        _event.value = Event(isAdd)
    }


    fun getMineacraftServer() {
        var serverHost : String = serverHostTxt.value.toString()
        val retrofit = Retrofit.Builder().baseUrl("https://api.mcstatus.io/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create()).build();

        val service = retrofit.create(MinecraftAPI::class.java);

        CoroutineScope(Dispatchers.Main).launch { // 코루틴 사용하여 retorfit2 GET 호출
            try {
                Log.d(title, serverHost)
                val result = service.getSuspendJava(serverHost)

                Log.d(title, "onResponse 성공: " + result);
                testText.value = result

                val jsonObject = JSONObject(result)
                var online : Boolean?

                Log.d(title, jsonObject.get("online").toString())

                onEvent(true) // view로 이벤트 전달해서 서버화면 변경

                if (jsonObject.get("online").toString().equals("true")) {
                    isServerAdd.value = true
                    serverStatus.value = "온라인"
                    online = true

                    val jsonVersion = JSONObject(jsonObject.get("version").toString())
                    val jsonPlayers = JSONObject(jsonObject.get("players").toString())

                    serverInputHost.value = jsonObject.get("host").toString()
                    serverVersion.value = jsonVersion.get("name_raw").toString()
                    serverPeople.value = jsonPlayers.get("online").toString() + " / " + jsonPlayers.get("max").toString()
                } else {
                    serverStatus.value = "오프라인"
                    serverInputHost.value = jsonObject.get("host").toString()
                    serverVersion.value = "오프라인"
                    serverPeople.value = "오프라인"

                    online = false
                }

                if (online) {

                }
            } catch (e : Exception) {
                Log.d(title, "통신 실패 : " + e.printStackTrace())
                Toast.makeText(getApplication(), "올바른 주소값을 입력해주세요",Toast.LENGTH_SHORT).show()
            }
        }

    }

}