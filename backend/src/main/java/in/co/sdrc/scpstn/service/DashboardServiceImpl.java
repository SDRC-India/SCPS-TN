package in.co.sdrc.scpstn.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import in.co.sdrc.scpstn.domain.Agency;
import in.co.sdrc.scpstn.domain.Area;
import in.co.sdrc.scpstn.domain.Data;
import in.co.sdrc.scpstn.domain.Indicator;
import in.co.sdrc.scpstn.domain.IndicatorClassification;
import in.co.sdrc.scpstn.domain.IndicatorClassificationIndicatorUnitSubgroupMapping;
import in.co.sdrc.scpstn.domain.IndicatorUnitSubgroup;
import in.co.sdrc.scpstn.domain.PublishHistory;
import in.co.sdrc.scpstn.domain.Subgroup;
import in.co.sdrc.scpstn.domain.Timeperiod;
import in.co.sdrc.scpstn.domain.Unit;
import in.co.sdrc.scpstn.models.Constants;
import in.co.sdrc.scpstn.models.DataCollectionModel;
import in.co.sdrc.scpstn.models.DataModel;
import in.co.sdrc.scpstn.models.HeaderFooter;
import in.co.sdrc.scpstn.models.IndicatorClassificationType;
import in.co.sdrc.scpstn.models.IndicatorType;
import in.co.sdrc.scpstn.models.LineSeries;
import in.co.sdrc.scpstn.models.ValueObject;
import in.co.sdrc.scpstn.repository.AgencyRepository;
import in.co.sdrc.scpstn.repository.AreaRepository;
import in.co.sdrc.scpstn.repository.DataRepository;
import in.co.sdrc.scpstn.repository.IndicatorClassificationRepository;
import in.co.sdrc.scpstn.repository.IndicatorRepository;
import in.co.sdrc.scpstn.repository.IndicatorUnitSubgroupRepository;
import in.co.sdrc.scpstn.repository.PublishHistoryRepository;
import in.co.sdrc.scpstn.repository.SubmissionRepository;
import in.co.sdrc.scpstn.repository.TimePeriodRepository;

/**
 * 
 * @author Azaruddin(azaruddin@sdrc.co.in)
 *
 */

@Service
@Scope("prototype")
public class DashboardServiceImpl implements DashboardService {

	@Autowired
	DataRepository dataRepository ;

	@Autowired
	private AgencyRepository agencyRepository;

	

	@Autowired
	IndicatorRepository indicatorRepository;

	@Autowired
	IndicatorClassificationRepository indicatorClassificationRepository;

	@Autowired
	IndicatorUnitSubgroupRepository indicatorUnitSubgroupRepository;

	@Autowired
	TimePeriodRepository timePeriodRepository;

	@Autowired
	SubmissionRepository submissionRepository;

	@Autowired
	AreaRepository areaRepository;

	@Autowired
	private ConfigurableEnvironment applicationMessageSource;

	private Map<String, List<LineSeries>> dataByArea = null;

//	private Map<String, Integer> ranks = null;

	private Map<String, Map<String, List<ValueObject>>> dataByTPBYSource = null;



	@Autowired
	private ServletContext context;

	@Autowired
	private PublishHistoryRepository publishHistoryRepository;

	private static DecimalFormat df = new DecimalFormat(".#");

	private static DecimalFormat df2 = new DecimalFormat("0.00");
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	

