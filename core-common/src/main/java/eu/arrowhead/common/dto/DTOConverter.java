package eu.arrowhead.common.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.util.Assert;

import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.database.entity.ServiceDefinition;
import eu.arrowhead.common.database.entity.ServiceInterface;
import eu.arrowhead.common.database.entity.ServiceRegistry;
import eu.arrowhead.common.database.entity.ServiceRegistryInterfaceConnection;
import eu.arrowhead.common.database.entity.System;

public class DTOConverter {
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public static SystemResponseDTO convertSystemToSystemResponseDTO(final System system) {
		Assert.notNull(system, "System is null");
		
		return new SystemResponseDTO(system.getId(),
				system.getSystemName(),
				system.getAddress(),
				system.getPort(),
				system.getAuthenticationInfo(),
				Utilities.convertZonedDateTimeToUTCString(system.getCreatedAt()),
				Utilities.convertZonedDateTimeToUTCString(system.getUpdatedAt()));		
	}
	
	//-------------------------------------------------------------------------------------------------
	public static SystemListResponseDTO convertSystemEntryListToSystemListResponseDTO(final Page<System> systemEntryList) {
		Assert.notNull(systemEntryList, "systemEntryList is null");
		
		final long count = systemEntryList.getTotalElements();		
		final SystemListResponseDTO systemListResponseDTO = new SystemListResponseDTO();
		systemListResponseDTO.setCount(count);
		systemListResponseDTO.setData(systemEntryListToSystemResponeDTOList(systemEntryList.getContent()));
		
		return systemListResponseDTO;
		
	}
		
	//-------------------------------------------------------------------------------------------------
	public static ServiceDefinitionResponseDTO convertServiceDefinitionToServiceDefinitionResponseDTO (final ServiceDefinition serviceDefinition) {
		Assert.notNull(serviceDefinition, "ServiceDefinition is null");
		
		return new ServiceDefinitionResponseDTO(serviceDefinition.getId(), serviceDefinition.getServiceDefinition(), Utilities.convertZonedDateTimeToUTCString(serviceDefinition.getCreatedAt()),
												Utilities.convertZonedDateTimeToUTCString(serviceDefinition.getUpdatedAt()));
	}
	
	//-------------------------------------------------------------------------------------------------
	public static ServiceDefinitionsListResponseDTO convertServiceDefinitionsListToServiceDefinitionListResponseDTO(final Page<ServiceDefinition> serviceDefinitions) {
		Assert.notNull(serviceDefinitions, "List of ServiceDefinition is null");
		
		final List<ServiceDefinitionResponseDTO> serviceDefinitionDTOs = new ArrayList<>(serviceDefinitions.getNumberOfElements());
		for (final ServiceDefinition definition : serviceDefinitions) {
			serviceDefinitionDTOs.add(convertServiceDefinitionToServiceDefinitionResponseDTO(definition));
		}		
		
		return new ServiceDefinitionsListResponseDTO(serviceDefinitionDTOs, serviceDefinitions.getTotalElements());
	}
	
	//-------------------------------------------------------------------------------------------------
	public static ServiceRegistryResponseDTO convertServiceRegistryToServiceRegistryResponseDTO(final ServiceRegistry entry) {
		Assert.notNull(entry, "SR entry is null.");
		Assert.notNull(entry.getServiceDefinition(), "Related service definition is null.");
		Assert.notNull(entry.getSystem(), "Related system is null.");
		Assert.notNull(entry.getInterfaceConnections(), "Related interface connection set is null.");
		Assert.isTrue(!entry.getInterfaceConnections().isEmpty(), "Related interface connection set is empty.");
		
		final ServiceDefinitionResponseDTO serviceDefinitionDTO = convertServiceDefinitionToServiceDefinitionResponseDTO(entry.getServiceDefinition());
		final SystemResponseDTO systemDTO = convertSystemToSystemResponseDTO(entry.getSystem());
		
		final ServiceRegistryResponseDTO dto = new ServiceRegistryResponseDTO();
		dto.setId(entry.getId());
		dto.setServiceDefinition(serviceDefinitionDTO);
		dto.setProvider(systemDTO);
		dto.setServiceUri(entry.getServiceUri());
		dto.setEndOfValidity(Utilities.convertZonedDateTimeToUTCString(entry.getEndOfValidity()));
		dto.setSecure(entry.getSecure());
		dto.setMetadata(Utilities.text2Map(entry.getMetadata()));
		dto.setVersion(entry.getVersion());
		dto.setInterfaces(collectInterfaces(entry.getInterfaceConnections()));
		dto.setCreatedAt(Utilities.convertZonedDateTimeToUTCString(entry.getCreatedAt()));
		dto.setUpdatedAt(Utilities.convertZonedDateTimeToUTCString(entry.getUpdatedAt()));
		
		return dto;
	}
	
