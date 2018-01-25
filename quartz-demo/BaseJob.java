package com.crm.restapi.schedule.job;

import com.crm.restapi.model.QuartzModel;
import com.crm.restapi.service.*;
import com.crm.restapi.service.Impl.MakeExcelService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Queue;
import javax.persistence.EntityManager;
import java.text.SimpleDateFormat;

@Component
public class BaseJob implements Job {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    SimpleDateFormat yMd = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat yMdp = new SimpleDateFormat("yyyy/MM/dd");
    SimpleDateFormat yMdH = new SimpleDateFormat("yyyyMMddHH");
    SimpleDateFormat yMdHms = new SimpleDateFormat("yyyyMMddHHmmss");
    SimpleDateFormat HH = new SimpleDateFormat("HH");
    @Autowired
    UserService userService;
    @Autowired
    AccountService accountService;
    @Autowired
    MakeExcelService makeExcelService;
    @Autowired
    MailAccountService mailAccountService;
    @Autowired
    UserCenterAccountService userCenterAccountService;
    @Autowired
    BdService bdService;
    @Autowired
    AdNowService adNowService;
    @Autowired
    SDJService sdjService;
    @Autowired
    DictService dictService;
    @Autowired
    UMService umService;
    @Autowired
    AdService adService;
    @Autowired
    QuartzService quartzService;
    @Autowired
    EntityManager entityManager;
    @Autowired
    Queue queue;
    @Autowired
    JmsMessagingTemplate jmsMessagingTemplate;

    public BaseJob() {
        logger.info("初始化class->" + this.getClass().getName() + "->success");

    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

    }

}