	@Override
	@Transactional
	public List<ValueObject> fetchIndicators(String sector) {
		IndicatorClassification sectorIC = new IndicatorClassification();
		sectorIC.setIndicatorClassificationId(new Integer(sector));
		
		//GET ALL INDICATORS THAT BELONG TO SUBSECTOR
		
//		List<Indicator> indicators = indicatorRepository.findAllByIndicatorClassificationAndIndicatorTypeNotIn(sectorIC, IndicatorType.DENOMINATOR,IndicatorType.DENOMINATOR);
				

		Agency agency = agencyRepository.findByAgencyId(1);
		// Added by Azhar
		List<Object[]> listofIndicators = indicatorRepository.findByIC_Type_And_Agency(sectorIC, agency);

		List<ValueObject> list = new ArrayList<ValueObject>();

		for (int i = 0; i < listofIndicators.size(); i++) {
			Object[] objects = listofIndicators.get(i);

			ValueObject vObject = new ValueObject();
			String indName = "";
			String unitName = "";
			String subName = "";
			for (Object obj : objects) {
				if (obj instanceof Indicator) {
					Indicator utIUS = (Indicator) obj;
					indName = utIUS.getIndicatorName();
					vObject.setKey(Integer.toString(utIUS.getIndicatorId()));
					vObject.setType(utIUS.getAppType().name());
					vObject.setClassification(utIUS.getClassifier()==null?"":utIUS.getClassifier().name());
					vObject.setGroupName(utIUS.getIndicatorType().toString());
				} else if (obj instanceof Unit) {
					Unit unitEn = (Unit) obj;
					unitName = unitEn.getUnitName();
				} else if (obj instanceof Subgroup) {
					Subgroup subgroupValsEn = (Subgroup) obj;
					subName = subgroupValsEn.getSubgroupVal();
				} else if (obj instanceof IndicatorClassificationIndicatorUnitSubgroupMapping) {
					IndicatorClassificationIndicatorUnitSubgroupMapping utIUS = (IndicatorClassificationIndicatorUnitSubgroupMapping) obj;
					vObject.setDescription(Integer.toString(utIUS.getIndicatorUnitSubgroup().getIndicatorUnitSubgroupId()));
				}
			}
			vObject.setValue(indName + ", " + subName + " (" + unitName + ")");
			list.add(vObject);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray fetchAllSectors() {
		List<IndicatorClassification> indicatorClassifications = indicatorClassificationRepository.findByIndicatorClassificationType(IndicatorClassificationType.SC);
		JSONArray allSectorArr = new JSONArray();
		JSONObject index =  null;
		for (IndicatorClassification indicatorClassification : indicatorClassifications) {
			if(indicatorClassification.getIndicatorClassificationId() == 41 || (indicatorClassification.getParent()!=null && indicatorClassification.getParent().getIndicatorClassificationId()==41))
			{
				continue;
			}
				
			JSONObject sectorObj = new JSONObject();
			if (null == indicatorClassification.getParent()) {
				sectorObj.put("key", indicatorClassification.getIndicatorClassificationId());
				sectorObj.put("value", indicatorClassification.getName());
				sectorObj.put("description", -1);
			} else {
				sectorObj.put("key", indicatorClassification.getIndicatorClassificationId());
				sectorObj.put("value", indicatorClassification.getName());
				sectorObj.put("description", indicatorClassification.getParent().getIndicatorClassificationId());
			}
			if(indicatorClassification.getIndicatorClassificationId() == 13) {
				index = new JSONObject();
				index = sectorObj;
			}else {
				allSectorArr.add(sectorObj);
			}
			
			
		}
		if(index!=null) {
			allSectorArr.add(0,index);
		}
		return allSectorArr;
	}

	@Override
	public List<ValueObject> fetchSources(String param) {

		List<IndicatorClassification> classificationsEns = indicatorClassificationRepository.findByIUS_Nid(Integer.parseInt(param), agencyRepository.findByAgencyId(1).getAgencyId());
		List<ValueObject> valueObjects = new ArrayList<>();
		for (IndicatorClassification classificationsEn : classificationsEns) {
			ValueObject object = new ValueObject();
			object.setKey(classificationsEn.getIndicatorClassificationId().toString());
			object.setValue(classificationsEn.getName());
			valueObjects.add(object);
		}
		return valueObjects;
	}

	@Override
	public List<ValueObject> fetchUtTimeperiod(Integer iusNid, Integer SourceNid) {
		List<Timeperiod> utTimeperiods = null;
		List<ValueObject> valueObjects = new ArrayList<>();

		
		utTimeperiods = timePeriodRepository.findBySource_Nid(iusNid, SourceNid);
		for (Timeperiod utTimeperiod : utTimeperiods) {
			
			ValueObject object = new ValueObject();
			object.setKey(utTimeperiod.getTimeperiodId().toString());
			
			LocalDateTime ldt = LocalDateTime.ofInstant(utTimeperiod.getStartDate().toInstant(),ZoneId.of("Asia/Calcutta"));
			
			object.setValue(ldt.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + ldt.getYear());
			
//			object.setValue(utTimeperiod.getTimePeriod());
			valueObjects.add(object);
		}

		Collections.reverse(valueObjects);
		return valueObjects;
	}

	@Override
	public DataCollectionModel fetchData(String iusId, String sourceId, String parentAreaCode, String timeperiodId, Integer childLevel) throws ParseException {

		// get parentArea from the area list
		Area area = areaRepository.findByAreaCode(parentAreaCode);

		List<Area> children = areaRepository.findAllByParentAreaId(area.getAreaId());

		Integer[] areaNids = new Integer[children.size()];

		IndicatorUnitSubgroup ius = indicatorUnitSubgroupRepository.findByIndicatorUnitSubgroupId(Integer.parseInt(iusId));
		Indicator ind = ius.getIndicator();
		boolean isIndicatorPositive = ind.isHighIsGood();

		int i = 0;
		for (Area utAreaEn : children) {
			areaNids[i] = utAreaEn.getAreaId();
			i++;
		}
		
		DataCollectionModel collection = new DataCollectionModel();
		
		// find All Data in Ascending Order
		List<Object[]> data=null;
		
		if(isIndicatorPositive)
		{
		    data = dataRepository.findDataByTimePeriodWithRankWithHighGood(Integer.parseInt(iusId), Integer.parseInt(timeperiodId), Integer.parseInt(sourceId), areaNids);
		}
		else
		{
			data = dataRepository.findDataByTimePeriodWithRankWithHighBad(Integer.parseInt(iusId), Integer.parseInt(timeperiodId), Integer.parseInt(sourceId), areaNids);
		}
		
		//set css slices
		List<ValueObject> legends = new ArrayList<ValueObject>();
		int range = 3;
		if (ind.getIndicatorType()==IndicatorType.INDEX_INDICATOR || ind.getIndicatorType()==IndicatorType.OVERALL||ind.getIndicatorType()==IndicatorType.INDEX_OVERALL_SOURCEWISE||ind.getIndicatorType()==IndicatorType.SUBSECTOR_INDEX) {
			
			
			legends.add(new ValueObject("0.00 - 0.60", Constants.Slices.FIRST_SLICE));
			legends.add(new ValueObject("0.61 - 0.80 ", Constants.Slices.SECOND_SLICE));
			legends.add(new ValueObject("0.81 - 1.00 ", Constants.Slices.FIFTHSLICES));
			legends.add(new ValueObject("Not Available", Constants.Slices.SIXTH_SLICES));
			
			collection.setLegends(legends);
		}else {
			// since we have to find dynamic legends we have to consider indicator
			// positive and negative aspect
			
			Double minDataValue = null;
			Double maxDataValue = null;
			String firstslices = Constants.Slices.FIRST_SLICE;
			String secondslices = Constants.Slices.SECOND_SLICE;
			String fifthslices = Constants.Slices.FIFTHSLICES;
			String sixthslices = Constants.Slices.SIXTH_SLICES;
				
				if(data.size() > 0 )
				{
				minDataValue = getFormattedDouble(Double.parseDouble(data.get(0)[0].toString()));
				maxDataValue = getFormattedDouble(Double.parseDouble(data.get(data.size() - 1)[0].toString()));
				Double difference = (maxDataValue - minDataValue) / range;
				if (difference == 0) {
					String firstSliceValue = Double.toString(minDataValue) + " - " + Double.toString(getFormattedDouble(minDataValue + difference));
					legends.add(!isIndicatorPositive ? new ValueObject(firstSliceValue, firstslices) : new ValueObject(firstSliceValue, fifthslices));
					legends.add(new ValueObject("Not Available", sixthslices));
				} else {
					String firstSliceValue = String.valueOf(getFormattedDouble(minDataValue) + 0.0) + " - " + String.valueOf(getFormattedDouble(minDataValue + difference) + 0.0);
					String secondSliceValue = Double.toString(getFormattedDouble(minDataValue + difference + 0.1)) + " - " + String.valueOf(0.0 + getFormattedDouble(minDataValue + (2 * difference)));
					String fifthSliceValue = Double.toString(getFormattedDouble(minDataValue + 2 * difference + 0.1)) + " - " + Double.toString((maxDataValue));

					if (isIndicatorPositive) {
						legends.add(new ValueObject(firstSliceValue, firstslices));
						legends.add(new ValueObject(secondSliceValue, secondslices));
						legends.add(new ValueObject(fifthSliceValue, fifthslices ));
						legends.add(new ValueObject("Not Available", sixthslices));
					} else {
						legends.add(new ValueObject(firstSliceValue, fifthslices));
						legends.add(new ValueObject(secondSliceValue, secondslices));
						legends.add(new ValueObject(fifthSliceValue, firstslices));
						legends.add(new ValueObject("Not Available", sixthslices));
					}
				}
			}
		}
		collection.setLegends(legends);
		// calculate Rank. All values that have same values will be ranked same.
		List<String> topPerformers  = new ArrayList<String>();
		List<String> bottomPerformers = new ArrayList<String>();
	    
		collection.dataCollection = new ArrayList<DataModel>();
		int position=0;
		for (int index = 0; index < data.size(); index++) {
			
			Object[] dataObject = data.get(index);
			double percentage = Double.parseDouble(dataObject[0].toString());
		
			DataModel utDataModel = new DataModel();
			if (ind.getIndicatorType()==IndicatorType.INDEX_INDICATOR||ind.getIndicatorType()==IndicatorType.OVERALL||ind.getIndicatorType()==IndicatorType.INDEX_OVERALL_SOURCEWISE||ind.getIndicatorType()==IndicatorType.SUBSECTOR_INDEX) {
				utDataModel.setValue(df2.format(percentage));
			} else {
				if ((percentage) > 1)
					utDataModel.setValue(df.format(percentage));
				else
					utDataModel.setValue("0" + df.format(percentage));
			}
			
			if (legends != null) {
				setCssForDataModel(legends, utDataModel, Double.parseDouble(utDataModel.getValue()), ius.getIndicator().getIndicatorId().toString(), sourceId, isIndicatorPositive);
			}
			utDataModel.setPosition(++position);
			utDataModel.setAreaCode(dataObject[1].toString());
			utDataModel.setAreaName(dataObject[2].toString());
			utDataModel.setAreaNid(Integer.parseInt(dataObject[3].toString()));
			utDataModel.setUnit("percent");
			utDataModel.setRank(dataObject[5].toString());
			//setting Rank
			collection.dataCollection.add(utDataModel);
		}
		Data utdataTopPerformer = null,utdataButtomPerformer = null;
		Area utareaTopPerformer = null,utareaButtomPerformer = null;
		String topPerformer,buttomPerformer;
		if (data.size() > 1) {
			int top = data.size() - 1;
			int buttom = 0;
//			if(isIndicatorPositive) {
//				utdataTopPerformer = (Data) data.get(top)[0];
//				utareaTopPerformer = (Area) data.get(top)[1];
//				utdataButtomPerformer = (Data) data.get(buttom)[0];
//				utareaButtomPerformer = (Area) data.get(buttom)[1];
//			}else {
//				utdataTopPerformer = (Data) data.get(buttom)[0];
//				utareaTopPerformer = (Area) data.get(buttom)[1];
//				utdataButtomPerformer = (Data) data.get(top)[0];
//				utareaButtomPerformer = (Area) data.get(top)[1];
//			}
			
//			topPerformer = new String(utareaTopPerformer.getAreaName() + " - " + String.format("%.1f", utdataTopPerformer.getPercentage()));
//			buttomPerformer = new String(utareaButtomPerformer.getAreaName() + " - " + String.format("%.1f", utdataButtomPerformer.getPercentage()));

//			topPerformers.add(topPerformer);
//			bottomPerformers.add(buttomPerformer);
		}
		collection.setTopPerformers(topPerformers);
		collection.setBottomPerformers(bottomPerformers);
		
		if(isIndicatorPositive) {
			Collections.reverse(collection.dataCollection);
		}
		
		collection.dataCollection.forEach(row->{
			Map<String,Object> map = new LinkedHashMap<>();
			map.put("District", row.getAreaName());
			map.put("Percent", row.getValue());
			map.put("Rank", Integer.parseInt(row.getRank()));
			collection.getTableModel().add(map);
		});
		
		return collection;
	}

	private void getChildren(Area[] utAreas, int i, int parentNid, ArrayList<Area> list) {

		for (Area utAreaEn : utAreas) {
			if (utAreaEn.getParentAreaId() == parentNid) {
				if (utAreaEn.getAreaLevel().getAreaLevelId() == i)
					list.add(utAreaEn);
				else
					getChildren(utAreas, i, utAreaEn.getAreaId(), list);
			}
		}

	}


	public Double getFormattedDouble(Double value) {
		Double formattedValue = value != null ? new BigDecimal(value).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue() : null;
		return formattedValue;
	}


	private void setCssForDataModel(List<ValueObject> list, DataModel data, Double value, String indicatorId, String sourceId, boolean isIndicatorPositive) {

		if (Integer.parseInt(sourceId) == Integer.parseInt(applicationMessageSource.getProperty("index.sourceId", null, null))) {
			if (value <= 0.61) {
				data.setCssClass(Constants.Slices.FIRST_SLICE);
			}

//			else if (value <= 0.40) {
//				data.setCssClass(Constants.Slices.SIXTH_SLICES);
//			} 
			else if (value <= 0.80)
			{
				data.setCssClass(Constants.Slices.SECOND_SLICE);
			}

//			else if (value <= 0.80) {
//				data.setCssClass(Constants.Slices.THIRD_SLICE);
//			}

			else if (value <= 1.0) {
				data.setCssClass(Constants.Slices.FIFTHSLICES);
			}

		} else {

			for (int index = 0; index < list.size(); index++) {
				ValueObject vObject = list.get(index);
				String[] vArray = vObject != null ? ((String) vObject.getKey()).split(" - ") : null;

				if (list.size() == 2) {
					if (isIndicatorPositive) {
						data.setCssClass(Constants.Slices.FIFTHSLICES);
					} else {
						data.setCssClass(Constants.Slices.FIRST_SLICE);
					}
				}

				else if (index == 3 || (vArray != null && new Double(vArray[0]).doubleValue() <= value && value <= new Double(vArray[1]).doubleValue())) {

					if (isIndicatorPositive) {
						switch (index) {
						case 0:
							data.setCssClass(Constants.Slices.FIRST_SLICE);
							break;
//						case 1:
//							data.setCssClass(Constants.Slices.SIXTH_SLICES);
//							break;
						case 1:
							data.setCssClass(Constants.Slices.SECOND_SLICE);
							break;

//						case 3:
//							data.setCssClass(Constants.Slices.THIRD_SLICE);
//							break;
						case 2:
							data.setCssClass(Constants.Slices.FIFTHSLICES);
							break;

						}
					} else {
						switch (index) {
						case 0:
							data.setCssClass(Constants.Slices.FIFTHSLICES);
							break;
//						case 1:
//							data.setCssClass(Constants.Slices.THIRD_SLICE);
//							break;
						case 1:
							data.setCssClass(Constants.Slices.SECOND_SLICE);
							break;

//						case 3:
//							data.setCssClass(Constants.Slices.SIXTH_SLICES);
//							break;
						case 2:
							data.setCssClass(Constants.Slices.FIRST_SLICE);
							break;
						}
					}

				}

			}

		}
	}

	@Override
	@Transactional
	public List<List<LineSeries>> fetchChartData(Integer iusNid, Integer areaNid,Integer sourceNid) throws ParseException {
		List<List<LineSeries>> lineCharts = new ArrayList<>();
		List<LineSeries> dataSeries = new ArrayList<>();
		List<Object[]> listData = new ArrayList<>();
		List<Object[]> resultListData =null;
		
//		UserModel userModel = null;
//		if(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()!="anonymousUser")
//			userModel=(UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		if (userModel == null || (Integer)userModel.getDesignationIds().toArray()[0]!=  8) {
			// public view of timeperiod
			resultListData = dataRepository.findDataWithSource(iusNid, areaNid,sourceNid);
			for(Object[] res : resultListData) {
				if(listData.size() < 12) {
					listData.add(0, res);
				}
			}
			
//		} else if ((Integer)userModel.getDesignationIds().toArray()[0] ==  8) {
//			// fetch all timeperiod for commisioner
//			 listData = dataRepository.findDataforCommissionerWithSource(iusNid, areaNid,sourceNid);
//		}

		populateDataByTimePeroid(listData);
		String indicatorId = iusNid.toString();
		IndicatorUnitSubgroup ius = indicatorUnitSubgroupRepository.findByIndicatorUnitSubgroupId(Integer.parseInt(indicatorId));

		boolean isPositveIndicator = ius.getIndicator().isHighIsGood();

		int sourceId = 0;

		if (listData != null && !listData.isEmpty()) {
			for (int i = 0; i < listData.size(); i++) {
				Object[] dataObjects = listData.get(i);
				sourceId = ((IndicatorClassification) dataObjects[3]).getIndicatorClassificationId();
				LineSeries lineChat = new LineSeries();
				for (Object dataObject : dataObjects) {
//					lineChat.setCssClass("#fff");
					if (dataObject instanceof IndicatorClassification) {
						IndicatorClassification classificationsEn = (IndicatorClassification) dataObject;
						lineChat.setSource(classificationsEn.getShortName() == null ? classificationsEn.getName() : classificationsEn.getShortName());
						lineChat.setKey(classificationsEn.getShortName() == null ? classificationsEn.getName() : classificationsEn.getShortName());
						sourceId = classificationsEn.getIndicatorClassificationId();
					} else if (dataObject instanceof Data) {
						Data data = (Data) dataObject;
						if (sourceId == Integer.parseInt(applicationMessageSource.getProperty("index.sourceId", null, null)))
						//	lineChat.setValue("0" + df2.format(data.getPercentage()));
							lineChat.setValue(df2.format(data.getPercentage()));
							else
							lineChat.setValue((data.getPercentage()) > 1 ? df.format(data.getPercentage()) : "0" + df.format(data.getPercentage()));
					} else if (dataObject instanceof Timeperiod) {
						Timeperiod timeperiod = (Timeperiod) dataObject;
						lineChat.setDate(timeperiod.getTimePeriod().replace(".", "-"));
					}

				}
				for (Object[] dataObject : listData) {
					Area utAreaEn = (Area) dataObject[1];
					IndicatorClassification classificationsEn = (IndicatorClassification) dataObject[3];
					String areaCode = utAreaEn.getAreaCode();
					List<ValueObject> list = dataByTPBYSource.get(areaCode).get(classificationsEn.getShortName());

					BigDecimal percentChange = null;
					if (sourceId == Integer.parseInt(applicationMessageSource.getProperty("index.sourceId", null, null))) {
						percentChange = list.size() >= 2 && (Double) (list.get(list.size() - 2).getValue()) > 0 ? new BigDecimal(((Math.abs((Double) (list.get(list.size() - 2).getValue()) - (Double) (list.get(list.size() - 1).getValue()))) / ((Double) (list.get(list.size() - 2).getValue()))) * 100).setScale(2, BigDecimal.ROUND_HALF_UP)
								: new BigDecimal(((Double) (list.get(list.size() - 1).getValue())) * 100);
					} else {
						percentChange = list.size() >= 2 && (Double) (list.get(list.size() - 2).getValue()) > 0 ? new BigDecimal(((Math.abs((Double) (list.get(list.size() - 2).getValue()) - (Double) (list.get(list.size() - 1).getValue()))) / ((Double) (list.get(list.size() - 2).getValue()))) * 100).setScale(1, BigDecimal.ROUND_HALF_UP)
								: new BigDecimal(((Double) (list.get(list.size() - 1).getValue())) * 100);
					}

					if (listData.size() > 1) {
						lineChat.setPercentageChange(percentChange != null ? ((Double) (list.get(listData.size() - 1).getValue()) >= (Double) (list.get(listData.size() - 2).getValue()) ? percentChange.toString() : "-" + percentChange.toString()) : null);
					} else {
						lineChat.setPercentageChange(percentChange.toString());
					}
					lineChat.setIsPositive(percentChange != null && list.size() >= 2 ? (Double) (list.get(listData.size() - 1).getValue()) > (Double) (list.get(listData.size() - 2).getValue()) ? isPositveIndicator ? true : false : isPositveIndicator ? false : true : false);

					if (isPositveIndicator && ((list.size() < 2) || (((Double) (list.get(listData.size() - 1).getValue()) > (Double) (list.get(listData.size() - 2).getValue()))))) {
						lineChat.setCssClass("uptrend");
						lineChat.setIsUpward(true);
					} else if (isPositveIndicator && ((list.size() < 2) || (((Double) (list.get(listData.size() - 1).getValue()) < (Double) (list.get(listData.size() - 2).getValue()))))) {
						lineChat.setCssClass("downtrend");
						lineChat.setIsUpward(false);
					} else if (!isPositveIndicator && ((list.size() < 2) || (((Double) (list.get(listData.size() - 1).getValue()) < (Double) (list.get(listData.size() - 2).getValue()))))) {
						lineChat.setCssClass("uptrend");
						lineChat.setIsUpward(false);
					}
					if (!isPositveIndicator && ((list.size() < 2) || (((Double) (list.get(listData.size() - 1).getValue()) > (Double) (list.get(listData.size() - 2).getValue()))))) {
						lineChat.setCssClass("downtrend");
						lineChat.setIsUpward(true);
					}

				}

				dataSeries.add(lineChat);
			}
		}
		lineCharts.add(dataSeries);
		return lineCharts;

	}

	private void populateDataByTimePeroid(List<Object[]> listData) throws ParseException {
		dataByArea = new HashMap<String, List<LineSeries>>();

		dataByTPBYSource = new HashMap<>();

		if (listData != null && !listData.isEmpty()) {
			Data utData = null;
			Area utAreaEn = null;
			Timeperiod utTimeperiod = null;
			IndicatorClassification classificationsEn = null;

			for (Object[] dataObject : listData) {
				utData = (Data) dataObject[0];
				utAreaEn = (Area) dataObject[1];
				utTimeperiod = (Timeperiod) dataObject[2];
				classificationsEn = (IndicatorClassification) dataObject[3];

				if (dataByTPBYSource.containsKey(utAreaEn.getAreaCode())) {
					Map<String, List<ValueObject>> dataByTPMap = dataByTPBYSource.get(utAreaEn.getAreaCode());

					if (dataByTPMap.containsKey(classificationsEn.getShortName())) {
						List<ValueObject> objects = dataByTPMap.get(classificationsEn.getShortName());
						objects.add(new ValueObject(getFormattedTP(utTimeperiod.getTimePeriod()), utData.getPercentage()));
					} else {
						List<ValueObject> objects = new ArrayList<>();
						objects.add(new ValueObject(getFormattedTP(utTimeperiod.getTimePeriod()), utData.getPercentage()));
						dataByTPMap.put(classificationsEn.getShortName(), objects);
					}

				} else {

					Map<String, List<ValueObject>> dataByTPMap = new HashMap<>();
					List<ValueObject> objects = new ArrayList<>();
					objects.add(new ValueObject(getFormattedTP(utTimeperiod.getTimePeriod()), utData.getPercentage()));
					dataByTPMap.put(classificationsEn.getShortName(), objects);

					dataByTPBYSource.put(utAreaEn.getAreaCode(), dataByTPMap);

				}

				if (dataByArea.containsKey(utAreaEn.getAreaCode())) {
					List<LineSeries> lineSeries = dataByArea.get(utAreaEn.getAreaCode());
					lineSeries.add(new LineSeries(classificationsEn.getShortName(), getFormattedTP(utTimeperiod.getTimePeriod()), utData.getPercentage()));
				} else {
					List<LineSeries> lineSeries = new ArrayList<>();
					lineSeries.add(new LineSeries(classificationsEn.getShortName(), getFormattedTP(utTimeperiod.getTimePeriod()), utData.getPercentage()));
					dataByArea.put(utAreaEn.getAreaCode(), lineSeries);
				}
			}
		}

	}

	public String getFormattedTP(String timePeriod) throws ParseException {

		// Date date = null;
		// try {
		// date = timePeriod != null ? new SimpleDateFormat("yyyy.MM")
		// .parse(timePeriod) : null;
		// } catch (ParseException e) {
		// e.printStackTrace();
		// date = timePeriod != null ? new SimpleDateFormat("yyyy.MMM")
		// .parse(timePeriod) : null;
		// }
		// String formattedTP = date != null ? new SimpleDateFormat("MMM yyyy")
		// .format(date) : null;

		return timePeriod;
	}
@Override
	public DataCollectionModel getTableData(String indicatorId, String sourceId, String parentAreaCode, String timeperiodId, Integer childLevel) {
		try {
			DataCollectionModel valList = fetchData(indicatorId, sourceId, parentAreaCode, timeperiodId, childLevel);
			int i = 1;
			valList.getDataCollection().sort(new Comparator<DataModel>() {

				@Override
				public int compare(DataModel o1, DataModel o2) {
					if(Integer.parseInt(o1.getRank())>Integer.parseInt(o2.getRank()))
						return 1;
					else if(Integer.parseInt(o1.getRank())<Integer.parseInt(o2.getRank()))
						return -1;
					else
					return 0;
				}
			});
		 return valList;
		}catch(Exception e) {
			
		}
		return null;
	}

	@SuppressWarnings({ "resource", "unused" })
	@Override
	public String exportPDF(List<String> svgs, String indicatorId, String sourceId, String parentAreaCode, String timeperiodId, Integer childLevel) {

		Agency agency = agencyRepository.findByAgencyId(1);
		String file = null;
		try {
			DataCollectionModel valList = fetchData(indicatorId, sourceId, parentAreaCode, timeperiodId, childLevel);

			Indicator indicatorObject = indicatorRepository.findByIndicator_NId(Integer.parseInt(indicatorId));
			Timeperiod timeperiod = timePeriodRepository.findByTimeperiodId(Integer.parseInt(timeperiodId));

			new FileOutputStream(new File(applicationMessageSource.getProperty("outputPathPdfLineChart") + "/map.svg")).write(svgs.get(2).getBytes());

			Font smallBold = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
			Font dataFont = new Font(Font.FontFamily.HELVETICA, 10);

			Document document = new Document(PageSize.A4.rotate());

			String outputPath = applicationMessageSource.getProperty("outputPathPdfLineChart", null, null);
			outputPath=indicatorObject.getIndicatorName()+"_"+sdf.format(new java.util.Date())+".pdf";
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputPath));
			// setting Header Footer.PLS Refer to
			// org.sdrc.cpisico.util.HeaderFooter
			HeaderFooter headerFooter = new HeaderFooter(context, agency);
			writer.setPageEvent(headerFooter);

			document.open();

			BaseColor cellColor = WebColors.getRGBColor("#E8E3E2");
			BaseColor headerCOlor = WebColors.getRGBColor("#333a3b");
			BaseColor siNoColor = WebColors.getRGBColor("#a6bdd9");

			Paragraph dashboardTitle = new Paragraph();
			dashboardTitle.setAlignment(Element.ALIGN_CENTER);
			dashboardTitle.setSpacingAfter(10);
			Chunk dashboardChunk = new Chunk("Score Card");
			dashboardTitle.add(dashboardChunk);

			Paragraph blankSpace = new Paragraph();
			blankSpace.setAlignment(Element.ALIGN_CENTER);
			blankSpace.setSpacingAfter(10);
			Chunk blankSpaceChunk = new Chunk("          ");
			blankSpace.add(blankSpaceChunk);

			Paragraph numberOfFacility = new Paragraph();
			numberOfFacility.setAlignment(Element.ALIGN_CENTER);
			Chunk numberOfFacilityChunk = new Chunk();
			numberOfFacility.add(numberOfFacilityChunk);

			Paragraph spiderDataParagraph = new Paragraph();
			spiderDataParagraph.setAlignment(Element.ALIGN_CENTER);
			spiderDataParagraph.setSpacingAfter(10);
			
			LocalDateTime ldt = LocalDateTime.ofInstant(timeperiod.getStartDate().toInstant(),ZoneId.of("Asia/Calcutta"));
			

			
			Chunk spiderChunk = new Chunk("Indicator : " + indicatorObject.getIndicatorName() + "  Timeperiod : " +ldt.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + ldt.getYear());
			spiderDataParagraph.add(spiderChunk);

			byte[] topBottomImage = null;
			int indentationMap = 0;
			float scalerMap = 0;
			
//			if (svgs.get(0).split(",").length > 1)
//				topBottomImage = Base64.decodeBase64(svgs.get(0).split(",")[1]);
//			Image topImage = null;
//			if (null != topBottomImage)
//				topImage = Image.getInstance(topBottomImage);
//
//	
//
//			if (null != topImage)
//				scalerMap = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin() - indentationMap) / topImage.getWidth()) * 100;
//			else
//				scalerMap = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin() - indentationMap) / 118) * 100;
//
//			int indentation1 = 0;
//			float scaler1 = 0.0f;
//			if (null != topImage)
//				scaler1 = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin() - indentation1) / topImage.getWidth()) * 18;
//			else
//				scaler1 = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin() - indentation1) / 118) * 18;
//			if (Integer.parseInt(svgs.get(3)) > 1024 && null != topImage) {
//				topImage.scalePercent(70);
//				topImage.setAbsolutePosition(20, 80);
//			} else if (Integer.parseInt(svgs.get(3)) > 1000 && null != topImage) {
//				topImage.scalePercent(70);
//				topImage.setAbsolutePosition(20, 80);
//			} else if (null != topImage) {
//				topImage.scalePercent(50);
//				topImage.setAbsolutePosition(20, -280);
//			}
			byte[] legendImage = Base64.decodeBase64(svgs.get(1).split(",")[1]);
			Image legendImageInstance = Image.getInstance(legendImage);

			// topImage.scalePercent(scalerMap);
			scalerMap = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin() - indentationMap) / legendImageInstance.getWidth()) * 100;

