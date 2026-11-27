package com.nyp.scaffoldingapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nyp.scaffoldingapp.ui.theme.glassSurface
import kotlin.math.exp


@Composable
fun FilterDropdown(
	label: String,
	options: List<String>,
	selected: String,
	onSelected: (String) -> Unit,
	modifier: Modifier = Modifier
) {
	var expanded by remember {mutableStateOf(false)}

	Column(modifier = modifier){
		Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onBackground)
		Box{
			OutlinedButton(
				{expanded = true},
				Modifier.fillMaxWidth(),
				shape = RoundedCornerShape(12.dp),
				contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp))
			{
				Text(text = selected,
					Modifier.weight(1f),
					maxLines = 1,
					overflow = TextOverflow.Ellipsis
				)
				Text("V")
			}

			DropdownMenu(
				expanded = expanded,
				onDismissRequest = { expanded = false }
			)
			{
				options.forEach { option ->
					DropdownMenuItem(
						text = { Text(option) },
						onClick = {
							onSelected(option)
							expanded = false
						}
					)
				}
			}
		}
	}
}