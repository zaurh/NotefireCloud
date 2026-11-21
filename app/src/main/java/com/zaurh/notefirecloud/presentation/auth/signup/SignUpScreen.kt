package com.zaurh.notefirecloud.presentation.auth.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import com.zaurh.notefirecloud.R
import com.zaurh.notefirecloud.presentation.components.AuthScaffold
import com.zaurh.notefirecloud.presentation.components.LoadingButton
import com.zaurh.notefirecloud.presentation.components.NotefireTextField
import com.zaurh.notefirecloud.presentation.theme.NotefireTheme
import com.zaurh.notefirecloud.presentation.util.ObserveEffects
import com.zaurh.notefirecloud.presentation.util.messageRes

@Composable
fun SignUpRoute(
    onNavigateBack: () -> Unit,
    viewModel: SignUpViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    ObserveEffects(viewModel.effects) { effect ->
        when (effect) {
            is SignUpEffect.ShowError -> snackbarHostState.showSnackbar(context.getString(effect.messageRes))
            SignUpEffect.NavigateBack -> onNavigateBack()
        }
    }

    SignUpScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun SignUpScreen(
    state: SignUpState,
    onIntent: (SignUpIntent) -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    AuthScaffold(
        title = stringResource(R.string.sign_up_title),
        subtitle = stringResource(R.string.sign_up_subtitle),
        snackbarHostState = snackbarHostState,
        onBack = onBack,
    ) {
        NotefireTextField(
            value = state.email,
            onValueChange = { onIntent(SignUpIntent.EmailChanged(it)) },
            label = R.string.email,
            leadingIcon = Icons.Outlined.Email,
            errorRes = state.emailError?.messageRes(),
            keyboardType = KeyboardType.Email,
            autofill = ContentType.EmailAddress + ContentType.NewUsername,
            enabled = !state.isLoading,
        )
        Spacer(Modifier.height(4.dp))
        NotefireTextField(
            value = state.password,
            onValueChange = { onIntent(SignUpIntent.PasswordChanged(it)) },
            label = R.string.password,
            leadingIcon = Icons.Outlined.Lock,
            errorRes = state.passwordError?.messageRes(),
            supportingRes = R.string.password_hint,
            isPassword = true,
            autofill = ContentType.NewPassword,
            enabled = !state.isLoading,
        )
        Spacer(Modifier.height(4.dp))
        NotefireTextField(
            value = state.confirmPassword,
            onValueChange = { onIntent(SignUpIntent.ConfirmPasswordChanged(it)) },
            label = R.string.confirm_password,
            leadingIcon = Icons.Outlined.Lock,
            errorRes = state.confirmPasswordError?.messageRes(),
            isPassword = true,
            imeAction = ImeAction.Done,
            onImeAction = { onIntent(SignUpIntent.Submit) },
            autofill = ContentType.NewPassword,
            enabled = !state.isLoading,
        )
        Spacer(Modifier.height(24.dp))
        LoadingButton(
            text = stringResource(R.string.sign_up),
            isLoading = state.isLoading,
            onClick = { onIntent(SignUpIntent.Submit) },
        )
        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                stringResource(R.string.have_account),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            TextButton(onClick = { onIntent(SignUpIntent.SignInClicked) }) {
                Text(stringResource(R.string.sign_in_link))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignUpScreenPreview() {
    NotefireTheme {
        SignUpScreen(state = SignUpState(), onIntent = {}, onBack = {}, snackbarHostState = SnackbarHostState())
    }
}
