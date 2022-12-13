package com.polware.sophosmobileapp.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test

class ValidationRepositoryTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun `invalid email or password format`(){
        val result = ValidationRepository(Application()).validateCredentials("peter@gmail.com", "a12")
        //assertThat(result.value).isEqualTo("Email format is invalid")
        assertThat(result.value).isEqualTo("Password must have at least 4 characters")
    }

}