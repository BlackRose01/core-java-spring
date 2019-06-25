package eu.arrowhead.common.database.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import eu.arrowhead.common.database.entity.ServiceDefinition;

@Repository
public interface ServiceDefinitionRepository extends RefreshableRepository<ServiceDefinition, Long> {
	
	Optional<ServiceDefinition> findByServiceDefinition(final String serviceDefinition);	
	
}