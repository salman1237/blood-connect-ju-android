@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.deshlet.bloodconnectju.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deshlet.bloodconnectju.data.remote.dto.DonorProfileUpdateRequest
import com.deshlet.bloodconnectju.data.remote.dto.UserDto
import com.deshlet.bloodconnectju.ui.components.GroupedDropdownField
import com.deshlet.bloodconnectju.ui.components.SimpleDropdownField
import com.deshlet.bloodconnectju.ui.components.UserAvatar
import com.deshlet.bloodconnectju.ui.components.selectedChipColors
import kotlinx.coroutines.launch

private val roleOptions = listOf("student" to "Student", "staff" to "Staff", "faculty" to "Teacher")
private val genderOptions = listOf("male" to "Male", "female" to "Female", "other" to "Other")
private val bloodGroupOptions = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

@Composable
fun ProfileScreen(
    onLoggedOut: () -> Unit,
    onAccountDeleted: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    // Same reasoning as RequestsScreen — refetch every time this screen
    // becomes visible again, not just the first time it's created. Safe
    // for in-progress edits: AccountCard/DonorProfileCard's local field
    // state is keyed on user.id, which doesn't change on a refresh.
    LaunchedEffect(Unit) { viewModel.refresh() }

    Scaffold(
        // No back arrow — this is one of the four bottom-tab roots
        // (Home/Requests/Donors/Profile), reached by switching tabs rather
        // than pushed onto a stack, so there's nothing to navigate back to.
        topBar = { TopAppBar(title = { Text("Profile") }) },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            val user = uiState.user
            when {
                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                user == null -> Text(
                    "Couldn't load your profile.",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                else -> ProfileContent(
                    user = user,
                    uiState = uiState,
                    viewModel = viewModel,
                    onLogOut = { scope.launch { viewModel.logout(); onLoggedOut() } },
                    onAccountDeleted = onAccountDeleted,
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    user: UserDto,
    uiState: ProfileUiState,
    viewModel: ProfileViewModel,
    onLogOut: () -> Unit,
    onAccountDeleted: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        uiState.statusMessage?.let { message ->
            Text(message, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
        }
        uiState.errorMessage?.let { message ->
            Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        AccountCard(
            user = user,
            uiState = uiState,
            onSave = viewModel::updateAccount,
            onUploadPhoto = viewModel::uploadPhoto,
            onRemovePhoto = viewModel::removePhoto,
        )
        DonorProfileCard(user, uiState, onSave = viewModel::updateDonorProfile)

        OutlinedButton(onClick = onLogOut, modifier = Modifier.fillMaxWidth()) {
            Text("Log out")
        }

        DangerZoneCard(uiState, onDelete = { password -> viewModel.deleteAccount(password, onAccountDeleted) })

        Spacer(Modifier.size(24.dp))
    }
}

@Composable
private fun AccountCard(
    user: UserDto,
    uiState: ProfileUiState,
    onSave: (String, String) -> Unit,
    onUploadPhoto: (Uri) -> Unit,
    onRemovePhoto: () -> Unit,
) {
    var name by remember(user.id) { mutableStateOf(user.name) }
    var email by remember(user.id) { mutableStateOf(user.email) }

    // The system Photo Picker (Android 13+, backported to API 24+ by the
    // activity library) — no READ_MEDIA_IMAGES/READ_EXTERNAL_STORAGE
    // permission needed, unlike a raw ACTION_GET_CONTENT/gallery intent.
    val pickPhotoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) onUploadPhoto(uri)
    }

    Card {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Account", style = MaterialTheme.typography.titleMedium)
            // Mirrors web's upload-photo form: a real photo when the
            // account has one, initials otherwise, with upload/remove
            // actions right next to it — visible everywhere avatar_url is
            // surfaced (UserResource), same as web's x-user-avatar.
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box {
                    UserAvatar(
                        name = user.name,
                        avatarUrl = user.avatar_url,
                        modifier = Modifier.size(72.dp),
                    )
                    if (uiState.isSavingPhoto) {
                        Box(
                            modifier = Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.35f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = Color.White)
                        }
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    TextButton(
                        onClick = { pickPhotoLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        enabled = !uiState.isSavingPhoto,
                    ) {
                        Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.size(6.dp))
                        Text("Upload photo")
                    }
                    if (user.avatar_url != null) {
                        TextButton(onClick = onRemovePhoto, enabled = !uiState.isSavingPhoto) {
                            Text("Remove photo", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                isError = uiState.fieldErrors.containsKey("name"),
                supportingText = uiState.fieldErrors["name"]?.firstOrNull()?.let { m -> { Text(m) } },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = uiState.fieldErrors.containsKey("email"),
                supportingText = uiState.fieldErrors["email"]?.firstOrNull()?.let { m -> { Text(m) } }
                    ?: if (!user.email_verified) {
                        { Text("Not yet verified", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    } else {
                        null
                    },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = { onSave(name, email) },
                enabled = !uiState.isSavingAccount,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (uiState.isSavingAccount) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text("Save account details")
                }
            }
        }
    }
}

@Composable
private fun DonorProfileCard(user: UserDto, uiState: ProfileUiState, onSave: (DonorProfileUpdateRequest) -> Unit) {
    var bloodGroup by remember(user.id) { mutableStateOf(user.donor_profile?.blood_group ?: "") }
    var role by remember(user.id) { mutableStateOf(user.role) }
    var gender by remember(user.id) { mutableStateOf(user.gender ?: "male") }
    var dateOfBirth by remember(user.id) { mutableStateOf(user.date_of_birth ?: "") }
    var department by remember(user.id) { mutableStateOf(user.department ?: "") }
    var hall by remember(user.id) { mutableStateOf(user.hall ?: "") }
    var batch by remember(user.id) { mutableStateOf(user.batch ?: "") }
    var phone by remember(user.id) { mutableStateOf(user.phone ?: "") }
    var hasWhatsapp by remember(user.id) { mutableStateOf(user.phone_has_whatsapp ?: true) }
    var whatsappNumber by remember(user.id) { mutableStateOf(user.whatsapp_number ?: "") }
    var hidePhone by remember(user.id) { mutableStateOf(user.phone_visibility == "admin_only") }
    var isAvailable by remember(user.id) { mutableStateOf(user.donor_profile?.is_available ?: true) }
    var lastDonationDate by remember(user.id) { mutableStateOf(user.donor_profile?.last_donation_date ?: "") }

    val meta = uiState.meta

    Card {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Donor profile", style = MaterialTheme.typography.titleMedium)

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Blood group", style = MaterialTheme.typography.labelLarge)
                bloodGroupOptions.chunked(4).forEach { rowItems ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        rowItems.forEach { group ->
                            FilterChip(
                                selected = bloodGroup == group,
                                onClick = { bloodGroup = group },
                                label = { Text(group) },
                                colors = selectedChipColors(),
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }

            if (user.canSelfServiceRole()) {
                Column {
                    Text("I am a", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.size(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        roleOptions.forEach { (value, label) ->
                            FilterChip(selected = role == value, onClick = { role = value }, label = { Text(label) }, colors = selectedChipColors())
                        }
                    }
                }
            }

            Column {
                Text("Gender", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.size(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    genderOptions.forEach { (value, label) ->
                        FilterChip(selected = gender == value, onClick = { gender = value }, label = { Text(label) }, colors = selectedChipColors())
                    }
                }
            }

            OutlinedTextField(
                value = dateOfBirth,
                onValueChange = { dateOfBirth = it },
                label = { Text("Date of birth (YYYY-MM-DD)") },
                singleLine = true,
                isError = uiState.fieldErrors.containsKey("date_of_birth"),
                supportingText = uiState.fieldErrors["date_of_birth"]?.firstOrNull()?.let { m -> { Text(m) } },
                modifier = Modifier.fillMaxWidth(),
            )

            if (role == "student") {
                SimpleDropdownField(
                    label = "Hall",
                    selected = hall,
                    options = meta?.halls ?: emptyList(),
                    onSelect = { hall = it },
                    errorText = uiState.fieldErrors["hall"]?.firstOrNull(),
                )
                SimpleDropdownField(
                    label = "Batch",
                    selected = batch,
                    options = meta?.batches ?: emptyList(),
                    onSelect = { batch = it },
                    errorText = uiState.fieldErrors["batch"]?.firstOrNull(),
                )
            }
            GroupedDropdownField(
                label = "Department",
                selected = department,
                groups = meta?.departments ?: emptyMap(),
                onSelect = { department = it },
                errorText = uiState.fieldErrors["department"]?.firstOrNull(),
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone (optional)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                    .padding(16.dp),
            ) {
                Text("This number has WhatsApp", style = MaterialTheme.typography.bodyMedium)
                Switch(checked = hasWhatsapp, onCheckedChange = { hasWhatsapp = it })
            }
            if (!hasWhatsapp) {
                OutlinedTextField(
                    value = whatsappNumber,
                    onValueChange = { whatsappNumber = it },
                    label = { Text("WhatsApp number (optional)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                    .padding(16.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Hide my number from other users", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Only admins can see it. Your WhatsApp button stays hidden from everyone else too.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(checked = hidePhone, onCheckedChange = { hidePhone = it })
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                    .padding(16.dp),
            ) {
                Column {
                    Text("Available to donate", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Show me in donor search results",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(checked = isAvailable, onCheckedChange = { isAvailable = it })
            }
            OutlinedTextField(
                value = lastDonationDate,
                onValueChange = { lastDonationDate = it },
                label = { Text("Last donation date (optional, YYYY-MM-DD)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = {
                    onSave(
                        DonorProfileUpdateRequest(
                            blood_group = bloodGroup,
                            role = if (user.canSelfServiceRole()) role else null,
                            gender = gender,
                            date_of_birth = dateOfBirth,
                            department = department,
                            hall = hall.ifBlank { null },
                            batch = batch.ifBlank { null },
                            phone = phone.ifBlank { null },
                            phone_has_whatsapp = hasWhatsapp,
                            whatsapp_number = if (hasWhatsapp) null else whatsappNumber.ifBlank { null },
                            phone_visibility = if (hidePhone) "admin_only" else "public",
                            is_available = isAvailable,
                            last_donation_date = lastDonationDate.ifBlank { null },
                        ),
                    )
                },
                enabled = !uiState.isSavingDonorProfile,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (uiState.isSavingDonorProfile) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text("Save donor profile")
                }
            }
        }
    }
}

/** Self-service role changes are limited to the three donor-tier roles — same rule as User::canSelfServiceRole() on the backend. */
private fun UserDto.canSelfServiceRole(): Boolean = role in setOf("student", "staff", "faculty")

@Composable
private fun DangerZoneCard(uiState: ProfileUiState, onDelete: (String) -> Unit) {
    var password by remember { mutableStateOf("") }
    var confirming by remember { mutableStateOf(false) }

    Card {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Danger zone", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
            Text(
                "Deleting your account removes your profile, donation history, and posted requests. This can't be undone.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (!confirming) {
                OutlinedButton(onClick = { confirming = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Delete account", color = MaterialTheme.colorScheme.error)
                }
            } else {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Confirm your password") },
                    singleLine = true,
                    isError = uiState.fieldErrors.containsKey("password"),
                    supportingText = uiState.fieldErrors["password"]?.firstOrNull()?.let { m -> { Text(m) } },
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { confirming = false }, modifier = Modifier.weight(1f)) { Text("Cancel") }
                    Button(
                        onClick = { onDelete(password) },
                        enabled = !uiState.isDeleting && password.isNotBlank(),
                        modifier = Modifier.weight(1f),
                    ) {
                        if (uiState.isDeleting) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Confirm delete")
                        }
                    }
                }
            }
        }
    }
}
