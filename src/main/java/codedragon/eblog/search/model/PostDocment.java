package codedragon.eblog.search.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : Code Dragon
 * create at:  2020/7/13  23:23
 */
@Data
@Document(indexName = "post", type = "post", createIndex = true)
public class PostDocment implements Serializable {

    @Id
    private Long id;

    //ik分词器
    @Field(type = FieldType.Text, searchAnalyzer = "ik_smart", analyzer = "ik_max_word")
    private String title;

    @Field(type = FieldType.Long)
    private Long authorId;

    @Field(type = FieldType.Keyword)
    private String authorName;
    private String authorAvatar;

    private Long categoryId;

    @Field(type = FieldType.Keyword)
    private String categoryName;

    @Field(type = FieldType.Date)
    private Date created;
    private Integer level;
    private Boolean recomment;
    private Integer commentCount;
    private Integer viewCount;
}
