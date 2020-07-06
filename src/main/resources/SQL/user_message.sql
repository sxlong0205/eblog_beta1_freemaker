CREATE TABLE `user_message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `from_user_id` bigint NOT NULL COMMENT '发送消息的用户ID',
  `to_user_id` bigint NOT NULL COMMENT '接收消息的用户ID',
  `post_id` bigint DEFAULT NULL COMMENT '消息可能关联的帖子',
  `comment_id` bigint DEFAULT NULL COMMENT '消息可能关联的评论',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `type` tinyint DEFAULT NULL COMMENT '消息类型',
  `created` datetime NOT NULL,
  `modified` datetime DEFAULT NULL,
  `status` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC

