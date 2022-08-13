create table reggie.setmeal
(
    id          bigint         not null comment '主键'
        primary key,
    category_id bigint         not null comment '菜品分类id',
    name        varchar(64)    not null comment '套餐名称',
    price       decimal(10, 2) not null comment '套餐价格',
    status      int            null comment '状态 0:停用 1:启用',
    code        varchar(32)    null comment '编码',
    description varchar(512)   null comment '描述信息',
    image       varchar(255)   null comment '图片',
    create_time datetime       not null comment '创建时间',
    update_time datetime       not null comment '更新时间',
    create_user bigint         not null comment '创建人',
    update_user bigint         not null comment '修改人',
    is_deleted  int default 0  not null comment '是否删除',
    constraint idx_setmeal_name
        unique (name)
)
    comment '套餐' collate = utf8_bin;

