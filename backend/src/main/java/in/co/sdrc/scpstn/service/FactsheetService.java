package in.co.sdrc.scpstn.service;

import in.co.sdrc.scpstn.domain.Agency;
import in.co.sdrc.scpstn.models.FactsheetObject;

public interface FactsheetService {

	Object getPrefetchData(int agencyId);

	Object getFactSheetData(FactsheetObject factsheetObject,Agency agency);
}
