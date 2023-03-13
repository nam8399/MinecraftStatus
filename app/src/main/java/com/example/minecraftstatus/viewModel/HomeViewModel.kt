package com.example.minecraftstatus.viewModel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.minecraftstatus.Data.MyApplication
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
    var serverName = MutableLiveData<String>()
    var serverEditionIndex = MutableLiveData<Int>()

    private val _event = MutableLiveData<Event<Boolean>>()


    init {
        testText.value = ""
        serverStatus.value = ""
        serverHostTxt.value = ""
        serverVersion.value = ""
        serverPeople.value = ""
        serverInputHost.value = ""
        serverName.value = ""
        isServerAdd.value = false
        serverEditionIndex.value = 0
    }

    val event: LiveData<Event<Boolean>>
        get() = _event

    fun onEvent(isAdd : Boolean) {
        _event.value = Event(isAdd)

        if (!isAdd) {
            MyApplication.prefs.setString("serverHost", "")
            MyApplication.prefs.setString("serverName", "")
            MyApplication.prefs.setString("serverEdition", "")

            testText.value = ""
            serverStatus.value = ""
            serverHostTxt.value = ""
            serverVersion.value = ""
            serverPeople.value = ""
            serverInputHost.value = ""
            serverName.value = ""
            serverEditionIndex.value = 0
        }
    }


    fun getMineacraftServer() { // 입력한 마인크래프트 서버의 상태를 받아오는 함수
        var serverHost : String = serverHostTxt.value.toString()
        val retrofit = Retrofit.Builder().baseUrl("https://api.mcstatus.io/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create()).build();

        val service = retrofit.create(MinecraftAPI::class.java);

        CoroutineScope(Dispatchers.Main).launch { // 코루틴 사용하여 retorfit2 GET 호출
            try {
                if ("".equals(serverHost) && "".equals(MyApplication.prefs.getString("serverHost", ""))) { // 저장된 메인 서버 주소값과 입력된 메인 서버 주소값이 없을경우
                    return@launch
                } else if (!"".equals(serverHost)){ // sharedPreferences에 메인 서버 값이 없을 경우 입력한 값으로 초기 셋팅
                    MyApplication.prefs.setString("serverHost", serverHost)
                    MyApplication.prefs.setString("serverName", serverName.value.toString())
                    MyApplication.prefs.setString("serverEdition", serverEditionIndex.value.toString())
                }

                serverHost = MyApplication.prefs.getString("serverHost", "") // 사용자가 값을 저장한 상태로 폰을 껐다가 켰을때 serverHost가 초기화 될 상황을 감안해 변수에 값을 다시 넣어준다
                serverName.value = MyApplication.prefs.getString("serverName", "") // 위와 마찬가지로 서버 이름도 넣어준다
                serverEditionIndex.value = MyApplication.prefs.getString("serverEdition", "").toInt() // 서버 에디션 정보도 넣어준다

                Log.d(title, serverHost)
                var result : String
                if (serverEditionIndex.value == 0) {
                    result = service.getSuspendJava(serverHost)
                } else {
                    result = service.getSuspendBE(serverHost)
                }


                Log.d(title, "onResponse 성공: " + result);
                testText.value = result

                val jsonObject = JSONObject(result)
                var online : Boolean?

                Log.d(title, jsonObject.get("online").toString())

                onEvent(true) // view로 이벤트 전달해서 서버 등록화면 변경

                if (jsonObject.get("online").toString().equals("true")) {
                    isServerAdd.value = true
                    serverStatus.value = "온라인"
                    online = true

                    val jsonVersion = JSONObject(jsonObject.get("version").toString())
                    val jsonPlayers = JSONObject(jsonObject.get("players").toString())

                    serverInputHost.value = jsonObject.get("host").toString()
                    try {
                        serverVersion.value = jsonVersion.get("name_raw").toString()
                    } catch (e : Exception) {
                        serverVersion.value = jsonVersion.get("name").toString()
                    }
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