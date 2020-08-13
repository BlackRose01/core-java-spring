/********************************************************************************
 * Copyright (c) 2020 FHB
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   FHB - implementation
 *   Arrowhead Consortia - conceptualization
 ********************************************************************************/

package eu.arrowhead.common.dto.shared;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Map;

public class DeviceRegistryOnboardingWithNameResponseDTO extends DeviceRegistryOnboardingResponseDTO implements Serializable {

    //=================================================================================================
    // members
    private static final long serialVersionUID = 1L;

    public DeviceRegistryOnboardingWithNameResponseDTO() {
    }

    public DeviceRegistryOnboardingWithNameResponseDTO(final long id, final DeviceResponseDTO device, final String endOfValidity,
                                                       final Map<String, String> metadata, final int version, final String createdAt,
                                                       final String updatedAt,
                                                       final CertificateCreationResponseDTO certificateResponse) {
        super(id, device, endOfValidity, metadata, version, createdAt, updatedAt, certificateResponse);
    }

    //-------------------------------------------------------------------------------------------------
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    // this class exist to keep the structure of <operation>RequestDTO, <operation>ResponseDTO
}
