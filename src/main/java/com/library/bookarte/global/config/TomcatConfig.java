package com.library.bookarte.global.config;

import org.apache.catalina.Context;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.tomcat.TomcatContextCustomizer;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {
    @Value("${server.tomcat.max-part-count}")
    private int maxPartCount;

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> {
            // 팩토리 레벨에서 지원하는 경우 설정
            // (Spring Boot 버전에 따라 application.yml 설정이 우선시되므로,
            // 1번 방법과 병행하거나 1번 방법을 주력으로 사용하는 것을 권장합니다.)

            factory.addContextCustomizers(new TomcatContextCustomizer() {
                @Override
                public void customize(Context context) {
                    context.setAllowCasualMultipartParsing(true);
                    // 로그를 통해 현재 주입된 환경변수 값이 몇인지 확인할 수도 있습니다.
                    // System.out.println("적용된 최대 파트 개수 제한: " + maxPartCount);
                }
            });
        };
    }
}
