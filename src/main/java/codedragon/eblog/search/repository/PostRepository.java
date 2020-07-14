package codedragon.eblog.search.repository;

import codedragon.eblog.search.model.PostDocment;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends ElasticsearchRepository<PostDocment, Long> {

    //符合JPA命名规范的接口
}
