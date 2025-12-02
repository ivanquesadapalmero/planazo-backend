package com.planazo.service;

import com.planazo.dto.request.ForgotPasswordRequest;
import com.planazo.dto.request.ResetPasswordRequest;

public interface PasswordResetService {

    void requestPasswordReset(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);
}
