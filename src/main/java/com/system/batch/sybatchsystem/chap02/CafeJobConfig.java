package com.system.batch.sybatchsystem.chap02;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.batch.infrastructure.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class CafeJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final int ORDER_TARGET = 5;
    private int coffeeCount = 0;

    // 1. 카페 문 여일  => openCafeStep
    // 2. 커피 만들기(5잔) => makeCoffeStep
    // 3. 마감 청소 및 퇴근 => closeCafeStep

    @Bean
    public Job cafeJob() {
        return  new JobBuilder("cafeJob",jobRepository)
                .start(openCafeStep())
                .next(makeCoffeeStep())
                .next(closeCafeStep())
                .build();
    }


    @Bean
    public Step openCafeStep() {
        return new StepBuilder("openCafeStep", jobRepository).tasklet((contribution, chunkContext) -> {
            System.out.println("[오픈] 카페 문을 열고 머신을 예열합니다.");
            return RepeatStatus.FINISHED;
        }, transactionManager).build();
    }

    @Bean
    public Step makeCoffeeStep() {
        return new StepBuilder("makeCoffeeStep", jobRepository).tasklet((contribution, chunkContext) -> {
            coffeeCount++;
            System.out.println("[제조] 아메리카노 " + coffeeCount + "잔 째 완성!");
            if (coffeeCount > ORDER_TARGET) {
                return RepeatStatus.CONTINUABLE;
            } else {
                System.out.println("[완료] 주문하신 커피" + ORDER_TARGET + "잔 나왔습니다");
                return RepeatStatus.FINISHED;
            }
        }, new ResourcelessTransactionManager()).build(); // 트랜젝션이 필요하지 않은 경우 사용하는 매니저
//        }, transactionManager).build(); // 트랜젝션이 필요한 경우 사용하는 매니저
    }

    @Bean
    public Step closeCafeStep() {
        return new StepBuilder("closeCafeStep", jobRepository).tasklet((contribution, chunkContext) -> {
            System.out.println("[마감] 마감합니다!");
            return RepeatStatus.FINISHED;
        }, transactionManager).build();
    }

}
