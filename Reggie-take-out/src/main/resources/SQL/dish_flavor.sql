create table reggie.dish_flavor
(
    id          bigint        not null comment '主键'
        primary key,
    dish_id     bigint        not null comment '菜品',
    name        varchar(64)   not null comment '口味名称',
    value       varchar(500)  null comment '口味数据list',
    create_time datetime      not null comment '创建时间',
    update_time datetime      not null comment '更新时间',
    create_user bigint        not null comment '创建人',
    update_user bigint        not null comment '修改人',
    is_deleted  int default 0 not null comment '是否删除'
)
    comment '菜品口味关系表' collate = utf8_bin;

