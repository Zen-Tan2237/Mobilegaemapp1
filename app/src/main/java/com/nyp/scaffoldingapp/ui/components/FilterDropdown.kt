package com.nyp.scaffoldingapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nyp.scaffoldingapp.ui.theme.glassSurface
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.rotate
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.ui.draw.blur


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
		Text(text = label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onBackground, letterSpacing = (-0.8).sp)
		Box(){
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.height(46.dp)
					.glassSurface(cornerRadius = 14)
					.clickable { expanded = !expanded }
					.padding(horizontal = 12.dp),

				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = selected,
					modifier = Modifier.weight(1f),
					style = MaterialTheme.typography.bodyMedium,
					fontWeight = FontWeight.Medium,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					letterSpacing = (-0.8).sp
				)

				Icon(
					imageVector = Icons.Default.KeyboardArrowDown,
					contentDescription = "Open dropdown",
					modifier = Modifier
						.size(20.dp)
						.rotate(if (expanded) 180f else 0f),
					tint = MaterialTheme.colorScheme.onSurfaceVariant
				)
			}
			BoxWithConstraints(
				modifier = Modifier.fillMaxWidth()
			) {
				val menuWidth = maxWidth

				// Keep your existing OutlinedButton here

				DropdownMenu(
					expanded = expanded,
					onDismissRequest = { expanded = false },

					modifier = Modifier
						.width(menuWidth)
						.glassSurface(cornerRadius = 20),

					shape = RoundedCornerShape(20.dp),

					containerColor = MaterialTheme.colorScheme.surface
						.copy(alpha = 0.85f),

					tonalElevation = 0.dp,
					shadowElevation = 0.dp
				) {
					options.forEach { option ->

						val isSelected = option == selected

						DropdownMenuItem(
							text = {
								Text(
									text = option,
									fontWeight = if (isSelected)
										FontWeight.SemiBold
									else
										FontWeight.Normal,

									color = if (isSelected)
										MaterialTheme.colorScheme.primary
									else
										MaterialTheme.colorScheme.onSurface
									,
									letterSpacing = (-0.8).sp
								)
							},
							trailingIcon = {
								if (isSelected) {
									Text(
										text = "✓",
										color = MaterialTheme.colorScheme.primary
									)
								}
							},
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
}