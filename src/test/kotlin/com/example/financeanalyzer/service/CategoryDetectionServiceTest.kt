package com.example.financeanalyzer.service

import com.example.financeanalyzer.model.Category
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CategoryDetectionServiceTest {

    private val service = CategoryDetectionService()

    @Test
    fun `detects transport category for Uber`() {
        assertEquals(Category.TRANSPORT, service.detectCategory("Uber trip to office"))
    }

    @Test
    fun `detects entertainment category for Netflix`() {
        assertEquals(Category.ENTERTAINMENT, service.detectCategory("Netflix monthly plan"))
    }

    @Test
    fun `detects food category for Perekrestok`() {
        assertEquals(Category.FOOD, service.detectCategory("Perekrestok groceries"))
    }

    @Test
    fun `returns other category for unknown description`() {
        assertEquals(Category.OTHER, service.detectCategory("Some random merchant"))
    }
}
