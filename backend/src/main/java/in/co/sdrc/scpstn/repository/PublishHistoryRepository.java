package in.co.sdrc.scpstn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.co.sdrc.scpstn.domain.PublishHistory;

public interface PublishHistoryRepository extends JpaRepository<PublishHistory,Integer >{
	
	PublishHistory findByDataBeingPublishedForMonthAndDataBeingPublishedForYear(Integer month,Integer year);


}
