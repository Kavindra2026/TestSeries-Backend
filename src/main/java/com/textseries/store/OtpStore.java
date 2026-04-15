package com.textseries.store;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class OtpStore {

	private static final Map<String, OtpData> otpMap = new ConcurrentHashMap<>();

	// OTP save with expiry (5 minutes)
	public static void save(String email, String otp) {
		otpMap.put(email, new OtpData(otp, LocalDateTime.now().plusMinutes(5)));
	}

	public static boolean verify(String email, String otp) {

		System.out.println("OTP VERIFY REQUEST: " + email);

		OtpData data = otpMap.get(email);

		if (data == null) {
			System.out.println("❌ No OTP found for email");
			return false;
		}

		if (LocalDateTime.now().isAfter(data.expiry)) {
			System.out.println("❌ OTP expired");
			otpMap.remove(email);
			return false;
		}

		if (data.otp.equals(otp.trim())) {
			System.out.println("✅ OTP verified successfully");
			otpMap.remove(email);
			return true;
		}

		System.out.println("❌ Invalid OTP entered");
		return false;
	}

	// inner class
	static class OtpData {
		String otp;
		LocalDateTime expiry;

		OtpData(String otp, LocalDateTime expiry) {
			this.otp = otp;
			this.expiry = expiry;
		}
	}
}