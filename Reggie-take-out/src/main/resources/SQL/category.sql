create table reggie.category
(
    id          bigint        not null comment '主键'
        primary key,
    type        int           null comment '类型   1 菜品分类 2 套餐分类',
    name        varchar(64)   not null comment '分类名称',
    sort        int default 0 not null comment '顺序',
    create_time datetime      not null comment '创建时间',
    update_time datetime      not null comment '更新时间',
    create_user bigint        not null comment '创建人',
    update_user bigint        not null comment '修改人',
    constraint idx_category_name
        unique (name)
)
    comment '菜品及套餐分类' collate = utf8_bin;