	//-------------------------------------------------------------------------------------------------
	public static ServiceRegistryListResponseDTO convertServiceRegistryListToServiceRegistryListResponseDTO(final Page<ServiceRegistry> serviceRegistryEntries) {
		Assert.notNull(serviceRegistryEntries, "List of serviceRegistryEntries is null");
		
		final List<ServiceRegistryResponseDTO> serviceRegistryEntryDTOs = new ArrayList<>(serviceRegistryEntries.getNumberOfElements());
		for (final ServiceRegistry srEntry: serviceRegistryEntries) {
			serviceRegistryEntryDTOs.add(convertServiceRegistryToServiceRegistryResponseDTO(srEntry));
		}
		
		return new ServiceRegistryListResponseDTO(serviceRegistryEntryDTOs, serviceRegistryEntries.getTotalElements());
	}
	
	//-------------------------------------------------------------------------------------------------
	public static ServiceRegistryGroupedResponseDTO convertServiceRegistryEntriesToServiceRegistryGroupedResponseDTO(final Page<ServiceRegistry> serviceRegistryEntries) {
		Assert.notNull(serviceRegistryEntries, "List of serviceRegistryEntries is null");
		
		final Map<Long, ServicesGroupedBySystemsResponseDTO> servicesBySystemId = new HashMap<>();
		final Map<String, ServicesGroupedByServiceDefinitionAndInterfaceResponseDTO> servicesByServiceDefinitionAndInterface = new HashMap<>();
		final List<IdValueDTO> servicesForAutoComplete = new ArrayList<>();
		final List<SystemResponseDTO> systemsForAutoComplete = new ArrayList<>();
		final List<IdValueDTO> interfacesForAutoComplete = new ArrayList<>();		
		final Set<Long> serviceIdsForAutoComplete = new HashSet<>();
		final Set<Long> systemIdsForAutoComplete = new HashSet<>();
		final Set<Long> interfaceIdsForAutoComplete = new HashSet<>();
						
		for (final ServiceRegistry srEntry: serviceRegistryEntries) {
			final long systemId = srEntry.getSystem().getId();
			final String systemName = srEntry.getSystem().getSystemName();
			final String systemAddress = srEntry.getSystem().getAddress();
			final int systemPort = srEntry.getSystem().getPort();
			final long serviceDefinitionId = srEntry.getServiceDefinition().getId();
			final String serviceDefinition = srEntry.getServiceDefinition().getServiceDefinition();		
			
			// Creating ServicesGroupedBySystemsResponseDTO
			if (servicesBySystemId.containsKey(systemId)) {
				servicesBySystemId.get(systemId).getServices().add(convertServiceRegistryToServiceRegistryResponseDTO(srEntry));
			} else {
				final ServicesGroupedBySystemsResponseDTO dto = new ServicesGroupedBySystemsResponseDTO(systemId, systemName, systemAddress, systemPort, new ArrayList<>());
				dto.getServices().add(convertServiceRegistryToServiceRegistryResponseDTO(srEntry));
				servicesBySystemId.put(systemId, dto);
			}
			
			// Filling up AutoCompleteDataResponseDTO						
			if (!serviceIdsForAutoComplete.contains(serviceDefinitionId)) {
				serviceIdsForAutoComplete.add(serviceDefinitionId);
				servicesForAutoComplete.add(new IdValueDTO(serviceDefinitionId, serviceDefinition));
			}
			if (!systemIdsForAutoComplete.contains(systemId)) {
				systemIdsForAutoComplete.add(systemId);
				systemsForAutoComplete.add(new SystemResponseDTO(systemId, systemName, systemAddress, systemPort, null, null, null));
			}
						
			final Set<ServiceRegistryInterfaceConnection> interfaceConnections = srEntry.getInterfaceConnections();
			for (final ServiceRegistryInterfaceConnection connection : interfaceConnections) {
				final long interfId = connection.getServiceInterface().getId();
				final String interfaceName = connection.getServiceInterface().getInterfaceName();
				
				if (!interfaceIdsForAutoComplete.contains(interfId)) {
					interfaceIdsForAutoComplete.add(interfId);
					interfacesForAutoComplete.add(new IdValueDTO(interfId, interfaceName));
				}
				
				// Creating ServicesGroupedByServiceDefinitionAndInterfaceResponseDTO
				final String key = serviceDefinitionId + "-" + interfId;
				if (servicesByServiceDefinitionAndInterface.containsKey(key)) {
					servicesByServiceDefinitionAndInterface.get(key).getProviderServices().add(convertServiceRegistryToServiceRegistryResponseDTO(srEntry));
				} else {
					final ServicesGroupedByServiceDefinitionAndInterfaceResponseDTO dto = new ServicesGroupedByServiceDefinitionAndInterfaceResponseDTO(serviceDefinitionId, serviceDefinition, interfaceName,  new ArrayList<>());
					dto.getProviderServices().add(convertServiceRegistryToServiceRegistryResponseDTO(srEntry));
					servicesByServiceDefinitionAndInterface.put(key, dto);
				}
			}
		}
		
		final AutoCompleteDataResponseDTO autoCompleteDataResponseDTO = new AutoCompleteDataResponseDTO(servicesForAutoComplete, systemsForAutoComplete, interfacesForAutoComplete);
		final List<ServicesGroupedBySystemsResponseDTO> servicesGroupedBySystemsResponseDTOList = new ArrayList<>();
		servicesGroupedBySystemsResponseDTOList.addAll(servicesBySystemId.values());
		final List<ServicesGroupedByServiceDefinitionAndInterfaceResponseDTO> servicesGroupedByServiceDefinitionAndInterfaceResponseDTOList = new ArrayList<>();
		servicesGroupedByServiceDefinitionAndInterfaceResponseDTOList.addAll(servicesByServiceDefinitionAndInterface.values());
		
		return new ServiceRegistryGroupedResponseDTO(servicesGroupedBySystemsResponseDTOList, servicesGroupedByServiceDefinitionAndInterfaceResponseDTOList, autoCompleteDataResponseDTO);
	}
	
