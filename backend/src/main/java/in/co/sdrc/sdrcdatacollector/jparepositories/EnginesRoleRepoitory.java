package in.co.sdrc.sdrcdatacollector.jparepositories;

import org.springframework.data.jpa.repository.JpaRepository;

import in.co.sdrc.sdrcdatacollector.jpadomains.EngineRole;

public interface EnginesRoleRepoitory extends JpaRepository<EngineRole, Integer>{

	EngineRole findByRoleCode(String string);

}