			int indentation2 = 0;
			float scaler2 = ((document.getPageSize().getWidth() - document.rightMargin() - indentation2) / legendImageInstance.getWidth()) * 18;

			// if (Integer.parseInt(svgs.get(3)) > 1024) {
			// legendImageInstance.setAbsolutePosition(240, 80);
			// legendImageInstance.scalePercent(70);
			// } else if (Integer.parseInt(svgs.get(3)) > 1000) {
			// legendImageInstance.setAbsolutePosition(400, 80);
			// legendImageInstance.scalePercent(70);
			// } else {
			
			legendImageInstance.setAbsolutePosition(600, 80);
			legendImageInstance.scalePercent(70);
			
			// }

			// legendImageInstance.scalePercent(scalerMap);
			/* googleMapImage.setAbsolutePosition(40,5); */

			Paragraph googleMapParagraph = new Paragraph();
			googleMapParagraph.setAlignment(Element.ALIGN_CENTER);
			googleMapParagraph.setSpacingAfter(40);
			Chunk googleMapChunk = new Chunk();
			googleMapParagraph.add(googleMapChunk);

			String css = "svg {" + "shape-rendering: geometricPrecision;" + "text-rendering:  geometricPrecision;" + "color-rendering: optimizeQuality;" + "image-rendering: optimizeQuality;" + "}";
			File cssFile = File.createTempFile("batik-default-override-", ".css");
			FileUtils.writeStringToFile(cssFile, css);

