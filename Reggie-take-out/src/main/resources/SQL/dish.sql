create table reggie.dish
(
    id          bigint         not null comment '主键'
        primary key,
    name        varchar(64)    not null comment '菜品名称',
    category_id bigint         not null comment '菜品分类id',
    price       decimal(10, 2) null comment '菜品价格',
    code        varchar(64)    not null comment '商品码',
    image       varchar(200)   not null comment '图片',
    description varchar(400)   null comment '描述信息',
    status      int default 1  not null comment '0 停售 1 起售',
    sort        int default 0  not null comment '顺序',
    create_time datetime       not null comment '创建时间',
    update_time datetime       not null comment '更新时间',
    create_user bigint         not null comment '创建人',
    update_user bigint         not null comment '修改人',
    is_deleted  int default 0  not null comment '是否删除',
    constraint idx_dish_name
        unique (name)
)
    comment '菜品管理' collate = utf8_bin;

