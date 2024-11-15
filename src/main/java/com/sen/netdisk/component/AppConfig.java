package com.sen.netdisk.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/10 20:48
 */
@Component
public class AppConfig {

    @Value("${project.folder.path}")
    private String projectFolderPath;


    public String getProjectFolderPath() {
        return this.projectFolderPath;
    }
}
