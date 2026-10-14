package com.example.data.repository

import com.example.data.local.HistoryDao
import com.example.data.local.HistoryEntity
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDao: HistoryDao) {
    val allHistory: Flow<List<HistoryEntity>> = historyDao.getAllHistory()

    suspend fun addCalculation(expression: String, result: String, angleMode: String): Long {
        return historyDao.insert(
            HistoryEntity(
                expression = expression,
                result = result,
                timestamp = System.currentTimeMillis(),
                angleMode = angleMode
            )
        )
    }

    suspend fun deleteItem(item: HistoryEntity) {
        historyDao.delete(item)
    }

    suspend fun deleteById(id: Long) {
        historyDao.deleteById(id)
    }

    suspend fun clearHistory() {
        historyDao.clearAll()
    }
}
