package in.co.sdrc.scpstn.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import in.co.sdrc.scpstn.domain.Agency;
import in.co.sdrc.scpstn.models.FactsheetObject;
import in.co.sdrc.scpstn.repository.AgencyRepository;
import in.co.sdrc.scpstn.service.FactsheetService;

@Controller
@RequestMapping("bypass/")
public class FactsheetController {

	@Autowired
	private FactsheetService factsheetService;

	@Autowired
	private AgencyRepository agencyRepository;
	


	@RequestMapping("getPrefetchData")
	@ResponseBody
	public Object getPrefetchData( HttpServletResponse res, HttpServletRequest req) {

		Agency agency = agencyRepository.findByAgencyId(1);
		return factsheetService.getPrefetchData(agency.getAgencyId());
	}

	@RequestMapping(value = { "factSheetData" })
	@ResponseBody
	public Object FactSheetData(@RequestBody FactsheetObject factsheetObject, HttpServletResponse res, HttpServletRequest req) {
			Agency agency = agencyRepository.findByAgencyId(1);
		return factsheetService.getFactSheetData(factsheetObject, agency);
	}
}
