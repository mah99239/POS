package com.casecode.data.utils

import kotlinx.coroutines.flow.Flow

interface  NetworkMonitor
{
   val isOnline: Flow<Boolean>
}