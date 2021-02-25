package in.co.sdrc.scpstn.job;


import org.quartz.impl.JobDetailImpl;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;


public class OverridenJobDetailFactoryBean extends JobDetailFactoryBean {

    private boolean requestsRecovery;

    public OverridenJobDetailFactoryBean() {
        super();
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        JobDetailImpl jobDetail = (JobDetailImpl) getObject();
        jobDetail.setRequestsRecovery(true);
    }

    public boolean isRequestsRecovery() {
        return requestsRecovery;
    }

    public void setRequestsRecovery(boolean requestsRecovery) {
        this.requestsRecovery = requestsRecovery;
    }
}
