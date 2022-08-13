create table reggie.address_book
(
    id            bigint                       not null comment '主键'
        primary key,
    user_id       bigint                       not null comment '用户id',
    consignee     varchar(50)                  not null comment '收货人',
    sex           tinyint                      not null comment '性别 0 女 1 男',
    phone         varchar(11)                  not null comment '手机号',
    province_code varchar(12) charset utf8mb4  null comment '省级区划编号',
    province_name varchar(32) charset utf8mb4  null comment '省级名称',
    city_code     varchar(12) charset utf8mb4  null comment '市级区划编号',
    city_name     varchar(32) charset utf8mb4  null comment '市级名称',
    district_code varchar(12) charset utf8mb4  null comment '区级区划编号',
    district_name varchar(32) charset utf8mb4  null comment '区级名称',
    detail        varchar(200) charset utf8mb4 null comment '详细地址',
    label         varchar(100) charset utf8mb4 null comment '标签',
    is_default    tinyint(1) default 0         not null comment '默认 0 否 1是',
    create_time   datetime                     not null comment '创建时间',
    update_time   datetime                     not null comment '更新时间',
    create_user   bigint                       not null comment '创建人',
    update_user   bigint                       not null comment '修改人',
    is_deleted    int        default 0         not null comment '是否删除'
)
    comment '地址管理' collate = utf8_bin;

