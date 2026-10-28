package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ConnectionConfig
import com.example.data.model.SessionLog
import com.example.data.model.UserLicense
import com.example.data.repository.GitHubRepository
import com.example.data.repository.PreferencesManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class DashboardTab(val title: String) {
    OVERVIEW("Overview"),
    SHIZUKU("Shizuku"),
    LINK("Link"),
    USERS("Users")
}

enum class StatusLevel {
    READY,
    LOADING,
    OK,
    ERROR
}

data class ToastMessage(
    val id: Long = System.currentTimeMillis() + (0..999).random(),
    val text: String,
    val isError: Boolean = false
)

data class DashboardUiState(
    val currentTab: DashboardTab = DashboardTab.OVERVIEW,
    val isBooting: Boolean = true,
    val isTopLoading: Boolean = false,
    val currentTimeString: String = "",
    val sessionLogs: List<SessionLog> = emptyList(),

    // Shizuku
    val shizukuConfig: ConnectionConfig = ConnectionConfig(),
    val shizukuIsConfigOpen: Boolean = false,
    val shizukuContent: String = "",
    val shizukuCurrentContent: String? = null,
    val shizukuCurrentSize: Long? = null,
    val shizukuStatusText: String = "Ready",
    val shizukuStatusLevel: StatusLevel = StatusLevel.READY,
    val isShizukuBusy: Boolean = false,

    // Link
    val linkConfig: ConnectionConfig = ConnectionConfig(),
    val linkIsConfigOpen: Boolean = false,
    val linkInput: String = "",
    val linkCurrentPreview: String? = null,
    val linkStatusText: String = "Ready",
    val linkStatusLevel: StatusLevel = StatusLevel.READY,
    val isLinkBusy: Boolean = false,

    // Users
    val usersConfig: ConnectionConfig = ConnectionConfig(),
    val usersIsConfigOpen: Boolean = false,
    val isUsersSyncing: Boolean = false,
    val usersLoaded: Boolean = false,
    val usersList: List<UserLicense> = emptyList(),
    val disabledUsersList: List<UserLicense> = emptyList(),
    val currentSha: String = "",
    val searchQuery: String = "",

    // Add user form
    val newDeviceId: String = "",
    val newUserKey: String = "",
    val newDaysCount: String = "",
    val newAllowOffline: Boolean = true,
    val isAddingUser: Boolean = false,

    // Modals
    val editingUser: UserLicense? = null,
    val editNewKey: String = "",
    val editNewDays: String = "",
    val editNewOffline: Boolean? = null,
    val isSavingEdit: Boolean = false,

    val deletingUser: UserLicense? = null,
    val isDeletingUser: Boolean = false
) {
    val totalUsersCount: Int
        get() = usersList.size + disabledUsersList.size

    val activeUsersCount: Int
        get() = usersList.count { it.isActive }

    val expiredUsersCount: Int
        get() = usersList.count { !it.isActive } + disabledUsersList.size

    val filteredUsers: List<UserLicense>
        get() {
            val all = usersList.map { it.copy(isDisabled = false) } + disabledUsersList.map { it.copy(isDisabled = true) }
            val q = searchQuery.trim().lowercase(Locale.ROOT)
            return if (q.isBlank()) {
                all
            } else {
                all.filter {
                    it.key.lowercase(Locale.ROOT).contains(q) ||
                    it.deviceId.lowercase(Locale.ROOT).contains(q)
                }
            }
        }
}

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application)
    private val repo = GitHubRepository()

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _toastEvents = MutableSharedFlow<ToastMessage>()
    val toastEvents: SharedFlow<ToastMessage> = _toastEvents.asSharedFlow()

    init {
        loadSavedConnections()
        startClock()
        startBootSequence()
    }

    private fun startBootSequence() {
        viewModelScope.launch {
            delay(1600)
            _uiState.update { it.copy(isBooting = false) }
            // Auto sync users if connection was already saved
            if (_uiState.value.usersConfig.isSaved) {
                fetchUsers(silent = true)
            }
        }
    }

    fun dismissBoot() {
        _uiState.update { it.copy(isBooting = false) }
    }

    private fun startClock() {
        viewModelScope.launch {
            while (true) {
                val nowStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                _uiState.update { it.copy(currentTimeString = nowStr) }
                delay(1000)
            }
        }
    }

    private fun loadSavedConnections() {
        val shizuku = prefs.getShizukuConfig()
        val link = prefs.getLinkConfig()
        val users = prefs.getUsersConfig()
        val disabled = prefs.getDisabledUsers()

        _uiState.update {
            it.copy(
                shizukuConfig = shizuku,
                shizukuIsConfigOpen = !shizuku.isSaved,
                linkConfig = link,
                linkIsConfigOpen = !link.isSaved,
                usersConfig = users,
                usersIsConfigOpen = !users.isSaved,
                disabledUsersList = disabled
            )
        }
    }

    fun switchTab(tab: DashboardTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    private fun logAction(text: String) {
        val newLog = SessionLog(text = text)
        _uiState.update { current ->
            val updated = listOf(newLog) + current.sessionLogs.take(19)
            current.copy(sessionLogs = updated)
        }
    }

    fun showToast(message: String, isError: Boolean = false) {
        viewModelScope.launch {
            _toastEvents.emit(ToastMessage(text = message, isError = isError))
        }
    }

    // ==============================================================
    // SHIZUKU ACTIONS
    // ==============================================================
    fun toggleShizukuConfig(isOpen: Boolean) {
        _uiState.update { it.copy(shizukuIsConfigOpen = isOpen) }
    }

    fun saveShizukuConfig(token: String, rawUrl: String) {
        if (token.isBlank()) {
            showToast("Error: token is empty", true)
            return
        }
        if (rawUrl.isBlank()) {
            showToast("Error: raw URL is empty", true)
            return
        }
        val config = ConnectionConfig.from(token, rawUrl)
        if (config.parsedInfo == null) {
            showToast("Error: " + (config.parseError ?: "Invalid RAW URL"), true)
            return
        }
        prefs.saveShizukuConfig(token, rawUrl)
        _uiState.update {
            it.copy(
                shizukuConfig = config,
                shizukuIsConfigOpen = false,
                shizukuStatusText = "Connection saved successfully",
                shizukuStatusLevel = StatusLevel.OK
            )
        }
        showToast("Shizuku connection saved")
        logAction("Shizuku connection saved")
    }

    fun clearShizukuConfig() {
        prefs.clearShizukuConfig()
        val emptyConfig = ConnectionConfig()
        _uiState.update {
            it.copy(
                shizukuConfig = emptyConfig,
                shizukuIsConfigOpen = true,
                shizukuStatusText = "Connection cleared",
                shizukuStatusLevel = StatusLevel.OK
            )
        }
        showToast("Shizuku connection cleared")
    }

    fun onShizukuContentChange(newContent: String) {
        _uiState.update { it.copy(shizukuContent = newContent) }
    }

    fun viewShizukuFile() {
        val config = _uiState.value.shizukuConfig
        val info = config.parsedInfo
        if (!config.isSaved || info == null) {
            _uiState.update { it.copy(shizukuIsConfigOpen = true) }
            showToast("Connection config required", true)
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isShizukuBusy = true,
                    isTopLoading = true,
                    shizukuStatusText = "Reading current content...",
                    shizukuStatusLevel = StatusLevel.LOADING
                )
            }
            try {
                val (fileResp, decoded) = repo.getFile(config.token, info)
                _uiState.update {
                    it.copy(
                        shizukuContent = decoded,
                        shizukuCurrentContent = decoded,
                        shizukuCurrentSize = fileResp.size,
                        shizukuStatusText = "Current content loaded",
                        shizukuStatusLevel = StatusLevel.OK
                    )
                }
                showToast("Current Shizuku loaded")
                logAction("Shizuku loaded (${decoded.length} chars)")
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        shizukuStatusText = "Error: " + (e.message ?: "Failed to read"),
                        shizukuStatusLevel = StatusLevel.ERROR
                    )
                }
                showToast(e.message ?: "Failed to read Shizuku", true)
            } finally {
                _uiState.update { it.copy(isShizukuBusy = false, isTopLoading = false) }
            }
        }
    }

    fun updateShizukuFile() {
        val config = _uiState.value.shizukuConfig
        val info = config.parsedInfo
        if (!config.isSaved || info == null) {
            _uiState.update { it.copy(shizukuIsConfigOpen = true) }
            showToast("Connection config required", true)
            return
        }

        val content = _uiState.value.shizukuContent
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isShizukuBusy = true,
                    isTopLoading = true,
                    shizukuStatusText = "Updating Shizuku on GitHub...",
                    shizukuStatusLevel = StatusLevel.LOADING
                )
            }
            try {
                repo.updateFile(config.token, info, content)
                _uiState.update {
                    it.copy(
                        shizukuCurrentContent = content,
                        shizukuCurrentSize = content.toByteArray(Charsets.UTF_8).size.toLong(),
                        shizukuStatusText = "Success: file updated",
                        shizukuStatusLevel = StatusLevel.OK
                    )
                }
                showToast("Shizuku updated on GitHub")
                logAction("Shizuku updated on GitHub (${info.path})")
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        shizukuStatusText = "Error: " + (e.message ?: "Update failed"),
                        shizukuStatusLevel = StatusLevel.ERROR
                    )
                }
                showToast(e.message ?: "Failed to update Shizuku", true)
            } finally {
                _uiState.update { it.copy(isShizukuBusy = false, isTopLoading = false) }
            }
        }
    }

    // ==============================================================
    // LINK ACTIONS
    // ==============================================================
    fun toggleLinkConfig(isOpen: Boolean) {
        _uiState.update { it.copy(linkIsConfigOpen = isOpen) }
    }

    fun saveLinkConfig(token: String, rawUrl: String) {
        if (token.isBlank()) {
            showToast("Error: token is empty", true)
            return
        }
        if (rawUrl.isBlank()) {
            showToast("Error: raw URL is empty", true)
            return
        }
        val config = ConnectionConfig.from(token, rawUrl)
        if (config.parsedInfo == null) {
            showToast("Error: " + (config.parseError ?: "Invalid RAW URL"), true)
            return
        }
        prefs.saveLinkConfig(token, rawUrl)
        _uiState.update {
            it.copy(
                linkConfig = config,
                linkIsConfigOpen = false,
                linkStatusText = "Connection saved successfully",
                linkStatusLevel = StatusLevel.OK
            )
        }
        showToast("Link connection saved")
        logAction("Link connection saved")
    }

    fun clearLinkConfig() {
        prefs.clearLinkConfig()
        val empty = ConnectionConfig()
        _uiState.update {
            it.copy(
                linkConfig = empty,
                linkIsConfigOpen = true,
                linkStatusText = "Connection cleared",
                linkStatusLevel = StatusLevel.OK
            )
        }
        showToast("Link connection cleared")
    }

    fun onLinkInputChange(newVal: String) {
        _uiState.update { it.copy(linkInput = newVal) }
    }

    fun viewLinkFile() {
        val config = _uiState.value.linkConfig
        val info = config.parsedInfo
        if (!config.isSaved || info == null) {
            _uiState.update { it.copy(linkIsConfigOpen = true) }
            showToast("Connection config required", true)
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLinkBusy = true,
                    isTopLoading = true,
                    linkStatusText = "Reading online link...",
                    linkStatusLevel = StatusLevel.LOADING
                )
            }
            try {
                val (_, decoded) = repo.getFile(config.token, info)
                val cleanLink = decoded.trim()
                _uiState.update {
                    it.copy(
                        linkInput = cleanLink,
                        linkCurrentPreview = cleanLink,
                        linkStatusText = "Current link loaded",
                        linkStatusLevel = StatusLevel.OK
                    )
                }
                showToast("Current link loaded")
                logAction("Link loaded: $cleanLink")
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        linkStatusText = "Error: " + (e.message ?: "Failed to read link"),
                        linkStatusLevel = StatusLevel.ERROR
                    )
                }
                showToast(e.message ?: "Failed to read link", true)
            } finally {
                _uiState.update { it.copy(isLinkBusy = false, isTopLoading = false) }
            }
        }
    }

    fun updateLinkFile() {
        val config = _uiState.value.linkConfig
        val info = config.parsedInfo
        if (!config.isSaved || info == null) {
            _uiState.update { it.copy(linkIsConfigOpen = true) }
            showToast("Connection config required", true)
            return
        }

        val linkText = _uiState.value.linkInput.trim()
        if (linkText.isBlank()) {
            showToast("Link cannot be empty", true)
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLinkBusy = true,
                    isTopLoading = true,
                    linkStatusText = "Updating link on GitHub...",
                    linkStatusLevel = StatusLevel.LOADING
                )
            }
            try {
                repo.updateFile(config.token, info, linkText)
                _uiState.update {
                    it.copy(
                        linkCurrentPreview = linkText,
                        linkStatusText = "Success: link updated",
                        linkStatusLevel = StatusLevel.OK
                    )
                }
                showToast("Link updated on GitHub")
                logAction("Link updated on GitHub ($linkText)")
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        linkStatusText = "Error: " + (e.message ?: "Update failed"),
                        linkStatusLevel = StatusLevel.ERROR
                    )
                }
                showToast(e.message ?: "Failed to update link", true)
            } finally {
                _uiState.update { it.copy(isLinkBusy = false, isTopLoading = false) }
            }
        }
    }

    // ==============================================================
    // USERS ACTIONS
    // ==============================================================
    fun toggleUsersConfig(isOpen: Boolean) {
        _uiState.update { it.copy(usersIsConfigOpen = isOpen) }
    }

    fun saveUsersConfig(token: String, rawUrl: String) {
        if (token.isBlank()) {
            showToast("Error: token is empty", true)
            return
        }
        if (rawUrl.isBlank()) {
            showToast("Error: raw URL is empty", true)
            return
        }
        val config = ConnectionConfig.from(token, rawUrl)
        if (config.parsedInfo == null) {
            showToast("Error: " + (config.parseError ?: "Invalid RAW URL"), true)
            return
        }
        prefs.saveUsersConfig(token, rawUrl)
        _uiState.update {
            it.copy(
                usersConfig = config,
                usersIsConfigOpen = false
            )
        }
        showToast("Users connection saved")
        logAction("Users connection saved")
        fetchUsers(silent = false)
    }

    fun clearUsersConfig() {
        prefs.clearUsersConfig()
        val empty = ConnectionConfig()
        _uiState.update {
            it.copy(
                usersConfig = empty,
                usersIsConfigOpen = true,
                usersLoaded = false,
                usersList = emptyList()
            )
        }
        showToast("Users connection cleared")
    }

    fun onSearchQueryChange(q: String) {
        _uiState.update { it.copy(searchQuery = q) }
    }

    fun onNewDeviceIdChange(v: String) = _uiState.update { it.copy(newDeviceId = v) }
    fun onNewUserKeyChange(v: String) = _uiState.update { it.copy(newUserKey = v) }
    fun onNewDaysCountChange(v: String) = _uiState.update { it.copy(newDaysCount = v) }
    fun onNewAllowOfflineChange(v: Boolean) = _uiState.update { it.copy(newAllowOffline = v) }

    fun fetchUsers(silent: Boolean = false) {
        val config = _uiState.value.usersConfig
        val info = config.parsedInfo
        if (!config.isSaved || info == null) {
            if (!silent) {
                _uiState.update { it.copy(usersIsConfigOpen = true) }
                showToast("Users connection required", true)
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isUsersSyncing = true, isTopLoading = true) }
            try {
                val (sha, list) = repo.fetchUsers(config.token, info)
                _uiState.update {
                    it.copy(
                        currentSha = sha,
                        usersList = list,
                        usersLoaded = true
                    )
                }
                if (!silent) {
                    showToast("Users synced (${list.size} active)")
                    logAction("Users synced (${list.size + _uiState.value.disabledUsersList.size} total)")
                }
            } catch (e: Exception) {
                if (!silent) {
                    showToast(e.message ?: "Failed to sync users", true)
                }
            } finally {
                _uiState.update { it.copy(isUsersSyncing = false, isTopLoading = false) }
            }
        }
    }

    fun addNewUser() {
        val deviceId = _uiState.value.newDeviceId.trim()
        val key = _uiState.value.newUserKey.trim()
        val daysCountStr = _uiState.value.newDaysCount.trim()
        val allowOffline = _uiState.value.newAllowOffline

        if (deviceId.isBlank() || key.isBlank() || daysCountStr.isBlank()) {
            showToast("Fill in all fields", true)
            return
        }
        val days = daysCountStr.toIntOrNull()
        if (days == null || days <= 0) {
            showToast("Enter valid license days", true)
            return
        }

        val config = _uiState.value.usersConfig
        val info = config.parsedInfo
        if (!config.isSaved || info == null) {
            _uiState.update { it.copy(usersIsConfigOpen = true) }
            showToast("Users connection required", true)
            return
        }

        val newUser = UserLicense(
            deviceId = deviceId,
            key = key,
            expiryDate = UserLicense.calculateExpiry(days),
            allowOffline = allowOffline,
            isDisabled = false
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isAddingUser = true, isTopLoading = true) }
            try {
                val updatedList = _uiState.value.usersList + newUser
                val newSha = repo.pushUsers(
                    token = config.token,
                    info = info,
                    shaToUse = _uiState.value.currentSha,
                    usersList = updatedList,
                    commitMessage = "Add $key"
                )
                _uiState.update {
                    it.copy(
                        currentSha = newSha,
                        usersList = updatedList,
                        newDeviceId = "",
                        newUserKey = "",
                        newDaysCount = ""
                    )
                }
                showToast("New user added: $key")
                logAction("User added: $key ($deviceId)")
            } catch (e: Exception) {
                showToast(e.message ?: "Failed to add user", true)
            } finally {
                _uiState.update { it.copy(isAddingUser = false, isTopLoading = false) }
            }
        }
    }

    fun toggleUserDisabled(user: UserLicense) {
        val config = _uiState.value.usersConfig
        val info = config.parsedInfo
        if (!config.isSaved || info == null) {
            showToast("Users connection required", true)
            return
        }

        val key = user.key
        val isCurrentlyDisabled = user.isDisabled

        viewModelScope.launch {
            _uiState.update { it.copy(isTopLoading = true) }
            try {
                val currentUsers = _uiState.value.usersList.toMutableList()
                val currentDisabled = _uiState.value.disabledUsersList.toMutableList()

                val newSha = if (!isCurrentlyDisabled) {
                    // Pause user: move from active to disabled
                    val found = currentUsers.find { it.key == key }
                    if (found != null) {
                        currentUsers.remove(found)
                        currentDisabled.add(found.copy(isDisabled = true))
                    }
                    prefs.saveDisabledUsers(currentDisabled)

                    repo.pushUsers(
                        token = config.token,
                        info = info,
                        shaToUse = _uiState.value.currentSha,
                        usersList = currentUsers,
                        commitMessage = "Disable $key"
                    )
                } else {
                    // Enable user: move from disabled to active
                    val found = currentDisabled.find { it.key == key }
                    if (found != null) {
                        currentDisabled.remove(found)
                        currentUsers.add(found.copy(isDisabled = false))
                    }
                    prefs.saveDisabledUsers(currentDisabled)

                    repo.pushUsers(
                        token = config.token,
                        info = info,
                        shaToUse = _uiState.value.currentSha,
                        usersList = currentUsers,
                        commitMessage = "Enable $key"
                    )
                }

                _uiState.update {
                    it.copy(
                        currentSha = newSha,
                        usersList = currentUsers,
                        disabledUsersList = currentDisabled
                    )
                }
                val msg = if (!isCurrentlyDisabled) "User paused" else "User enabled again"
                showToast(msg)
                logAction("$msg: $key")
            } catch (e: Exception) {
                showToast(e.message ?: "Failed to toggle user status", true)
            } finally {
                _uiState.update { it.copy(isTopLoading = false) }
            }
        }
    }

    fun openEditUserDialog(user: UserLicense) {
        _uiState.update {
            it.copy(
                editingUser = user,
                editNewKey = "",
                editNewDays = "",
                editNewOffline = null
            )
        }
    }

    fun dismissEditDialog() {
        _uiState.update { it.copy(editingUser = null) }
    }

    fun onEditKeyChange(v: String) = _uiState.update { it.copy(editNewKey = v) }
    fun onEditDaysChange(v: String) = _uiState.update { it.copy(editNewDays = v) }
    fun onEditOfflineChange(v: Boolean?) = _uiState.update { it.copy(editNewOffline = v) }

    fun saveEditUser() {
        val user = _uiState.value.editingUser ?: return
        val newKey = _uiState.value.editNewKey.trim()
        val newDaysStr = _uiState.value.editNewDays.trim()
        val newOffline = _uiState.value.editNewOffline

        val newDays = if (newDaysStr.isNotBlank()) newDaysStr.toIntOrNull() else null
        val updatedExpiry = if (newDays != null && newDays > 0) UserLicense.calculateExpiry(newDays) else user.expiryDate
        val updatedKey = if (newKey.isNotBlank()) newKey else user.key
        val updatedOffline = newOffline ?: user.allowOffline

        val updatedUser = user.copy(
            key = updatedKey,
            expiryDate = updatedExpiry,
            allowOffline = updatedOffline
        )

        val isTargetInDisabled = user.isDisabled

        viewModelScope.launch {
            _uiState.update { it.copy(isSavingEdit = true, isTopLoading = true) }
            try {
                if (isTargetInDisabled) {
                    val currentDisabled = _uiState.value.disabledUsersList.toMutableList()
                    val idx = currentDisabled.indexOfFirst { it.key == user.key }
                    if (idx != -1) {
                        currentDisabled[idx] = updatedUser
                    }
                    prefs.saveDisabledUsers(currentDisabled)
                    _uiState.update {
                        it.copy(
                            disabledUsersList = currentDisabled,
                            editingUser = null
                        )
                    }
                    showToast("User changes saved")
                    logAction("User edited (local): $updatedKey")
                } else {
                    val config = _uiState.value.usersConfig
                    val info = config.parsedInfo ?: throw Exception("Users config required")

                    val currentUsers = _uiState.value.usersList.toMutableList()
                    val idx = currentUsers.indexOfFirst { it.key == user.key }
                    if (idx != -1) {
                        currentUsers[idx] = updatedUser
                    }

                    val newSha = repo.pushUsers(
                        token = config.token,
                        info = info,
                        shaToUse = _uiState.value.currentSha,
                        usersList = currentUsers,
                        commitMessage = "Edit user $updatedKey"
                    )

                    _uiState.update {
                        it.copy(
                            currentSha = newSha,
                            usersList = currentUsers,
                            editingUser = null
                        )
                    }
                    showToast("Changes confirmed")
                    logAction("User edited: $updatedKey")
                }
            } catch (e: Exception) {
                showToast(e.message ?: "Failed to edit user", true)
            } finally {
                _uiState.update { it.copy(isSavingEdit = false, isTopLoading = false) }
            }
        }
    }

    fun openDeleteDialog(user: UserLicense) {
        _uiState.update { it.copy(deletingUser = user) }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(deletingUser = null) }
    }

    fun confirmDeleteUser() {
        val user = _uiState.value.deletingUser ?: return
        val config = _uiState.value.usersConfig
        val info = config.parsedInfo

        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingUser = true, isTopLoading = true) }
            try {
                val currentDisabled = _uiState.value.disabledUsersList.filter { it.key != user.key }
                prefs.saveDisabledUsers(currentDisabled)

                val currentUsers = _uiState.value.usersList.filter { it.key != user.key }

                val newSha = if (info != null && config.isSaved && !user.isDisabled) {
                    repo.pushUsers(
                        token = config.token,
                        info = info,
                        shaToUse = _uiState.value.currentSha,
                        usersList = currentUsers,
                        commitMessage = "Delete ${user.key}"
                    )
                } else {
                    _uiState.value.currentSha
                }

                _uiState.update {
                    it.copy(
                        currentSha = newSha,
                        usersList = currentUsers,
                        disabledUsersList = currentDisabled,
                        deletingUser = null
                    )
                }
                showToast("User deleted")
                logAction("User deleted: ${user.key}")
            } catch (e: Exception) {
                showToast(e.message ?: "Failed to delete user", true)
            } finally {
                _uiState.update { it.copy(isDeletingUser = false, isTopLoading = false) }
            }
        }
    }
}
