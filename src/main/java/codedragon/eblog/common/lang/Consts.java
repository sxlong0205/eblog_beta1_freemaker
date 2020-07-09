package codedragon.eblog.common.lang;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author : Code Dragon
 * create at:  2020/7/9  14:55
 */
@Slf4j
@Component
@Data
public class Consts {
    @Value("${file.upload.dir}")
    private String uploadDir;
}
