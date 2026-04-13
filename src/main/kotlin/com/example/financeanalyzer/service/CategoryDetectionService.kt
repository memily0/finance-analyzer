package com.example.financeanalyzer.service

import com.example.financeanalyzer.model.Category
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class CategoryDetectionService {

    fun detectCategory(description: String): Category {
        val normalized = description.lowercase(Locale.getDefault())

        return when {
            normalized.containsAny("uber", "taxi", "lyft") -> Category.TRANSPORT
            normalized.containsAny("netflix", "spotify", "steam") -> Category.ENTERTAINMENT
            normalized.containsAny("perekrestok", "pyaterochka", "lidl", "grocery") -> Category.FOOD
            normalized.containsAny("salary", "payroll") -> Category.SALARY
            normalized.containsAny("pharmacy", "clinic", "hospital") -> Category.HEALTH
            normalized.containsAny("electric", "water bill", "internet", "utility") -> Category.UTILITIES
            normalized.containsAny("mall", "amazon", "shop") -> Category.SHOPPING
            else -> Category.OTHER
        }
    }

    private fun String.containsAny(vararg tokens: String): Boolean {
        return tokens.any { contains(it) }
    }
}
