package com.rz.paas.test.inChannel.adapter

interface Observable {
    fun addObserver(o: AdapterObserver)


    fun deleteObservers()
}