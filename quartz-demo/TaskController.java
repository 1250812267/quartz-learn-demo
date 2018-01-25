package com.crm.restapi.controller;

import com.crm.restapi.annotation.XQuartz;
import com.crm.restapi.param.QuartzParam;
import com.crm.restapi.result.ApiResult;
import com.crm.restapi.service.QuartzService;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@Validated
@RequestMapping(value = "/task")
@RestController
public class TaskController extends BaseController {
    @Autowired
    QuartzService quartzService;
    @Autowired
    @Qualifier("TioadScheduler")//
    private Scheduler scheduler;
    @Value("${quartzpath}")
    private String quartzpath;//这是配置好的反射path

    @XQuartz
    @GetMapping("/start")
    public ApiResult startTask() throws ClassNotFoundException, SchedulerException {
        try {
            if (scheduler == null) {
                scheduler.start();
                return new ApiResult().success();
            } else {
                return new ApiResult().success();
            }
        } catch (SchedulerException e) {
            return new ApiResult().failure(e.getMessage());
        }
    }

    @XQuartz
    @PostMapping(value = "/startjob")
    public ApiResult startjob(@RequestBody @Valid QuartzParam quartzParam) throws ClassNotFoundException, SchedulerException {
        try {
            Class jobclass = Class.forName(quartzpath + quartzParam.getClassName());
            JobDetail job = newJob(jobclass)
                    .withIdentity(quartzParam.getJobName(), quartzParam.getJobGroup())
                    .build();
            Trigger trigger = newTrigger()
                    .withIdentity(quartzParam.getTgName(), quartzParam.getTgGroup())
                    .startNow()
                    .withSchedule(cronSchedule(quartzParam.getTrigger()))
                    .build();
            scheduler.scheduleJob(job, trigger);
            return new ApiResult().success();
        } catch (Exception e) {
            return new ApiResult().failure(e.getMessage());
        }
    }

    @XQuartz
    @GetMapping(value = "/job")
    public ApiResult getJob(String name, String group) throws SchedulerException {
        try {
            return new ApiResult().success(scheduler.getTrigger(TriggerKey.triggerKey(name, group)));
        } catch (SchedulerException e) {
            return new ApiResult().failure(e.getMessage());
        }
    }

    @XQuartz
    @PostMapping(value = "/remove")
    public ApiResult removeJob(@RequestBody HashMap jobk) throws SchedulerException {
        try {
            String jobName = (String) jobk.get("jobName");
            String jobGroup = (String) jobk.get("jobGroup");
            scheduler.deleteJob(JobKey.jobKey(jobName, jobGroup));
            return new ApiResult().success("remove success");
        } catch (SchedulerException e) {
            return new ApiResult().failure(e.getMessage());
        }
    }

    @XQuartz
    @GetMapping(value = "/clear")
    public ApiResult clearJob() throws SchedulerException {
        try {
            scheduler.clear();
            return new ApiResult().success("clear success");
        } catch (SchedulerException e) {
            return new ApiResult().failure(e.getMessage());
        }
    }

    @XQuartz
    @GetMapping("/list")
    public ApiResult list(String jobgroup,
                          String tgname,
                          String tggroup,
                          @RequestParam(value = "type", defaultValue = "0")
                                      Integer type) throws ClassNotFoundException, SchedulerException {
        try {
            HashMap map = new HashMap();
            if (type == 0) {
                Set<JobKey> jobKeySet = scheduler.getJobKeys(GroupMatcher.anyJobGroup());
                Set<TriggerKey> triggerKeySet = scheduler.getTriggerKeys(GroupMatcher.anyTriggerGroup());
                List<HashMap> tgList = new ArrayList<>();
                for(TriggerKey triggerKey : triggerKeySet){
                    CronTrigger  cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
                    HashMap m = new HashMap();
                    m.put("tg", cronTrigger.getCronExpression());
                    m.put("tgkey", triggerKey);
                    tgList.add(m);
                }
                map.put("joblist", jobKeySet);
                map.put("tglist", tgList);
                map.put("jobNum", jobKeySet.size());
                map.put("tgNum", triggerKeySet.size());
                return new ApiResult().success(map);
            } else if(type == 1){
                map.put("jobDedail", scheduler.getJobKeys(GroupMatcher.groupEquals(jobgroup)));
                CronTrigger  cronTrigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey(tgname, tggroup));
                map.put("tg",  cronTrigger.getCronExpression());
                return new ApiResult().success(map);
            }
            return new ApiResult().success();
        } catch (SchedulerException e) {
            return new ApiResult().failure(e.getMessage());
        }
    }

    @XQuartz
    @GetMapping("/listjob")
    public ApiResult listJob() throws Exception {
        try {
            return new ApiResult().success(quartzService.listAll());
        } catch (Exception e) {
            return new ApiResult().failure(e.getMessage());
        }
    }

}