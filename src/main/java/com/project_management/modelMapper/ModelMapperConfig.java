package com.project_management.modelMapper;

import com.project_management.entity.Project;
import com.project_management.entity.Task;
import com.project_management.response.ProjectResponse;
import com.project_management.response.TaskResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Enforce strict matching to prevent loose string-to-UUID guessing
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);

        // Project -> ProjectResponse
        modelMapper.addMappings(new PropertyMap<Project, ProjectResponse>() {
            @Override
            protected void configure() {
                skip().setCreatedBy((UUID) null);
                skip().setCreatedByName(null);
            }
        });

        modelMapper.typeMap(Project.class, ProjectResponse.class)
                .setPostConverter(context -> {
                    Project source = context.getSource();
                    ProjectResponse dest = context.getDestination();

                    if (source != null && source.getCreatedBy() != null) {

                        dest.setCreatedBy(source.getCreatedBy().getId());

                        dest.setCreatedByName(
                                source.getCreatedBy().getFirstName()
                                        + " "
                                        + source.getCreatedBy().getLastName()
                        );
                    }

                    return dest;
                });

        // Task -> TaskResponse
        modelMapper.addMappings(new PropertyMap<Task, TaskResponse>() {
            @Override
            protected void configure() {
                // Cast null to the exact parameter type expected by your setter
                skip().setCreatedBy((UUID) null);
                skip().setAssignedTo((UUID) null);
            }
        });

        modelMapper.typeMap(Task.class, TaskResponse.class)
                .setPostConverter(context -> {
                    Task source = context.getSource();
                    TaskResponse dest = context.getDestination();
                    if (source != null) {
                        if (source.getCreatedBy() != null) {
                            dest.setCreatedBy(source.getCreatedBy().getId());
                        }
                        if (source.getAssignedTo() != null) {
                            dest.setAssignedTo(source.getAssignedTo().getId());
                        }
                    }
                    return dest;
                });

        return modelMapper;
    }
}