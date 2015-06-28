package com.sidooo.log;

import org.aspectj.lang.annotation.Aspect;


import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: kimzhang
 * Date: 15-2-12
 * Time: 下午8:14
 * To change this template use File | Settings | File Templates.
 */
@Aspect
@Service
public class LogAspect {

//    private static final Logger logger = LoggerFactory.getLogger("wmouth");

//    @After("execution(* com.sidooo.entity.EntityService.getDictionary(..))")
//    public void authorith(JoinPoint joinPoint) {
////        System.setProperty("log4j.configuration", "log4j.properties");
//        logger.info("AUTHORITH");
//    }

//    @Before("execution(* com.sidooo.web.*.*(..))")
//    public void before() {
//        System.out.println("BEFORE RUNNING");
//    }
}