	//-------------------------------------------------------------------------------------------------
	public static ServiceInterfaceResponseDTO convertServiceInterfaceToServiceInterfaceResponseDTO(final ServiceInterface intf) {
		Assert.notNull(intf, "Interface entry is null.");
		
		return new ServiceInterfaceResponseDTO(intf.getId(), intf.getInterfaceName(), Utilities.convertZonedDateTimeToUTCString(intf.getCreatedAt()), 
											   Utilities.convertZonedDateTimeToUTCString(intf.getUpdatedAt()));
	}
	
	//-------------------------------------------------------------------------------------------------
	public static ServiceQueryResultDTO convertListOfServiceRegistryEntriesToServiceQueryResultDTO(final List<ServiceRegistry> entries, final int unfilteredHits) {
		final ServiceQueryResultDTO result = new ServiceQueryResultDTO();
		
		if (entries != null) {
			Assert.isTrue(unfilteredHits >= entries.size(), "Invalid value of unfiltered hits:" + unfilteredHits);
			result.setUnfilteredHits(unfilteredHits);
			for (final ServiceRegistry srEntry : entries) {
				result.getServiceQueryData().add(convertServiceRegistryToServiceRegistryResponseDTO(srEntry));
			}
		}
		
		return result;
	}
	
	//=================================================================================================
	// assistant methods
	
	//-------------------------------------------------------------------------------------------------
	private DTOConverter() {
		throw new UnsupportedOperationException();
	}
	
	//-------------------------------------------------------------------------------------------------
	private static List<SystemResponseDTO> systemEntryListToSystemResponeDTOList(final List<System> systemList) {
		final List<SystemResponseDTO> systemResponseDTOs = new ArrayList<>(systemList.size());
		
		for (final System system : systemList) {
			systemResponseDTOs.add(convertSystemToSystemResponseDTO(system));
		}
		
		return systemResponseDTOs;
	}
	
	//-------------------------------------------------------------------------------------------------
	private static List<ServiceInterfaceResponseDTO> collectInterfaces(final Set<ServiceRegistryInterfaceConnection> interfaceConnections) {
		final List<ServiceInterfaceResponseDTO> result = new ArrayList<>(interfaceConnections.size());
		for (final ServiceRegistryInterfaceConnection conn : interfaceConnections) {
			result.add(convertServiceInterfaceToServiceInterfaceResponseDTO(conn.getServiceInterface()));
		}
		result.sort((dto1, dto2) -> dto1.getInterfaceName().compareToIgnoreCase(dto2.getInterfaceName()));
		
		return result;
	}
}