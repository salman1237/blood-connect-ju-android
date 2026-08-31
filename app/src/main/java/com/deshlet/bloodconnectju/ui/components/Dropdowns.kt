package com.deshlet.bloodconnectju.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

// Plain Box + DropdownMenu rather than ExposedDropdownMenuBox — its
// menuAnchor() signature has changed across recent Compose Material3
// versions, and this stays correct regardless of which one resolves.

@Composable
fun SimpleDropdownField(
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    errorText: String? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { Text("▾") },
            isError = errorText != null,
            supportingText = errorText?.let { message -> { Text(message) } },
            modifier = Modifier.fillMaxWidth(),
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { expanded = true },
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { onSelect(option); expanded = false })
            }
        }
    }
}

/**
 * A compact filter trigger for a long, flat option list (halls — 21 of
 * them) that a horizontally-scrolling chip row (fine for the 8-item blood
 * group filter) wouldn't fit reasonably. Mirrors the `hall` query param
 * both `/requests` and `/donors` already accept server-side — that filter
 * was fully wired through ApiService/Repository on this client already,
 * just never surfaced as a control on either screen.
 */
@Composable
fun HallFilterChip(
    halls: List<String>,
    selected: String?,
    onSelect: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        FilterChip(
            selected = selected != null,
            onClick = { expanded = true },
            label = {
                Text(selected ?: "Hall", maxLines = 1, overflow = TextOverflow.Ellipsis)
            },
            trailingIcon = { Text("▾") },
            colors = selectedChipColors(),
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("All halls") }, onClick = { onSelect(null); expanded = false })
            halls.forEach { hall ->
                DropdownMenuItem(text = { Text(hall) }, onClick = { onSelect(hall); expanded = false })
            }
        }
    }
}

@Composable
fun GroupedDropdownField(
    label: String,
    selected: String,
    groups: Map<String, List<String>>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    errorText: String? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { Text("▾") },
            isError = errorText != null,
            supportingText = errorText?.let { message -> { Text(message) } },
            modifier = Modifier.fillMaxWidth(),
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { expanded = true },
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            groups.forEach { (faculty, names) ->
                Text(
                    faculty,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                )
                names.forEach { name ->
                    DropdownMenuItem(text = { Text(name) }, onClick = { onSelect(name); expanded = false })
                }
            }
        }
    }
}
