package org.example.remedy.application.dropping.port.in;

import org.example.remedy.global.security.auth.AuthDetails;
import org.example.remedy.presentation.dropping.dto.request.DroppingCreateRequest;

public interface MusicDroppingService {

    void createDropping(AuthDetails authDetails, DroppingCreateRequest request);
}
