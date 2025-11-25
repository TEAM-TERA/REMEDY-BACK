package org.example.remedy.domain.oauth2.application.mapper;

import org.example.remedy.domain.user.domain.User;
import org.example.remedy.domain.oauth2.domain.OAuth2UserInfo;

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
