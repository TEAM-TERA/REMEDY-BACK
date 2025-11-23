package org.example.remedy.infrastructure.oauth2;

import org.example.remedy.domain.user.domain.User;

public class Oauth2Mapper {
	public static User toUserEntity(OAuth2UserInfo oAuth2UserInfo) {
		return new User(
			oAuth2UserInfo.getName(),
			oAuth2UserInfo.getEmail(),
			oAuth2UserInfo.getProfileImage(),
			oAuth2UserInfo.getBirthDate(),
			oAuth2UserInfo.getGender(),
			oAuth2UserInfo.getProvider(),
			oAuth2UserInfo.getProviderId()
		);
	}
}
