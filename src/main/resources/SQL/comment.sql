CREATE TABLE `comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论的内容',
  `parent_id` bigint DEFAULT NULL COMMENT '回复的评论ID',
  `post_id` bigint NOT NULL COMMENT '评论的内容ID',
  `user_id` bigint NOT NULL COMMENT '评论的用户ID',
  `vote_up` int unsigned NOT NULL DEFAULT '0' COMMENT '“顶”的数量',
  `vote_down` int unsigned NOT NULL DEFAULT '0' COMMENT '“踩”的数量',
  `level` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '置顶等级',
  `status` tinyint DEFAULT NULL COMMENT '评论的状态',
  `created` datetime NOT NULL COMMENT '评论的时间',
  `modified` datetime DEFAULT NULL COMMENT '评论的更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC

