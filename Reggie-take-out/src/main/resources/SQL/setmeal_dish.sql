create table reggie.setmeal_dish
(
    id          bigint         not null comment '主键'
        primary key,
    setmeal_id  varchar(32)    not null comment '套餐id ',
    dish_id     varchar(32)    not null comment '菜品id',
    name        varchar(32)    null comment '菜品名称 （冗余字段）',
    price       decimal(10, 2) null comment '菜品原价（冗余字段）',
    copies      int            not null comment '份数',
    sort        int default 0  not null comment '排序',
    create_time datetime       not null comment '创建时间',
    update_time datetime       not null comment '更新时间',
    create_user bigint         not null comment '创建人',
    update_user bigint         not null comment '修改人',
    is_deleted  int default 0  not null comment '是否删除'
)
    comment '套餐菜品关系' collate = utf8_bin;

