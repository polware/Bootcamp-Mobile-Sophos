package com.polware.sophosmobileapp.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.polware.sophosmobileapp.data.models.OfficesModel
import com.polware.sophosmobileapp.resources.RetrofitTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class OfficesMapViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule = InstantTaskExecutorRule()
    @Mock
    private lateinit var apiOfficeObserver: Observer<OfficesModel>
    private val mockWebServer: MockWebServer = MockWebServer()
    private lateinit var officesMapViewModel: OfficesMapViewModel
    private lateinit var apiHelper: ApiHelperImpl

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        officesMapViewModel = OfficesMapViewModel()
        officesMapViewModel.getOfficesList()
        officesMapViewModel.observeOfficesList().observeForever(apiOfficeObserver)
        mockWebServer.start()
        apiHelper = ApiHelperImpl(RetrofitTest.apiInterface)
    }

    @After
    fun shutdown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `read Sample Success Json File `() {
        val data = fileReader(this, "success_response_office.json")
        assertNotNull(data)
    }

    inline fun <reified T> fileReader(caller: T, filePath: String): String =
        T::class.java.getResource(filePath)?.readText() ?: throw IllegalArgumentException(
            "Could not find file $filePath. Make sure to put it in the correct resources folder for $caller's runtime.")

    @Test
    fun `fetch offices and check response code 200`(){
        val response = MockResponse()
            .setResponseCode(200)
            .setBody(fileReader(this, "success_response_office.json"))
        mockWebServer.enqueue(response)
        val actualResponse = apiHelper.getAllOffices().execute()
        assertThat(response.toString().contains("200")).isEqualTo(actualResponse.code().toString().contains("200"))
    }

    @Test
    fun `fetch details and check response success returned`(){
        val response = MockResponse()
            .setResponseCode(200)
            .setBody(fileReader(this, "json_string_format.json"))
        mockWebServer.enqueue(response)
        val mockResponse = response.getBody()?.readUtf8()
        val  actualResponse = apiHelper.getAllOffices().execute()
        val jsonToString = Gson().toJson(actualResponse.body())
        //assertEquals(mockResponse, jsonToString)
        assertThat(mockResponse).isEqualTo(jsonToString)
    }

}