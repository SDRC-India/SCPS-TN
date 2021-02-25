package in.co.sdrc.scpstn.models;

import java.util.List;
import java.util.Map;

import in.co.sdrc.scpstn.domain.Area;
import lombok.Data;

@Data
public class QuickStatPageVO {

	private List<Area> districts;

	private List<QuickStartVO> quickStats;

	private List<String> tableColumn;

	private List<Map<String,Object>> tableData;
}
