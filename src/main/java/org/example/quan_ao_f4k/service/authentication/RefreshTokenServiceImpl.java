package org.example.quan_ao_f4k.service.authentication;

import org.example.quan_ao_f4k.model.authentication.RefreshToken;
import org.example.quan_ao_f4k.model.authentication.User;
import org.example.quan_ao_f4k.repository.authentication.RefreshTokenRepository;
import org.example.quan_ao_f4k.repository.authentication.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService{
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	@Override
	public RefreshToken createRefreshToken(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found "+ username));
		RefreshToken refreshToken = refreshTokenRepository.findByUserId(user.getId());
		if (refreshToken == null) {
			long tokenExpire = 7 * 24 * 60 * 60 * 1000;
			refreshToken = RefreshToken.builder()
					.token(UUID.randomUUID().toString())
					.expireAt(LocalDateTime.ofInstant(Instant.now().plusMillis(tokenExpire), ZoneOffset.UTC))
					.user(user)
					.build();
			refreshTokenRepository.save(refreshToken);
		}
		return refreshToken;

	}

	@Override
	public RefreshToken verifiRefreshToken(String refreshToken) {
		RefreshToken refreshToken1 = refreshTokenRepository.findByToken(refreshToken)
				.orElseThrow(() -> new RuntimeException("Refresh token not found"));
		if (refreshToken1.getExpireAt().isBefore(LocalDateTime.now())) {
			refreshTokenRepository.delete(refreshToken1);
			throw new RuntimeException("Refresh token expired");
		}
		return refreshToken1;
	}

	@Override
	public void deleteRefreshToken(String refreshToken) {
		refreshTokenRepository.findByToken(refreshToken).ifPresent(refreshTokenRepository::delete);
	}

}
