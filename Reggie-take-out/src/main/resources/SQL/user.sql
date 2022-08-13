create table reggie.user
(
    id        bigint        not null comment '主键'
        primary key,
    name      varchar(50)   null comment '姓名',
    phone     varchar(100)  not null comment '手机号',
    sex       varchar(2)    null comment '性别',
    id_number varchar(18)   null comment '身份证号',
    avatar    varchar(500)  null comment '头像',
    status    int default 0 null comment '状态 0:禁用，1:正常'
)
    comment '用户信息' collate = utf8_bin;

