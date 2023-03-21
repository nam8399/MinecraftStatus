package com.sikstree.minecraftstatus.viewModel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sikstree.minecraftstatus.Data.MyApplication
import com.sikstree.minecraftstatus.Data.ServerItem
import com.sikstree.minecraftstatus.Model.Event
import com.sikstree.minecraftstatus.Model.MinecraftAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class ServerListViewModel( application: Application) : AndroidViewModel(application){

// ViewModel()을 상속받을 경우
// class MainViewModel():ViewModel(){}

    //LiveData
//값이 변경되는 경우 MutableLiveData로 선언한다.

    private val title = "ServerListViewModel"

    var testText = MutableLiveData<String>()
    var serverStatus = MutableLiveData<String>()
    var serverHostTxt = MutableLiveData<String>()
    var serverVersion = MutableLiveData<String>()
    var serverPeople = MutableLiveData<String>()
    var serverInputHost = MutableLiveData<String>()
    var isServerAdd = MutableLiveData<Boolean>()
    var serverName = MutableLiveData<String>()
    var serverEditionIndex = MutableLiveData<Int>()
    var serverSlotIndex = MutableLiveData<Int>()
    var showDialog = MutableLiveData<Boolean>() // 다이얼로그를 띄우기 위한 LiveData 변수
    var serverList = MutableLiveData<ServerItem>()
    var isListNull = MutableLiveData<Boolean>()

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
        showDialog.value = false
    }

    val event: LiveData<Event<Boolean>>
        get() = _event

    fun onEvent(isAdd : Boolean) {
        _event.value = Event(isAdd)

        if (!isAdd) {
            MyApplication.prefs.setString("serverHost_list", "")
            MyApplication.prefs.setString("serverName_list", "")
            MyApplication.prefs.setString("serverEdition_list", "")

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

    fun getMinecraftServer() { // 입력한 마인크래프트 서버의 상태를 받아오는 함수
        showDialog.value = true // 다이얼로그 띄우기

        var serverHost : String = serverHostTxt.value.toString()
        val retrofit = Retrofit.Builder().baseUrl("https://api.mcstatus.io/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create()).build();

        val service = retrofit.create(MinecraftAPI::class.java);

        CoroutineScope(Dispatchers.Main).launch { // 코루틴 사용하여 retorfit2 GET 호출
            try {
                if ("".equals(serverHost)) {
                    showDialog.value = false
                    return@launch
                }

                Log.d(title, serverHost)
                var result : String
                if (serverEditionIndex.value == 0) {
                    result = service.getSuspendJava(serverHost)
                } else {
                    result = service.getSuspendBE(serverHost)
                }

                when (serverSlotIndex.value) { // 이미 슬롯에 리스트가 저장되어 있다면 토스트문구 띄우면서 리턴
                    0 -> {
                        if (!"".equals(MyApplication.prefs.getString("slot1", ""))) {
                            Toast.makeText(getApplication(), "이미 저장된 리스트가 있습니다.", Toast.LENGTH_SHORT).show()
                            showDialog.value = false
                            return@launch
                        } else {
                            MyApplication.prefs.setString("slot1", serverHost)
                        }
                    }
                    1 -> {
                        if (!"".equals(MyApplication.prefs.getString("slot2", ""))) {
                            Toast.makeText(getApplication(), "이미 저장된 리스트가 있습니다.", Toast.LENGTH_SHORT).show()
                            showDialog.value = false
                            return@launch
                        } else {
                            MyApplication.prefs.setString("slot2",serverHost)
                        }
                    }
                    2 -> {
                        if (!"".equals(MyApplication.prefs.getString("slot3", ""))) {
                            Toast.makeText(getApplication(), "이미 저장된 리스트가 있습니다.", Toast.LENGTH_SHORT).show()
                            showDialog.value = false
                            return@launch
                        } else {
                            MyApplication.prefs.setString("slot3",serverHost)
                        }
                    }
                    3 -> {
                        if (!"".equals(MyApplication.prefs.getString("slot4", ""))) {
                            Toast.makeText(getApplication(), "이미 저장된 리스트가 있습니다.", Toast.LENGTH_SHORT).show()
                            showDialog.value = false
                            return@launch
                        } else {
                            MyApplication.prefs.setString("slot4",serverHost)
                        }
                    }

                }


                Log.d(title, "onResponse 성공: " + result);
                testText.value = result

                val jsonObject = JSONObject(result)
                var online : Boolean?

                Log.d(title, jsonObject.get("online").toString())

//                onEvent(true) // view로 이벤트 전달해서 서버 등록화면 변경되는

                if (jsonObject.get("online").toString().equals("true")) {
                    isServerAdd.value = true
                    online = true

                    serverObjectSetting(online, jsonObject)

                    var slotName : String = ""
                    if (serverSlotIndex.value == 0) {
                        slotName = "저장슬롯 1"
                    } else if (serverSlotIndex.value == 1) {
                        slotName = "저장슬롯 2"
                    } else if (serverSlotIndex.value == 2) {
                        slotName = "저장슬롯 3"
                    } else if (serverSlotIndex.value == 3) {
                        slotName = "저장슬롯 4"
                    }

                    serverList.value = ServerItem(slotName, serverStatus.value.toString(), serverInputHost.value.toString(), serverVersion.value.toString(), serverPeople.value.toString())
                } else {
                    online = false

                    serverObjectSetting(online, jsonObject)
                }

                showDialog.value = false // 다이얼로그 종료

                if (online) {

                }
            } catch (e : Exception) {
                Log.d(title, "통신 실패 : " + e.printStackTrace())
                Toast.makeText(getApplication(), "올바른 주소값을 입력해주세요",Toast.LENGTH_SHORT).show()

                if (MyApplication.prefs.getString("slot1","").equals(serverHost)) {
                    MyApplication.prefs.setString("slot1","")
                } else if (MyApplication.prefs.getString("slot2","").equals(serverHost)) {
                    MyApplication.prefs.setString("slot2","")
                } else if (MyApplication.prefs.getString("slot3","").equals(serverHost)) {
                    MyApplication.prefs.setString("slot3","")
                } else if (MyApplication.prefs.getString("slot4","").equals(serverHost)) {
                    MyApplication.prefs.setString("slot4","")
                }
                showDialog.value = false // 다이얼로그 종료
            }
        }

    }


    fun getMinecraftServer(index : String, serverhost : String) { // 입력한 마인크래프트 서버의 상태를 받아오는 함수
        showDialog.value = true // 다이얼로그 띄우기

        var serverHost : String = serverhost
        val retrofit = Retrofit.Builder().baseUrl("https://api.mcstatus.io/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create()).build();

        val service = retrofit.create(MinecraftAPI::class.java);

        CoroutineScope(Dispatchers.Main).launch { // 코루틴 사용하여 retorfit2 GET 호출
            try {
                if ("".equals(serverHost)) {
                    showDialog.value = false
                    return@launch
                }

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

//                onEvent(true) // view로 이벤트 전달해서 서버 등록화면 변경되는

                if (jsonObject.get("online").toString().equals("true")) {
                    isServerAdd.value = true
                    online = true

                    serverObjectSetting(online, jsonObject)

                    serverList.value = ServerItem(index, serverStatus.value.toString(), serverInputHost.value.toString(), serverVersion.value.toString(), serverPeople.value.toString())
               } else {
                    online = false

                    serverObjectSetting(online, jsonObject)
                    serverList.value = ServerItem(index, serverStatus.value.toString(), serverInputHost.value.toString(), serverVersion.value.toString(), serverPeople.value.toString())
                }

                showDialog.value = false // 다이얼로그 종료

                if (online) {

                }
            } catch (e : Exception) {
                Log.d(title, "통신 실패 : " + e.printStackTrace())
                Toast.makeText(getApplication(), "서버 상태가 올바르지 않습니다. 다시 등록해주세요\n $serverHost", Toast.LENGTH_SHORT).show()

                if (MyApplication.prefs.getString("slot1","").equals(serverHost)) {
                    MyApplication.prefs.setString("slot1","")
                } else if (MyApplication.prefs.getString("slot2","").equals(serverHost)) {
                    MyApplication.prefs.setString("slot2","")
                } else if (MyApplication.prefs.getString("slot3","").equals(serverHost)) {
                    MyApplication.prefs.setString("slot3","")
                } else if (MyApplication.prefs.getString("slot4","").equals(serverHost)) {
                    MyApplication.prefs.setString("slot4","")
                }
                showDialog.value = false // 다이얼로그 종료
            }
        }

    }

    private fun serverObjectSetting(online : Boolean, jsonObject: JSONObject) {
        if (online) {
            val jsonVersion = JSONObject(jsonObject.get("version").toString())
            val jsonPlayers = JSONObject(jsonObject.get("players").toString())

            serverStatus.value = "온라인"
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
        }
    }


}