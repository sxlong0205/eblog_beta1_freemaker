CREATE TABLE `post` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `edit_mode` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '编辑模式：html可视化，markdown ..',
  `category_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `vote_up` int unsigned NOT NULL DEFAULT '0' COMMENT '支持人数',
  `vote_down` int unsigned NOT NULL DEFAULT '0' COMMENT '反对人数',
  `view_count` int unsigned NOT NULL DEFAULT '0' COMMENT '访问量',
  `comment_count` int NOT NULL DEFAULT '0' COMMENT '评论数量',
  `recommend` tinyint(1) DEFAULT NULL COMMENT '是否为精华',
  `level` tinyint NOT NULL DEFAULT '0' COMMENT '置顶等级',
  `status` tinyint DEFAULT NULL COMMENT '状态',
  `created` datetime DEFAULT NULL COMMENT '创建日期',
  `modified` datetime DEFAULT NULL COMMENT '最后更新日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC

INSERT INTO `post` VALUES ('1', 'Github上最值得学习的100个Java开源项目，涵盖各种技术栈！', ' 你有多久没好好学习一个开源项目了？\n\n你是否经常为找不到好的开源项目而烦恼？\n\n你是否为不知道怎么入手去看一个开源项目？\n\n你是否想看别人的项目学习笔记？\n\n你是否想跟着别人的项目搭建过程一步一步跟着做项目？\n\n为了让更多Java的开发者能更容易找到值得学习的开源项目，我搭建了这个Java开源学习网站，宗旨梳理Java知识，共享开源项目笔记。来瞧一瞧：\n\nimg[//image-1300566513.cos.ap-guangzhou.myqcloud.com/upload/images/20200414/473f73a3eb6f471e8154620a9c1d5306.png] \n\n网站截图中可以看出，点击筛选条件组合之后，再点击搜索就会搜索出对应的开源项目。\n\n比如打开renren-fast项目，可以看到具体项目的信息，以及模块解析。\n\nimg[//image-1300566513.cos.ap-guangzhou.myqcloud.com/upload/images/20200414/f74740692dab4d9c937cd56336ead1b4.png]\n\n这样，学习一个开源项目就不再费劲，每天学习一个开源项目，在不知不觉中提升技术水平！目前网站已经收录了近100个开源项目，让Java不再难懂！\n\n直接扫公众号下方的二维码，回复关键字【网站】即可获得网站的域名地址！\n\nimg[//image-1300566513.cos.ap-guangzhou.myqcloud.com/upload/images/20200414/f58f7c6d038c4dfb99bd9cf40935392e.png]\n\n关注上方的公众号二维码\n\n回复【网站】即可获取项目域名地址。\n', '0', '1', '1', '0', '0', '5', '0', '1', '1', null, '2020-04-28 14:41:41', '2020-04-28 14:41:41');
INSERT INTO `post` VALUES ('2', '关注公众号：MarkerHub，一起学习Java', '关注学习：\n\nimg[//image-1300566513.cos.ap-guangzhou.myqcloud.com/upload/images/20200414/f58f7c6d038c4dfb99bd9cf40935392e.png] ', '0', '1', '1', '0', '0', '3', '0', '1', '1', null, '2020-04-28 14:55:16', '2020-04-28 14:55:16');
INSERT INTO `post` VALUES ('3', '111111111111', '1222222222222222', '0', '1', '1', '0', '0', '1', '0', '0', '0', null, '2020-04-28 14:55:48', '2020-04-28 14:55:48');

