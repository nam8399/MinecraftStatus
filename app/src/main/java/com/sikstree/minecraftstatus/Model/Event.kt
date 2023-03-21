package com.sikstree.minecraftstatus.Model

open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) { // 이벤트가 이미 처리 되었다면
            null                     // null 을 반환하고
        } else {                     // 그렇지 않으면
            hasBeenHandled = true    // 이벤트가 처리되었다고 표시한 후에
            content                  // 값을 반환함
        }
    }

    // 이벤트의 처리 여부에 상관 없이 값을 반환
    fun peekContent(): T = content
}
