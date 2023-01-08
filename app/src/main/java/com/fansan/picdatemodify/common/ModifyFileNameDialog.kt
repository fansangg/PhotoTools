package com.fansan.picdatemodify.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blankj.utilcode.util.ReflectUtils
import com.blankj.utilcode.util.TimeUtils
import com.fansan.picdatemodify.ui.theme.ButtonColor
import com.fansan.picdatemodify.ui.widgets.SpacerH
import com.fansan.picdatemodify.ui.widgets.SpacerW
import com.fansan.picdatemodify.util.rememberMutableStateOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyFileNameDialog(
	confirm: (String, String, String, Boolean, Boolean) -> Unit, cancel: () -> Unit
) {
	val sampleTime = remember {
		mutableStateOf(System.currentTimeMillis())
	}
	val focusRequester = remember { FocusRequester() }
	val focusManager = LocalFocusManager.current
	val useTaken = rememberMutableStateOf(value = true)
	val prefix = rememberMutableStateOf(value = "IMG")
	val formatSelectorShow = rememberMutableStateOf(value = false)
	var prefixError by rememberMutableStateOf(value = false)
	val symbol = rememberMutableStateOf(value = "_")
	val skip = rememberMutableStateOf(value = false)
	val timeFormat = rememberMutableStateOf(value = "yyyyMMddHHmmss")
	val timeFormatValue by remember {
		derivedStateOf {
			TimeUtils.millis2String(sampleTime.value, timeFormat.value)
		}
	}
	LaunchedEffect(key1 = prefix.value, block = {
		prefixError = if (prefix.value.isNotEmpty()) !ReflectUtils.reflect("android.os.FileUtils")
			.method("isValidExtFilename", prefix.value).get<Boolean>()
		else false
	})
	var formatOffset by rememberMutableStateOf(value = 0f)
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.wrapContentHeight()
			.background(
				color = MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium
			)
			.padding(all = 12.dp), horizontalAlignment = Alignment.CenterHorizontally
	) {

		Text(text = "批量重命名照片", fontSize = 18.sp)
		SpacerH(height = 12.dp)
		Text(
			text = "示例：${if (prefix.value.isNotEmpty()) "${prefix.value}${symbol.value}" else ""}${
				TimeUtils.millis2String(
					sampleTime.value, timeFormat.value
				)
			}.png", fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis
		)
		SpacerH(height = 12.dp)
		Box {
			Row(
				modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
			) {
				OutlinedTextField(value = prefix.value,
				                  onValueChange = {
					                  prefix.value = it
				                  },
				                  isError = prefixError,
				                  singleLine = true,
				                  supportingText = {
					                  if (prefixError) {
						                  Text(text = "非法字符", fontSize = 8.sp)
					                  } else {
						                  Text(text = "前缀名", fontSize = 8.sp)
					                  }
				                  },
				                  placeholder = {
					                  Text(text = "空")
				                  },

				                  modifier = Modifier
					                  .weight(.28f)
					                  .focusRequester(focusRequester = focusRequester),
				                  colors = TextFieldDefaults.outlinedTextFieldColors(
					                  focusedBorderColor = MaterialTheme.colorScheme.outline,
					                  focusedLabelColor = MaterialTheme.colorScheme.outline,
					                  focusedSupportingTextColor = MaterialTheme.colorScheme.outline,
					                  unfocusedSupportingTextColor = MaterialTheme.colorScheme.outline,
					                  unfocusedBorderColor = MaterialTheme.colorScheme.outline,
					                  unfocusedLabelColor = MaterialTheme.colorScheme.outline,
					                  cursorColor = MaterialTheme.colorScheme.outline,
					                  selectionColors = TextSelectionColors(
						                  MaterialTheme.colorScheme.outline,
						                  MaterialTheme.colorScheme.outline
					                  )
				                  )
				)

				SpacerW(width = 12.dp)

				OutlinedTextField(value = timeFormatValue,
				                  onValueChange = {                    //timeFormatValue = it
				                  },
				                  modifier = Modifier
					                  .weight(.72f)
					                  .noRippleClick {
						                  formatSelectorShow.value = true
						                  focusManager.clearFocus()
					                  }
					                  .onGloballyPositioned {
						                  formatOffset = it.positionInParent().x
					                  },
				                  enabled = false,
				                  singleLine = true,
				                  supportingText = {
					                  Text(text = "日期格式", fontSize = 8.sp)
				                  },
				                  colors = TextFieldDefaults.outlinedTextFieldColors(
					                  disabledBorderColor = MaterialTheme.colorScheme.outline,
					                  disabledLabelColor = MaterialTheme.colorScheme.outline,
					                  disabledSupportingTextColor = MaterialTheme.colorScheme.outline,
					                  disabledTextColor = MaterialTheme.colorScheme.onSurface
				                  ),
				                  textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
				)
			}


			DateFormatDropMenu(formatSelectorShow, formatOffset, sampleTime, timeFormat, symbol)
		}


		Divider(modifier = Modifier.padding(vertical = 18.dp))

		Text(
			text = "选择重命名的日期来源",
			fontSize = 10.sp,
			color = MaterialTheme.colorScheme.outline,
			modifier = Modifier.align(alignment = Alignment.Start)
		)

		SpacerH(height = 6.dp)

		Column(
			modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)
		) {

			Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.noRippleClick {
				useTaken.value = true
				skip.value = false
			}) {
				RadioButton(
					selected = useTaken.value, onClick = {
						useTaken.value = true
						skip.value = false
					}, colors = RadioButtonDefaults.colors(
						selectedColor = ButtonColor,
						unselectedColor = MaterialTheme.colorScheme.outline
					)
				)

				Text(text = "使用原数据日期")
			}

			Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.noRippleClick {
				useTaken.value = false
				skip.value = false
			}) {
				RadioButton(
					selected = !useTaken.value, onClick = {
						useTaken.value = false
						skip.value = false
					}, colors = RadioButtonDefaults.colors(
						selectedColor = ButtonColor,
						unselectedColor = MaterialTheme.colorScheme.outline
					)
				)

				Text(text = "使用修改日期")
			}
		}

		if (useTaken.value) {
			Divider(modifier = Modifier.padding(vertical = 12.dp))
			Text(
				text = "当照片缺少元数据日期时",
				color = MaterialTheme.colorScheme.outline,
				fontSize = 10.sp,
				modifier = Modifier.align(alignment = Alignment.Start)
			)
			SpacerH(height = 6.dp)
			Column(
				modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)
			) {

				Row(
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier.noRippleClick {
						skip.value = false
					}
				) {
					RadioButton(
						selected = !skip.value, onClick = {
							skip.value = false
						}, colors = RadioButtonDefaults.colors(
							selectedColor = ButtonColor,
							unselectedColor = MaterialTheme.colorScheme.outline
						)
					)

					Text(text = "使用修改日期")

				}

				Row(
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier.noRippleClick {
						skip.value = true
					}
				) {
					RadioButton(
						selected = skip.value, onClick = {
							skip.value = true
						}, colors = RadioButtonDefaults.colors(
							selectedColor = ButtonColor,
							unselectedColor = MaterialTheme.colorScheme.outline
						)
					)

					Text(text = "跳过操作")

				}
			}
		}


		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(top = 24.dp),
			horizontalArrangement = Arrangement.Center
		) {


			CommonButton(
				content = "取消", modifier = Modifier.weight(.5f)
			) {
				cancel.invoke()
			}
			SpacerW(width = 12.dp)

			CommonButton(
				content = "确定", modifier = Modifier.weight(.5f), enable = !prefixError
			) {
				confirm.invoke(prefix.value, timeFormat.value, symbol.value, useTaken.value, skip.value)
			}
		}
	}
}

