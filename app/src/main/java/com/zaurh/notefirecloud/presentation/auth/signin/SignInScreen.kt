package com.zaurh.notefirecloud.presentation.auth.signin

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
fun SignInRoute(
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgotPassword: (email: String) -> Unit,
    viewModel: SignInViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    ObserveEffects(viewModel.effects) { effect ->
        when (effect) {
            is SignInEffect.ShowError -> snackbarHostState.showSnackbar(context.getString(effect.messageRes))
            is SignInEffect.NavigateToForgotPassword -> onNavigateToForgotPassword(effect.email)
            SignInEffect.NavigateToSignUp -> onNavigateToSignUp()
        }
    }

    SignInScreen(state = state, onIntent = viewModel::onIntent, snackbarHostState = snackbarHostState)
}

@Composable
fun SignInScreen(
    state: SignInState,
    onIntent: (SignInIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    AuthScaffold(
        title = stringResource(R.string.sign_in_title),
        subtitle = stringResource(R.string.sign_in_subtitle),
        snackbarHostState = snackbarHostState,
    ) {
        NotefireTextField(
            value = state.email,
            onValueChange = { onIntent(SignInIntent.EmailChanged(it)) },
            label = R.string.email,
            leadingIcon = Icons.Outlined.Email,
            errorRes = state.emailError?.messageRes(),
            keyboardType = KeyboardType.Email,
            autofill = ContentType.EmailAddress + ContentType.Username,
            enabled = !state.isLoading,
        )
        Spacer(Modifier.height(4.dp))
        NotefireTextField(
            value = state.password,
            onValueChange = { onIntent(SignInIntent.PasswordChanged(it)) },
            label = R.string.password,
            leadingIcon = Icons.Outlined.Lock,
            errorRes = state.passwordError?.messageRes(),
            isPassword = true,
            imeAction = ImeAction.Done,
            onImeAction = { onIntent(SignInIntent.Submit) },
            autofill = ContentType.Password,
            enabled = !state.isLoading,
        )
        TextButton(
            onClick = { onIntent(SignInIntent.ForgotPasswordClicked) },
            modifier = Modifier.align(Alignment.End),
        ) {
            Text(stringResource(R.string.forgot_password_link))
        }
        Spacer(Modifier.height(16.dp))
        LoadingButton(
            text = stringResource(R.string.sign_in),
            isLoading = state.isLoading,
            onClick = { onIntent(SignInIntent.Submit) },
        )
        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                stringResource(R.string.no_account),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            TextButton(onClick = { onIntent(SignInIntent.SignUpClicked) }) {
                Text(stringResource(R.string.create_account_link))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignInScreenPreview() {
    NotefireTheme {
        SignInScreen(state = SignInState(), onIntent = {}, snackbarHostState = SnackbarHostState())
    }
}