			String svg_URI_input = Paths.get(new File(applicationMessageSource.getProperty("outputPathPdfLineChart")  + "/map.svg").getPath()).toUri().toURL().toString();
			TranscoderInput input_svg_image = new TranscoderInput(svg_URI_input);
			// Step-2: Define OutputStream to PNG Image and attach to
			// TranscoderOutput
			ByteArrayOutputStream png_ostream = new ByteArrayOutputStream();
			TranscoderOutput output_png_image = new TranscoderOutput(png_ostream);
			// Step-3: Create PNGTranscoder and define hints if required
			PNGTranscoder my_converter = new PNGTranscoder();
			// Step-4: Convert and Write output
			my_converter.transcode(input_svg_image, output_png_image);
			png_ostream.flush();

			Image spiderImage = Image.getInstance(png_ostream.toByteArray());

			int indentation = 0;
			float scaler = 0;
			// if (Integer.parseInt(svgs.get(3)) > 1024) {
			// scaler = ((document.getPageSize().getWidth() -
			// document.leftMargin() - document.rightMargin() - indentation) /
			// spiderImage.getWidth()) * 130;
			// spiderImage.setAbsolutePosition(-50, 80);
			// }
			
			// else if (Integer.parseInt(svgs.get(3)) > 1000) {
			// scaler = ((document.getPageSize().getWidth() -
			// document.leftMargin() - document.rightMargin() - indentation) /
			// spiderImage.getWidth()) * 100;
			// spiderImage.setAbsolutePosition(-10, 80);
			// }
			
