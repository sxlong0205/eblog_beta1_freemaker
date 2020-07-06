CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '昵称',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮件',
  `mobile` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机电话',
  `point` int unsigned NOT NULL DEFAULT '0' COMMENT '积分',
  `sign` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '个性签名',
  `gender` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '性别',
  `wechat` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信号',
  `vip_level` int DEFAULT NULL COMMENT 'vip等级',
  `birthday` datetime DEFAULT NULL COMMENT '生日',
  `avatar` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '头像',
  `post_count` int unsigned NOT NULL DEFAULT '0' COMMENT '内容数量',
  `comment_count` int unsigned NOT NULL DEFAULT '0' COMMENT '评论数量',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态',
  `lasted` datetime DEFAULT NULL COMMENT '最后的登陆时间',
  `created` datetime NOT NULL COMMENT '创建日期',
  `modified` datetime DEFAULT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`) USING BTREE,
  UNIQUE KEY `email` (`email`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC

INSERT INTO `user` VALUES ('1', 'MarkerHub', '96e79218965eb72c92a549dd5a330112', '11111@qq.com', null, '0', '关注公众号：MarkerHub，一起学Java！！', '0', null, '0', null, '/res/images/avatar/default.png', '0', '0', '0', '2020-04-28 14:54:11', '2020-04-28 14:37:24', null);
INSERT INTO `user` VALUES ('2', 'test007', '96e79218965eb72c92a549dd5a330112', '1111@qq.com', null, '0', null, '0', null, '0', null, '/res/images/avatar/default.png', '0', '0', '0', null, '2020-04-28 14:45:07', null);
INSERT INTO `user` VALUES ('3', 'test004', '96e79218965eb72c92a549dd5a330112', '144d11@qq.com', null, '0', null, '0', null, '0', null, '/res/images/avatar/default.png', '0', '0', '0', null, '2020-04-28 14:48:26', null);
INSERT INTO `user` VALUES ('4', 'test005', '96e79218965eb72c92a549dd5a330112', '144d15@qq.com', null, '0', null, '0', null, '0', null, '/res/images/avatar/default.png', '0', '0', '0', null, '2020-04-28 14:48:26', null);
INSERT INTO `user` VALUES ('5', 'test00756', '96e79218965eb72c92a549dd5a330112', '45454541@qq.com', null, '0', null, '0', null, '0', null, '/res/images/avatar/default.png', '0', '0', '0', null, '2020-04-28 14:53:49', null);
