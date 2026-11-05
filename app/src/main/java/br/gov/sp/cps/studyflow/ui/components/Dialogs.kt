package br.gov.sp.cps.studyflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import br.gov.sp.cps.studyflow.model.Priority
import br.gov.sp.cps.studyflow.model.Subject
import java.text.SimpleDateFormat
import java.util.*

val AVAILABLE_COLORS = listOf(
    "#3F51B5", "#2196F3", "#00BCD4", "#009688",
    "#4CAF50", "#8BC34A", "#FF9800", "#FF5722",
    "#E91E63", "#9C27B0", "#673AB7", "#607D8B"
)

fun parseHexColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        Color(0xFF3F51B5)
    }
}

@Composable
fun AddSubjectDialog(
    initialSubject: Subject? = null,
    onDismiss: () -> Unit,
    onSave: (name: String, code: String, professor: String, colorHex: String) -> Unit
) {
    var name by remember { mutableStateOf(initialSubject?.name ?: "") }
    var code by remember { mutableStateOf(initialSubject?.code ?: "") }
    var professor by remember { mutableStateOf(initialSubject?.professor ?: "") }
    var selectedColor by remember { mutableStateOf(initialSubject?.colorHex ?: AVAILABLE_COLORS[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialSubject == null) "Nova Matéria" else "Editar Matéria") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome da Matéria*") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Código/Sigla (ex: MAT101)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = professor,
                    onValueChange = { professor = it },
                    label = { Text("Nome do Professor") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Cor da Matéria", style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AVAILABLE_COLORS.take(6).forEach { colorHex ->
                        ColorBadge(
                            colorHex = colorHex,
                            isSelected = selectedColor == colorHex,
                            onClick = { selectedColor = colorHex }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AVAILABLE_COLORS.drop(6).take(6).forEach { colorHex ->
                        ColorBadge(
                            colorHex = colorHex,
                            isSelected = selectedColor == colorHex,
                            onClick = { selectedColor = colorHex }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name.trim(), code.trim(), professor.trim(), selectedColor)
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun ColorBadge(colorHex: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(parseHexColor(colorHex))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    subjects: List<Subject>,
    onDismiss: () -> Unit,
    onSave: (subjectId: Long, title: String, description: String, dueDateMillis: Long, priority: Priority, scheduleNotification: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedSubjectId by remember { mutableStateOf(subjects.firstOrNull()?.id ?: 0L) }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIA) }
    var scheduleNotification by remember { mutableStateOf(true) }

    var expandedSubjectDropdown by remember { mutableStateOf(false) }

    val calendar = remember { Calendar.getInstance() }
    var dateString by remember {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
        mutableStateOf(sdf.format(calendar.time))
    }
    var dueDateMillis by remember { mutableLongStateOf(calendar.timeInMillis) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova Tarefa / Trabalho") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título da Tarefa*") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Dropdown de Matérias
                ExposedDropdownMenuBox(
                    expanded = expandedSubjectDropdown,
                    onExpandedChange = { expandedSubjectDropdown = !expandedSubjectDropdown }
                ) {
                    val currentSubjectName = subjects.find { it.id == selectedSubjectId }?.name ?: "Selecione a Matéria"
                    OutlinedTextField(
                        value = currentSubjectName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Matéria*") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSubjectDropdown) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedSubjectDropdown,
                        onDismissRequest = { expandedSubjectDropdown = false }
                    ) {
                        subjects.forEach { subject ->
                            DropdownMenuItem(
                                text = { Text(subject.name) },
                                onClick = {
                                    selectedSubjectId = subject.id
                                    expandedSubjectDropdown = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                // Prioridade
                Text("Prioridade", style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Priority.values().forEach { priority ->
                        FilterChip(
                            selected = selectedPriority == priority,
                            onClick = { selectedPriority = priority },
                            label = { Text(priority.name) }
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = scheduleNotification,
                        onCheckedChange = { scheduleNotification = it }
                    )
                    Text("Agendar notificação de lembrete", style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && selectedSubjectId > 0) {
                        onSave(selectedSubjectId, title.trim(), description.trim(), dueDateMillis, selectedPriority, scheduleNotification)
                    }
                },
                enabled = title.isNotBlank() && selectedSubjectId > 0
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExamDialog(
    subjects: List<Subject>,
    onDismiss: () -> Unit,
    onSave: (subjectId: Long, title: String, location: String, notes: String, examDateMillis: Long, scheduleNotification: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedSubjectId by remember { mutableStateOf(subjects.firstOrNull()?.id ?: 0L) }
    var scheduleNotification by remember { mutableStateOf(true) }

    var expandedSubjectDropdown by remember { mutableStateOf(false) }

    val calendar = remember { Calendar.getInstance() }
    var examDateMillis by remember { mutableLongStateOf(calendar.timeInMillis) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova Prova / Avaliação") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título da Prova (ex: P1, P2)*") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Dropdown de Matérias
                ExposedDropdownMenuBox(
                    expanded = expandedSubjectDropdown,
                    onExpandedChange = { expandedSubjectDropdown = !expandedSubjectDropdown }
                ) {
                    val currentSubjectName = subjects.find { it.id == selectedSubjectId }?.name ?: "Selecione a Matéria"
                    OutlinedTextField(
                        value = currentSubjectName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Matéria*") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSubjectDropdown) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedSubjectDropdown,
                        onDismissRequest = { expandedSubjectDropdown = false }
                    ) {
                        subjects.forEach { subject ->
                            DropdownMenuItem(
                                text = { Text(subject.name) },
                                onClick = {
                                    selectedSubjectId = subject.id
                                    expandedSubjectDropdown = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Local / Sala (ex: Lab 04)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Observações / Conteúdo cobrado") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = scheduleNotification,
                        onCheckedChange = { scheduleNotification = it }
                    )
                    Text("Agendar notificação de lembrete", style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && selectedSubjectId > 0) {
                        onSave(selectedSubjectId, title.trim(), location.trim(), notes.trim(), examDateMillis, scheduleNotification)
                    }
                },
                enabled = title.isNotBlank() && selectedSubjectId > 0
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