			// else {
			scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin() - indentation) / spiderImage.getWidth()) * 110;
			spiderImage.setAbsolutePosition(100, 80);
			// }
			// spiderImage.scalePercent(scaler);

			PdfPTable mapDataTable = new PdfPTable(4);

			float[] mapDatacolumnWidths = new float[] { 8f, 30f, 30f, 30f };
			mapDataTable.setWidths(mapDatacolumnWidths);

			PdfPCell mapDataCell0 = new PdfPCell(new Paragraph("Sl.No.", smallBold));
			PdfPCell mapDataCell1 = new PdfPCell(new Paragraph("District ", smallBold));

			PdfPCell mapDataCell3 = new PdfPCell(new Paragraph("Percent", smallBold));
			PdfPCell mapDataCell4 = new PdfPCell(new Paragraph("Rank", smallBold));

			mapDataCell0.setBackgroundColor(siNoColor);
			mapDataCell1.setBackgroundColor(headerCOlor);

			mapDataCell3.setBackgroundColor(headerCOlor);
			mapDataCell4.setBackgroundColor(headerCOlor);

			mapDataCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			mapDataCell0.setHorizontalAlignment(Element.ALIGN_CENTER);
			mapDataCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			mapDataCell4.setHorizontalAlignment(Element.ALIGN_CENTER);

			mapDataCell0.setBorderColor(BaseColor.WHITE);
			mapDataCell1.setBorderColor(BaseColor.WHITE);
			mapDataCell3.setBorderColor(BaseColor.WHITE);
			mapDataCell4.setBorderColor(BaseColor.WHITE);

			mapDataTable.addCell(mapDataCell0);
			mapDataTable.addCell(mapDataCell1);

			mapDataTable.addCell(mapDataCell3);
			mapDataTable.addCell(mapDataCell4);

			int i = 1;
			valList.getDataCollection().sort(new Comparator<DataModel>() {

				@Override
				public int compare(DataModel o1, DataModel o2) {
					if(Integer.parseInt(o1.getRank())>Integer.parseInt(o2.getRank()))
						return 1;
					else if(Integer.parseInt(o1.getRank())<Integer.parseInt(o2.getRank()))
						return -1;
					else
					return 0;
				}
			});
			for (DataModel mapData : valList.getDataCollection()) {

				PdfPCell data0 = new PdfPCell(new Paragraph(Integer.toString(i), dataFont));
				data0.setHorizontalAlignment(Element.ALIGN_CENTER);
				data0.setBorderColor(BaseColor.WHITE);
				PdfPCell data1 = new PdfPCell(new Paragraph(mapData.getAreaName(), dataFont));
				data1.setHorizontalAlignment(Element.ALIGN_LEFT);
				data1.setBorderColor(BaseColor.WHITE);

				PdfPCell data3 = new PdfPCell(new Paragraph(mapData.getValue(), dataFont));
				data3.setHorizontalAlignment(Element.ALIGN_CENTER);
				data3.setBorderColor(BaseColor.WHITE);

				PdfPCell data4 = new PdfPCell(new Paragraph(mapData.getRank(), dataFont));
				data4.setHorizontalAlignment(Element.ALIGN_CENTER);
				data4.setBorderColor(BaseColor.WHITE);

				if (i % 2 == 0) {
					data0.setBackgroundColor(cellColor);
					data1.setBackgroundColor(cellColor);
					data3.setBackgroundColor(cellColor);
					data4.setBackgroundColor(cellColor);
				} else {
					data0.setBackgroundColor(BaseColor.LIGHT_GRAY);
					data1.setBackgroundColor(BaseColor.LIGHT_GRAY);
					data3.setBackgroundColor(BaseColor.LIGHT_GRAY);
					data4.setBackgroundColor(BaseColor.LIGHT_GRAY);
				}

				mapDataTable.addCell(data0);
				mapDataTable.addCell(data1);

				mapDataTable.addCell(data3);
				mapDataTable.addCell(data4);

				i++;

			}
			// Spider Data Table

			document.add(blankSpace);

			document.add(spiderDataParagraph);

			document.add(spiderImage);

