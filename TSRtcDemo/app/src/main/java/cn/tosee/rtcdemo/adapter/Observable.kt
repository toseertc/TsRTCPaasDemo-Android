package cn.tosee.rtc.test.inChannel.adapter

interface Observable {
    fun addObserver(o: AdapterObserver)


    fun deleteObservers()
}