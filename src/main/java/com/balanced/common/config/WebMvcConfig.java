package com.balanced.common.config;

import com.balanced.common.resolver.SubjectArgumentResolver;
import com.balanced.common.resolver.UserIdArgumentResolver;
import com.balanced.common.resolver.WorkspaceIdArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new WorkspaceIdArgumentResolver());
        resolvers.add(new UserIdArgumentResolver());
        resolvers.add(new SubjectArgumentResolver());
    }
}