//			if (valList.getBottomPerformers().size() > 0) {
//				document.add(topImage);
//			}
			document.add(legendImageInstance);

			document.newPage();
			document.add(mapDataTable);

			document.close();
			file = outputPath;
		}

		catch (Exception e) {

			e.printStackTrace();
		}

		return file;
	}

	@SuppressWarnings("resource")
	@Override
	public String exportPDFLine(List<String> svgs, Integer iusNid, Integer areaNid,Integer sourceNid) {

		String file = null;
		try {
			Agency agency = agencyRepository.findByAgencyId(1);
			// File f = new File((applicationMessageSource.getProperty("outputPathPdfLineChart") + "/trendSVG.svg"));
			
			// new FileOutputStream(f).write(svgs.get(3).getBytes());
			Area area = areaRepository.findByAreaId(areaNid);

			List<List<LineSeries>> lineSeries = fetchChartData(iusNid, areaNid,sourceNid);

			Font smallBold = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
			Font dataFont = new Font(Font.FontFamily.HELVETICA, 10);

			Document document = new Document(PageSize.A4.rotate());

			// String outputPath = applicationMessageSource.getMessage("outputPathPdfLineChart", null, null) +
			// "LineChart.pdf";
			String outputPath = File.createTempFile(area.getAreaName()+"_"+sdf.format(new java.util.Date()), ".pdf").toString();// "LineChart.pdf";

			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputPath));
			// setting Header Footer.PLS Refer to
			// org.sdrc.cpisico.util.HeaderFooter
			HeaderFooter headerFooter = new HeaderFooter(context, agency);
			writer.setPageEvent(headerFooter);

			document.open();

			BaseColor cellColor = WebColors.getRGBColor("#E8E3E2");
			BaseColor headerCOlor = WebColors.getRGBColor("#333a3b");
			BaseColor siNoColor = WebColors.getRGBColor("#3b9bd5");

			Paragraph dashboardTitle = new Paragraph();
			dashboardTitle.setAlignment(Element.ALIGN_CENTER);
			dashboardTitle.setSpacingAfter(10);
			Chunk dashboardChunk = new Chunk("Line Chart Score Card");
			dashboardTitle.add(dashboardChunk);

			Paragraph blankSpace = new Paragraph();
			blankSpace.setAlignment(Element.ALIGN_CENTER);
			blankSpace.setSpacingAfter(10);
			Chunk blankSpaceChunk = new Chunk("          ");
			blankSpace.add(blankSpaceChunk);

			Paragraph numberOfFacility = new Paragraph();
			numberOfFacility.setAlignment(Element.ALIGN_CENTER);
			Chunk numberOfFacilityChunk = new Chunk();
			numberOfFacility.add(numberOfFacilityChunk);

			Paragraph spiderDataParagraph = new Paragraph();
			spiderDataParagraph.setAlignment(Element.ALIGN_CENTER);
			spiderDataParagraph.setSpacingAfter(10);
			Chunk spiderChunk = new Chunk("Indicator : " + svgs.get(2) + "  Area : " + svgs.get(1));
			spiderDataParagraph.add(spiderChunk);

			byte[] lineChartImage = Base64.decodeBase64(svgs.get(0).split(",")[1]);
			Image lineTrendChartImage = Image.getInstance(lineChartImage);

			if (Integer.parseInt(svgs.get(3)) > 1024) {
				lineTrendChartImage.scalePercent(80);
				lineTrendChartImage.setAbsolutePosition(170, 150);
			} else if (Integer.parseInt(svgs.get(3)) > 1000) {
				lineTrendChartImage.scalePercent(80);
				lineTrendChartImage.setAbsolutePosition(200, 150);
			} else {
				lineTrendChartImage.scalePercent(90);
				lineTrendChartImage.setAbsolutePosition(180, 100);
			}

			// String css = "svg {" + "shape-rendering: geometricPrecision;" + "text-rendering:  geometricPrecision;" + "color-rendering: optimizeQuality;" + "image-rendering: optimizeQuality;" + "}";
			// File cssFile = File.createTempFile("batik-default-override-", ".css");
			// FileUtils.writeStringToFile(cssFile, css);

			// String svg_URI_input = Paths.get(new File(applicationMessageSource.getProperty("outputPathPdfLineChart") + "/trendSVG.svg").getPath()).toUri().toURL().toString();
			// TranscoderInput input_svg_image = new TranscoderInput(svg_URI_input);
			// // Step-2: Define OutputStream to PNG Image and attach to
			// // TranscoderOutput
			// ByteArrayOutputStream png_ostream = new ByteArrayOutputStream();
			// TranscoderOutput output_png_image = new TranscoderOutput(png_ostream);
			// // Step-3: Create PNGTranscoder and define hints if required
			// PNGTranscoder my_converter = new PNGTranscoder();
			// // Step-4: Convert and Write output
		
			// my_converter.transcode(input_svg_image, output_png_image);
			// png_ostream.flush();

			// Image spiderImage = Image.getInstance(png_ostream.toByteArray());

			// int indentation = 0;
			// float scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin() - indentation) / spiderImage.getWidth()) * 60;
			// if (Integer.parseInt(svgs.get(4)) > 1024) {
			// 	spiderImage.scalePercent(scaler);
			// 	spiderImage.setAbsolutePosition(175, 165);
			// } else if (Integer.parseInt(svgs.get(4)) > 1000) {
			// 	scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin() - indentation) / spiderImage.getWidth()) * 45;
			// 	spiderImage.scalePercent(scaler);
			// 	spiderImage.setAbsolutePosition(200, 160);
			// } else {
			// 	scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin() - indentation) / spiderImage.getWidth()) * 45;
			// 	spiderImage.scalePercent(scaler);
			// 	spiderImage.setAbsolutePosition(200, 150);
			// }
			// legendImageInstance.scalePercent(scalerMap);
			/* googleMapImage.setAbsolutePosition(40,5); */

			Paragraph googleMapParagraph = new Paragraph();
			googleMapParagraph.setAlignment(Element.ALIGN_CENTER);
			googleMapParagraph.setSpacingAfter(10);
			Chunk googleMapChunk = new Chunk();
			googleMapParagraph.add(googleMapChunk);

			PdfPTable mapDataTable = new PdfPTable(3);

			float[] mapDatacolumnWidths = new float[] { 8f, 15f, 15f };
			mapDataTable.setWidths(mapDatacolumnWidths);

			PdfPCell mapDataCell0 = new PdfPCell(new Paragraph("Sl.No.", smallBold));
			PdfPCell mapDataCell1 = new PdfPCell(new Paragraph("Data Value ", smallBold));

			PdfPCell mapDataCell3 = new PdfPCell(new Paragraph("Time Period", smallBold));

			mapDataCell0.setBackgroundColor(siNoColor);
			mapDataCell1.setBackgroundColor(headerCOlor);
			mapDataCell3.setBackgroundColor(headerCOlor);

			mapDataCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			mapDataCell0.setHorizontalAlignment(Element.ALIGN_CENTER);
			mapDataCell3.setHorizontalAlignment(Element.ALIGN_CENTER);

			mapDataCell0.setBorderColor(BaseColor.WHITE);
			mapDataCell1.setBorderColor(BaseColor.WHITE);
			mapDataCell3.setBorderColor(BaseColor.WHITE);

			mapDataTable.addCell(mapDataCell0);
			mapDataTable.addCell(mapDataCell1);

			mapDataTable.addCell(mapDataCell3);

			int i = 1;
			for (LineSeries seriesData : lineSeries.get(0)) {

				PdfPCell data0 = new PdfPCell(new Paragraph(Integer.toString(i), dataFont));
				data0.setHorizontalAlignment(Element.ALIGN_CENTER);
				data0.setBorderColor(BaseColor.WHITE);
				PdfPCell data1 = new PdfPCell(new Paragraph(seriesData.getValue().toString(), dataFont));
				data1.setHorizontalAlignment(Element.ALIGN_LEFT);
				data1.setBorderColor(BaseColor.WHITE);

				PdfPCell data3 = new PdfPCell(new Paragraph(seriesData.getDate(), dataFont));
				data3.setHorizontalAlignment(Element.ALIGN_CENTER);
				data3.setBorderColor(BaseColor.WHITE);

				if (i % 2 == 0) {
					data0.setBackgroundColor(cellColor);
					data1.setBackgroundColor(cellColor);
					data3.setBackgroundColor(cellColor);

				} else {
					data0.setBackgroundColor(BaseColor.LIGHT_GRAY);
					data1.setBackgroundColor(BaseColor.LIGHT_GRAY);
					data3.setBackgroundColor(BaseColor.LIGHT_GRAY);

				}

				mapDataTable.addCell(data0);
				mapDataTable.addCell(data1);

				mapDataTable.addCell(data3);

				i++;

			}
			// Spider Data Table

			document.add(blankSpace);

			document.add(spiderDataParagraph);

			document.add(lineTrendChartImage);

			// document.add(spiderImage);

			document.newPage();
			document.add(mapDataTable);

			document.close();
			file = outputPath;

		} catch (Exception e) {

			e.printStackTrace();
		}
		return file;
	}

	@Override
	@Transactional
	public boolean publishData() {
		LocalDateTime now = LocalDateTime.now();
		int year = now.getYear();

		int month = now.getMonthValue();
		if (month == 1) {
			month = 12;
			year = year - 1;

		} else {
			month = month - 1;
		}
		String monthString = "";
		if (month >= 10) {
			monthString = month + "";
		} else {
			monthString = "0" + month;
		}
	
		return publishData(1,year,month,monthString);
		

	}

	@Override
	@Transactional
	public boolean publishData(int agencyId, int year, int month, String monthString) {

		Agency agency = agencyRepository.findByAgencyId(agencyId);
		List<Area> areas = areaRepository.findAllByParentAreaId(agency.getState().getAreaId());
		Timeperiod timePeriod = timePeriodRepository.findByTimePeriod(year + "." + monthString);
		List<Data> datas = dataRepository.findByTimePeriodAndAreaIn(timePeriod, areas);

		PublishHistory history = publishHistoryRepository.findByDataBeingPublishedForMonthAndDataBeingPublishedForYear(month, year);
		if (history == null) {
			history = new PublishHistory();
			history.setDataBeingPublishedForMonth(month);
			history.setDataBeingPublishedForYear(year);
			history.setDataPublishedDate(new Date());
			history = publishHistoryRepository.save(history);
		}
		


		for (Data data : datas) {
			data.setPublished(true);
		}
		dataRepository.save(datas);
		return true;

	}

	@Override
	public boolean displayPublishButton() {

//		UserModel userModel = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		if (userModel != null) {
//			Agency agency = agencyRepository.findAll().get(0);
//			LocalDateTime now = LocalDateTime.now();
//			int day = now.getDayOfMonth();
//			int year = now.getYear();
//			int month = now.getMonthValue();
//			if (month == 1) {
//				month = 12;
//				year = year - 1;
//			} else {
//				month = month - 1;
//			}
//
//			PublishHistory history = publishHistoryRepository.findByDataBeingPublishedForMonthAndDataBeingPublishedForYear(month, year);
//			return history == null && day > agency.getLastDayForDataEntry();
//		}
		return false;
	}
}
