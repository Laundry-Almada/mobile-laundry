package com.almalaundry.profile

import com.almalaundry.featured.auth.data.repositories.AuthRepository
import com.almalaundry.featured.profile.data.model.Laundry
import com.almalaundry.featured.profile.data.model.UpdateProfileRequest
import com.almalaundry.featured.profile.data.model.UserData
import com.almalaundry.featured.profile.data.model.UserResponse
import com.almalaundry.featured.profile.data.repository.ProfileRepository
import com.almalaundry.featured.profile.presentation.viewmodels.ProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class ProfileViewModelTest {

    private lateinit var viewModel: ProfileViewModel
    private val mockProfileRepository: ProfileRepository = mock()
    private val mockAuthRepository: AuthRepository = mock()
    private val testDispatcher = UnconfinedTestDispatcher()

    // Data dummy untuk init load
    private val initialUserResponse = UserResponse(
        success = true,
        message = "Initial load",
        data = UserData(
            id = "init",
            name = "Init User",
            email = "init@example.com",
            role = "user",
            laundry = Laundry("1", "Init Laundry")
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Setup mock response untuk init load
        runBlocking {
            whenever(mockProfileRepository.getUser()).thenReturn(Result.success(initialUserResponse))
        }

        viewModel = ProfileViewModel(mockProfileRepository, mockAuthRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should load profile data`() = runBlocking {
        // Verify bahwa getUser() dipanggil saat init
        verify(mockProfileRepository).getUser()

        // Verifikasi state awal
        val initialState = viewModel.state.value
        assertEquals("Init User", initialState.name)
        assertEquals("init@example.com", initialState.email)
    }

    @Test
    fun `loadProfileData should update state with user data on success`() = runBlocking {
        // Given
        val mockUserResponse = UserResponse(
            success = true,
            message = "Success",
            data = UserData(
                id = "1",
                name = "John Doe",
                email = "john@example.com",
                role = "user",
                laundry = Laundry("1", "Laundry Name")
            )
        )

        whenever(mockProfileRepository.getUser()).thenReturn(Result.success(mockUserResponse))

        // When
        viewModel.loadProfileData()

        // Then
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals("John Doe", state.name)
        assertEquals("john@example.com", state.email)
        assertEquals("user", state.role)
        assertEquals("Laundry Name", state.laundryName)
        assertNull(state.error)
    }

    @Test
    fun `loadProfileData should update state with error on failure`() = runBlocking {
        // Given
        val errorMessage = "Error loading profile"
        whenever(mockProfileRepository.getUser()).thenReturn(Result.failure(Exception(errorMessage)))

        // When
        viewModel.loadProfileData()

        // Then
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `updateProfile should update state and call onSuccess when successful`() = runBlocking {
        // Given
        val mockUserResponse = UserResponse(
            success = true,
            message = "Success",
            data = UserData(
                id = "1",
                name = "Updated Name",
                email = "updated@example.com",
                role = "user",
                laundry = Laundry("1", "Laundry Name")
            )
        )

        var onSuccessCalled = false
        var onErrorCalled = false

        whenever(mockProfileRepository.updateProfile(any())).thenReturn(Result.success(mockUserResponse))

        // When
        viewModel.updateProfile(
            name = "Updated Name",
            email = "updated@example.com",
            password = "newpassword",
            onSuccess = { onSuccessCalled = true },
            onError = { onErrorCalled = true }
        )

        // Then
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals("Updated Name", state.name)
        assertEquals("updated@example.com", state.email)
        assertTrue(onSuccessCalled)
        assertFalse(onErrorCalled)
    }

    @Test
    fun `updateProfile should update state and call onError when failed`() = runBlocking {
        // Given
        val errorMessage = "Update failed"
        var onSuccessCalled = false
        var onErrorCalled = false

        whenever(mockProfileRepository.updateProfile(any())).thenReturn(Result.failure(Exception(errorMessage)))

        // When
        viewModel.updateProfile(
            name = "Updated Name",
            email = "updated@example.com",
            password = "newpassword",
            onSuccess = { onSuccessCalled = true },
            onError = { onErrorCalled = true }
        )

        // Then
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
        assertFalse(onSuccessCalled)
        assertTrue(onErrorCalled)
    }

    @Test
    fun `updateProfile should handle empty password`(): Unit = runBlocking {
        // Given
        val mockUserResponse = UserResponse(
            success = true,
            message = "Success",
            data = UserData(
                id = "1",
                name = "Updated Name",
                email = "updated@example.com",
                role = "user",
                laundry = Laundry("1", "Laundry Name")
            )
        )

        whenever(mockProfileRepository.updateProfile(any())).thenReturn(Result.success(mockUserResponse))

        // When
        viewModel.updateProfile(
            name = "Updated Name",
            email = "updated@example.com",
            password = "",
            onSuccess = {},
            onError = {}
        )

        // Then
        verify(mockProfileRepository).updateProfile(
            UpdateProfileRequest(
                name = "Updated Name",
                email = "updated@example.com",
                password = null
            )
        )
    }

    @Test
    fun `logout should update state to logged out when successful`() = runBlocking {
        // Given
        whenever(mockAuthRepository.logout()).thenReturn(Result.success(Unit))

        // When
        viewModel.logout()

        // Then
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertTrue(state.isLoggedOut)
        assertNull(state.error)
    }

    @Test
    fun `logout should update state with error when failed`() = runBlocking {
        // Given
        val errorMessage = "Logout failed"
        whenever(mockAuthRepository.logout()).thenReturn(Result.failure(Exception(errorMessage)))

        // When
        viewModel.logout()

        // Then
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertFalse(state.isLoggedOut)
        assertEquals(errorMessage, state.error)
    }
}