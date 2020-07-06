CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '内容描述',
  `summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `icon` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标',
  `post_count` int unsigned DEFAULT '0' COMMENT '该分类的内容数量',
  `order_num` int DEFAULT NULL COMMENT '排序编码',
  `parent_id` bigint unsigned DEFAULT NULL COMMENT '父级分类的ID',
  `meta_keywords` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'SEO关键字',
  `meta_description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'SEO描述内容',
  `created` datetime DEFAULT NULL COMMENT '创建日期',
  `modified` datetime DEFAULT NULL,
  `status` tinyint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC



INSERT INTO `category` VALUES ('1', '提问', null, null, null, '0', null, null, null, null, '2020-04-28 22:36:48', null, '0');
INSERT INTO `category` VALUES ('2', '分享', null, null, null, '0', null, null, null, null, '2020-04-28 22:36:48', null, '0');
INSERT INTO `category` VALUES ('3', '讨论', null, null, null, '0', null, null, null, null, '2020-04-28 22:36:48', null, '0');
INSERT INTO `category` VALUES ('4', '建议', null, null, null, '0', null, null, null, null, '2020-04-28 22:36:48', null, '0');