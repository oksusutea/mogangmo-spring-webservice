package com.oksusutea.webservice.job;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j //log사용을 위한 lombok 어노테이션
@RequiredArgsConstructor // 생성자 DI를 위한 lombok 어노테이션
@Configuration
public class SimpleJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;  // 생성자 DI 받음
    private final StepBuilderFactory stepBuilderFactory;    //생성자 DI 받음

    @Bean
    public Job simpleJob(){
        return jobBuilderFactory.get("simpleJob")
                .start(simpleStep1("20181201"))
                .build();
    }

    @Bean
    @JobScope
    public Step simpleStep1(@Value("#{jobParameters[requestDate]}") String requestDate){
        return stepBuilderFactory.get("simpleStep1")
                .tasklet((contribution,chunkContext) -> {
                    log.info(">>>>>This is Step1");
                    log.info(">>>>> requset Date={}",requestDate);
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}

/*
 * @Configuration : Spring Batch의 모든 Job은 @Configuration으로 등록하여 사용
 * jobBuilderFactory.get("simpleJob") : simpleJob이란 이름의 Batch Job 생성,Builder를 통해 이름을 지정
 * stepBuilderFactory.get("simpleStep1") : simpleStep이란 이름의 Batch Step 생성.
 * .tasklet((contribution, chunkContext)) : step안에서 수행될 기능을 명시, tasklet은 step 안에서 단일로 수행될 커스텀한 기능들을 선언할 때 사용
 *
 *  */