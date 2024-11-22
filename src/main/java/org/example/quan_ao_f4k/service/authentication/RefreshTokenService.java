package org.example.quan_ao_f4k.service.authentication;

import org.example.quan_ao_f4k.model.authentication.RefreshToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public interface RefreshTokenService {
	RefreshToken createRefreshToken(String username);
	RefreshToken verifiRefreshToken(String refreshToken);
	void deleteRefreshToken(String refreshToken);
}
