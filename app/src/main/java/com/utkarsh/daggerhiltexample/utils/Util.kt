package com.utkarsh.daggerhiltexample.utils

fun isCacheGood(startTime: Long, endTime: Long): Boolean {
    return (endTime - startTime) < (60*60*2*1000)
}