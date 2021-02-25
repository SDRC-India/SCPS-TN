package in.co.sdrc.scpstn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import in.co.sdrc.scpstn.models.QuickStatPageVO;
import in.co.sdrc.scpstn.service.QuickStatsService;

@Controller
@RequestMapping("/bypass")
public class QuickStatsController {

	@Autowired
	QuickStatsService quickStatsService;
	
	@RequestMapping("/getQuickStats")
	@ResponseBody
	public ResponseEntity<QuickStatPageVO> getQuickStats(){
		return new ResponseEntity<QuickStatPageVO>(quickStatsService.getQuickStats(), HttpStatus.OK);
	}
	
}

