package by.voiteshonok.valacugi.access

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import by.voiteshonok.valacugi.ui.theme.AtlasError
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val InvalidCredentialsMessage: String = "WRONG LOGIN / PASSWORD"

@Composable
fun AccessScreen(
    modifier: Modifier = Modifier,
    onContinue: suspend (AccessCredentials) -> Unit = {}
) {
    var identification: String by remember { mutableStateOf("") }
    var credential: String by remember { mutableStateOf("") }
    var isAuthenticating: Boolean by remember { mutableStateOf(false) }
    var errorMessage: String? by remember { mutableStateOf(null) }
    val coroutineScope = rememberCoroutineScope()
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "[  VALACUGI  ]",
                modifier = Modifier.statusBarsPadding(),
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    letterSpacing = (-0.5).sp,
                    color = MaterialTheme.colorScheme.primary
                ),
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
            Spacer(modifier = Modifier.weight(1.2f))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "ACCESS SYSTEM",
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 28.sp,
                        letterSpacing = (-0.5).sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "ENTER CREDENTIALS TO PROCEED",
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                AtlasField(
                    label = "IDENTIFICATION",
                    placeholder = "USER@DOMAIN.COM",
                    value = identification,
                    keyboardType = KeyboardType.Email,
                    onValueChange = { nextValue: String ->
                        identification = nextValue
                        errorMessage = null
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                AtlasField(
                    label = "CREDENTIAL",
                    placeholder = "PASSWORD",
                    value = credential,
                    keyboardType = KeyboardType.Password,
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = { nextValue: String ->
                        credential = nextValue
                        errorMessage = null
                    }
                )
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage!!,
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            letterSpacing = 0.5.sp,
                            color = AtlasError
                        )
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    enabled = !isAuthenticating,
                    onClick = {
                        if (isAuthenticating) return@Button
                        val credentials = AccessCredentials(
                            identification = identification.trim(),
                            credential = credential
                        )
                        isAuthenticating = true
                        errorMessage = null
                        coroutineScope.launch {
                            delay(900L)
                            if (!AccessCredentialsValidator.isValid(
                                    identification = credentials.identification,
                                    credential = credentials.credential
                                )
                            ) {
                                errorMessage = InvalidCredentialsMessage
                                isAuthenticating = false
                                return@launch
                            }
                            onContinue(credentials)
                            isAuthenticating = false
                        }
                    }
                ) {
                    Text(
                        text = if (isAuthenticating) "AUTHENTICATING..." else "CONTINUE  →",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            letterSpacing = 2.sp
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.weight(0.8f))
        }
    }
}

data class AccessCredentials(
    val identification: String,
    val credential: String
)

@Composable
private fun AtlasField(
    label: String,
    placeholder: String,
    value: String,
    keyboardType: KeyboardType,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                singleLine = true,
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                visualTransformation = visualTransformation,
                decorationBox = { innerTextField: @Composable () -> Unit ->
                    if (value.isBlank()) {
                        Text(
                            text = placeholder,
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        )
                    }
                    innerTextField()
                }
            )
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .align(Alignment.BottomStart)
            ) {
                drawLine(
                    color = Color.Black,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1f
                )
            }
        }
    }
}
