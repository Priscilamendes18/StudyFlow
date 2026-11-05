package br.gov.sp.cps.studyflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.gov.sp.cps.studyflow.model.Priority
import br.gov.sp.cps.studyflow.model.Subject
import br.gov.sp.cps.studyflow.model.TaskItem
import java.text.SimpleDateFormat
import java.util.*

enum class TaskFilter {
    TODAS, PENDENTES, CONCLUIDAS
}

@Composable
fun TasksScreen(
    tasks: List<TaskItem>,
    subjects: List<Subject>,
    onAddTaskClick: () -> Unit,
    onTaskToggle: (Long, Boolean) -> Unit,
    onDeleteTask: (Long) -> Unit
) {
    var selectedFilter by remember { mutableStateOf(TaskFilter.TODAS) }
    var selectedSubjectIdFilter by remember { mutableStateOf<Long?>(null) }

    val filteredTasks = tasks.filter { task ->
        val matchesFilter = when (selectedFilter) {
            TaskFilter.TODAS -> true
            TaskFilter.PENDENTES -> !task.isCompleted
            TaskFilter.CONCLUIDAS -> task.isCompleted
        }
        val matchesSubject = selectedSubjectIdFilter == null || task.subjectId == selectedSubjectIdFilter
        matchesFilter && matchesSubject
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Título e Botão de Adicionar Tarefa
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tarefas e Trabalhos",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Acompanhe seus prazos e entregas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onAddTaskClick,
                    enabled = subjects.isNotEmpty(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Nova")
                }
            }
        }

        if (subjects.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = "Cadastre ao menos uma matéria na aba 'Matérias' antes de adicionar tarefas.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        // Filtros de Estado
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskFilter.values().forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter.name) }
                    )
                }
            }
        }

        // Lista de Tarefas
        if (filteredTasks.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Nenhuma tarefa encontrada neste filtro.", fontWeight = FontWeight.SemiBold)
                        Text(
                            "Clique no botão 'Nova' para adicionar trabalhos e exercícios.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredTasks) { task ->
                TaskCard(
                    task = task,
                    onTaskToggle = { onTaskToggle(task.id, it) },
                    onDelete = { onDeleteTask(task.id) }
                )
            }
        }
    }
}

@Composable
fun TaskCard(
    task: TaskItem,
    onTaskToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))
    val dueDateStr = sdf.format(Date(task.dueDateMillis))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = onTaskToggle
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = task.subjectName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Surface(
                    color = when (task.priority) {
                        Priority.ALTA -> MaterialTheme.colorScheme.errorContainer
                        Priority.MEDIA -> MaterialTheme.colorScheme.primaryContainer
                        Priority.BAIXA -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Prioridade: ${task.priority.name}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Excluir Tarefa",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 48.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Data Limite: $dueDateStr",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 48.dp)
            )
        }
    }
}
