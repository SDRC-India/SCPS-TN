package in.co.sdrc.scpstn.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.stereotype.Component;

import in.co.sdrc.scpstn.service.AggregationService;

@Component
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class DailyJob implements Job{

	@Autowired
	public AggregationService aggreagationService;
	
	JobDataMap jMap = new JobDataMap(); 

	@Value("${daily.cron.frequency.jobwithcrontrigger}")
    private String frequency;
	
	@Value("${daily.job.group.name}")
    private String jobGroupName;
	
	@Value("${report.cron.trigger.group.name}")
    private String triggerGroupName;
	
	@Value("${jobdatamap.key}")
    private String jobDataMapKey;
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			logger.info("Tamilnadu SCPS Aggregation Starting.");
			aggreagationService.aggregateLastMonthData();
			aggreagationService.startPublishingForCurrentMonth();
		} catch (Exception e) {
				logger.error("Aggration failed. {}",e);
		}		
	}

	@Bean(name = "dailyJobBean")
    public JobDetailFactoryBean dailyMailReportJob() {
		JobDetailFactoryBean jBean = ConfigureQuartz.createJobDetail(this.getClass());
		jBean.setRequestsRecovery(true);
		jBean.setGroup(jobGroupName); //get these from prop file
		jBean.setDescription("Daily job that does and aggregation and publishing data based on db configuration in agency table");
		jBean.setJobDataMap(jMap);
		jMap.putAsString(jobDataMapKey, 0);
        return jBean;
    }
	
    @Bean(name = "dailyJobTrigger")
    public CronTriggerFactoryBean dailyJobTrigger(@Qualifier("dailyJobBean") JobDetailFactoryBean jdfb) {
    	CronTriggerFactoryBean cronTriggerFactoryBean = ConfigureQuartz.createCronTrigger(jdfb.getObject(),frequency);
    	cronTriggerFactoryBean.setGroup(triggerGroupName);
    	cronTriggerFactoryBean.setDescription("Fire daily");
    	return cronTriggerFactoryBean;
    }
}
