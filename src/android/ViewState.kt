package de.impacgroup.zoomimageview.module

enum class ViewState(val key: String) {
    DID_CLOSE("didClose"),
    WILL_CLOSE("willClose"),
    UNKNOWN("unknown");

    companion object {
        fun getFor(name: String): ViewState {
            return when(name) {
                DID_CLOSE.key -> DID_CLOSE
                WILL_CLOSE.key -> WILL_CLOSE
                else -> UNKNOWN
            }
        }
    }
}