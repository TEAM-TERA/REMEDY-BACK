package org.example.remedy.domain.dropping.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
	include = JsonTypeInfo.As.PROPERTY,
	property = "type"
)
@JsonSubTypes({
	@JsonSubTypes.Type(value = MusicDroppingPayload.class, name = "music"),
	@JsonSubTypes.Type(value = PlaylistDroppingPayload.class, name = "playlist"),
	@JsonSubTypes.Type(value = VoteDroppingPayload.class, name = "vote")
})
public interface Payload {
}