@Composable
private fun DateFormatDropMenu(
	formatSelectorShow: MutableState<Boolean>,
	formatOffset: Float,
	sampleTime: MutableState<Long>,
	timeFormat: MutableState<String>,
	symbol: MutableState<String>
) {
	DropdownMenu(
		expanded = formatSelectorShow.value, onDismissRequest = { formatSelectorShow.value = false }, offset = DpOffset(
			x = with(LocalDensity.current) { formatOffset.toDp() }, y = 0.dp
		)
	) {
		DropdownMenuItem(text = {
			Text(
				text = TimeUtils.millis2String(
					sampleTime.value, "yyyyMMddHHmmss"
				)
			)
		}, onClick = {
			timeFormat.value = "yyyyMMddHHmmss"
			symbol.value = "_"
			formatSelectorShow.value = false
		}, trailingIcon = {
			if (timeFormat.value == "yyyyMMddHHmmss") {
				Icon(
					painter = rememberVectorPainter(image = Icons.Default.Check), contentDescription = "check"
				)
			}
		})

		DropdownMenuItem(text = {
			Text(
				text = TimeUtils.millis2String(
					sampleTime.value, "yyyyMMdd_HHmmss"
				)
			)
		}, onClick = {
			timeFormat.value = "yyyyMMdd_HHmmss"
			symbol.value = "_"
			formatSelectorShow.value = false
		}, trailingIcon = {
			if (timeFormat.value == "yyyyMMdd_HHmmss") {
				Icon(
					painter = rememberVectorPainter(image = Icons.Default.Check), contentDescription = "check"
				)
			}
		})

		DropdownMenuItem(text = {
			Text(
				text = TimeUtils.millis2String(
					sampleTime.value, "yyyy_MM_dd_HH_mm_ss"
				)
			)
		}, onClick = {
			timeFormat.value = "yyyy_MM_dd_HH_mm_ss"
			symbol.value = "_"
			formatSelectorShow.value = false
		}, trailingIcon = {
			if (timeFormat.value == "yyyy_MM_dd_HH_mm_ss") {
				Icon(
					painter = rememberVectorPainter(image = Icons.Default.Check), contentDescription = "check"
				)
			}
		})

		DropdownMenuItem(text = {
			Text(
				text = TimeUtils.millis2String(
					sampleTime.value, "yyyyMMdd-HHmmss"
				)
			)
		}, onClick = {
			timeFormat.value = "yyyyMMdd-HHmmss"
			symbol.value = "-"
			formatSelectorShow.value = false
		}, trailingIcon = {
			if (timeFormat.value == "yyyyMMdd-HHmmss") {
				Icon(
					painter = rememberVectorPainter(image = Icons.Default.Check), contentDescription = "check"
				)
			}
		})

		DropdownMenuItem(text = {
			Text(
				text = TimeUtils.millis2String(
					sampleTime.value, "yyyy-MM-dd-HH-mm-ss"
				)
			)
		}, onClick = {
			timeFormat.value = "yyyy-MM-dd-HH-mm-ss"
			symbol.value = "-"
			formatSelectorShow.value = false
		}, trailingIcon = {
			if (timeFormat.value == "yyyy-MM-dd-HH-mm-ss") {
				Icon(
					painter = rememberVectorPainter(image = Icons.Default.Check), contentDescription = "check"
				)
			}
		})
	}
}