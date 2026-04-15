package com.textseries.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Component
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	public JwtFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			String token = authHeader.substring(7);

			try {
				if (jwtUtil.validateToken(token)) {

					String email = jwtUtil.extractEmail(token);
					String role = jwtUtil.extractRole(token);

					if (SecurityContextHolder.getContext().getAuthentication() == null) {

						UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, // ✅
																													// ab
																													// clear
																													// hai
								null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));

						SecurityContextHolder.getContext().setAuthentication(auth);
					}
				}

			} catch (Exception e) {
				// ✅ ignore token issues
			}

		}

		filterChain.doFilter(request, response); // ✅ single exit point
	}
}