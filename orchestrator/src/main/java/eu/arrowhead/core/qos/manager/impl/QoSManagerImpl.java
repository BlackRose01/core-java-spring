package eu.arrowhead.core.qos.manager.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.database.entity.QoSReservation;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;
import eu.arrowhead.common.dto.shared.SystemRequestDTO;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.core.qos.database.service.QoSReservationDBService;
import eu.arrowhead.core.qos.manager.QoSManager;

public class QoSManagerImpl implements QoSManager {
	
	//=================================================================================================
	// members

	private static final Logger logger = LogManager.getLogger(QoSManagerImpl.class);

	@Autowired
	private QoSReservationDBService qosReservationDBService;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public List<OrchestrationResultDTO> filterReservedProviders(final List<OrchestrationResultDTO> orList, final SystemRequestDTO requester) {
		Assert.notNull(orList, "'orList' is null.");
		Assert.notNull(requester, "Requester system is null.");
		Assert.isTrue(!Utilities.isEmpty(requester.getSystemName()),  "Requester system's name is null or empty.");
		Assert.isTrue(!Utilities.isEmpty(requester.getAddress()),  "Requester system's address is null or empty.");
		Assert.notNull(requester.getPort(), "Requester system's port is null.");
		
		if (orList.isEmpty()) {
			return orList;
		}
		
		final List<QoSReservation> reservations = qosReservationDBService.getAllReservationsExceptMine(requester.getSystemName(), requester.getAddress(), requester.getPort());
		final List<OrchestrationResultDTO> result = new ArrayList<>();
		for (final OrchestrationResultDTO dto : orList) {
			if (!isReserved(dto, reservations)) {
				result.add(dto);
			}
		}
		
		return result;
	}
	
	//-------------------------------------------------------------------------------------------------
	@Override
	public List<OrchestrationResultDTO> reserveProvidersTemporarily(List<OrchestrationResultDTO> orList, final SystemRequestDTO requester) {
		Assert.notNull(orList, "'orList' is null.");
		Assert.notNull(requester, "'requester' is null.");
		Assert.isTrue(!Utilities.isEmpty(requester.getSystemName()),  "Requester system's name is null or empty.");
		Assert.isTrue(!Utilities.isEmpty(requester.getAddress()),  "Requester system's address is null or empty.");
		Assert.notNull(requester.getPort(), "Requester system's port is null.");
		
		if (orList.isEmpty()) {
			return orList;
		}
		
		final List<OrchestrationResultDTO> result = new ArrayList<OrchestrationResultDTO>(orList.size());
		for (final OrchestrationResultDTO dto : orList) {
			try {
				// try to lock
				qosReservationDBService.applyTemporaryLock(requester.getSystemName(), requester.getAddress(), requester.getPort(), dto);
				result.add(dto);
			} catch (final ArrowheadException ex) {
				// should means locking is failed because somebody already reserved that service => logging and removing
				logger.debug("{}/{} may be already locked by someone else", dto.getProvider().getSystemName(), dto.getService().getServiceDefinition());
			}
		}
		
		return result;
	}
	
	//-------------------------------------------------------------------------------------------------
	@Override
	public void confirmReservation(OrchestrationResultDTO selected, List<OrchestrationResultDTO> orList, final SystemRequestDTO requester) {
		Assert.notNull(selected, "'selected' is null.");
		Assert.notEmpty(orList, "'orList' is null or empty.");
		Assert.notNull(requester, "'requester' is null.");
		Assert.isTrue(!Utilities.isEmpty(requester.getSystemName()),  "Requester system's name is null or empty.");
		Assert.isTrue(!Utilities.isEmpty(requester.getAddress()),  "Requester system's address is null or empty.");
		Assert.notNull(requester.getPort(), "Requester system's port is null.");
		
		for (final OrchestrationResultDTO dto : orList) {
			if (dto.getProvider().getId() == selected.getProvider().getId() && dto.getService().getId() == selected.getProvider().getId()) {
				qosReservationDBService.extendReservation(selected, requester);
			} else {
				qosReservationDBService.removeTemporaryLock(dto);
			}
		}
	}
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	private boolean isReserved(final OrchestrationResultDTO dto, final List<QoSReservation> reservations) {
		for (final QoSReservation reservation : reservations) {
			if (reservation.getReservedProviderId() == dto.getProvider().getId() && reservation.getReservedServiceId() == dto.getService().getId()) {
				return true;
			}
		}
		
		return false;
	}
}