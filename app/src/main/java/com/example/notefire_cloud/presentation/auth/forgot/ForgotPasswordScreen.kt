package com.example.notefire_cloud.presentation.auth.forgot

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notefire_cloud.R
import com.example.notefire_cloud.presentation.components.AuthScaffold
import com.example.notefire_cloud.presentation.components.LoadingButton
import com.example.notefire_cloud.presentation.components.NotefireTextField
import com.example.notefire_cloud.presentation.theme.NotefireTheme
import com.example.notefire_cloud.presentation.util.ObserveEffects
import com.example.notefire_cloud.presentation.util.messageRes

@Composable
fun ForgotPasswordRoute(
    onNavigateBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    ObserveEffects(viewModel.effects) { effect ->
        when (effect) {
            is ForgotPasswordEffect.ShowMessage -> snackbarHostState.showSnackbar(context.getString(effect.messageRes))
        }
    }

    ForgotPasswordScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun ForgotPasswordScreen(
    state: ForgotPasswordState,
    onIntent: (ForgotPasswordIntent) -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    AuthScaffold(
        title = stringResource(R.string.forgot_title),
        subtitle = stringResource(R.string.forgot_subtitle),
        snackbarHostState = snackbarHostState,
        onBack = onBack,
    ) {
        NotefireTextField(
            value = state.email,
            onValueChange = { onIntent(ForgotPasswordIntent.EmailChanged(it)) },
            label = R.string.email,
            leadingIcon = Icons.Outlined.Email,
            errorRes = state.emailError?.messageRes(),
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done,
            onImeAction = { onIntent(ForgotPasswordIntent.Submit) },
            autofill = ContentType.EmailAddress,
            enabled = !state.isLoading,
        )
        Spacer(Modifier.height(24.dp))
        LoadingButton(
            text = stringResource(R.string.send_reset_link),
            isLoading = state.isLoading,
            onClick = { onIntent(ForgotPasswordIntent.Submit) },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ForgotPasswordScreenPreview() {
    NotefireTheme {
        ForgotPasswordScreen(ForgotPasswordState(), onIntent = {}, onBack = {}, snackbarHostState = SnackbarHostState())
    }
}
