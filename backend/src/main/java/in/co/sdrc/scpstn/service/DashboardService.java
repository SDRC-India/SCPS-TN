package in.co.sdrc.scpstn.service;

import java.text.ParseException;
import java.util.List;

import org.json.simple.JSONArray;

import in.co.sdrc.scpstn.models.DataCollectionModel;
import in.co.sdrc.scpstn.models.LineSeries;
import in.co.sdrc.scpstn.models.ValueObject;

public interface DashboardService {

	List<ValueObject> fetchIndicators(String param);

	List<ValueObject> fetchSources(String param);

	List<ValueObject> fetchUtTimeperiod(Integer iusNid, Integer SourceNid);

	JSONArray fetchAllSectors();

	DataCollectionModel fetchData(String indicatorId, String sourceId, String parentAreaCode, String timeperiodId, Integer childLevel) throws ParseException;

	List<List<LineSeries>> fetchChartData(Integer iusNid, Integer areaNid, Integer sourceNid) throws ParseException;


	String exportPDF(List<String> svgs, String indicatorId, String sourceId, String parentAreaCode, String timeperiodId, Integer childLevel);

	String exportPDFLine(List<String> svgs, Integer iusNid, Integer areaNid, Integer sourceNid);

	public boolean publishData();

	boolean displayPublishButton();

	boolean publishData(int agencyId, int year, int month, String monthString);

	DataCollectionModel getTableData(String indicatorId, String sourceId, String parentAreaCode, String timeperiodId,
			Integer childLevel);
}
