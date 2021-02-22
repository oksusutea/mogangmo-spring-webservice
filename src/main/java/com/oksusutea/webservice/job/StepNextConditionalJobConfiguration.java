package com.oksusutea.webservice.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class StepNextConditionalJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job stepNextConditinalJob(){
        return jobBuilderFactory.get("stepNextConditinalJob")
                .start(conditionalJobStep1())
                    .on("FAILED") // FAILED 일경우
                    .to(conditionalJobStep3()) // step3으로 이동한다.
                    .on("*")    //step3의 결과와 관계없이
                    .end()      //step3이동 후 flow 종료한다.
               .from(conditionalJobStep1()) //step1로부터
                    .on("*")    // FAIL 외 모든 경우
                    .to(conditionalJobStep2())  //step2로 이동한다.
                    .next(conditionalJobStep3())    //step2가 정상 종료되면 step3로 이동
                    .on("*")    //step3 결과 관계없이
                    .end()      //step3로 이동하면 flow 종료
                .end()  //job 종료
                .build();
    }
    // on이 캐치하는 값은 BatchStatus가 아닌 ExitStatus다.
    @Bean
    public Step conditionalJobStep1() {
        return stepBuilderFactory.get("step1")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> This is stepNextConditionalJob Step1");

                    /* ExitStatus를 FAILED로 지정한다.
                     * 해당 status를 보고 flow가 진행된다.*/

                    //contribution.setExitStatus(ExitStatus.FAILED);
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step conditionalJobStep2(){
        return stepBuilderFactory.get("conditionalJobStep2")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> This is stepNextConditionalJob step2");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
    @Bean
    public Step conditionalJobStep3(){
        return stepBuilderFactory.get("conditionalJobStep3")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> This is stepNextConditionalJob Step3");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

}
