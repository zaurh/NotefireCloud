package com.zaurh.notefirecloud

import com.zaurh.notefirecloud.domain.validation.AuthValidator
import com.zaurh.notefirecloud.domain.validation.EmailError
import com.zaurh.notefirecloud.domain.validation.PasswordError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthValidatorTest {

    @Test fun `valid email passes`() = assertNull(AuthValidator.validateEmail(" user.name+tag@mail.co "))
    @Test fun `blank email is empty`() = assertEquals(EmailError.Empty, AuthValidator.validateEmail("  "))
    @Test fun `malformed email is invalid`() = assertEquals(EmailError.Invalid, AuthValidator.validateEmail("user@mail"))

    @Test fun `short new password is rejected`() =
        assertEquals(PasswordError.TooShort, AuthValidator.validateNewPassword("1234567"))

    @Test fun `8 character password is accepted`() = assertNull(AuthValidator.validateNewPassword("12345678"))

    @Test fun `existing password only needs to be non-empty`() = assertNull(AuthValidator.validateExistingPassword("1"))

    @Test fun `mismatched confirmation is rejected`() =
        assertEquals(PasswordError.Mismatch, AuthValidator.validateConfirmation("password1", "password2"))
}
