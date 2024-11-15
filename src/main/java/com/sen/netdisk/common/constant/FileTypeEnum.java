package com.sen.netdisk.common.constant;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/11 13:21
 */
public enum FileTypeEnum implements IEnum<Integer> {
    VIDEO(FileCategoryEnum.VIDEO, 1, "视频", new String[]{".mp4", ".avi", ".rmvb", ".mkv", ".m4a", ".mov"}),
    AUDIO(FileCategoryEnum.AUDIO, 2, "音频", new String[]{".mp3", ".wav", ".wma", ".flac", ".aac"}),
    PICTURE(FileCategoryEnum.PICTURE, 3, "图片", new String[]{".jpg", ".jpeg", ".png", ".webp", ".gif", ".bmp",".JPG"}),
    PDF(FileCategoryEnum.DOCUMENT, 4, "PDF", new String[]{".pdf"}),
    DOC(FileCategoryEnum.DOCUMENT, 5, "DOC", new String[]{".doc", ".docx"}),
    EXCEL(FileCategoryEnum.DOCUMENT, 6, "EXCEL", new String[]{".xls", ".xlsx"}),
    TXT(FileCategoryEnum.DOCUMENT, 7, "TXT", new String[]{".txt"}),
    CODE(FileCategoryEnum.OTHER, 8, "CODE", new String[]{".java", ".html", ".json", ".go", ".json", ".md"}),
    ZIP(FileCategoryEnum.OTHER, 9, "ZIP", new String[]{".rar", ".zip", ".7z"}),
    OTHER(FileCategoryEnum.OTHER, 10, "其他", new String[]{});

    private final FileCategoryEnum fileCategoryEnum;

    private final Integer code;
    private final String description;

    private final String[] suffix;

    FileTypeEnum(FileCategoryEnum fileCategoryEnum, Integer code, String description, String[] suffix) {
        this.fileCategoryEnum = fileCategoryEnum;
        this.code = code;
        this.description = description;
        this.suffix = suffix;
    }

    public static FileTypeEnum getFileTypeBySuffix(String fileSuffix) {
        Optional<FileTypeEnum> optionalFileTypeEnum = Stream.of(FileTypeEnum.values())
                .filter(fileTypeEnum -> {
                    List<String> list = Arrays.asList(fileTypeEnum.getSuffix());
                    return list.contains(fileSuffix);
                }).findFirst();
        return optionalFileTypeEnum.orElse(FileTypeEnum.OTHER);
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getCodeString() {
        return code.toString();
    }

    @Override
    public String getDescription() {
        return description;
    }

    public FileCategoryEnum getFileCategoryEnum() {
        return fileCategoryEnum;
    }

    public String[] getSuffix() {
        return suffix;
    }
}
